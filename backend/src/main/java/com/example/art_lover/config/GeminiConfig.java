package com.example.art_lover.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@Configuration
public class GeminiConfig {

    @Bean(destroyMethod = "close")
    public Client geminiClient(@Value("${ai.apiKey}") String apiKey) {
        return Client.builder()
                     .apiKey(apiKey)
                     .build();
    }
}
