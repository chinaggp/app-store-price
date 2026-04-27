package com.hypo.appstoreprice.pojo.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceHistoryResDTO {
    private String date;
    private BigDecimal price;
    private BigDecimal cnyPrice;
}
