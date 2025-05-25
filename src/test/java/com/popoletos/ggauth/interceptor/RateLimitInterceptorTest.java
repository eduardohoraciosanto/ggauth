package com.popoletos.ggauth.interceptor;

import com.popoletos.ggauth.ratelimit.RateLimit;
import com.popoletos.ggauth.ratelimit.RateLimitService;
import com.popoletos.ggauth.ratelimit.RateLimitTiers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RateLimitInterceptorTest {

    private RateLimitInterceptor interceptor;
    private RateLimitService rateLimitService;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setup() {
        rateLimitService = mock(RateLimitService.class);
        interceptor = new RateLimitInterceptor(rateLimitService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    static class MockController {
        @RateLimit(tier = "APPLICATION", operationCost = 2)
        public void limitedMethod() {}

        public void openMethod() {}
    }

    @Test
    void shouldAllowWhenRateLimitAnnotationPresentAndTokenConsumed() throws Exception {
        Method method = MockController.class.getMethod("limitedMethod");
        HandlerMethod handler = new HandlerMethod(new MockController(), method);

        when(request.getAttribute(any())).thenReturn("app123");
        when(rateLimitService.tryConsumeToken(RateLimitTiers.APPLICATION, "app123", 2))
                .thenReturn(true);

        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
    }

    @Test
    void shouldRejectWhenRateLimitAnnotationPresentAndTokenDenied() throws Exception {
        Method method = MockController.class.getMethod("limitedMethod");
        HandlerMethod handler = new HandlerMethod(new MockController(), method);

        when(request.getAttribute(any())).thenReturn("app123");
        when(rateLimitService.tryConsumeToken(RateLimitTiers.APPLICATION, "app123", 2))
                .thenReturn(false);

        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void shouldAllowWhenNoAnnotationAndGlobalTokenConsumed() throws Exception {
        Method method = MockController.class.getMethod("openMethod");
        HandlerMethod handler = new HandlerMethod(new MockController(), method);

        when(request.getAttribute(any())).thenReturn("anon");
        when(rateLimitService.tryConsumeToken(RateLimitTiers.GLOBAL, "anon", 1)).thenReturn(true);

        boolean result = interceptor.preHandle(request, response, handler);
        assertTrue(result);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void shouldRejectWhenNoAnnotationAndGlobalTokenDenied() throws Exception {
        Method method = MockController.class.getMethod("openMethod");
        HandlerMethod handler = new HandlerMethod(new MockController(), method);

        when(request.getAttribute(any())).thenReturn("anon");
        when(rateLimitService.tryConsumeToken(RateLimitTiers.GLOBAL, "anon", 1)).thenReturn(false);

        boolean result = interceptor.preHandle(request, response, handler);
        assertFalse(result);
        verify(response).setStatus(429);
    }
}