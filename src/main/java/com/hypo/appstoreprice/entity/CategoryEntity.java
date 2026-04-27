package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class CategoryEntity {
    @TableId
    private String id;
    private String name;
    private String description;
    private String icon;
    private Integer sortOrder;
    private String createdAt;
}
