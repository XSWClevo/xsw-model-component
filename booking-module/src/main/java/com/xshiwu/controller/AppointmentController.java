package com.xshiwu.controller;

import com.xshiwu.common.BaseResponse;
import com.xshiwu.common.ResultUtils;
import com.xshiwu.pojo.dto.AppointmentRequest;
import com.xshiwu.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController()
@RequestMapping("/appointment")
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    @GetMapping("/hello")
    public String hello() {
        return "appointment";
    }

    public BaseResponse<Long> addAppointment(@RequestBody AppointmentRequest appointmentRequest, HttpServletRequest request) {

        appointmentService.addAppointment(appointmentRequest, (String) request.getAttribute("token"));

        return ResultUtils.success(1L);
    }
}

