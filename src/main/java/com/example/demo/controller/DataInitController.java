package com.example.demo.controller;

import com.example.demo.model.entities.*;
import com.example.demo.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DataInitController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private OpinionLikeRepository opinionLikeRepository;

    @PostMapping("/api/cleanup-old-teams")
    public ResponseEntity<Map<String, Object>> cleanupOldTeams() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Delete opinions related to games with old teams
            List<Game> oldGames = gameRepository.findAll().stream()
                    .filter(g -> (g.getTeamOneId() >= 1 && g.getTeamOneId() <= 14) ||
                                 (g.getTeamTwoId() >= 1 && g.getTeamTwoId() <= 14))
                    .toList();

            for (Game game : oldGames) {
                List<Opinion> opinions = opinionRepository.findAll().stream()
                        .filter(o -> o.getGame().getId() == game.getId())
                        .toList();
                opinionRepository.deleteAll(opinions);
            }

            // Delete old games
            gameRepository.deleteAll(oldGames);

            // Delete old teams (1-14)
            for (int i = 1; i <= 14; i++) {
                teamRepository.deleteById((long) i);
            }

            response.put("success", true);
            response.put("message", "Old teams cleaned up successfully!");
            response.put("remainingTeams", teamRepository.count());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/init-test-data")
    public ResponseEntity<Map<String, Object>> initializeTestData() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Create teams
            Team t1 = createTeam("T1", "T1", "Korea",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819614211_t1-2021-worlds.png",
                "@T1LoL");
            Team geng = createTeam("Gen.G", "GEN", "Korea",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370350784_GenG-FullonDark.png",
                "@GenG");
            Team g2 = createTeam("G2 Esports", "G2", "Europe",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2FG2-FullonDark.png",
                "@G2esports");
            Team fnatic = createTeam("Fnatic", "FNC", "Europe",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819807831_fnc-2021-worlds.png",
                "@FNATIC");
            Team c9 = createTeam("Cloud9", "C9", "North America",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819640922_c9-2021-worlds.png",
                "@Cloud9");
            Team tl = createTeam("Team Liquid", "TL", "North America",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819669096_tl-2021-worlds.png",
                "@TeamLiquid");
            Team jdg = createTeam("JD Gaming", "JDG", "China",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819624910_jdg-2021-worlds.png",
                "@JDGaming");
            Team blg = createTeam("Bilibili Gaming", "BLG", "China",
                "https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370109137_BLG-FullonDark.png",
                "@bilibili_gaming");

            // Create players for each team
            // T1 Roster
            createPlayer("Zeus", "The Summit Ruler", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F1703186989700_zeus.png", t1);
            createPlayer("Oner", "The Shadow Jungler", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F1703186990000_oner.png", t1);
            createPlayer("Faker", "The Unkillable Demon King", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F1703186990100_faker.png", t1);
            createPlayer("Gumayusi", "The Marksman Prodigy", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F1703186990200_gumayusi.png", t1);
            createPlayer("Keria", "The Roaming Playmaker", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F1703186990300_keria.png", t1);

            // Gen.G Roster
            createPlayer("Kiin", "The Teleport Master", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkiin.png", geng);
            createPlayer("Canyon", "The Jungle Maestro", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcanyon.png", geng);
            createPlayer("Chovy", "The Solo Queue King", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fchovy.png", geng);
            createPlayer("Peyz", "The Rising Star", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fpeyz.png", geng);
            createPlayer("Lehends", "The Vision Master", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Flehends.png", geng);

            // G2 Esports Roster
            createPlayer("BrokenBlade", "The Turkish Titan", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbrokenblade.png", g2);
            createPlayer("Yike", "The Young Gun", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fyike.png", g2);
            createPlayer("Caps", "Baby Faker", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcaps.png", g2);
            createPlayer("Hans Sama", "The French Sniper", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhanssama.png", g2);
            createPlayer("Mikyx", "The Hook God", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmikyx.png", g2);

            // Fnatic Roster
            createPlayer("Oscarinin", "The Spanish Samurai", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Foscarinin.png", fnatic);
            createPlayer("Razork", "The Aggressive Jungler", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Frazork.png", fnatic);
            createPlayer("Humanoid", "The Czech Carry", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhumanoid.png", fnatic);
            createPlayer("Noah", "The Precision Striker", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fnoah.png", fnatic);
            createPlayer("Jun", "The Tactical Support", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fjun.png", fnatic);

            // Cloud9 Roster
            createPlayer("Thanatos", "The NA Hope", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fthanatos.png", c9);
            createPlayer("Blaber", "The NA Flash", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fblaber.png", c9);
            createPlayer("Jojopyun", "The Young Prodigy", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fjojopyun.png", c9);
            createPlayer("Berserker", "The Korean Import", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fberserker.png", c9);
            createPlayer("Vulcan", "The Shot Caller", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fvulcan.png", c9);

            // Team Liquid Roster
            createPlayer("Impact", "The Top Die", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fimpact.png", tl);
            createPlayer("Umti", "The Jungle Innovator", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fumti.png", tl);
            createPlayer("APA", "The NA Mid Hope", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fapa.png", tl);
            createPlayer("Yeon", "The Rising ADC", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fyeon.png", tl);
            createPlayer("CoreJJ", "The World Champion", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcorejj.png", tl);

            // JD Gaming Roster
            createPlayer("369", "The LPL Top", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F369.png", jdg);
            createPlayer("Kanavi", "The Korean Powerhouse", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkanavi.png", jdg);
            createPlayer("Knight", "The Chinese Superstar", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fknight.png", jdg);
            createPlayer("Ruler", "The Worlds MVP", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fruler.png", jdg);
            createPlayer("Missing", "The Defensive Wall", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmissing.png", jdg);

            // Bilibili Gaming Roster
            createPlayer("Bin", "The Carry God", "Top", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbin.png", blg);
            createPlayer("Xun", "The Aggressive Invader", "Jungle", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fxun.png", blg);
            createPlayer("Yagao", "The Consistent Mid", "Mid", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fyagao.png", blg);
            createPlayer("Elk", "The Team Fighter", "ADC", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Felk.png", blg);
            createPlayer("ON", "The Engage Master", "Support", "https://am-a.akamaihd.net/image?resize=200:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fon.png", blg);

            // Create test users
            User user1 = createUser("testuser", "test@leagueopinion.com", "password123");
            User user2 = createUser("faker_fan", "faker@leagueopinion.com", "password123");
            User user3 = createUser("lec_viewer", "lec@leagueopinion.com", "password123");

            // Get regions
            Region korea = regionRepository.findById(3L).orElseThrow();
            Region europe = regionRepository.findById(2L).orElseThrow();
            Region northAmerica = regionRepository.findById(1L).orElseThrow();
            Region china = regionRepository.findById(4L).orElseThrow();
            Region international = regionRepository.findById(5L).orElseThrow();

            // Create games
            Game game1 = createGame("2025-10-25T18:00:00", (int)t1.getId(), (int)geng.getId(), 65, 35, korea);
            Game game2 = createGame("2025-10-26T20:00:00", (int)g2.getId(), (int)fnatic.getId(), 55, 45, europe);
            Game game3 = createGame("2025-10-26T22:00:00", (int)c9.getId(), (int)tl.getId(), 48, 52, northAmerica);
            Game game4 = createGame("2025-10-27T14:00:00", (int)jdg.getId(), (int)blg.getId(), 60, 40, china);
            Game game5 = createGame("2025-10-28T18:00:00", (int)geng.getId(), (int)t1.getId(), 45, 55, korea);
            Game game6 = createGame("2025-10-29T20:00:00", (int)t1.getId(), (int)g2.getId(), 70, 30, international);

            // Create opinions
            Opinion op1 = createOpinion(70, 30, "T1 looking unstoppable this season! Faker is in peak form.", user1, game1);
            Opinion op2 = createOpinion(65, 35, "Gen.G has been struggling lately, T1 should take this easily.", user2, game1);
            Opinion op3 = createOpinion(60, 40, "I think T1 wins but Gen.G will put up a fight in early game.", user3, game1);

            Opinion op4 = createOpinion(50, 50, "This is going to be so close! Classic EU rivalry.", user1, game2);
            Opinion op5 = createOpinion(55, 45, "G2 has better macro, I give them the edge.", user2, game2);
            Opinion op6 = createOpinion(45, 55, "Fnatic bot lane is looking really strong, I favor them.", user3, game2);

            Opinion op7 = createOpinion(40, 60, "Team Liquid has been on fire! CoreJJ is playing like a monster.", user1, game3);
            Opinion op8 = createOpinion(45, 55, "C9 has potential but TL is more consistent right now.", user2, game3);
            Opinion op9 = createOpinion(50, 50, "NA is so unpredictable, this could go either way.", user3, game3);

            Opinion op10 = createOpinion(65, 35, "JDG are the defending champions, they got this.", user1, game4);
            Opinion op11 = createOpinion(60, 40, "BLG has upset potential but JDG should win.", user2, game4);
            Opinion op12 = createOpinion(55, 45, "LPL is so competitive, but JDG has more experience.", user3, game4);

            Opinion op13 = createOpinion(40, 60, "T1 again! They are just better in every role.", user1, game5);
            Opinion op14 = createOpinion(35, 65, "Gen.G needs to figure something out, T1 is too good.", user2, game5);

            Opinion op15 = createOpinion(75, 25, "LCK vs LEC, and LCK always wins. T1 dominates.", user1, game6);
            Opinion op16 = createOpinion(70, 30, "G2 is good but T1 is on another level internationally.", user2, game6);
            Opinion op17 = createOpinion(65, 35, "East vs West, and East wins. T1 by a mile.", user3, game6);

            // Create opinion likes (users liking other users' opinions)
            // User1 likes some opinions from user2 and user3
            createOpinionLike(user1, op2);  // user1 likes user2's opinion on T1 vs Gen.G
            createOpinionLike(user1, op5);  // user1 likes user2's opinion on G2 vs Fnatic
            createOpinionLike(user1, op11); // user1 likes user2's opinion on JDG vs BLG
            createOpinionLike(user1, op6);  // user1 likes user3's opinion on G2 vs Fnatic

            // User2 likes some opinions from user1 and user3
            createOpinionLike(user2, op1);  // user2 likes user1's opinion on T1 vs Gen.G
            createOpinionLike(user2, op7);  // user2 likes user1's opinion on C9 vs TL
            createOpinionLike(user2, op10); // user2 likes user1's opinion on JDG vs BLG
            createOpinionLike(user2, op3);  // user2 likes user3's opinion on T1 vs Gen.G
            createOpinionLike(user2, op9);  // user2 likes user3's opinion on C9 vs TL

            // User3 likes some opinions from user1 and user2
            createOpinionLike(user3, op1);  // user3 likes user1's opinion on T1 vs Gen.G
            createOpinionLike(user3, op4);  // user3 likes user1's opinion on G2 vs Fnatic
            createOpinionLike(user3, op13); // user3 likes user1's opinion on Gen.G vs T1 (rematch)
            createOpinionLike(user3, op2);  // user3 likes user2's opinion on T1 vs Gen.G
            createOpinionLike(user3, op8);  // user3 likes user2's opinion on C9 vs TL

            response.put("success", true);
            response.put("message", "Test data initialized successfully with real teams and players!");
            response.put("teams", teamRepository.count());
            response.put("players", playerRepository.count());
            response.put("users", userRepository.count());
            response.put("games", gameRepository.count());
            response.put("opinions", opinionRepository.count());
            response.put("opinionLikes", opinionLikeRepository.count());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private Team createTeam(String name, String tag, String region, String image, String twitter) {
        Team team = new Team();
        team.setName(name);
        team.setTag(tag);
        team.setRegion(region);
        team.setImage(image);
        team.setTwitter(twitter);
        return teamRepository.save(team);
    }

    private User createUser(String username, String email, String password) {
        // Check if user exists
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            return existingUser;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private Game createGame(String matchDateTime, int team1Id, int team2Id, int team1Percent, int team2Percent, Region region) {
        Game game = new Game();
        game.setMatchStartDateTime(matchDateTime);
        game.setTeamOneId(team1Id);
        game.setTeamTwoId(team2Id);
        game.setTeamOnePercent(team1Percent);
        game.setTeamTwoPercent(team2Percent);
        game.setRegion(region);
        return gameRepository.save(game);
    }

    private Opinion createOpinion(int team1Percent, int team2Percent, String comment, User user, Game game) {
        Opinion opinion = new Opinion();
        opinion.setTeamOnePercent(team1Percent);
        opinion.setTeamTwoPercent(team2Percent);
        opinion.setComment(comment);
        opinion.setCreatedAt(new java.util.Date());
        opinion.setOwner(user);
        opinion.setGame(game);
        return opinionRepository.save(opinion);
    }

    private Player createPlayer(String name, String nickname, String role, String image, Team team) {
        Player player = new Player();
        player.setName(name);
        player.setNickname(nickname);
        player.setRole(role);
        player.setImage(image);
        player.setTeam(team);
        return playerRepository.save(player);
    }

    private OpinionLike createOpinionLike(User user, Opinion opinion) {
        OpinionLike like = new OpinionLike();
        like.setUser(user);
        like.setOpinion(opinion);
        like.setCreatedAt(LocalDateTime.now());
        return opinionLikeRepository.save(like);
    }
}
