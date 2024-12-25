package com.example.aiexpensetracker.rest.configuration.OpenAIConfiguration;

import com.example.aiexpensetracker.api.impl.openai.OpenAIService;
import com.example.aiexpensetracker.core.api.aiservice.AIService;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfiguration {
    @Value("${openai.secret}")
    private String apiSecret;

    @Bean
    public AIService aiService() {
        return new OpenAIService(this.openAiService());
    }

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(this.apiSecret);
    }
}

