package com.xm.cryptorecomandation.controller;

import java.time.LocalDate;
import java.util.List;
import com.xm.cryptorecomandation.model.CryptoMetrics;
import com.xm.cryptorecomandation.model.CrytoNormalized;
import com.xm.cryptorecomandation.service.CryptoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Tag(name = "Crypto recommendation API")
@RestController
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(description = "Invalid parameters provided",
                implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "NOT FOUND",
            content = @Content(schema = @Schema(description = "Requested crypto is not processed. We don't have any data to process"))),
        @ApiResponse(responseCode = "200", description = "Request completed successfully",
            content = @Content(schema = @Schema(implementation = CryptoMetrics.class)))
    })
    @Operation(summary = "Retrieves metrics values for a requested cryptocurrency",
        description = "Retrieves oldest/newest/min/max values for a requested cryptocurrency")

    @GetMapping(value = "/api/crypto/{cryptoSymbol}/metrics")
    public ResponseEntity<CryptoMetrics> retrieveCryptoData(@PathVariable
                                                            @Parameter(description = "Crypto currency symbol. Ex:BTC", required = true,
                                                                schema = @Schema(implementation = String.class), in = PATH)
                                                            String cryptoSymbol) {
        CryptoMetrics cryptoMetrics = cryptoService.retrieveCryptoMetrics(cryptoSymbol.toUpperCase());

        return ResponseEntity.ok(cryptoMetrics);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(description = "Invalid parameters provided",
                implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "NOT FOUND",
            content = @Content(schema = @Schema(description = "Requested crypto is not processed"))),
        @ApiResponse(responseCode = "200", description = "Request completed successfully",
            content = @Content(schema = @Schema(implementation = List.class)))
    })
    @Operation(summary = "Retrieves descending sorted list of all the processed crypto",
        description = "Retrives descending sorted list including crypto name and normalized value for entire data")
    @GetMapping(value = "/api/sorted/normalized/crypto")
    public ResponseEntity<List<CrytoNormalized>> retrieveCryptoData() {
        List<CrytoNormalized> normalizedSortedList = cryptoService.retrieveSortedCrytoBasedOnNormalizedPrices();

        return ResponseEntity.ok(normalizedSortedList);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(schema = @Schema(description = "Invalid parameters provided",
                implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "NOT FOUND",
            content = @Content(schema = @Schema(description = "Requested crypto is not processed"))),
        @ApiResponse(responseCode = "200", description = "Request completed successfully",
            content = @Content(schema = @Schema(implementation = List.class)))
    })
    @Operation(summary = "Crypto with the highest normalized range ",
        description = "Retrieves cryptocurrency with the highest normalized range for a specific day")
    @GetMapping(value = "/api/highest/normalized/crypto/{requestedDate}")
    public ResponseEntity<CrytoNormalized> retrieveCryptoWithHighestNormalized(@PathVariable
                                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                               @Parameter(description = "Local date to be processed. Ex:2020-01-01", required = true,
                                                                                   schema = @Schema(implementation = LocalDate.class), in = PATH)
                                                                               LocalDate requestedDate) {
        CrytoNormalized crytoNormalized = cryptoService.retrieveCryptoWithHighestNormalized(requestedDate);

        return ResponseEntity.ok(crytoNormalized);
    }
}
