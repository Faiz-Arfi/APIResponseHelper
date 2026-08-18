package dev.faizarfi.utility.apierrorhelper.model;

import java.util.List;

public record ApiErrorDetails(
        String code,
        String message,
        List<FieldError> fieldErrors,
        String stackTrace
) {
    public record FieldError(String field, String rejectedValue, String message){}
}
