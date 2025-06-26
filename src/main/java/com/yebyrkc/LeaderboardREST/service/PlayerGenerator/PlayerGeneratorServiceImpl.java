package com.yebyrkc.LeaderboardREST.service.PlayerGenerator;

import com.yebyrkc.LeaderboardREST.model.LeaderboardEntry;
import com.yebyrkc.LeaderboardREST.service.Leaderboard.LeaderboardService;
import com.yebyrkc.LeaderboardREST.service.PlayerGenerator.PlayerGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PlayerGeneratorServiceImpl implements PlayerGeneratorService {

    private final LeaderboardService leaderboardService;
    private final Random random = new Random();

    // Random username parts
    private static final String[] NAME_PREFIXES = {
            "Swift", "Night", "Golden", "Cyber", "Silent", "Fierce",
            "Crimson", "Lone", "Wild", "Dark", "Pixel", "Ghost",
            "Rapid", "Stealth", "Ancient", "Shadow", "Storm", "Turbo",
            "Drift", "Neon", "Venom", "Inferno", "Bright", "Silver",
            "Iron", "Neptune", "Jade", "Rogue", "Vortex", "Nova",
            "Titan", "Omega", "Lunar", "Solar", "Meteor", "Blaze",
            "Hyper", "Frost", "Glacial", "Cobalt", "Emerald", "Onyx",
            "Vivid", "Zephyr", "Primal", "Abyss", "Astral", "Nova",
            "Grim", "Phantom"
    };

    private static final String[] NAME_SUFFIXES = {
            "Eagle", "Wolf", "Tiger", "Fish", "Hawk", "Dragon",
            "Falcon", "Ninja", "Bot", "Hunter", "Knight", "Phoenix",
            "Raider", "Sniper", "Striker", "Viper", "Gamer", "Racer",
            "Raven", "Wizard", "Sorcerer", "Guardian", "Titan", "Spartan",
            "Viking", "Rogue", "Paladin", "Samurai", "Champion", "Wizard",
            "Shifter", "Stalker", "Crusader", "Patriot", "Seeker",
            "Blaster", "Nomad", "Phantom", "Marauder", "Specter",
            "Ranger", "Cyborg", "Enforcer", "Crusher", "Slayer",
            "Avenger", "Gladiator", "Oracle", "Warden", "Zealot"
    };

    //Can be converted to UUID later
    private static final int MAX_NUMBER_SUFFIX = 9999;

    @Autowired
    public PlayerGeneratorServiceImpl(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    //Generate Random users and add them to LeaderboardEntry
//    @Override
//    public void generatePlayers(int count) {
//        for (int i = 1; i <= count; i++) {
//            String playerId = "player:" + i;
//
//            // username like SwiftTiger123
//            String username = NAME_PREFIXES[random.nextInt(NAME_PREFIXES.length)]
//                    + NAME_SUFFIXES[random.nextInt(NAME_SUFFIXES.length)]
//                    + random.nextInt(MAX_NUMBER_SUFFIX);
//
//            // level 1-50
//            int level = 1 + random.nextInt(50);
//
//            // score between 0 and 4500
//            double score = 0 + random.nextDouble() * 4500;
//
//            leaderboardService.addPlayer(playerId, username, level, score);
//        }
//    }
    @Override
    public void generatePlayers(int count) {
        List<LeaderboardEntry> entries = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            String playerId = "player:" + i;
//             username like SwiftTiger123
            String username = NAME_PREFIXES[random.nextInt(NAME_PREFIXES.length)]
                    + NAME_SUFFIXES[random.nextInt(NAME_SUFFIXES.length)]
                    + random.nextInt(MAX_NUMBER_SUFFIX);

            // level 1-50
            int level = 1 + random.nextInt(50);

            // score between 0 and 4500
            double score = 0 + random.nextDouble() * 4500;
            // random username, level, score as before
            entries.add(new LeaderboardEntry(playerId, username, score, level, Instant.now()));
        }
        leaderboardService.addPlayers(entries);  // single call
    }
}
