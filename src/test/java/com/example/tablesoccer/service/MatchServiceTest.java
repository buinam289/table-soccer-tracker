package com.example.tablesoccer.service;

import com.example.tablesoccer.model.Debt;
import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.MatchRequest;
import com.example.tablesoccer.repository.DebtLedger;
import com.example.tablesoccer.repository.MatchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {
    @Mock
    private DebtLedger debtLedger;

    @Mock
    private DebtService debtService;
    
    @Mock
    private MatchRepository matchRepository;
    
    @Mock
    private LlmService llmService;
    
    @Captor
    private ArgumentCaptor<Match> matchCaptor;
    
    private MatchService matchService;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        matchService = new MatchService(debtLedger, debtService, matchRepository, llmService, objectMapper);
    }
    
    @Test
    void testRecordMatch() throws IOException {
        // Setup
        MatchRequest request = new MatchRequest();
        request.setTeam1(Arrays.asList("A", "B"));
        request.setTeam2(Arrays.asList("C", "D"));
        request.setWinner("team1");
        
        when(debtLedger.getAllDebts()).thenReturn(new ArrayList<>());
        
        // Execute
        matchService.recordMatch(request);
        
        // Verify match was saved
        verify(matchRepository).saveMatch(matchCaptor.capture());
        Match savedMatch = matchCaptor.getValue();
        
        // Assertions for saved match
        assertEquals(request.getTeam1(), savedMatch.getTeam1());
        assertEquals(request.getTeam2(), savedMatch.getTeam2());
        assertEquals(request.getWinner(), savedMatch.getWinner());
        assertNotNull(savedMatch.getId());
        assertNotNull(savedMatch.getTimestamp());
        
        // Verify debts were calculated and saved
        verify(debtLedger).saveDebts(anyList());
    }
    
    @Test
    void testRecordMatchFromMessage() throws IOException {
        // Setup
        String message = "A and B defeated C and D";
        String jsonResult = "{\"team1\":[\"A\",\"B\"],\"team2\":[\"C\",\"D\"],\"winner\":\"team1\"}";
        
        when(llmService.interpretMessage(message)).thenReturn(jsonResult);
        when(debtLedger.getAllDebts()).thenReturn(new ArrayList<>());
        
        // Execute
        Match match = matchService.recordMatchFromMessage(message);
        
        // Verify LLM service was called
        verify(llmService).interpretMessage(message);
        
        // Verify match was saved
        verify(matchRepository).saveMatch(any(Match.class));
        
        // Assertions for returned match
        assertEquals(Arrays.asList("A", "B"), match.getTeam1());
        assertEquals(Arrays.asList("C", "D"), match.getTeam2());
        assertEquals("team1", match.getWinner());
        assertNotNull(match.getId());
        assertNotNull(match.getTimestamp());
        
        // Verify debts were calculated and saved
        verify(debtLedger).saveDebts(anyList());
    }
    
    @Test
    void testGetPlayerMatches() throws IOException {
        // Setup
        String playerName = "A";
        List<Match> expectedMatches = Arrays.asList(
            createMatch(Arrays.asList("A", "B"), Arrays.asList("C", "D"), "team1"),
            createMatch(Arrays.asList("E", "F"), Arrays.asList("A", "G"), "team2")
        );
        
        when(matchRepository.getMatchesByPlayer(playerName)).thenReturn(expectedMatches);
        
        // Execute
        List<Match> actualMatches = matchService.getPlayerMatches(playerName);
        
        // Verify
        assertEquals(expectedMatches.size(), actualMatches.size());
        assertEquals(expectedMatches, actualMatches);
    }
    
    private Match createMatch(List<String> team1, List<String> team2, String winner) {
        MatchRequest request = new MatchRequest();
        request.setTeam1(team1);
        request.setTeam2(team2);
        request.setWinner(winner);
        return new Match(request);
    }
}