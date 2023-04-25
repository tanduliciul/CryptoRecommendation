package com.xm.cryptorecomandation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.xm.cryptorecomandation.exception.CryptoNotFoundException;
import com.xm.cryptorecomandation.model.CryptoData;
import com.xm.cryptorecomandation.model.LocalDateCrypto;
import com.xm.cryptorecomandation.model.CryptoMetrics;
import com.xm.cryptorecomandation.model.CrytoNormalized;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.xm.cryptorecomandation.service.CsvProcessorService.cryptoMetricsMap;
import static com.xm.cryptorecomandation.service.CsvProcessorService.cryptoPricesMap;
import static com.xm.cryptorecomandation.service.CsvProcessorService.cryptoToMapOfDatesAndPricesList;

@Service
public class CryptoService {

    public CryptoMetrics retrieveCryptoMetrics(String cryptoSymbol) {
        CryptoMetrics cryptoMetrics = cryptoMetricsMap.get(cryptoSymbol.toUpperCase());
        if (cryptoMetrics == null) {
            throw new CryptoNotFoundException("Requested crytocurrency is not present in our store");
        }
        return cryptoMetrics;
    }

    public List<CrytoNormalized> retrieveSortedCrytoBasedOnNormalizedPrices() {
        List<CrytoNormalized> normalizedList = new ArrayList<>();
        cryptoMetricsMap.forEach((symbol, metrics) -> {
            double normalizedValue = getNormalizedValue(metrics.getMaxPrice().doubleValue(), metrics.getMinPrice().doubleValue());
            normalizedList.add(new CrytoNormalized(symbol, normalizedValue));
        });

        return normalizedList.stream()
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());
    }


    public CrytoNormalized retrieveCryptoWithHighestNormalized(LocalDate requestedDate) {
        CrytoNormalized highestNormalizedCrypto = new CrytoNormalized();
        cryptoToMapOfDatesAndPricesList.forEach((symbol, dateToCryptoData) -> {
            List<LocalDateCrypto> localDateCryptos = dateToCryptoData.get(requestedDate);
            if (!CollectionUtils.isEmpty(localDateCryptos)) {
                handleNormalizedCrypto(highestNormalizedCrypto, symbol, localDateCryptos);
            }
        });


        if (highestNormalizedCrypto.getNormalizedValue() == null) {
            throw new IllegalArgumentException("Requested date is not recorded");
        }
        return highestNormalizedCrypto;
    }

    private void handleNormalizedCrypto(CrytoNormalized highestNormalizedCrypto, String symbol, List<LocalDateCrypto> localDateCryptos) {
        BigDecimal maxPricePerDay = localDateCryptos.stream().max(Comparator.naturalOrder()).get().getPrice();
        BigDecimal minPricePerDay = localDateCryptos.stream().min(Comparator.naturalOrder()).get().getPrice();
        double normalizedValue = getNormalizedValue(maxPricePerDay.doubleValue(), minPricePerDay.doubleValue());
        if (highestNormalizedCrypto.getNormalizedValue() == null || highestNormalizedCrypto.getNormalizedValue() < normalizedValue) {
            highestNormalizedCrypto.setCryptoName(symbol);
            highestNormalizedCrypto.setNormalizedValue(normalizedValue);
        }
    }


    private double getNormalizedValue(double maxPrice, double minPrice) {
        return (maxPrice - minPrice) / minPrice;
    }
}
