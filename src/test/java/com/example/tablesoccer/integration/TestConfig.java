package com.example.tablesoccer.integration;

import com.example.tablesoccer.service.LlmService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public LlmService mockLlmService() {
        return new MockLlmService();
    }
    
    /**
     * Mock implementation of LlmService for testing
     */
    public static class MockLlmService extends LlmService {
        
        public MockLlmService() {
            super(null);
        }
        
        @Override
        public String interpretMessage(String message) throws IOException {
            // Return a fixed response for testing
            return "{\"team1\":[\"Alpha\",\"Beta\"],\"team2\":[\"Gamma\",\"Delta\"],\"winner\":\"team1\"}";
        }
    }
}