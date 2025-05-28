package com.popoletos.ggauth.aop;

import com.popoletos.ggauth.annotations.ToMDC;
import com.popoletos.ggauth.mdc.MdcKeys;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Aspect responsible for injecting method parameter values into the
 * {@link org.slf4j.MDC} (Mapped Diagnostic Context) based on the presence of the
 * {@link ToMDC} annotation on method parameters.
 * <p>
 * This aspect runs <b>before</b> the execution of any method containing at least one
 * parameter annotated with {@code @ToMDC}, and inserts the parameter’s value into the MDC
 * under the corresponding {@link MdcKeys MdcKey} name.
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * public void process(@ToMDC(MdcKey.REQUEST_ID) String requestId) {
 *     log.info("Processing...");
 * }
 * }</pre>
 *
 * <p>This will automatically result in {@code MDC.put("REQUEST_ID", requestId)} before
 * the method body executes.</p>
 *
 * @see ToMDC
 * @see MdcKeys
 * @see org.slf4j.MDC
 */
@Aspect
@Component
public class MdcInjectorAspect {

    @Before("execution(* *(.., @com.popoletos.ggauth.annotations.ToMDC (*), ..))")
    public void injectToMDC(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof ToMDC) {
                    ToMDC toMDC = (ToMDC) annotation;
                    String key = toMDC.value().name();
                    Object value = args[i];
                    if (value != null) {
                        MDC.put(key, value.toString());
                    }
                }
            }
        }
    }
}
