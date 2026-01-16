package com.example.art_lover.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate rt = new RestTemplate();

        rt.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(
                    "User-Agent",
                    "ArtLoverApp/1.0 (contact: dev@example.com)");
            return execution.execute(request, body);
        });

        return rt;
    }
}