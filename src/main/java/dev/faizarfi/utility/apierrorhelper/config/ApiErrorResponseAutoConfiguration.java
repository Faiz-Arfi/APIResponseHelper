package dev.faizarfi.utility.apierrorhelper.config;

import dev.faizarfi.utility.apierrorhelper.exception.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dev.faizarfi.utility.apierrorhelper.advice.GlobalResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ApiResponseProperties.class)
@ConditionalOnProperty(prefix = "api-response", name = "enable", havingValue = "true", matchIfMissing = true)
public class ApiErrorResponseAutoConfiguration {

    @Bean
    public GlobalResponseBodyAdvice globalResponseBodyAdvice(ObjectMapper objectMapper) {
        return new GlobalResponseBodyAdvice(objectMapper);
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(ApiResponseProperties apiResponseProperties) {
        return new GlobalExceptionHandler(apiResponseProperties);
    }
}
