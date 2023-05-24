package com.xshiwu.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("appointment_user")
public class AppointmentUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long appointmentId;

    private Long userId;

    private Long startTime;

    private Long endTime;

    private Integer appointmentType;

    public AppointmentUser() {
    }

    public AppointmentUser(Long appointmentId, Long userId, Long startTime, Long endTime, Integer appointmentType) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.appointmentType = appointmentType;
    }
}
