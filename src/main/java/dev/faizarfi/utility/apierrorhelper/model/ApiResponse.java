package dev.faizarfi.utility.apierrorhelper.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        int status,
        String message,
        T data,
        ApiErrorDetails error,
        Instant timestamp,
        String path
) {
    // Helper method to quickly construct a Success response
    public static <T> ApiResponse<T> success(int status, String message, T data, String path) {
        return new ApiResponse<>(true, status, message, data, null, Instant.now(), path);
    }

    // Helper method to quickly construct a Failure response
    public static <T> ApiResponse<T> failure(int status, String message, ApiErrorDetails error, String path) {
        return new ApiResponse<>(false, status, message, null, error, Instant.now(), path);
    }
}
