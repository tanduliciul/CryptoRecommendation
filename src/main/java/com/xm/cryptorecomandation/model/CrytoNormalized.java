package com.xm.cryptorecomandation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrytoNormalized implements Comparable<CrytoNormalized> {

    private String cryptoName;
    private Double normalizedValue;

    @Override
    public int compareTo(CrytoNormalized o) {
        if (o.normalizedValue == this.normalizedValue) {
            return 0;
        } else if (this.normalizedValue > o.normalizedValue) {
            return -1;
        } else {
            return 1;
        }
    }
}
