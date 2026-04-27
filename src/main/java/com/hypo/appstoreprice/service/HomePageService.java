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
            AppInfoEntity featApp = apps.get(0);
            HomePageResDTO.FeaturedAppDTO featDto = new HomePageResDTO.FeaturedAppDTO();
            featDto.setAppId(featApp.getAppId());
            featDto.setName(featApp.getName());
            featDto.setSubtitle(featApp.getSubtitle());
            featDto.setIconUrl(featApp.getIconUrl());
            featDto.setDiscountDesc("今日精选降价");
            res.setFeatured(featDto);
        }
        
        List<HomePageResDTO.RegionDealDTO> deals = new ArrayList<>();
        if (apps.size() > 1) {
            AppInfoEntity trApp = apps.get(1 % apps.size());
            HomePageResDTO.RegionDealDTO deal = new HomePageResDTO.RegionDealDTO();
            deal.setAreaCode("tr");
            deal.setAreaName("土耳其");
            deal.setAppId(trApp.getAppId());
            deal.setAppName(trApp.getName());
            deal.setIconUrl(trApp.getIconUrl());
            deal.setCnyPrice("¥2.50");
            deals.add(deal);
        }
        res.setRegionDeals(deals);

        return res;
    }
}
