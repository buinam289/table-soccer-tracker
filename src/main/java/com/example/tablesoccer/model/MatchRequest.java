package com.example.tablesoccer.model;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class MatchRequest {
    @NotEmpty(message = "Team 1 cannot be empty")
    private List<String> team1;
    
    @NotEmpty(message = "Team 2 cannot be empty")
    private List<String> team2;
    
    @NotNull(message = "Winner must be specified")
    private String winner;

    public List<String> getTeam1() {
        return team1;
    }

    public void setTeam1(List<String> team1) {
        this.team1 = team1;
    }

    public List<String> getTeam2() {
        return team2;
    }

    public void setTeam2(List<String> team2) {
        this.team2 = team2;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}