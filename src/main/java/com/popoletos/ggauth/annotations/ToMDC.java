package com.popoletos.ggauth.annotations;

import com.popoletos.ggauth.mdc.MdcKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark method parameters whose values should be injected into the
 * {@link org.slf4j.MDC} (Mapped Diagnostic Context) under the specified {@link MdcKeys}.
 * <p>
 * This annotation is intended to be used in combination with a custom AOP aspect that
 * scans method parameters and puts their values into the MDC for contextual logging.
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * public void handleRequest(@ToMDC(MdcKey.REQUEST_ID) String requestId,
 *                           @ToMDC(MdcKey.PLAYER:ID) String playerId) {
 *     log.info("Handling request");
 * }
 * }</pre>
 *
 * <p>This would result in the following MDC entries:</p>
 * <ul>
 *   <li>{@code requestId=...}</li>
 *   <li>{@code playerId=...}</li>
 * </ul>
 *
 * @see org.slf4j.MDC
 * @see com.popoletos.ggauth.aop.MdcInjectorAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ToMDC {
    MdcKeys value();
}
