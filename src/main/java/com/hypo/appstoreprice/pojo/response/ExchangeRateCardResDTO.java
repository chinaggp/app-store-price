package com.hypo.appstoreprice.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateCardResDTO {
    private String areaCode;
    private String areaName;
    private String currencyCode;
    private String baseCurrencyCode;
    private BigDecimal exchangeRate;
    private String exchangeRateText;
    private String updatedAt;
}
