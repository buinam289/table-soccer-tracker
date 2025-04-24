package com.example.tablesoccer.controller;

import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.SlackRequest;
import com.example.tablesoccer.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SlackController.class)
public class SlackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchService matchService;

    @Test
    void testRecordMatch() throws Exception {
        // Prepare test data
        SlackRequest slackRequest = new SlackRequest();
        SlackRequest.SlackMessage message = new SlackRequest.SlackMessage();
        message.setText("A and B defeated C and D");
        slackRequest.setMessage(message);
        
        // Set up the mock
        Match match = createMatch();
        when(matchService.recordMatchFromMessage(anyString())).thenReturn(match);
        
        // Perform the test
        mockMvc.perform(post("/slack/record-match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slackRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_type").value("in_channel"))
                .andExpect(jsonPath("$.text").exists());
    }
    
    @Test
    void testRecordMatchWithNoMessage() throws Exception {
        // Empty request
        SlackRequest slackRequest = new SlackRequest();
        
        mockMvc.perform(post("/slack/record-match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slackRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_type").value("ephemeral"))
                .andExpect(jsonPath("$.text").value("❌ No message text provided"));
    }

    private Match createMatch() {
        Match match = new Match();
        match.setId(UUID.randomUUID().toString());
        match.setTeam1(Arrays.asList("A", "B"));
        match.setTeam2(Arrays.asList("C", "D"));
        match.setWinner("team1");
        match.setTimestamp(LocalDateTime.now());
        return match;
    }
}