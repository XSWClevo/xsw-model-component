package com.xshiwu.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

@Data
@TableName("schedule")
public class Schedule {
    /* 日程ID */
    private Integer id;
    /* 用户ID */
    private Integer userId;
    /* 预约ID */
    private Integer appointmentId;
    /* 开始时间 */
    private Date startTime;
    /* 结束时间 */
    private Date endTime;
    /* 创建时间 */
    private Date createdAt;
    /* 更新时间 */
    private Date updatedAt;
}

