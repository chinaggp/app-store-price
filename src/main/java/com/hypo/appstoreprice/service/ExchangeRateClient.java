package com.hypo.appstoreprice.service;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeRateClient {
    Map<String, BigDecimal> getLatestRates(String baseCurrencyCode);
}
