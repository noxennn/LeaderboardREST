package com.yebyrkc.LeaderboardREST.controller;

import com.yebyrkc.LeaderboardREST.service.PlayerGenerator.PlayerGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
public class PlayerGeneratorController {


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
        playerGeneratorService.generatePlayers(count);
        return ResponseEntity.ok("Successfully generated " + count + " players.");
    }
}
