package com.example.tablesoccer.controller;

import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.MatchRequest;
import com.example.tablesoccer.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@Validated
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<Void> recordMatch(@Valid @RequestBody MatchRequest matchRequest) {
        try {
            matchService.recordMatch(matchRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Match>> getPlayerMatches(@RequestParam String player) {
        try {
            return ResponseEntity.ok(matchService.getPlayerMatches(player));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}