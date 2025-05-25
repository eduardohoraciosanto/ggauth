package com.popoletos.ggauth.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

import static com.popoletos.ggauth.context.RequestAttributeNames.REQUESTER_ID;
import static com.popoletos.ggauth.context.RequestAttributeNames.REQUEST_ID;

/** Identifies a Request by
 *  <ul>
 *      <li>
 *          Grabbing or assigning X-Request-ID field.
 *      </li>
 *      <li>
 *          Determining who is the caller.
 *      </li>
 *  </ul>
 * <p>Values are saved inside the request attributes</p>
 * */
@Component
public class RequestIdentifierInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var requestId = getRequestId(request);
        // set response header to allow clients to know the req-id
        response.setHeader("X-Request-Id", requestId);
        // save request-id in the request attributes
        request.setAttribute(REQUEST_ID, requestId);
        // save the requester-id in the attributes
        request.setAttribute(REQUESTER_ID, getClientId(request));
        return true;
    }

    /**
     * Retrieves the {@code X-Request-ID} header from the HTTP request.
     * <p>
     * If the header is missing or blank, a new UUID is generated as the request ID.
     * </p>
     *
     * @param request the incoming HTTP servlet request
     * @return the existing request ID from the header, or a newly generated UUID if absent
     */
    private String getRequestId(HttpServletRequest request) {
        var requestId = request.getHeader("X-Request-ID");
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    /**
     * Extracts the client Identification from the request.
     * <p>
     * It first attempts to retrieve the IP from the {@code X-Forwarded-For} header,
     * then from the {@code X-Real-IP} header. If none are present or valid,
     * it falls back to {@link HttpServletRequest#getRemoteAddr()}.
     * </p>
     *
     * @param request the incoming HTTP servlet request
     * @return the resolved client IP address
     */
    private String getClientId(HttpServletRequest request) {
        String ip = extractFromHeader(request, "X-Forwarded-For");
        if (ip == null) {
            ip = extractFromHeader(request, "X-Real-IP");
        }
        return ip != null ? ip : request.getRemoteAddr();
    }

    /**
     * Attempts to extract a valid IP address from the specified header.
     * <p>
     * Some headers like {@code X-Forwarded-For} may contain a comma-separated list of IPs;
     * this method returns the first non-blank and non-"unknown" IP it finds.
     * </p>
     *
     * @param request the HTTP request
     * @param header  the name of the header to extract the IP from
     * @return the first valid IP address found, or {@code null} if none is present
     */
    private String extractFromHeader(HttpServletRequest request, String header) {
        String headerValue = request.getHeader(header);
        if (headerValue != null && !headerValue.isBlank()) {
            // X-Forwarded-For may contain multiple IPs
            String[] parts = headerValue.split(",");
            for (String part : parts) {
                String candidate = part.trim();
                if (!candidate.isEmpty() && !"unknown".equalsIgnoreCase(candidate)) {
                    return candidate;
                }
            }
        }
        return null;
    }
}
