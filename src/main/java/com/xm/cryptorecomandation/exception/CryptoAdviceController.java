package com.xm.cryptorecomandation.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CryptoAdviceController {

    @ExceptionHandler(value = {CryptoNotFoundException.class, IllegalArgumentException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessageDTO resourceNotFoundException(Exception ex) {
        ErrorMessageDTO message = new ErrorMessageDTO(HttpStatus.NOT_FOUND, LocalDateTime.now(), ex.getMessage());
        return message;
    }
}
