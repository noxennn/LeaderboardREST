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
        logger.debug("LeaderboardController.incrementScore - playerId={}, incrementBy={}", playerId, incrementBy);
        return leaderboardService.incrementScore(playerId, incrementBy);
    }

    /**
     * Get top N players
     */
    @GetMapping("/top")
    public List<LeaderboardEntry> getTopPlayers(@RequestParam int n) {
        logger.debug("LeaderboardController.getTopPlayers - count={}", n);
        return leaderboardService.getTopPlayers(n);
    }

    /**
     * Get a player's data
     */
    @GetMapping("/{playerId}")
    public LeaderboardEntry getPlayer(@PathVariable String playerId) {
        logger.debug("LeaderboardController.getPlayer - playerId={}", playerId);
        return leaderboardService.getPlayer(playerId);
    }

    /**
     * Get a player's current rank
     */
    @GetMapping("/{playerId}/rank")
    public long getRank(@PathVariable String playerId) {
        logger.debug("LeaderboardController.getRank - playerId={}", playerId);
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
        logger.debug("LeaderboardController.addPlayer - playerId={}", playerId);
        leaderboardService.addPlayer(playerId, username, level, score);
    }
    @DeleteMapping("/player")
    public  ResponseEntity<String> deletePlayer(@RequestParam String playerId){
        logger.debug("LeaderboardController.deletePlayer - playerId={}", playerId);
        leaderboardService.deletePlayer(playerId);
        return ResponseEntity.ok("Succesfully deleted player with playerId: "+playerId);
    }

    @DeleteMapping("/players")
    public ResponseEntity<String> deleteAllPlayers(){
        logger.debug("LeaderboardController.deleteAllPlayers");
        leaderboardService.deleteAllPlayers();
        return ResponseEntity.ok("Succesfully deleted all players");
    }

}
