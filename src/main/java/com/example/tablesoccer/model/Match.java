package com.example.tablesoccer.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Match {
    private String id;
    private List<String> team1;
    private List<String> team2;
    private String winner;
    private LocalDateTime timestamp;

    // Default constructor for Jackson
    public Match() {
    }

    public Match(MatchRequest request) {
        this.id = UUID.randomUUID().toString();
        this.team1 = request.getTeam1();
        this.team2 = request.getTeam2();
        this.winner = request.getWinner();
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}