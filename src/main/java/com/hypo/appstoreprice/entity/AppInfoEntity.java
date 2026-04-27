package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("app_info")
public class AppInfoEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String appId;
    private String name;
    private String subtitle;
    private String developer;
    private String iconUrl;
    private String categoryId;
    private String categoryName;
    private Double rating;
    private String reviewCount;
    private String createdAt;
    private String updatedAt;
}
