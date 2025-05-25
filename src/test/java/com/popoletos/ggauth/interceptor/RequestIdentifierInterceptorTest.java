package com.popoletos.ggauth.interceptor;

import com.popoletos.ggauth.context.RequestAttributeNames;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RequestIdentifierInterceptorTest {

    private RequestIdentifierInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new RequestIdentifierInterceptor();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void preHandle_withHeader_setsHeaderAndAttributes() {
        when(request.getHeader("X-Request-ID")).thenReturn("abc-123");
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verify(response).setHeader("X-Request-Id", "abc-123");
        verify(request).setAttribute(RequestAttributeNames.REQUEST_ID, "abc-123");
        verify(request).setAttribute(RequestAttributeNames.REQUESTER_ID, "10.0.0.1");
    }

    @Test
    void preHandle_withoutHeader_generatesRequestId() {
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.0.42");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);

        // Capture header value to check it's not null/blank
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq("X-Request-Id"), captor.capture());
        String generatedId = captor.getValue();
        assertNotNull(generatedId);
        assertFalse(generatedId.isBlank());

        verify(request).setAttribute(RequestAttributeNames.REQUEST_ID, generatedId);
        verify(request).setAttribute(RequestAttributeNames.REQUESTER_ID, "192.168.0.42");
    }
}
