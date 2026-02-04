package com.example.art_lover.exceptions;

public class ArtworkNotFoundException extends RuntimeException {
    public ArtworkNotFoundException(String id) {
        super("Artwork not found: " + id);
    }
}