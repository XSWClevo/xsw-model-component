package com.xshiwu.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.exception.BusinessException;
import com.xshiwu.mapper.AppointmentMapper;
import com.xshiwu.model.entity.User;
import com.xshiwu.pojo.dto.AppointmentRequest;
import com.xshiwu.pojo.entity.Appointment;
import com.xshiwu.service.AppointmentService;
import com.xshiwu.service.UserService;
import com.xshiwu.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {

    @Resource
    private UserService userService;

    @Override
    public void addAppointment(AppointmentRequest appointmentRequest, String token) {
        if (!JwtUtils.checkToken(token)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "身份认证失败");
        }
        // 获取用户ID
        String userId = JwtUtils.parseToken(token);
        // 用户ID查库获取当前登录用户信息
        User user = userService.getById(Long.parseLong(userId));
        if (ObjectUtil.isEmpty(user)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 插入预约表
        Appointment appointment = new Appointment();
        appointment.setUserId(appointmentRequest.getAppointmentPersonId());
        appointment.setServiceTypeId(appointmentRequest.getServiceType());
        appointment.setDate(new Date());
        appointment.setStartTime(appointmentRequest.getDateRange().getStartTime());
        appointment.setEndTime(appointmentRequest.getDateRange().getEndTime());
        appointment.setStatus("预约中");
        appointment.setCreatedAt(new Date());
        appointment.setUpdatedAt(new Date());

        this.baseMapper.insert(appointment);

        // TODO 插入预约人ID与被预约人ID信息到关联表

    }
}
