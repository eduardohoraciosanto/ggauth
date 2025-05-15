package com.popoletos.ggauth.controller.errors;

import com.popoletos.ggauth.exceptions.RandomPingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class ErrorsHandlerTest {
    @Autowired
    private ErrorsHandler errorsHandler;

    @Test
    void testGenericException() {
        var exceptionMessage = "exception message";
        var res = errorsHandler.handleUnhandledExceptions(new IllegalArgumentException(exceptionMessage));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
        assertEquals("unknown_error", Objects.requireNonNull(res.getBody()).errorCode());
        assertEquals(exceptionMessage, Objects.requireNonNull(res.getBody()).errorMessage());
    }

    @Test
    void testExceptionWithResponseStatus() {
        var exceptionMessage = "exception message";
        var res = errorsHandler.handleUnhandledExceptions(new RandomPingException(exceptionMessage));

        assertEquals(HttpStatus.I_AM_A_TEAPOT, res.getStatusCode());
        assertEquals(
                "Random Ping Exception", Objects.requireNonNull(res.getBody()).errorMessage());
        assertEquals("418 I_AM_A_TEAPOT", Objects.requireNonNull(res.getBody()).errorCode());
    }
}
