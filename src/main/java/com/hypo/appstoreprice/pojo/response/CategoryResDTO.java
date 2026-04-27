package com.hypo.appstoreprice.pojo.response;

import lombok.Data;

/**
 * 分类响应 DTO
 */
@Data
public class CategoryResDTO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private Integer appCount;
}
