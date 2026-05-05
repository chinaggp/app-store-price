package com.hypo.appstoreprice.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hypo.appstoreprice.entity.AppInfoEntity;
import com.hypo.appstoreprice.entity.WatchedAppEntity;
import com.hypo.appstoreprice.mapper.AppInfoMapper;
import com.hypo.appstoreprice.mapper.WatchedAppMapper;
import com.hypo.appstoreprice.pojo.response.GetAppInfoResDTO;
import com.hypo.appstoreprice.service.AppService;
import com.hypo.appstoreprice.service.PriceSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceSnapshotTask {

    private final WatchedAppMapper watchedAppMapper;
    private final AppService appService;
    private final PriceSnapshotService priceSnapshotService;
    private final AppInfoMapper appInfoMapper;

    @Scheduled(cron = "${price-snapshot.cron:0 0 4 * * ?}")
    public void executeSnapshot() {
        log.info("Start price snapshot task");
        String today = DateUtil.formatDate(new Date());

        List<WatchedAppEntity> watchedApps = watchedAppMapper.selectList(
            new LambdaQueryWrapper<WatchedAppEntity>().eq(WatchedAppEntity::getEnabled, 1)
        );

        for (WatchedAppEntity app : watchedApps) {
            try {
                log.info("Fetching app price snapshot: {} - {}", app.getAppId(), app.getName());
                List<GetAppInfoResDTO> appInfos = appService.getAppInfo(app.getAppId());

                boolean infoUpdated = false;
                for (GetAppInfoResDTO info : appInfos) {
                    if (info.getPrice() != null) {
                        priceSnapshotService.saveSnapshot(
                            app.getAppId(),
                            info.getArea(),
                            info.getPrice().getCurrencyCode(),
                            info.getPrice().getPrice(),
                            info.getPrice().getCnyPrice(),
                            today
                        );
                    }

                    if (!infoUpdated) {
                        AppInfoEntity appInfo = appInfoMapper.selectOne(
                            new LambdaQueryWrapper<AppInfoEntity>().eq(AppInfoEntity::getAppId, app.getAppId())
                        );
                        if (appInfo == null) {
                            appInfo = new AppInfoEntity();
                            appInfo.setAppId(app.getAppId());
                            appInfo.setName(info.getName());
                            appInfo.setSubtitle(info.getSubtitle());
                            appInfo.setDeveloper(info.getDeveloper());
                            appInfo.setCategoryId(app.getCategoryId());
                            appInfo.setCreatedAt(DateUtil.now());
                            appInfo.setUpdatedAt(DateUtil.now());
                            appInfoMapper.insert(appInfo);
                        } else {
                            appInfo.setName(info.getName());
                            appInfo.setSubtitle(info.getSubtitle());
                            appInfo.setDeveloper(info.getDeveloper());
                            appInfo.setUpdatedAt(DateUtil.now());
                            appInfoMapper.updateById(appInfo);
                        }
                        infoUpdated = true;
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Fetch snapshot failed for app {}: {}", app.getAppId(), e.getMessage(), e);
            }
        }
        log.info("Price snapshot task finished");
    }
}
