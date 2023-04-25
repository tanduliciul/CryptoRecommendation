package com.xm.cryptorecomandation.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CryptoMetrics {

    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private BigDecimal oldestPrice;
    private BigDecimal newestPrice;
}
