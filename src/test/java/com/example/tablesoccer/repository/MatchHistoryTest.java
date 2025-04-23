package com.example.tablesoccer.repository;

import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.MatchRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchHistoryTest {
    
    private MatchHistory matchHistory;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Set up the test instance with temp directory
        System.setProperty("data.directory", tempDir.toString());
        matchHistory = new MatchHistory() {
            @Override
            protected String getMatchesFilePath() {
                return tempDir.resolve("matches.json").toString();
            }
        };
    }
    
    @Test
    void testSaveAndGetAllMatches() throws IOException {
        // Create test match
        MatchRequest request = new MatchRequest();
        request.setTeam1(Arrays.asList("A", "B"));
        request.setTeam2(Arrays.asList("C", "D"));
        request.setWinner("team1");
        
        Match match = new Match(request);
        
        // Save the match
        matchHistory.saveMatch(match);
        
        // Verify the match was saved correctly
        List<Match> savedMatches = matchHistory.getAllMatches();
        assertEquals(1, savedMatches.size());
        
        Match savedMatch = savedMatches.get(0);
        assertEquals(match.getId(), savedMatch.getId());
        assertEquals(match.getTeam1(), savedMatch.getTeam1());
        assertEquals(match.getTeam2(), savedMatch.getTeam2());
        assertEquals(match.getWinner(), savedMatch.getWinner());
        assertNotNull(savedMatch.getTimestamp());
    }
    
    @Test
    void testGetMatchesByPlayer() throws IOException {
        // Create test matches with player "A" in different teams
        MatchRequest request1 = new MatchRequest();
        request1.setTeam1(Arrays.asList("A", "B"));
        request1.setTeam2(Arrays.asList("C", "D"));
        request1.setWinner("team1");
        
        MatchRequest request2 = new MatchRequest();
        request2.setTeam1(Arrays.asList("E", "F"));
        request2.setTeam2(Arrays.asList("A", "G"));
        request2.setWinner("team2");
        
        MatchRequest request3 = new MatchRequest();
        request3.setTeam1(Arrays.asList("H", "I"));
        request3.setTeam2(Arrays.asList("J", "K"));
        request3.setWinner("team1");
        
        // Save all matches
        Match match1 = new Match(request1);
        Match match2 = new Match(request2);
        Match match3 = new Match(request3);
        
        matchHistory.saveMatch(match1);
        matchHistory.saveMatch(match2);
        matchHistory.saveMatch(match3);
        
        // Test retrieving matches for player "A"
        List<Match> playerMatches = matchHistory.getMatchesByPlayer("A");
        assertEquals(2, playerMatches.size());
        
        // Verify we have both matches involving player "A"
        boolean foundMatch1 = false;
        boolean foundMatch2 = false;
        
        for (Match match : playerMatches) {
            if (match.getId().equals(match1.getId())) {
                foundMatch1 = true;
            } else if (match.getId().equals(match2.getId())) {
                foundMatch2 = true;
            }
        }
        
        assertTrue(foundMatch1, "Match 1 should be found for player A");
        assertTrue(foundMatch2, "Match 2 should be found for player A");
    }
}