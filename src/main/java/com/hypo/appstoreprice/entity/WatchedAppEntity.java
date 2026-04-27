package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("watched_app")
public class WatchedAppEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String appId;
    private String name;
    private String categoryId;
    private Integer enabled;
    private String createdAt;
}
