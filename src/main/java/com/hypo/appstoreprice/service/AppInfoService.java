package com.hypo.appstoreprice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hypo.appstoreprice.entity.AppInfoEntity;
import com.hypo.appstoreprice.entity.PriceSnapshotEntity;
import com.hypo.appstoreprice.entity.WatchedAppEntity;
import com.hypo.appstoreprice.mapper.AppInfoMapper;
import com.hypo.appstoreprice.mapper.PriceSnapshotMapper;
import com.hypo.appstoreprice.mapper.WatchedAppMapper;
import com.hypo.appstoreprice.pojo.bean.Money;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import com.hypo.appstoreprice.pojo.response.AppSummaryResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppInfoService {

    private final WatchedAppMapper watchedAppMapper;
    private final AppInfoMapper appInfoMapper;
    private final PriceSnapshotMapper priceSnapshotMapper;

    public Page<AppSummaryResDTO> getAppsByCategory(String categoryId, Integer pageParam, Integer sizeParam) {
        Page<WatchedAppEntity> page = new Page<>(pageParam, sizeParam);
        watchedAppMapper.selectPage(page, new LambdaQueryWrapper<WatchedAppEntity>()
                .eq(WatchedAppEntity::getCategoryId, categoryId)
                .eq(WatchedAppEntity::getEnabled, 1)
                .orderByDesc(WatchedAppEntity::getId)); // 简单按ID倒序，实际可以按热门程度

        List<AppSummaryResDTO> records = page.getRecords().stream().map(watchedApp -> {
            AppSummaryResDTO dto = new AppSummaryResDTO();
            dto.setAppId(watchedApp.getAppId());
            dto.setName(watchedApp.getName());

            AppInfoEntity appInfo = appInfoMapper.selectOne(
                    new LambdaQueryWrapper<AppInfoEntity>().eq(AppInfoEntity::getAppId, watchedApp.getAppId())
            );

            if (appInfo != null) {
                dto.setSubtitle(appInfo.getSubtitle());
                dto.setCategoryName(appInfo.getCategoryName());
                dto.setIconUrl(appInfo.getIconUrl());
                dto.setRating(appInfo.getRating() != null ? BigDecimal.valueOf(appInfo.getRating()) : null);
                dto.setReviewCount(appInfo.getReviewCount());
            }

            // 获取最新价格信息 (查询各个区域最新快照的最低价)
            List<PriceSnapshotEntity> snapshots = priceSnapshotMapper.selectList(
                    new LambdaQueryWrapper<PriceSnapshotEntity>()
                            .eq(PriceSnapshotEntity::getAppId, watchedApp.getAppId())
                            .orderByDesc(PriceSnapshotEntity::getSnapshotDate)
                            // 限制取最近一天的记录即可，这里简化处理，实际需要更严谨的"最新一天"查询
            );

            if (!snapshots.isEmpty()) {
                // 分组取每个地区的最新一条
                Map<String, PriceSnapshotEntity> latestSnapshots = snapshots.stream()
                        .collect(Collectors.toMap(
                                PriceSnapshotEntity::getAreaCode,
                                s -> s,
                                (existing, replacement) -> existing // 保持第一条即最新的
                        ));

                // 找到中国区价格
                PriceSnapshotEntity cnSnapshot = latestSnapshots.get("cn");
                if (cnSnapshot != null) {
                    dto.setCurrentPrice(new Money(cnSnapshot.getCurrencyCode(), BigDecimal.valueOf(cnSnapshot.getPrice())));
                }

                // 找到最低价地区
                PriceSnapshotEntity lowestSnapshot = latestSnapshots.values().stream()
                        .min((s1, s2) -> Double.compare(s1.getCnyPrice(), s2.getCnyPrice()))
                        .orElse(null);

                if (lowestSnapshot != null) {
                    dto.setLowestRegion(AreaEnum.getByCurrencyCode(lowestSnapshot.getCurrencyCode()).getName());
                    dto.setLowestPrice(new Money(lowestSnapshot.getCurrencyCode(), BigDecimal.valueOf(lowestSnapshot.getPrice())));
                }
            }

            return dto;
        }).collect(Collectors.toList());

        Page<AppSummaryResDTO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(records);
        return resultPage;
    }
}
