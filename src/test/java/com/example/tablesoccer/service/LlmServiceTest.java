package com.example.tablesoccer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class LlmServiceTest {

    private LlmService llmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper();
        llmService = new LlmService(objectMapper);
    }

    @Test
    void testInterpretMessage_BasicMessage() throws IOException {
        // Test with a simple message
        String message = "A and B beat C and D";
        String result = llmService.interpretMessage(message);
        
        // Verify result contains expected JSON structure
        assertTrue(result.contains("\"team1\""));
        assertTrue(result.contains("\"team2\""));
        assertTrue(result.contains("\"winner\""));
    }

    @Test
    void testInterpretMessage_ComplexMessage() throws IOException {
        // Test with a more complex message
        String message = "Yesterday's game: Team Alice and Bob defeated Team Charlie and Dave 5-3";
        String result = llmService.interpretMessage(message);
        
        // Verify result contains expected JSON structure
        assertTrue(result.contains("\"team1\""));
        assertTrue(result.contains("\"team2\""));
        assertTrue(result.contains("\"winner\""));
    }
}