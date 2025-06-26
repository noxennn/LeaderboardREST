package com.yebyrkc.LeaderboardREST.controller;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.service.Leaderboard.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {
    @Autowired
    private  LeaderboardService leaderboardService;



    /**
     * Increment a player's score
     */
    @PostMapping("/{playerId}/increment")
    public double incrementScore(
            @PathVariable String playerId,
            @RequestParam double incrementBy
    ) {
        return leaderboardService.incrementScore(playerId, incrementBy);
    }

    /**
     * Get top N players
     */
    @GetMapping("/top")
    public List<LeaderboardEntry> getTopPlayers(@RequestParam int n) {
        return leaderboardService.getTopPlayers(n);
    }

    /**
     * Get a player's data
     */
    @GetMapping("/{playerId}")
    public LeaderboardEntry getPlayer(@PathVariable String playerId) {
        return leaderboardService.getPlayer(playerId);
    }

    /**
     * Get a player's current rank
     */
    @GetMapping("/{playerId}/rank")
    public long getRank(@PathVariable String playerId) {
        return leaderboardService.getPlayerRank(playerId);
    }
    /** Add new player to LeaderboardEntry
     *
     */
    @PostMapping("/player")
    public void addPlayer(
            @RequestParam String playerId,
            @RequestParam String username,
            @RequestParam int level,
            @RequestParam double score
    ) {
        leaderboardService.addPlayer(playerId, username, level, score);
    }


}
