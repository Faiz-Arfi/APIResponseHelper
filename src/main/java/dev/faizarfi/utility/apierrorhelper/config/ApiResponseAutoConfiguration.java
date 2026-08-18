package dev.faizarfi.utility.apierrorhelper.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import dev.faizarfi.utility.apierrorhelper.advice.GlobalResponseBodyAdvice;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(ApiResponseProperties.class)
@ConditionalOnProperty(prefix = "api-response", name = "enable", havingValue = "true", matchIfMissing = true)
public class ApiResponseAutoConfiguration {

    @Bean
    public GlobalResponseBodyAdvice globalResponseBodyAdvice() {
        return new GlobalResponseBodyAdvice();
    }
}
