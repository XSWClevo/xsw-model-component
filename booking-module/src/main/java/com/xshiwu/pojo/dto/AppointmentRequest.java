package com.xshiwu.pojo.dto;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

@Data
public class AppointmentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // 被预约人的ID
    @NonNull
    private Long appointmentPersonId;

    // 预约范围时间，这里改用时间戳更好
    @NonNull
    private DateRange dateRange;

    @NonNull
    private Integer serviceType;
}
