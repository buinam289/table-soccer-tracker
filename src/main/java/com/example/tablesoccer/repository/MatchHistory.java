package com.example.tablesoccer.repository;

import com.example.tablesoccer.model.Match;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MatchHistory implements MatchRepository {
    private static final String MATCHES_FILE = "data/matches.json";
    private final ObjectMapper objectMapper;

    public MatchHistory() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        
        // Create the data directory if it doesn't exist
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        // Create the matches file if it doesn't exist
        File matchesFile = new File(getMatchesFilePath());
        if (!matchesFile.exists()) {
            try {
                objectMapper.writeValue(matchesFile, new ArrayList<Match>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveMatch(Match match) throws IOException {
        List<Match> matches = getAllMatches();
        matches.add(match);
        objectMapper.writeValue(new File(getMatchesFilePath()), matches);
    }

    @Override
    public List<Match> getAllMatches() throws IOException {
        File matchesFile = new File(getMatchesFilePath());
        if (!matchesFile.exists()) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(matchesFile, new TypeReference<List<Match>>() {});
    }

    @Override
    public List<Match> getMatchesByPlayer(String playerName) throws IOException {
        List<Match> allMatches = getAllMatches();
        return allMatches.stream()
                .filter(match -> 
                    match.getTeam1().contains(playerName) || 
                    match.getTeam2().contains(playerName))
                .collect(Collectors.toList());
    }
    
    // Protected method for allowing tests to override the file path
    protected String getMatchesFilePath() {
        return MATCHES_FILE;
    }
}