package com.hypo.appstoreprice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hypo.appstoreprice.common.BizException;
import com.hypo.appstoreprice.pojo.request.GetAppInfoReqDTO;
import com.hypo.appstoreprice.pojo.request.GetAppListReqDTO;
import com.hypo.appstoreprice.pojo.response.AreaResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppInfoComparisonResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppInfoResDTO;
import com.hypo.appstoreprice.pojo.response.GetAppListResDTO;
import com.hypo.appstoreprice.service.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hypo.appstoreprice.pojo.request.GetAppsByCategoryReqDTO;
import com.hypo.appstoreprice.pojo.request.GetPriceHistoryReqDTO;
import com.hypo.appstoreprice.pojo.request.SearchAppsReqDTO;
import com.hypo.appstoreprice.pojo.response.*;
import com.hypo.appstoreprice.service.AppInfoService;
import com.hypo.appstoreprice.service.CategoryService;
import com.hypo.appstoreprice.service.HomePageService;
import com.hypo.appstoreprice.service.PriceSnapshotService;
import com.hypo.appstoreprice.entity.PriceSnapshotEntity;
import com.hypo.appstoreprice.task.PriceSnapshotTask;
import cn.hutool.core.date.DateUtil;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;

/**
 * app controller
 *
 * @author hypo
 * @date 2025-09-16
 */
@RestController
@RequestMapping("app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final CategoryService categoryService;
    private final AppInfoService appInfoService;
    private final PriceSnapshotTask priceSnapshotTask;
    private final PriceSnapshotService priceSnapshotService;
    private final HomePageService homePageService;

    @Value("${debug.trigger-price-snapshot-enabled:false}")
    private boolean triggerPriceSnapshotEnabled;

    /**
     * get homepage data
     */
    @PostMapping("getHomePageData")
    public HomePageResDTO getHomePageData() {
        return homePageService.getHomePageData();
    }

    /**
     * search apps (enhanced)
     */
    @PostMapping("searchApps")
    public java.util.List<AppSearchResDTO> searchApps(@RequestBody @Validated SearchAppsReqDTO reqDTO) {
        com.hypo.appstoreprice.pojo.request.GetAppListReqDTO innerReq = new com.hypo.appstoreprice.pojo.request.GetAppListReqDTO();
        innerReq.setAppName(reqDTO.getKeyword());
        innerReq.setAreaCode(reqDTO.getAreaCode());
        java.util.List<com.hypo.appstoreprice.pojo.response.GetAppListResDTO> rawList = appService.getAppList(innerReq);
        return rawList.stream().map(raw -> {
            AppSearchResDTO dto = new AppSearchResDTO();
            dto.setAppId(raw.getAppId());
            dto.setName(raw.getAppName());
            dto.setIconUrl(raw.getAppImage());
            dto.setPriceText("");
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * trigger price snapshot (debug only)
     */
    @PostMapping("triggerPriceSnapshot")
    public void triggerPriceSnapshot() {
        if (!triggerPriceSnapshotEnabled) {
            throw new BizException("triggerPriceSnapshot is disabled");
        }
        CompletableFuture.runAsync(priceSnapshotTask::executeSnapshot);
    }

    /**
     * get price history
     */
    @PostMapping("getPriceHistory")
    public java.util.List<PriceHistoryResDTO> getPriceHistory(@RequestBody @Validated GetPriceHistoryReqDTO reqDTO) {
        String startDate = DateUtil.formatDate(DateUtil.offsetMonth(new java.util.Date(), -reqDTO.getMonths()));
        String endDate = DateUtil.formatDate(new java.util.Date());

        java.util.List<PriceSnapshotEntity> snapshots = priceSnapshotService.getSnapshotsByDateRange(
            reqDTO.getAppId(), reqDTO.getAreaCode(), startDate, endDate
        );

        return snapshots.stream().map(s -> {
            PriceHistoryResDTO dto = new PriceHistoryResDTO();
            dto.setDate(s.getSnapshotDate());
            dto.setPrice(java.math.BigDecimal.valueOf(s.getPrice()));
            dto.setCnyPrice(java.math.BigDecimal.valueOf(s.getCnyPrice()));
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * get category list
     */
    @PostMapping("getCategoryList")
    public List<CategoryResDTO> getCategoryList() {
        return categoryService.getCategoryList();
    }

    /**
     * get apps by category
     */
    @PostMapping("getAppsByCategory")
    public Page<AppSummaryResDTO> getAppsByCategory(@RequestBody @Validated GetAppsByCategoryReqDTO reqDTO) {
        return appInfoService.getAppsByCategory(reqDTO.getCategoryId(), reqDTO.getPage(), reqDTO.getSize());
    }

    /**
     * get area list
     *
     * @return {@link List }<{@link AreaResDTO }>
     */
    @PostMapping("getAreaList")
    public List<AreaResDTO> getAreaList() {
        return appService.getAreaList();
    }

    /**
     * get popular search word list
     *
     * @return {@link List }<{@link String }>
     */
    @PostMapping("getPopularSearchWordList")
    public List<String> getPopularSearchWordList() {
        return appService.getPopularSearchWordList();
    }

    /**
     * get initial app list
     *
     * @return {@link List }<{@link GetAppListResDTO }>
     */
    @PostMapping("getInitialAppList")
    public List<GetAppListResDTO> getInitialAppList() {
        return appService.getInitialAppList();
    }

    /**
     * get app list
     *
     * @param reqDTO req dto
     * @return {@link List }<{@link GetAppListResDTO }>
     */
    @PostMapping("getAppList")
    public List<GetAppListResDTO> getAppList(@RequestBody @Validated GetAppListReqDTO reqDTO) {
        return appService.getAppList(reqDTO);
    }

    /**
     * get app info
     *
     * @return {@link GetAppInfoResDTO }
     */
    @PostMapping("getAppInfo")
    public List<GetAppInfoResDTO> getAppInfo(@RequestBody @Validated GetAppInfoReqDTO reqDTO) {
        return appService.getAppInfo(reqDTO.getAppId());
    }

    /**
     * get app info comparison
     *
     * @param reqDTO req dto
     * @return {@link List }<{@link GetAppInfoComparisonResDTO }>
     */
    @PostMapping("getAppInfoComparison")
    public List<GetAppInfoComparisonResDTO> getAppInfoComparison(@RequestBody @Validated GetAppInfoReqDTO reqDTO) {
        return appService.getAppInfoComparison(reqDTO.getAppId());
    }

}
