package com.yebyrkc.LeaderboardREST.controller;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.service.Leaderboard.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardController.class);

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
        logger.debug("Incrementing score for playerId={} by {}", playerId, incrementBy);
        return leaderboardService.incrementScore(playerId, incrementBy);
    }

    /**
     * Get top N players
     */
    @GetMapping("/top")
    public List<LeaderboardEntry> getTopPlayers(@RequestParam int n) {
        logger.debug("Fetching top {} players", n);
        return leaderboardService.getTopPlayers(n);
    }

    /**
     * Get a player's data
     */
    @GetMapping("/{playerId}")
    public LeaderboardEntry getPlayer(@PathVariable String playerId) {
        logger.debug("Fetching player with id={}", playerId);
        return leaderboardService.getPlayer(playerId);
    }

    /**
     * Get a player's current rank
     */
    @GetMapping("/{playerId}/rank")
    public long getRank(@PathVariable String playerId) {
        logger.debug("Fetching rank for playerId={}", playerId);
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
        logger.debug("Adding player via controller with playerId={}", playerId);
        leaderboardService.addPlayerEntry(playerId, username, level, score);
    }
    @DeleteMapping("/player")
    public  ResponseEntity<String> deletePlayer(@RequestParam String playerId){
        logger.debug("Deleting player with id={}", playerId);
        leaderboardService.deletePlayerEntry(playerId);
        return ResponseEntity.ok("Succesfully deleted player with playerId: "+playerId);
    }

    @DeleteMapping("/players")
    public ResponseEntity<String> deleteAllPlayers(){
        logger.debug("Deleting all players through controller");
        leaderboardService.deleteAllPlayerEntries();
        return ResponseEntity.ok("Succesfully deleted all players");
    }

}
