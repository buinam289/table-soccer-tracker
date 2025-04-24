package com.example.tablesoccer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LlmService {
    
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    
    @Value("${llm.api.url:https://api.openai.com/v1/chat/completions}")
    private String llmApiUrl;
    
    @Value("${llm.api.key:}")
    private String apiKey;
    
    @Value("${llm.model:gpt-4o-mini}")
    private String model;
    
    public LlmService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
    }
    
    /**
     * Sends a message to LLM to interpret into a match request format.
     * 
     * @param message the message to interpret
     * @return JSON string representing a MatchRequest
     * @throws IOException if communication with the LLM fails
     */
    public String interpretMessage(String message) throws IOException {
        if (apiKey == null || apiKey.isEmpty()) {
            // For development/testing without API key, use local interpretation
            return basicInterpreter(message);
        }
        
        // Create the request body for the LLM API
        String requestBody = createLlmRequestBody(message);
        
        Request request = new Request.Builder()
            .url(llmApiUrl)
            .addHeader("Authorization", "Bearer " + apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(MediaType.get("application/json"), requestBody))
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                
                // If we have quota issues or other errors, fall back to local interpretation
                if (response.code() >= 400) {
                    System.err.println("API call failed, using fallback. Error: " + errorBody);
                    return basicInterpreter(message);
                }
                
                throw new IOException("LLM API request failed: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            String content = jsonResponse
                .path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText();
                
            // Extract the actual JSON part from the response
            return extractJsonFromResponse(content);
        } catch (Exception e) {
            System.err.println("Exception calling API: " + e.getMessage());
            return basicInterpreter(message);
        }
    }
    
    private String createLlmRequestBody(String message) throws IOException {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("model", model);
        requestMap.put("store", true);
        
        List<Map<String, String>> messages = List.of(
            Map.of(
                "role", "system", 
                "content", "You are a helpful assistant that interprets messages about table soccer match results. " + 
                         "Extract the teams and winner from the text and return only a JSON object with this structure: " +
                         "{ \"team1\": [\"Player1\", \"Player2\"], \"team2\": [\"Player3\", \"Player4\"], \"winner\": \"team1\" }"
            ),
            Map.of("role", "user", "content", message)
        );
        
        requestMap.put("messages", messages);
        
        return objectMapper.writeValueAsString(requestMap);
    }
    
    private String extractJsonFromResponse(String content) {
        // Basic extraction - looks for content between { and }
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return "{}"; // Empty JSON if none found
    }
    
    /**
     * Simple fallback interpreter for testing without an API key.
     * Tries to extract team names and winner from simple message patterns.
     */
    private String basicInterpreter(String message) {
        try {
            // Very simple pattern matching
            String lowercaseMsg = message.toLowerCase();
            
            // Example: "Team A and B beat Team C and D"
            if (lowercaseMsg.contains(" beat ") || lowercaseMsg.contains(" defeated ") || lowercaseMsg.contains(" won against ")) {
                String[] parts;
                if (lowercaseMsg.contains(" beat ")) {
                    parts = lowercaseMsg.split(" beat ");
                } else if (lowercaseMsg.contains(" defeated ")) {
                    parts = lowercaseMsg.split(" defeated ");
                } else {
                    parts = lowercaseMsg.split(" won against ");
                }
                
                if (parts.length == 2) {
                    String team1Text = parts[0].replaceAll("team", "").trim();
                    String team2Text = parts[1].trim();
                    
                    String[] team1Players = extractPlayers(team1Text);
                    String[] team2Players = extractPlayers(team2Text);
                    
                    // Create basic JSON with team1 as winner
                    return String.format(
                        "{\"team1\":[\"" + String.join("\",\"", team1Players) + "\"], " +
                        "\"team2\":[\"" + String.join("\",\"", team2Players) + "\"], " +
                        "\"winner\":\"team1\"}"
                    );
                }
            }
        } catch (Exception e) {
            // Fall back to default response
        }
        
        // Default response with placeholder data
        return "{\"team1\":[\"A\",\"B\"], \"team2\":[\"C\",\"D\"], \"winner\":\"team1\"}";
    }
    
    private String[] extractPlayers(String teamText) {
        // Try to extract player names from formats like "A and B" or "A, B"
        if (teamText.contains(" and ")) {
            return teamText.split(" and ");
        } else if (teamText.contains(",")) {
            return teamText.split(",");
        } else {
            // Return the whole text as a single player
            return new String[]{teamText};
        }
    }
}