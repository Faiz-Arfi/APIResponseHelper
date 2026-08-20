package dev.faizarfi.utility.apierrorhelper.advice;

import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import dev.faizarfi.utility.apierrorhelper.annotation.SkipResponseWrapping;
import dev.faizarfi.utility.apierrorhelper.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public GlobalResponseBodyAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        boolean hasMethodAnnotation = returnType.hasMethodAnnotation(SkipResponseWrapping.class);
        boolean hasClassAnnotation = returnType.getContainingClass().isAnnotationPresent(SkipResponseWrapping.class);

        return !(hasMethodAnnotation || hasClassAnnotation);
    }

    @Override
    public @Nullable Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType,
            MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {
            
            if(body instanceof ApiResponse<?>) {
                return body;
            }

            String path = request.getURI().getPath();
            // Default status
            int status = HttpStatus.OK.value();
            //Inspect Servlet status if available
            if(request instanceof ServletServerHttpRequest servletRequest) {
                HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
                Object statusAttr = httpServletRequest.getAttribute("jakarta.servlet.error.status_code");
                if(statusAttr instanceof Integer statusCode) {
                    status = statusCode;
                }
            }

            ApiResponse<Object> apiResponse = ApiResponse.success(status, "Operation completed successfully", body, path);

        // Crucial fix: If the converter is StringHttpMessageConverter, return a JSON string
        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(apiResponse);
            } catch (Exception e) {
                throw new RuntimeException("Error serializing ApiResponse to JSON String", e);
            }
        }

        return apiResponse;
    }

}
