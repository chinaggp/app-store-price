package com.hypo.appstoreprice.pojo.response;

import lombok.Data;

@Data
public class AppSearchResDTO {
    private String appId;
    private String name;
    private String subtitle;
    private String categoryName;
    private String iconUrl;
    private Double rating;
    private String lowestRegion;
    private String priceText;
    private String oldPriceText;
    private String discountText;
}
