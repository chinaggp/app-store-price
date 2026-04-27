package com.hypo.appstoreprice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("app_info")
public class AppInfoEntity {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 应用在商店的唯一ID */
    private String appId;

    /** 应用名称 */
    private String name;

    /** 应用副标题 */
    private String subtitle;

    /** 开发者名称 */
    private String developer;

    /** 应用图标链接 */
    private String iconUrl;

    /** 分类ID */
    private String categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 评分 */
    private Double rating;

    /** 评论数量 */
    private String reviewCount;

    /** 创建时间 */
    private String createdAt;

    /** 更新时间 */
    private String updatedAt;
}
