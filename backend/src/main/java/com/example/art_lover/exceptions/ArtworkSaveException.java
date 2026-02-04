package com.example.art_lover.exceptions;

public class ArtworkSaveException extends RuntimeException {
    public ArtworkSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtworkSaveException(String message) {
        super(message);
    }
}