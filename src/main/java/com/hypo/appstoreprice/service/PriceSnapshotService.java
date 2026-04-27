package com.hypo.appstoreprice.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hypo.appstoreprice.entity.PriceSnapshotEntity;
import com.hypo.appstoreprice.mapper.PriceSnapshotMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceSnapshotService {

    private final PriceSnapshotMapper priceSnapshotMapper;

    public void saveSnapshot(String appId, String areaCode, String currencyCode, BigDecimal price, BigDecimal cnyPrice, String date) {
        PriceSnapshotEntity existing = priceSnapshotMapper.selectOne(
            new LambdaQueryWrapper<PriceSnapshotEntity>()
                .eq(PriceSnapshotEntity::getAppId, appId)
                .eq(PriceSnapshotEntity::getAreaCode, areaCode)
                .eq(PriceSnapshotEntity::getSnapshotDate, date)
        );
        if (existing == null) {
            PriceSnapshotEntity entity = new PriceSnapshotEntity();
            entity.setAppId(appId);
            entity.setAreaCode(areaCode);
            entity.setCurrencyCode(currencyCode);
            entity.setPrice(price.doubleValue());
            entity.setCnyPrice(cnyPrice.doubleValue());
            entity.setSnapshotDate(date);
            entity.setCreatedAt(DateUtil.now());
            priceSnapshotMapper.insert(entity);
        } else {
            existing.setPrice(price.doubleValue());
            existing.setCnyPrice(cnyPrice.doubleValue());
            priceSnapshotMapper.updateById(existing);
        }
    }

    public PriceSnapshotEntity getLatestSnapshot(String appId, String areaCode) {
        return priceSnapshotMapper.selectOne(
            new LambdaQueryWrapper<PriceSnapshotEntity>()
                .eq(PriceSnapshotEntity::getAppId, appId)
                .eq(PriceSnapshotEntity::getAreaCode, areaCode)
                .orderByDesc(PriceSnapshotEntity::getSnapshotDate)
                .last("LIMIT 1")
        );
    }

    public List<PriceSnapshotEntity> getSnapshotsByDateRange(String appId, String areaCode, String startDate, String endDate) {
        return priceSnapshotMapper.selectList(
            new LambdaQueryWrapper<PriceSnapshotEntity>()
                .eq(PriceSnapshotEntity::getAppId, appId)
                .eq(PriceSnapshotEntity::getAreaCode, areaCode)
                .ge(PriceSnapshotEntity::getSnapshotDate, startDate)
                .le(PriceSnapshotEntity::getSnapshotDate, endDate)
                .orderByAsc(PriceSnapshotEntity::getSnapshotDate)
        );
    }

    @Data
    public static class DiscountInfo {
        private String discountType;
        private String discountText;
        private double percentage;
        public DiscountInfo(String discountType, String discountText, double percentage) {
            this.discountType = discountType;
            this.discountText = discountText;
            this.percentage = percentage;
        }
    }

    public DiscountInfo detectDiscount(String appId, String areaCode) {
        PriceSnapshotEntity latest = getLatestSnapshot(appId, areaCode);
        if (latest == null) return null;

        String weekAgoDate = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -7));
        PriceSnapshotEntity weekAgo = priceSnapshotMapper.selectOne(
            new LambdaQueryWrapper<PriceSnapshotEntity>()
                .eq(PriceSnapshotEntity::getAppId, appId)
                .eq(PriceSnapshotEntity::getAreaCode, areaCode)
                .le(PriceSnapshotEntity::getSnapshotDate, weekAgoDate)
                .orderByDesc(PriceSnapshotEntity::getSnapshotDate)
                .last("LIMIT 1")
        );

        PriceSnapshotEntity historicalLow = priceSnapshotMapper.selectOne(
            new LambdaQueryWrapper<PriceSnapshotEntity>()
                .eq(PriceSnapshotEntity::getAppId, appId)
                .eq(PriceSnapshotEntity::getAreaCode, areaCode)
                .orderByAsc(PriceSnapshotEntity::getCnyPrice)
                .last("LIMIT 1")
        );

        if (weekAgo != null && latest.getCnyPrice() < weekAgo.getCnyPrice()) {
            double discount = (weekAgo.getCnyPrice() - latest.getCnyPrice()) / weekAgo.getCnyPrice() * 100;
            return new DiscountInfo("近期降价", String.format("-%.0f%%", discount), discount);
        }
        
        if (historicalLow != null && latest.getCnyPrice() <= historicalLow.getCnyPrice() && latest.getCnyPrice() > 0) {
            return new DiscountInfo("历史低价", "历史低价", 0);
        }

        return null;
    }
}
