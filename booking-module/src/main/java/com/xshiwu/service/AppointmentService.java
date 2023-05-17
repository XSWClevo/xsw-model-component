package com.xshiwu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xshiwu.pojo.dto.AppointmentRequest;
import com.xshiwu.pojo.entity.Appointment;

public interface AppointmentService extends IService<Appointment> {

    void addAppointment(AppointmentRequest appointmentRequest, String token);
}
