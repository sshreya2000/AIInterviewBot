package com.company.aiinterview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient geminiRestClient(GeminiConfig geminiConfig) {
        return RestClient.builder()
                .baseUrl(geminiConfig.getBaseUrl())
                .build();
    }
}
