package com.hypo.appstoreprice.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hypo.appstoreprice.common.BizException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class FrankfurterExchangeRateClient implements ExchangeRateClient {

    private final String apiBaseUrl;

    public FrankfurterExchangeRateClient(
        @Value("${exchange-rate.frankfurter-base-url:https://api.frankfurter.dev/v1}") String apiBaseUrl
    ) {
        this.apiBaseUrl = apiBaseUrl;
    }

    @Override
    public Map<String, BigDecimal> getLatestRates(String baseCurrencyCode) {
        String url = StrUtil.format("{}/latest?base={}", apiBaseUrl, baseCurrencyCode);
        JSONObject result = JSON.parseObject(HttpUtil.get(url));
        JSONObject rates = result.getJSONObject("rates");
        if (rates == null || rates.isEmpty()) {
            throw new BizException("failed to fetch exchange rates from Frankfurter");
        }
        Map<String, BigDecimal> rateMap = new HashMap<>();
        rates.forEach((key, value) -> rateMap.put(key, new BigDecimal(Convert.toStr(value))));
        return rateMap;
    }
}
