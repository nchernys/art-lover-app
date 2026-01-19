package com.example.art_lover.dto.r2;

public class R2ImageUploadResponse {

    private final String url;
    private final String key;

    public R2ImageUploadResponse(String url, String key) {
        this.url = url;
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public String getKey() {
        return key;
    }
}
