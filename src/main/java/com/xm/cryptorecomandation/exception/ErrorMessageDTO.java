package com.xm.cryptorecomandation.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessageDTO {
    private HttpStatus statusCode;
    private LocalDateTime timestamp;
    private String message;
}
