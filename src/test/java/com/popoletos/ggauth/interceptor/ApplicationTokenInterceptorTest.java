package com.popoletos.ggauth.interceptor;

import com.popoletos.ggauth.annotations.RequiresApplicationToken;
import com.popoletos.ggauth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ApplicationTokenInterceptorTest {
    private static final String TEST_TOKEN = "token";
    private static final String TEST_APP_ID = "appId";

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private ApplicationTokenInterceptor interceptor;

    static class AnnotatedController {
        @RequiresApplicationToken
        public void secureMethod() {}
    }

    static class PlainController {
        public void openMethod() {}
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        interceptor = new ApplicationTokenInterceptor(tokenService);
    }

    @Test
    void preHandle_WithAnnotationAndHeader_ShouldReturnTrue() throws Exception {
        Method method = AnnotatedController.class.getMethod("secureMethod");
        HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + TEST_TOKEN);
        when(tokenService.validateToken(TEST_TOKEN)).thenReturn(true);
        when(tokenService.getSubject(TEST_TOKEN)).thenReturn(TEST_APP_ID);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        verify(request, times(1)).getHeader("Authorization");
        verify(tokenService, times(1)).validateToken(TEST_TOKEN);
        verify(tokenService, times(1)).getSubject(TEST_TOKEN);
    }

    @Test
    void preHandle_WithAnnotationAndMissingHeader_ShouldReturnFalse() throws Exception {
        Method method = AnnotatedController.class.getMethod("secureMethod");
        HandlerMethod handlerMethod = new HandlerMethod(new AnnotatedController(), method);

        when(request.getHeader("Authorization")).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertFalse(result);
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void preHandle_WithoutAnnotation_ShouldReturnTrue() throws Exception {
        Method method = PlainController.class.getMethod("openMethod");
        HandlerMethod handlerMethod = new HandlerMethod(new PlainController(), method);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        verify(response, never()).sendError(anyInt());
    }
}