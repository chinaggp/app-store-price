package com.hypo.appstoreprice.pojo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetPriceHistoryReqDTO {
    @NotBlank(message = "appId cannot be empty")
    private String appId;
    
    private String areaCode = "cn";
    
    private Integer months = 6;
}
