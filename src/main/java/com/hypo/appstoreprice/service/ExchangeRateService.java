package com.hypo.appstoreprice.service;

import com.hypo.appstoreprice.common.LogUtil;
import com.hypo.appstoreprice.pojo.enums.AreaEnum;
import com.hypo.appstoreprice.pojo.response.ExchangeRateCardResDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ExchangeRateService {

    private static final String DEFAULT_BASE_CURRENCY = "USD";
    private static final List<String> HOME_PAGE_AREA_CODES = List.of("tr", "ar", "in", "br");

    private final ExchangeRateClient exchangeRateClient;
    private final Map<String, CachedExchangeRateCards> cache = new ConcurrentHashMap<>();

    public ExchangeRateService(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    public List<ExchangeRateCardResDTO> getHomePageExchangeRates() {
        LocalDate today = LocalDate.now();
        CachedExchangeRateCards cached = cache.get(DEFAULT_BASE_CURRENCY);
        if (cached != null && today.equals(cached.cacheDate())) {
            return cached.cards();
        }

        try {
            List<ExchangeRateCardResDTO> cards = buildHomePageCards(today, exchangeRateClient.getLatestRates(DEFAULT_BASE_CURRENCY));
            cache.put(DEFAULT_BASE_CURRENCY, new CachedExchangeRateCards(today, cards));
            return cards;
        } catch (RuntimeException ex) {
            if (cached != null && !cached.cards().isEmpty()) {
                return cached.cards();
            }
            throw ex;
        }
    }

    private List<ExchangeRateCardResDTO> buildHomePageCards(LocalDate today, Map<String, BigDecimal> rateMap) {
        List<ExchangeRateCardResDTO> cards = new ArrayList<>();
        for (String areaCode : HOME_PAGE_AREA_CODES) {
            AreaEnum area = AreaEnum.getByCode(areaCode);
            BigDecimal rate = rateMap.get(area.getCurrencyCode());
            if (rate == null) {
                LogUtil.warn(log, "skip missing exchange rate", "areaCode", areaCode, "currencyCode", area.getCurrencyCode());
                continue;
            }
            BigDecimal roundedRate = rate.setScale(2, RoundingMode.HALF_UP);
            cards.add(new ExchangeRateCardResDTO(
                area.getCode(),
                area.getName(),
                area.getCurrencyCode(),
                DEFAULT_BASE_CURRENCY,
                roundedRate,
                String.format("1 %s = %s %s", DEFAULT_BASE_CURRENCY, roundedRate.toPlainString(), area.getCurrencyCode()),
                today.toString()
            ));
        }
        return cards;
    }

    private record CachedExchangeRateCards(LocalDate cacheDate, List<ExchangeRateCardResDTO> cards) {
    }
}
