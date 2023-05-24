package com.xshiwu.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.constant.CommonConstant;
import com.xshiwu.exception.BusinessException;
import com.xshiwu.exception.ThrowUtils;
import com.xshiwu.mapper.AppointmentMapper;
import com.xshiwu.model.entity.User;
import com.xshiwu.pojo.dto.AppointmentRequest;
import com.xshiwu.pojo.entity.Appointment;
import com.xshiwu.pojo.entity.AppointmentUser;
import com.xshiwu.service.AppointmentService;
import com.xshiwu.service.AppointmentUserService;
import com.xshiwu.service.UserService;
import com.xshiwu.utils.JwtUtils;
import com.xshiwu.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Resource
    private UserService userService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private AppointmentUserService appointmentUserService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addAppointment(AppointmentRequest appointmentRequest, String token) {
        if (!JwtUtils.checkToken(token)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "身份认证失败");
        }
        // 获取用户ID
        String userId = JwtUtils.parseToken(token);
        checkCurrentUser(userId);
        Long startTime = appointmentRequest.getDateRange().getStartTime();
        Long endTime = appointmentRequest.getDateRange().getEndTime();
        // 被预约人ID
        Long appointmentPersonId = appointmentRequest.getAppointmentPersonId();
        Integer serviceType = appointmentRequest.getServiceType();
        // 根据当前的要被预约的用户ID加锁，避免多个用户同时预约一个人的情况
        synchronized (String.valueOf(appointmentPersonId).intern()) {
            // 判断被预约人在此时间段内有没有被预约
            checkAppointmentStatus(startTime, endTime, appointmentPersonId);

            // 插入预约表
            Appointment appointment = new Appointment();
            appointment.setUserId(appointmentPersonId);
            appointment.setServiceTypeId(serviceType);
            appointment.setDate(new Date());
            appointment.setStartTime(startTime);
            appointment.setEndTime(endTime);
            appointment.setStatus("预约中");
            appointment.setCreatedAt(new Date());
            appointment.setUpdatedAt(new Date());
            this.baseMapper.insert(appointment);

            AppointmentUser one = appointmentUserService.getOne(new LambdaQueryWrapper<AppointmentUser>().eq(AppointmentUser::getAppointmentId, appointmentPersonId));
            ThrowUtils.throwIf(ObjectUtil.isNotEmpty(one), ErrorCode.OPERATION_ERROR, "此用户已被预约");
            AppointmentUser appointmentUser = new AppointmentUser(appointmentPersonId, Long.parseLong(userId), startTime, endTime, serviceType);
            // 插入信息到关联表
            appointmentUserService.save(appointmentUser);
        }
    }

    /**
     * 校验当前用户的信息
     */
    private void checkCurrentUser(String userId) {
        // 用户ID获取当前登录用户信息
        User user = (User) redisUtils.get(CommonConstant.AUTHORIZED_USER + userId);
        if (ObjectUtil.isEmpty(user)) {
            user = userService.getById(Long.parseLong(userId));
            if (ObjectUtil.isEmpty(user)) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
            } else {
                redisUtils.set(CommonConstant.AUTHORIZED_USER + user.getId(), user, 3600);
                String userToken = JwtUtils.getJwtToken(String.valueOf(user.getId()), user.getUserName());
                // authorized:userId ：token信息
                redisUtils.set(CommonConstant.AUTHORIZED_TOKEN + user.getId(), userToken, 3600);
            }
        }
    }

    /**
     * 用户ID与开始、结束时间查询，该用户在这个时间段里面是否被预约
     */
    private void checkAppointmentStatus(Long startTime, Long endTime, Long appointmentPersonId) {
        LambdaQueryWrapper<Appointment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(Appointment::getId, appointmentPersonId)
                .between(Appointment::getStartTime, startTime, endTime)
                .between(Appointment::getEndTime, startTime, endTime);
        List<Appointment> appointmentList = this.list(lambdaQueryWrapper);
        ThrowUtils.throwIf(CollectionUtil.isNotEmpty(appointmentList), ErrorCode.OPERATION_ERROR, "此用户已被预约");
    }
}
