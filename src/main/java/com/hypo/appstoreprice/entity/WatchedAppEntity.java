package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("watched_app")
public class WatchedAppEntity {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 应用唯一ID */
    private String appId;

    /** 应用名称 */
    private String name;

    /** 分类ID */
    private String categoryId;

    /** 是否启用 (1: 启用, 0: 禁用) */
    private Integer enabled;

    /** 创建时间 */
    private String createdAt;
}
