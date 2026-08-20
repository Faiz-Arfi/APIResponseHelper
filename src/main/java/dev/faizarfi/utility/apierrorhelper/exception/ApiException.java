package dev.faizarfi.utility.apierrorhelper.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String msg) {
        super(msg);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApiException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
