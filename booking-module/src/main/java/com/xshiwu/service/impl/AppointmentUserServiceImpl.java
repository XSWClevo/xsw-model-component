package com.xshiwu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshiwu.mapper.AppointmentUserMapper;
import com.xshiwu.pojo.entity.AppointmentUser;
import com.xshiwu.service.AppointmentUserService;
import org.springframework.stereotype.Service;

@Service
public class AppointmentUserServiceImpl extends ServiceImpl<AppointmentUserMapper, AppointmentUser> implements AppointmentUserService {
}
