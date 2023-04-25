package com.xm.cryptorecomandation.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LocalDateCrypto implements Comparable<LocalDateCrypto> {

    private LocalDate localDate;
    private String symbol;
    private BigDecimal price;

    @Override
    public int compareTo(LocalDateCrypto o) {
        if (o.getPrice().equals(this.price)) {
            return 0;
        } else if (this.getPrice().doubleValue() > o.getPrice().doubleValue()) {
            return 1;
        } else {
            return -1;
        }
    }
}
