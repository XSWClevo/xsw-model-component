package com.xshiwu.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("service_type")
public class ServiceType {
    /* 服务类型ID */
    private Integer id;
    /* 名称 */
    private String name;
    /* 描述 */
    private String description;
    /* 价格 */
    private BigDecimal price;
    /* 创建时间 */
    private Date createdAt;
    /* 更新时间 */
    private Date updatedAt;
}
