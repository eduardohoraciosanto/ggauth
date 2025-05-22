package com.popoletos.ggauth.interceptor;

import annotations.RequiresApplicationId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ApplicationIdInterceptorTest {
    private static final String TEST_APP_ID = "appId";

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ApplicationIdInterceptor interceptor;

    static class ValidController {
        @RequiresApplicationId
        public void validMethod(@RequestHeader("application-id") String applicationId) {}
    }

    static class InvalidController {
        public void invalidMethod() {}
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interceptor = new ApplicationIdInterceptor();
    }

    @Test
    void preHandle_WithAnnotationAndHeader_ShouldReturnTrue() throws Exception {
        Method method = ValidController.class.getMethod("validMethod", String.class);
        HandlerMethod handlerMethod = new HandlerMethod(new ValidController(), method);

        when(request.getHeader("Application-id")).thenReturn(TEST_APP_ID);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        verify(request, times(1)).getHeader("Application-id");
    }

    @Test
    void preHandle_WithAnnotationButNoHeader_ShouldReturnTrue() throws Exception {
        Method method = ValidController.class.getMethod("validMethod", String.class);
        HandlerMethod handlerMethod = new HandlerMethod(new ValidController(), method);

        when(request.getHeader("Application-id")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertFalse(result);
        verify(request, times(1)).getHeader("Application-id");
    }

    @Test
    void preHandle_WithoutAnnotation_ShouldReturnTrue() throws Exception {
        Method method = InvalidController.class.getMethod("invalidMethod");
        HandlerMethod handlerMethod = new HandlerMethod(new InvalidController(), method);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        verifyNoInteractions(request);
    }
}
