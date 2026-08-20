package dev.faizarfi.utility.apierrorhelper.exception;

import dev.faizarfi.utility.apierrorhelper.config.ApiResponseProperties;
import dev.faizarfi.utility.apierrorhelper.model.ApiErrorDetails;
import dev.faizarfi.utility.apierrorhelper.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ApiResponseProperties apiResponseProperties;

    public GlobalExceptionHandler(ApiResponseProperties apiResponseProperties) {
        this.apiResponseProperties = apiResponseProperties;
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Object>> handleApiException(ApiException ex, HttpServletRequest request) {
        HttpStatus status = ex.getStatus();

        ApiErrorDetails errorDetails = new ApiErrorDetails(
                status.name(),
                ex.getMessage(),
                null,
                getStackTraceIfEnabled(ex)
        );

        ApiResponse<Object> response = ApiResponse.failure(
                status.value(),
                ex.getMessage(),
                errorDetails,
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex,  HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<ApiErrorDetails.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ApiErrorDetails.FieldError(
                        error.getField(),
                        error.getRejectedValue() != null ? error.getRejectedValue().toString(): "null",
                        error.getDefaultMessage()
                        )
                ).toList();

        ApiErrorDetails errorDetails = new ApiErrorDetails(
                "VALIDATION_FAILED",
                "Input validation failed for request payload",
                fieldErrors,
                getStackTraceIfEnabled(ex)
        );

        ApiResponse<Object> response = ApiResponse.failure(
                status.value(),
                "Validation Error",
                errorDetails,
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiErrorDetails errorDetails = new ApiErrorDetails(
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                null,
                getStackTraceIfEnabled(ex)
        );

        ApiResponse<Object> response = ApiResponse.failure(
                status.value(),
                "Interval Server Error",
                errorDetails,
                request.getRequestURI()
        );

        return new ResponseEntity<>(response, status);
    }

    private String getStackTraceIfEnabled(Exception ex) {
        if (!apiResponseProperties.isIncludeStacktrace()) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        ex.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
