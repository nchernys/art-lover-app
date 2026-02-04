package com.example.art_lover.exceptions;

public class ImageRecognitionException extends RuntimeException {
    public ImageRecognitionException(String message, Throwable cause) {
        super(message, cause);
    }
}