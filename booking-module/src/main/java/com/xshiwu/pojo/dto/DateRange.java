package com.xshiwu.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DateRange implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long startTime;

    private Long endTime;
}
