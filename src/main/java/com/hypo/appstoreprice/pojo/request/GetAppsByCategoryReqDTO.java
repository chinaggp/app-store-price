package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetAppsByCategoryReqDTO {
    @NotBlank(message = "categoryId cannot be empty")
    private String categoryId;
    
    private String subCategory;
    
    private Integer page = 1;
    
    private Integer size = 20;
}
