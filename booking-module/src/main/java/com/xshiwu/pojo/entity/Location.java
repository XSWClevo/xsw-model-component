package com.xshiwu.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("location")
public class Location {
    /* 地点ID */
    private Integer id;
    /* 名称 */
    private String name;
    /* 地址 */
    private String address;
    /* 创建时间 */
    private Date createdAt;
    /* 更新时间 */
    private Date updatedAt;
}


