package com.xm.cryptorecomandation.service;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.xm.cryptorecomandation.model.CryptoData;
import com.xm.cryptorecomandation.model.CryptoMetrics;
import com.xm.cryptorecomandation.model.LocalDateCrypto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static java.lang.String.format;

@Service
@Slf4j
public class CsvProcessorService {

    @Value("${supported.crypto.currencies}")
    private List<String> supportedCrypto;

    public static final ConcurrentMap<String, List<CryptoData>> cryptoPricesMap = new ConcurrentHashMap<>();
    public static final ConcurrentMap<String, CryptoMetrics> cryptoMetricsMap = new ConcurrentHashMap<>();
    public static final ConcurrentMap<String, Map<LocalDate, List<LocalDateCrypto>>> cryptoToMapOfDatesAndPricesList = new ConcurrentHashMap<>();


    @PostConstruct
    public void readAndProcessCryptoCsvFiles() {
        supportedCrypto.forEach((crypto) -> {
            String fileToProcess = format("/Prices/%s_values.csv", crypto);
            log.info("Started processing file {}", fileToProcess);
            procesEveryFile(fileToProcess);
            log.info("Finished processing file {}", fileToProcess);
        });
    }

    private void procesEveryFile(String fileName) {
        InputStreamReader inputStreamReader = new InputStreamReader(
            CsvProcessorService.class.getResourceAsStream(fileName)
        );

        CSVReaderBuilder readerBuilder = new CSVReaderBuilder(inputStreamReader);
        // Skip the header line
        readerBuilder.withSkipLines(1);
        CSVParserBuilder csvParserBuilder = new CSVParserBuilder();
        csvParserBuilder.withSeparator(',');
        readerBuilder.withCSVParser(csvParserBuilder.build());
        List<CryptoData> cryptoDataList = new ArrayList<>();

        try (CSVReader reader = readerBuilder.build()) {

            String[] nextLine;
            String currency = null;
            BigDecimal maxPriceForCurrency = BigDecimal.ZERO;
            BigDecimal minPriceForCurrency = BigDecimal.valueOf(Double.MAX_VALUE);
            BigDecimal oldestPrice = null;
            BigDecimal newestPrice = null;
            Instant oldestValue = null;
            Instant newestValue = null;

            while ((nextLine = reader.readNext()) != null) {
                currency = String.valueOf(nextLine[1]);
                Instant time = Instant.ofEpochMilli(Long.valueOf(nextLine[0]));
                BigDecimal retrievedPrice = new BigDecimal(nextLine[2]);

                maxPriceForCurrency = computeMaxPrice(maxPriceForCurrency, retrievedPrice);
                minPriceForCurrency = computeMinPrice(minPriceForCurrency, retrievedPrice);

                if (oldestValue == null || time.isBefore(oldestValue)) {
                    oldestValue = time;
                    oldestPrice = retrievedPrice;
                }
                if (newestValue == null || time.isAfter(newestValue)) {
                    newestValue = time;
                    newestPrice = retrievedPrice;
                }

                cryptoDataList.add(new CryptoData(time, currency, retrievedPrice));
            }

            cryptoPricesMap.put(currency, cryptoDataList);
            populateCryptoMetrics(currency, maxPriceForCurrency, minPriceForCurrency, oldestPrice, newestPrice);
            computeCryptoToMapOfDatesAndPricesList(currency, cryptoDataList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(format("Could not read the csv file %s", fileName));
        }
    }

    private void computeCryptoToMapOfDatesAndPricesList(String symbol, List<CryptoData> cryptoDataList) {
        Map<LocalDate, List<LocalDateCrypto>> dateToCryptoListMap = cryptoDataList.stream()
            .map((cd) -> transformToCrytoDataDay(cd))
            .collect(Collectors.groupingBy(LocalDateCrypto::getLocalDate));

        cryptoToMapOfDatesAndPricesList.put(symbol, dateToCryptoListMap);
    }

    private LocalDateCrypto transformToCrytoDataDay(CryptoData cd) {
        LocalDate specificDate = LocalDateTime.ofInstant(cd.getTime(), ZoneOffset.UTC).toLocalDate();
        return new LocalDateCrypto(specificDate, cd.getSymbol(), cd.getPrice());
    }

    private void populateCryptoMetrics(String currency, BigDecimal maxPriceForCurrency, BigDecimal minPriceForCurrency, BigDecimal oldestPrice, BigDecimal newestPrice) {
        CryptoMetrics cryptoMetrics = CryptoMetrics.builder()
            .oldestPrice(oldestPrice)
            .newestPrice(newestPrice)
            .maxPrice(maxPriceForCurrency)
            .minPrice(minPriceForCurrency)
            .build();

        cryptoMetricsMap.put(currency, cryptoMetrics);
    }

    private BigDecimal computeMaxPrice(BigDecimal maxPrice, BigDecimal price) {
        if (maxPrice.equals(null) || price.compareTo(maxPrice) > 0) {
            return price;
        }
        return maxPrice;
    }

    private BigDecimal computeMinPrice(BigDecimal minPrice, BigDecimal price) {
        if (minPrice.equals(null) || price.compareTo(minPrice) < 0) {
            return price;
        }
        return minPrice;
    }

}
