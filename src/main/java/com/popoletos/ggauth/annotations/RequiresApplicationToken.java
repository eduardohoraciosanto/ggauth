package com.popoletos.ggauth.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** <p>Specifies if a controller handler method needs an application token</p>
 *  <p>To be used in conjunction with an Interceptor that process this annotation at runtime</p>
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequiresApplicationToken {
    boolean value() default true;
}
