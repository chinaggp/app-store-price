package com.hypo.appstoreprice.pojo.response;

import lombok.Data;
import java.util.List;

@Data
public class HomePageResDTO {
    private List<DiscountAppDTO> recentDiscounts;
    private FeaturedAppDTO featured;
    private List<RegionDealDTO> regionDeals;

    @Data
    public static class DiscountAppDTO {
        private String appId;
        private String name;
        private String iconUrl;
        private String discountText;
        private String currentPrice;
    }

    @Data
    public static class FeaturedAppDTO {
        private String appId;
        private String name;
        private String subtitle;
        private String iconUrl;
        private String discountDesc;
    }

    @Data
    public static class RegionDealDTO {
        private String areaCode;
        private String areaName;
        private String appId;
        private String appName;
        private String iconUrl;
        private String cnyPrice;
    }
}
