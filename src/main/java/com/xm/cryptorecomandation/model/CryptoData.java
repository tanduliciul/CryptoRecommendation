package com.xm.cryptorecomandation.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoData implements Comparable<CryptoData>{

    private Instant time;

    private String symbol;
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CryptoData that = (CryptoData) o;
        return time.equals(that.time) && price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, price);
    }

    @Override
    public int compareTo(CryptoData o) {
        if (o.getPrice().equals(this.price)) {
            return 0;
        } else if (this.getPrice().doubleValue() > o.getPrice().doubleValue()) {
            return -1;
        } else {
            return 1;
        }
    }
}
