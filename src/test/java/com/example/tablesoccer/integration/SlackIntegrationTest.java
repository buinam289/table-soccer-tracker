package com.example.tablesoccer.integration;

import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.SlackRequest;
import com.example.tablesoccer.repository.MatchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "llm.api.key=placeholder",
    "llm.model=gpt-4o-mini"
})
public class SlackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Test
    void testRecordMatchFromSlackMessage() throws Exception {
        // Count existing matches before test
        int initialMatchCount = matchRepository.getAllMatches().size();
        
        // Create a slack request
        SlackRequest slackRequest = new SlackRequest();
        SlackRequest.SlackMessage message = new SlackRequest.SlackMessage();
        message.setText("Team Alpha and Beta beat Team Gamma and Delta 10-5");
        slackRequest.setMessage(message);
        
        SlackRequest.SlackUser user = new SlackRequest.SlackUser();
        user.setId("U123456");
        slackRequest.setUser(user);
        
        SlackRequest.SlackChannel channel = new SlackRequest.SlackChannel();
        channel.setId("C123456");
        slackRequest.setChannel(channel);
        
        slackRequest.setType("message_action");
        slackRequest.setCallbackId("record_match");
        slackRequest.setTriggerId("trigger123");
        
        // Send the request
        mockMvc.perform(post("/slack/record-match")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slackRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_type").value("in_channel"))
                .andExpect(jsonPath("$.text").exists());
        
        // Verify a new match was created
        List<Match> matches = matchRepository.getAllMatches();
        assertEquals(initialMatchCount + 1, matches.size());
        
        // Get the latest match
        Match latestMatch = matches.get(matches.size() - 1);
        
        // Check if it contains expected data (this is simplified as the exact response depends on LLM)
        assertNotNull(latestMatch.getId());
        assertNotNull(latestMatch.getTimestamp());
        assertFalse(latestMatch.getTeam1().isEmpty());
        assertFalse(latestMatch.getTeam2().isEmpty());
        assertNotNull(latestMatch.getWinner());
    }
}
