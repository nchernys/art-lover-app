package com.example.art_lover.dto.common;

import java.time.Instant;

public class ApiErrorResponse {

    private final boolean success = false;
    private final String message;
    private final int status;
    private final Instant timestamp = Instant.now();

    public ApiErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
