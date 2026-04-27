package com.hypo.appstoreprice.pojo.response;

import com.hypo.appstoreprice.pojo.bean.Money;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppSummaryResDTO {
    private String appId;
    private String name;
    private String subtitle;
    private String categoryName;
    private String iconUrl;
    private BigDecimal rating;
    private String reviewCount;
    private Money currentPrice;
    private String lowestRegion;
    private Money lowestPrice;
    private String discountText;
}
