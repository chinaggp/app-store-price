package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("category")
public class CategoryEntity {
    /** 分类ID */
    @TableId
    private String id;

    /** 分类名称 */
    private String name;

    /** 分类描述 */
    private String description;

    /** 分类图标 */
    private String icon;

    /** 排序权重 */
    private Integer sortOrder;

    /** 创建时间 */
    private String createdAt;
}
