package com.example.tablesoccer.controller;

import com.example.tablesoccer.model.Match;
import com.example.tablesoccer.model.SlackRequest;
import com.example.tablesoccer.service.MatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/slack")
public class SlackController {

    private final MatchService matchService;

    public SlackController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/record-match")
    public ResponseEntity<Object> recordMatch(@RequestBody SlackRequest slackRequest) {
        try {
            // Validate the incoming request
            if (slackRequest.getMessage() == null || slackRequest.getMessage().getText() == null) {
                return createErrorResponse("No message text provided");
            }
            
            // Extract message text and process it
            String messageText = slackRequest.getMessage().getText();
            Match match = matchService.recordMatchFromMessage(messageText);
            
            // Create a friendly response for Slack
            Map<String, Object> response = createSuccessResponse(match);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return createErrorResponse("Error processing match: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Unexpected error: " + e.getMessage());
        }
    }
    
    private ResponseEntity<Object> createErrorResponse(String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("response_type", "ephemeral");
        response.put("text", "❌ " + errorMessage);
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> createSuccessResponse(Match match) {
        Map<String, Object> response = new HashMap<>();
        response.put("response_type", "in_channel");
        
        String team1Names = String.join(" and ", match.getTeam1());
        String team2Names = String.join(" and ", match.getTeam2());
        String winnerTeam = match.getWinner().equals("team1") ? team1Names : team2Names;
        String loserTeam = match.getWinner().equals("team1") ? team2Names : team1Names;
        
        String message = String.format("✅ Match recorded: *%s* defeated *%s*", winnerTeam, loserTeam);
        response.put("text", message);
        
        return response;
    }
}