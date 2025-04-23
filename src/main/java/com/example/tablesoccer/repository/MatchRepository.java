package com.example.tablesoccer.repository;

import com.example.tablesoccer.model.Match;
import java.io.IOException;
import java.util.List;

public interface MatchRepository {
    void saveMatch(Match match) throws IOException;
    List<Match> getAllMatches() throws IOException;
    List<Match> getMatchesByPlayer(String playerName) throws IOException;
}