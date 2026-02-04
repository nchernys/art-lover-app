package com.example.art_lover.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.art_lover.dto.common.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- 400 BAD REQUEST ----------

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            IllegalArgumentException ex) {

        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ---------- 401 UNAUTHORIZED ----------

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // ---------- 403 FORBIDDEN ----------

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleForbidden(
            UnauthorizedAccessException ex) {

        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ---------- 404 NOT FOUND ----------

    @ExceptionHandler(ArtworkNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ArtworkNotFoundException ex) {

        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ---------- 409 CONFLICT ----------

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            EmailAlreadyExistsException ex) {

        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ---------- 500 INTERNAL SERVER ERROR ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex) {

        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again.");
    }

    // ---------- IMAGE RECOGNITION ERROR ----------

    @ExceptionHandler(ImageRecognitionException.class)
    public ResponseEntity<ApiErrorResponse> handleImageRecognition(
            ImageRecognitionException ex) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
    }

    // ---------- IMAGE RECOGNITION ERROR ----------

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ApiErrorResponse> handleImageProcessing(
            ImageProcessingException ex) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
    }

    // ---------- SAVE ARTWORK ERROR ----------

    @ExceptionHandler(ArtworkSaveException.class)
    public ResponseEntity<ApiErrorResponse> handleArtworkSave(
            ArtworkSaveException ex) {

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage());
    }

    // ---------- helper ----------

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String message) {

        return ResponseEntity
                .status(status)
                .body(new ApiErrorResponse(message, status.value()));
    }
}
