package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SearchAppsReqDTO {
    @NotBlank(message = "keyword cannot be empty")
    private String keyword;
    
    private String filter;
    
    private String areaCode = "cn";
}
