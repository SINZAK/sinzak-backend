package net.sinzak.server.config.auth;


import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolverSupport;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;


public class CustomAuthenticationPrincipalArgumentResolver extends HandlerMethodArgumentResolverSupport {

    protected CustomAuthenticationPrincipalArgumentResolver(ReactiveAdapterRegistry adapterRegistry) {
        super(adapterRegistry);
    }

    private ExpressionParser parser = new SpelExpressionParser();

    private BeanResolver beanResolver;

    public void setBeanResolver(BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return findMethodAnnotation(AuthUser.class, parameter) != null;
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext,
                                        ServerWebExchange exchange) {
        ReactiveAdapter adapter = getAdapterRegistry().getAdapter(parameter.getParameterType());
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .flatMap((authentication) -> {
                    Mono<Object> principal = Mono
                            .justOrEmpty(resolvePrincipal(parameter, authentication.getPrincipal()));
                    return (adapter != null) ? Mono.just(adapter.fromPublisher(principal)) : principal;
                });
    }

    private Object resolvePrincipal(MethodParameter parameter, Object principal) {
        AuthUser annotation = findMethodAnnotation(AuthUser.class, parameter);
        String expressionToParse = annotation.expression();
        if (StringUtils.hasLength(expressionToParse)) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setRootObject(principal);
            context.setVariable("this", principal);
            context.setBeanResolver(this.beanResolver);
            Expression expression = this.parser.parseExpression(expressionToParse);
            principal = expression.getValue(context);
        }

        if (isInvalidType(parameter, principal)) {
            if (annotation.errorOnInvalidType()) {
                throw new ClassCastException(principal + " is not assignable to " + parameter.getParameterType());
            }
            return null;
        }
        return principal;
    }

    private boolean isInvalidType(MethodParameter parameter, Object principal) {
        if (principal == null) {
            return false;
        }
        Class<?> typeToCheck = parameter.getParameterType();
        boolean isParameterPublisher = Publisher.class.isAssignableFrom(parameter.getParameterType());
        if (isParameterPublisher) {
            ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
            Class<?> genericType = resolvableType.resolveGeneric(0);
            if (genericType == null) {
                return false;
            }
            typeToCheck = genericType;
        }
        return !ClassUtils.isAssignable(typeToCheck, principal.getClass());
    }


    private <T extends Annotation> T findMethodAnnotation(Class<T> annotationClass, MethodParameter parameter) {
        T annotation = parameter.getParameterAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
        for (Annotation toSearch : annotationsToSearch) {
            annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }
}
