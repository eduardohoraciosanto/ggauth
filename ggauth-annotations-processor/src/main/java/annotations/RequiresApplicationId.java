package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Handlers annotated with this will enforce header checking for Application ID.
 * */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface RequiresApplicationId {
    boolean value() default true;
}
