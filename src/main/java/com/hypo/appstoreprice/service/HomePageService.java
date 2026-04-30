package com.hypo.appstoreprice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hypo.appstoreprice.entity.AppInfoEntity;
import com.hypo.appstoreprice.mapper.AppInfoMapper;
import com.hypo.appstoreprice.pojo.response.HomePageResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomePageService {

    private final AppInfoMapper appInfoMapper;
    private final ExchangeRateService exchangeRateService;

    public HomePageResDTO getHomePageData() {
        HomePageResDTO res = new HomePageResDTO();

        List<AppInfoEntity> apps = appInfoMapper.selectList(
            new LambdaQueryWrapper<AppInfoEntity>().last("LIMIT 10")
        );

        List<HomePageResDTO.DiscountAppDTO> discounts = new ArrayList<>();
        for (AppInfoEntity app : apps) {
            HomePageResDTO.DiscountAppDTO dto = new HomePageResDTO.DiscountAppDTO();
            dto.setAppId(app.getAppId());
            dto.setName(app.getName());
            dto.setIconUrl(app.getIconUrl());
            dto.setDiscountText("-50%");
            dto.setCurrentPrice("¥" + String.format("%.2f", Math.random() * 50));
            discounts.add(dto);
        }
        res.setRecentDiscounts(discounts);

        if (!apps.isEmpty()) {
            AppInfoEntity featuredApp = apps.get(0);
            HomePageResDTO.FeaturedAppDTO featured = new HomePageResDTO.FeaturedAppDTO();
            featured.setAppId(featuredApp.getAppId());
            featured.setName(featuredApp.getName());
            featured.setSubtitle(featuredApp.getSubtitle());
            featured.setIconUrl(featuredApp.getIconUrl());
            featured.setDiscountDesc("今日精选");
            res.setFeatured(featured);
        }

        res.setRegionDeals(new ArrayList<>());
        res.setExchangeRates(exchangeRateService.getHomePageExchangeRates());
        return res;
    }
}
