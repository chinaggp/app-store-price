package com.hypo.appstoreprice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hypo.appstoreprice.entity.AppInfoEntity;
import com.hypo.appstoreprice.entity.PriceSnapshotEntity;
import com.hypo.appstoreprice.mapper.AppInfoMapper;
import com.hypo.appstoreprice.mapper.PriceSnapshotMapper;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import com.hypo.appstoreprice.pojo.response.HomePageResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomePageService {

    private final AppInfoMapper appInfoMapper;
    private final PriceSnapshotMapper priceSnapshotMapper;
    private final ExchangeRateService exchangeRateService;

    public HomePageResDTO getHomePageData() {
        HomePageResDTO res = new HomePageResDTO();

        List<PriceSnapshotEntity> snapshots = priceSnapshotMapper.selectList(
            new LambdaQueryWrapper<PriceSnapshotEntity>()
                .orderByDesc(PriceSnapshotEntity::getSnapshotDate)
                .orderByDesc(PriceSnapshotEntity::getId)
        );

        if (snapshots.isEmpty()) {
            res.setRecentDiscounts(new ArrayList<>());
            res.setRegionDeals(new ArrayList<>());
            res.setExchangeRates(exchangeRateService.getHomePageExchangeRates());
            return res;
        }

        Map<String, PriceSnapshotEntity> latestByAppArea = buildLatestByAppArea(snapshots);
        Map<String, AppInfoEntity> appInfoMap = loadAppInfoMap(latestByAppArea.values());

        res.setRecentDiscounts(buildRecentDiscounts(snapshots, latestByAppArea.values(), appInfoMap));
        res.setFeatured(buildFeatured(latestByAppArea.values(), appInfoMap));
        res.setRegionDeals(buildRegionDeals(latestByAppArea.values(), appInfoMap));
        res.setExchangeRates(exchangeRateService.getHomePageExchangeRates());
        return res;
    }

    private Map<String, PriceSnapshotEntity> buildLatestByAppArea(List<PriceSnapshotEntity> snapshots) {
        Map<String, PriceSnapshotEntity> latestByAppArea = new LinkedHashMap<>();
        for (PriceSnapshotEntity snapshot : snapshots) {
            latestByAppArea.putIfAbsent(buildAppAreaKey(snapshot.getAppId(), snapshot.getAreaCode()), snapshot);
        }
        return latestByAppArea;
    }

    private Map<String, AppInfoEntity> loadAppInfoMap(Collection<PriceSnapshotEntity> snapshots) {
        List<String> appIds = snapshots.stream()
            .map(PriceSnapshotEntity::getAppId)
            .distinct()
            .toList();
        if (appIds.isEmpty()) {
            return Map.of();
        }
        return appInfoMapper.selectList(
            new LambdaQueryWrapper<AppInfoEntity>().in(AppInfoEntity::getAppId, appIds)
        ).stream().collect(Collectors.toMap(AppInfoEntity::getAppId, item -> item, (left, right) -> left));
    }

    private List<HomePageResDTO.DiscountAppDTO> buildRecentDiscounts(
        List<PriceSnapshotEntity> snapshots,
        Collection<PriceSnapshotEntity> latestSnapshots,
        Map<String, AppInfoEntity> appInfoMap
    ) {
        Map<String, List<PriceSnapshotEntity>> latestByApp = latestSnapshots.stream()
            .collect(Collectors.groupingBy(PriceSnapshotEntity::getAppId));

        return latestByApp.entrySet().stream()
            .map(entry -> buildDiscountCard(entry.getKey(), entry.getValue(), snapshots, appInfoMap))
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(HomePageDiscountCard::percentage).reversed())
            .limit(10)
            .map(HomePageDiscountCard::dto)
            .toList();
    }

    private HomePageDiscountCard buildDiscountCard(
        String appId,
        List<PriceSnapshotEntity> latestSnapshots,
        List<PriceSnapshotEntity> allSnapshots,
        Map<String, AppInfoEntity> appInfoMap
    ) {
        AppInfoEntity appInfo = appInfoMap.get(appId);
        if (appInfo == null) {
            return null;
        }

        PriceSnapshotEntity latest = latestSnapshots.stream()
            .filter(snapshot -> Objects.equals("cn", snapshot.getAreaCode()))
            .findFirst()
            .orElseGet(() -> latestSnapshots.stream()
                .filter(snapshot -> snapshot.getCnyPrice() != null)
                .min(Comparator.comparing(PriceSnapshotEntity::getCnyPrice))
                .orElse(null));
        if (latest == null || latest.getCnyPrice() == null || latest.getCnyPrice() <= 0) {
            return null;
        }

        PriceSnapshotEntity previous = allSnapshots.stream()
            .filter(snapshot -> Objects.equals(snapshot.getAppId(), appId))
            .filter(snapshot -> Objects.equals(snapshot.getAreaCode(), latest.getAreaCode()))
            .filter(snapshot -> !Objects.equals(snapshot.getSnapshotDate(), latest.getSnapshotDate()))
            .filter(snapshot -> snapshot.getCnyPrice() != null && snapshot.getCnyPrice() > 0)
            .findFirst()
            .orElse(null);
        if (previous == null || previous.getCnyPrice() <= latest.getCnyPrice()) {
            return null;
        }

        double percentage = (previous.getCnyPrice() - latest.getCnyPrice()) / previous.getCnyPrice() * 100;
        HomePageResDTO.DiscountAppDTO dto = new HomePageResDTO.DiscountAppDTO();
        dto.setAppId(appId);
        dto.setName(appInfo.getName());
        dto.setIconUrl(appInfo.getIconUrl());
        dto.setDiscountText(String.format("-%.0f%%", percentage));
        dto.setCurrentPrice(formatPrice(latest.getCurrencyCode(), latest.getPrice()));
        return new HomePageDiscountCard(dto, percentage);
    }

    private HomePageResDTO.FeaturedAppDTO buildFeatured(
        Collection<PriceSnapshotEntity> latestSnapshots,
        Map<String, AppInfoEntity> appInfoMap
    ) {
        PriceSnapshotEntity featuredSnapshot = latestSnapshots.stream()
            .filter(snapshot -> Objects.equals("cn", snapshot.getAreaCode()))
            .max(Comparator.comparing(PriceSnapshotEntity::getSnapshotDate)
                .thenComparing(snapshot -> snapshot.getCnyPrice() == null ? Double.MAX_VALUE : snapshot.getCnyPrice(), Comparator.reverseOrder()))
            .orElseGet(() -> latestSnapshots.stream()
                .max(Comparator.comparing(PriceSnapshotEntity::getSnapshotDate)
                    .thenComparing(snapshot -> snapshot.getCnyPrice() == null ? Double.MAX_VALUE : snapshot.getCnyPrice(), Comparator.reverseOrder()))
                .orElse(null));
        if (featuredSnapshot == null) {
            return null;
        }

        AppInfoEntity appInfo = appInfoMap.get(featuredSnapshot.getAppId());
        if (appInfo == null) {
            return null;
        }

        HomePageResDTO.FeaturedAppDTO featured = new HomePageResDTO.FeaturedAppDTO();
        featured.setAppId(appInfo.getAppId());
        featured.setName(appInfo.getName());
        featured.setSubtitle(appInfo.getSubtitle());
        featured.setIconUrl(appInfo.getIconUrl());
        featured.setDiscountDesc(formatRegionPrice(featuredSnapshot));
        return featured;
    }

    private List<HomePageResDTO.RegionDealDTO> buildRegionDeals(
        Collection<PriceSnapshotEntity> latestSnapshots,
        Map<String, AppInfoEntity> appInfoMap
    ) {
        return latestSnapshots.stream()
            .filter(snapshot -> snapshot.getCnyPrice() != null)
            .collect(Collectors.groupingBy(PriceSnapshotEntity::getAreaCode))
            .values()
            .stream()
            .map(areaSnapshots -> areaSnapshots.stream()
                .min(Comparator.comparing(PriceSnapshotEntity::getCnyPrice))
                .orElse(null))
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(PriceSnapshotEntity::getCnyPrice))
            .limit(8)
            .map(snapshot -> {
                AppInfoEntity appInfo = appInfoMap.get(snapshot.getAppId());
                if (appInfo == null) {
                    return null;
                }
                HomePageResDTO.RegionDealDTO dto = new HomePageResDTO.RegionDealDTO();
                dto.setAreaCode(snapshot.getAreaCode());
                dto.setAreaName(AreaEnum.getByCode(snapshot.getAreaCode()).getName());
                dto.setAppId(snapshot.getAppId());
                dto.setAppName(appInfo.getName());
                dto.setIconUrl(appInfo.getIconUrl());
                dto.setCnyPrice(formatPrice("CNY", snapshot.getCnyPrice()));
                return dto;
            })
            .filter(Objects::nonNull)
            .toList();
    }

    private String formatRegionPrice(PriceSnapshotEntity snapshot) {
        return AreaEnum.getByCode(snapshot.getAreaCode()).getName() + "区现价 " + formatPrice(snapshot.getCurrencyCode(), snapshot.getPrice());
    }

    private String formatPrice(String currencyCode, Double price) {
        if (price == null) {
            return "";
        }
        String symbol = switch (currencyCode) {
            case "CNY" -> "¥";
            case "USD" -> "$";
            case "HKD" -> "HK$";
            case "TWD" -> "NT$";
            case "JPY" -> "¥";
            case "BRL" -> "R$";
            default -> AreaEnum.getByCurrencyCode(currencyCode).getCurrency();
        };
        return symbol + BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String buildAppAreaKey(String appId, String areaCode) {
        return appId + ":" + areaCode;
    }

    private record HomePageDiscountCard(HomePageResDTO.DiscountAppDTO dto, double percentage) {
    }
}
