package net.sinzak.server.config.auth;

import org.springframework.security.core.Authentication;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthUser {

    boolean errorOnInvalidType() default false;

    String expression() default "";
}
