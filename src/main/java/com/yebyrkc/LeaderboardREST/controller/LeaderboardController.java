package com.yebyrkc.LeaderboardREST.controller;

import com.yebyrkc.LeaderboardREST.dto.IncrementRequestDTO;
import com.yebyrkc.LeaderboardREST.dto.LeaderboardEntryDTO;
import com.yebyrkc.LeaderboardREST.dto.PlayerGenerationRequestDTO;
import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.repository.LeaderboardRepository;
import com.yebyrkc.LeaderboardREST.service.LeaderboardService;
import com.yebyrkc.LeaderboardREST.service.PlayerGenerator;
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
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private PlayerGenerator playerGenerator;

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
    public LeaderboardEntry getLeaderboardEntry(@PathVariable String playerId) {
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

    /**
     * Increment a player's score
     */
    @PostMapping("/{playerId}/score/incrementBy")
    public double incrementScore(@PathVariable String playerId,@RequestBody IncrementRequestDTO dto) {
        logger.debug("Incrementing score for playerId={} by {}", playerId, dto.getIncrementBy());

        return leaderboardService.incrementScore("player:"+playerId, dto.getIncrementBy());
    }

    /** Add new player to LeaderboardEntry
     *
     */
    @PostMapping("/player")
    public void addLeaderboardEntry(@RequestBody LeaderboardEntryDTO dto) {
        logger.debug("Adding player via controller with playerId={}", dto.getPlayerId());
        leaderboardService.addPlayerEntry(dto.getPlayerId(), dto.getUsername(), dto.getLevel(), dto.getScore());
    }

    /** Generate new players for Leaderboard
     *
     */
    @PostMapping("/players")
    public ResponseEntity<String> generateLeaderboardEntries(@RequestBody PlayerGenerationRequestDTO dto) {
        logger.debug("Generating {} players via controller", dto.getCount());
        playerGenerator.generatePlayerEntries(dto.getCount());
        return ResponseEntity.ok("Successfully generated " + dto.getCount() + " players.");
    }

    /** Delete existing player from LeaderboardEntry
     *
     */
    @DeleteMapping("/player")
    public  ResponseEntity<String> deleteLeaderboardEntry(@RequestParam String playerId){
        logger.debug("Deleting player with id={}", playerId);
        leaderboardService.deletePlayerEntry(playerId);
        return ResponseEntity.ok("Successfully deleted player with playerId: "+playerId);
    }

    @DeleteMapping("/players")
    public ResponseEntity<String> deleteAllLeaderboardEntries(){
        logger.debug("Deleting all players through controller");
        leaderboardService.deleteAllPlayerEntries();
        return ResponseEntity.ok("Successfully deleted all players");
    }



}
