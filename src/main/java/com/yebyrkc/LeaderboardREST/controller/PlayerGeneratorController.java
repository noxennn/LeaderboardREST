package com.yebyrkc.LeaderboardREST.controller;

import com.yebyrkc.LeaderboardREST.service.PlayerGenerator.PlayerGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerGeneratorController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerGeneratorController.class);

    @Autowired
    private final PlayerGeneratorService playerGeneratorService;

    public PlayerGeneratorController(PlayerGeneratorService playerGeneratorService) {
        this.playerGeneratorService = playerGeneratorService;
    }

    /**
     * Generates a specified number of players with randomized data.
     * Example: POST /api/generator/players?count=1000
     *
     * @param count Number of players to generate.
     */
    @PostMapping("")
    public ResponseEntity<String> generatePlayers(@RequestParam(defaultValue = "1") int count) {
        logger.debug("Generating {} players via controller", count);
        playerGeneratorService.generatePlayerEntries(count);
        return ResponseEntity.ok("Successfully generated " + count + " players.");
    }
}
