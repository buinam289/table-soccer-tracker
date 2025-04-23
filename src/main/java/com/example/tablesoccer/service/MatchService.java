package com.example.tablesoccer.service;

import com.example.tablesoccer.model.Debt;
import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.MatchRequest;
import com.example.tablesoccer.repository.DebtLedger;
import com.example.tablesoccer.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {
    private final DebtLedger debtLedger;
    private final DebtService debtService;
    private final MatchRepository matchRepository;

    public MatchService(DebtLedger debtLedger, DebtService debtService, MatchRepository matchRepository) {
        this.debtLedger = debtLedger;
        this.debtService = debtService;
        this.matchRepository = matchRepository;
    }

    public void recordMatch(MatchRequest matchRequest) throws IOException {
        // Create and save match record
        Match match = new Match(matchRequest);
        matchRepository.saveMatch(match);
        
        // Process debts
        List<Debt> currentDebts = new ArrayList<>(debtLedger.getAllDebts());
        List<Debt> newDebts = calculateDebts(matchRequest);
        
        currentDebts.addAll(newDebts);
        debtLedger.saveDebts(currentDebts);
    }

    public List<Match> getPlayerMatches(String playerName) throws IOException {
        return matchRepository.getMatchesByPlayer(playerName);
    }

    private List<Debt> calculateDebts(MatchRequest matchRequest) {
        List<Debt> debts = new ArrayList<>();
        List<String> winners = matchRequest.getWinner().equals("team1") ? 
            matchRequest.getTeam1() : matchRequest.getTeam2();
        List<String> losers = matchRequest.getWinner().equals("team1") ? 
            matchRequest.getTeam2() : matchRequest.getTeam1();

        for (int i = 0; i < winners.size(); i++) {
            debts.add(new Debt(losers.get(i), winners.get(i), 1));
        }

        return debts;
    }
}