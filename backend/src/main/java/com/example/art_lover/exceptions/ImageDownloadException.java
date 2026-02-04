package com.example.art_lover.exceptions;

public class ImageDownloadException extends RuntimeException {

    public ImageDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageDownloadException(String message) {
        super(message);
    }
}
