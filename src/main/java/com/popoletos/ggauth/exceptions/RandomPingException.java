package com.popoletos.ggauth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "Random Ping Exception")
public class RandomPingException extends RuntimeException {
    public RandomPingException(String errorMessage) {
        super(errorMessage);
    }
}
