package com.example.tablesoccer.controller;

import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.MatchRequest;
import com.example.tablesoccer.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchService matchService;

    @Test
    void testRecordMatch() throws Exception {
        MatchRequest request = new MatchRequest();
        request.setTeam1(Arrays.asList("A", "B"));
        request.setTeam2(Arrays.asList("C", "D"));
        request.setWinner("team1");

        mockMvc.perform(post("/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testRecordMatchError() throws Exception {
        MatchRequest request = new MatchRequest();
        request.setTeam1(Arrays.asList("A", "B"));
        request.setTeam2(Arrays.asList("C", "D"));
        request.setWinner("team1");

        doThrow(new IOException("Test exception")).when(matchService).recordMatch(any(MatchRequest.class));

        mockMvc.perform(post("/matches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetPlayerMatches() throws Exception {
        String playerName = "A";

        List<Match> matches = Arrays.asList(createMatch(), createMatch());
        when(matchService.getPlayerMatches(playerName)).thenReturn(matches);

        mockMvc.perform(get("/matches")
                .param("player", playerName))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(matches)));
    }

    @Test
    void testGetPlayerMatchesError() throws Exception {
        String playerName = "A";

        when(matchService.getPlayerMatches(playerName)).thenThrow(new IOException("Test exception"));

        mockMvc.perform(get("/matches")
                .param("player", playerName))
                .andExpect(status().isInternalServerError());
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
