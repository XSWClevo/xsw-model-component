package com.xshiwu.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@Data
@TableName("appointment")
public class Appointment {
    /* 预约ID */
    private Long id;
    /* 用户ID */
    private Long userId;
    /* 服务类型ID */
    private Integer serviceTypeId;
    /* 地点ID */
    private Integer locationId;
    /* 日期 */
    private Date date;
    /* 开始时间 */
    private Long startTime;
    /* 结束时间 */
    private Long endTime;
    /* 状态 */
    private String status;
    /* 创建时间 */
    private Date createdAt;
    /* 更新时间 */
    private Date updatedAt;
}

