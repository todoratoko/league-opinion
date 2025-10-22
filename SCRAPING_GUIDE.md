# Web Scraping Implementation Guide

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Dependencies](#dependencies)
4. [Match Scraper](#match-scraper)
5. [Odds Scraper](#odds-scraper)
6. [Configuration](#configuration)
7. [Best Practices](#best-practices)
8. [Troubleshooting](#troubleshooting)
9. [Alternatives](#alternatives)

---

## Overview

This guide documents the web scraping implementation for LeagueOpinion to collect:
- **Match data** (teams, schedules) - scraped hourly
- **Live odds** (betting odds for matches) - scraped every 5 minutes

### Technology Stack
- **Jsoup** - HTML parsing library
- **Spring @Scheduled** - Task scheduling
- **MySQL/PostgreSQL** - Data storage

### Legal Considerations
⚠️ **IMPORTANT**:
- Always check website Terms of Service
- Respect robots.txt
- Implement rate limiting
- Consider using official APIs when available

---

## Architecture

```
┌─────────────────────────────────────────┐
│     Spring Boot Application             │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  MatchScraperService             │  │
│  │  @Scheduled(cron = "0 0 * * * *")│  │ ← Runs every hour
│  └──────────┬───────────────────────┘  │
│             │                           │
│             ▼                           │
│  ┌──────────────────────────────────┐  │
│  │  OddsScraperService              │  │
│  │  @Scheduled(fixedRate = 300000)  │  │ ← Runs every 5 min
│  └──────────┬───────────────────────┘  │
│             │                           │
│             ▼                           │
│  ┌──────────────────────────────────┐  │
│  │  Database (Game, Team, Odds)     │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

---

## Dependencies

### Add to `pom.xml`:

```xml
<!-- Jsoup for HTML parsing -->
<dependency>
    <groupId>org.jsoup</groupId>
    <artifactId>jsoup</artifactId>
    <version>1.17.2</version>
</dependency>

<!-- Optional: For dynamic JavaScript-heavy sites -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.16.1</version>
</dependency>

<!-- Optional: User agent rotation -->
<dependency>
    <groupId>com.github.javafaker</groupId>
    <artifactId>javafaker</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Enable Scheduling

In `DemoApplication.java`:
```java
@SpringBootApplication
@EnableScheduling  // Add this annotation
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

---

## Match Scraper

### Purpose
Scrapes upcoming match schedules and team information.

### Frequency
Runs every hour (`@Scheduled(cron = "0 0 * * * *")`)

### Implementation

```java
package com.example.demo.services;

import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Team;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class MatchScraperService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Value("${scraper.match.url}")
    private String matchUrl;

    @Value("${scraper.enabled:true}")
    private boolean scraperEnabled;

    // Run every hour at minute 0
    @Scheduled(cron = "0 0 * * * *")
    public void scrapeUpcomingMatches() {
        if (!scraperEnabled) {
            log.info("Scraper is disabled");
            return;
        }

        log.info("Starting match scraper...");

        try {
            Document doc = Jsoup.connect(matchUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .referrer("https://google.com")
                    .get();

            // Adjust selectors based on actual website structure
            Elements matchElements = doc.select(".match-item");

            int scrapedCount = 0;
            for (Element matchElement : matchElements) {
                try {
                    String team1Name = matchElement.select(".team1-name").text();
                    String team2Name = matchElement.select(".team2-name").text();
                    String matchTime = matchElement.select(".match-time").attr("data-time");

                    if (team1Name.isEmpty() || team2Name.isEmpty()) {
                        continue;
                    }

                    // Check if match already exists
                    if (!matchExists(team1Name, team2Name, matchTime)) {
                        saveMatch(team1Name, team2Name, matchTime);
                        scrapedCount++;
                    }

                } catch (Exception e) {
                    log.error("Error parsing match element: {}", e.getMessage());
                }
            }

            log.info("Match scraping completed. Scraped {} new matches", scrapedCount);

        } catch (Exception e) {
            log.error("Error scraping matches: {}", e.getMessage(), e);
        }

        // Add delay to be respectful
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean matchExists(String team1, String team2, String time) {
        // Check if match already exists in database
        LocalDateTime matchTime = parseMatchTime(time);
        return gameRepository.existsByTeamsAndTime(team1, team2, matchTime);
    }

    private void saveMatch(String team1Name, String team2Name, String matchTime) {
        Team team1 = getOrCreateTeam(team1Name);
        Team team2 = getOrCreateTeam(team2Name);

        Game game = new Game();
        game.setTeamOne(team1);
        game.setTeamTwo(team2);
        game.setGameTime(parseMatchTime(matchTime));
        game.setCreatedAt(LocalDateTime.now());

        gameRepository.save(game);
        log.info("Saved new match: {} vs {}", team1Name, team2Name);
    }

    private Team getOrCreateTeam(String teamName) {
        Team team = teamRepository.findByName(teamName);
        if (team == null) {
            team = new Team();
            team.setName(teamName);
            team.setCreatedAt(LocalDateTime.now());
            teamRepository.save(team);
            log.info("Created new team: {}", teamName);
        }
        return team;
    }

    private LocalDateTime parseMatchTime(String timeString) {
        try {
            // Adjust format based on website's time format
            // Example: "2025-10-22T18:00:00Z"
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            return LocalDateTime.parse(timeString, formatter);
        } catch (Exception e) {
            log.error("Error parsing time: {}", timeString);
            return LocalDateTime.now().plusDays(1);
        }
    }
}
```

### Required Repository Methods

Add to `GameRepository.java`:
```java
@Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Game g " +
       "WHERE (g.teamOne.name = :team1 AND g.teamTwo.name = :team2 " +
       "OR g.teamOne.name = :team2 AND g.teamTwo.name = :team1) " +
       "AND g.gameTime = :matchTime")
boolean existsByTeamsAndTime(@Param("team1") String team1,
                              @Param("team2") String team2,
                              @Param("matchTime") LocalDateTime matchTime);
```

Add to `TeamRepository.java`:
```java
Team findByName(String name);
```

---

## Odds Scraper

### Purpose
Scrapes live betting odds for upcoming matches.

### Frequency
Runs every 5 minutes (`@Scheduled(fixedRate = 300000)`)

### Implementation

```java
package com.example.demo.services;

import com.example.demo.model.entities.Game;
import com.example.demo.model.entities.Odds;
import com.example.demo.model.repositories.GameRepository;
import com.example.demo.model.repositories.OddsRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OddsScraperService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OddsRepository oddsRepository;

    @Value("${scraper.odds.url}")
    private String oddsBaseUrl;

    @Value("${scraper.enabled:true}")
    private boolean scraperEnabled;

    // Run every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void scrapeLiveOdds() {
        if (!scraperEnabled) {
            return;
        }

        log.info("Starting odds scraper...");

        try {
            // Get upcoming/live matches from database
            List<Game> upcomingGames = gameRepository.findUpcomingGames();

            log.info("Found {} upcoming games to scrape odds for", upcomingGames.size());

            int scrapedCount = 0;
            for (Game game : upcomingGames) {
                try {
                    scrapeOddsForMatch(game);
                    scrapedCount++;

                    // Delay between requests to be respectful
                    Thread.sleep(1000);

                } catch (Exception e) {
                    log.error("Error scraping odds for game {}: {}", game.getId(), e.getMessage());
                }
            }

            log.info("Odds scraping completed. Updated {} games", scrapedCount);

        } catch (Exception e) {
            log.error("Error in odds scraping process: {}", e.getMessage(), e);
        }
    }

    private void scrapeOddsForMatch(Game game) throws Exception {
        // Construct URL for specific match
        String url = oddsBaseUrl + "/match/" + game.getId();

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .referrer("https://google.com")
                .get();

        // Adjust selectors based on actual website structure
        String team1OddsStr = doc.select(".team1-odds").first().text();
        String team2OddsStr = doc.select(".team2-odds").first().text();

        if (team1OddsStr.isEmpty() || team2OddsStr.isEmpty()) {
            log.warn("Could not find odds for game {}", game.getId());
            return;
        }

        Double team1Odds = parseOdds(team1OddsStr);
        Double team2Odds = parseOdds(team2OddsStr);

        // Save odds to database
        saveOdds(game, team1Odds, team2Odds);

        log.debug("Updated odds for {} vs {}: {} / {}",
                game.getTeamOne().getName(),
                game.getTeamTwo().getName(),
                team1Odds,
                team2Odds);
    }

    private void saveOdds(Game game, Double team1Odds, Double team2Odds) {
        Odds odds = new Odds();
        odds.setGame(game);
        odds.setTeam1Odds(team1Odds);
        odds.setTeam2Odds(team2Odds);
        odds.setScrapedAt(LocalDateTime.now());

        oddsRepository.save(odds);
    }

    private Double parseOdds(String oddsString) {
        try {
            // Remove any non-numeric characters except decimal point
            String cleaned = oddsString.replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            log.error("Could not parse odds: {}", oddsString);
            return 0.0;
        }
    }
}
```

### Create Odds Entity

```java
package com.example.demo.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "odds")
@Getter
@Setter
@NoArgsConstructor
public class Odds {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "team1_odds")
    private Double team1Odds;

    @Column(name = "team2_odds")
    private Double team2Odds;

    @Column(name = "scraped_at")
    private LocalDateTime scrapedAt;
}
```

### Create Odds Repository

```java
package com.example.demo.model.repositories;

import com.example.demo.model.entities.Odds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OddsRepository extends JpaRepository<Odds, Long> {

    @Query("SELECT o FROM Odds o WHERE o.game.id = :gameId ORDER BY o.scrapedAt DESC")
    List<Odds> findByGameIdOrderByScrapedAtDesc(@Param("gameId") Long gameId);

    @Query("SELECT o FROM Odds o WHERE o.game.id = :gameId " +
           "ORDER BY o.scrapedAt DESC LIMIT 1")
    Odds findLatestOddsByGameId(@Param("gameId") Long gameId);
}
```

### Required GameRepository Methods

Add to `GameRepository.java`:
```java
@Query("SELECT g FROM Game g WHERE g.gameTime > CURRENT_TIMESTAMP " +
       "AND g.gameTime < :maxTime ORDER BY g.gameTime ASC")
List<Game> findUpcomingGames(@Param("maxTime") LocalDateTime maxTime);

// Or simpler version
@Query("SELECT g FROM Game g WHERE g.gameTime > CURRENT_TIMESTAMP " +
       "ORDER BY g.gameTime ASC")
List<Game> findUpcomingGames();
```

---

## Configuration

### application.properties

```properties
# Scraper Configuration
scraper.enabled=true
scraper.match.url=https://example-bookmaker.com/esports/lol/matches
scraper.odds.url=https://example-bookmaker.com/esports/lol

# Scraper scheduling (optional - overrides @Scheduled annotations)
# scraper.match.cron=0 0 * * * *
# scraper.odds.rate=300000

# Database configuration
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### Environment Variables

For production (Render/Railway):
```bash
DATABASE_URL=jdbc:mysql://host:3306/leagueopinion
DB_USERNAME=your_username
DB_PASSWORD=your_password
SCRAPER_ENABLED=true
SCRAPER_MATCH_URL=https://...
SCRAPER_ODDS_URL=https://...
```

---

## Best Practices

### 1. Rate Limiting
```java
private static final long DELAY_BETWEEN_REQUESTS = 2000; // 2 seconds

try {
    Thread.sleep(DELAY_BETWEEN_REQUESTS);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### 2. User Agent Rotation
```java
private static final String[] USER_AGENTS = {
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
};

private String getRandomUserAgent() {
    int index = ThreadLocalRandom.current().nextInt(USER_AGENTS.length);
    return USER_AGENTS[index];
}
```

### 3. Error Handling
```java
try {
    Document doc = Jsoup.connect(url)
        .userAgent(userAgent)
        .timeout(10000)
        .get();
} catch (HttpStatusException e) {
    if (e.getStatusCode() == 429) {
        log.warn("Rate limited, backing off...");
        Thread.sleep(60000); // Wait 1 minute
    }
} catch (IOException e) {
    log.error("Network error: {}", e.getMessage());
} catch (Exception e) {
    log.error("Unexpected error: {}", e.getMessage(), e);
}
```

### 4. Respect robots.txt
```java
// Check robots.txt before scraping
// Example: https://example.com/robots.txt
```

### 5. Caching
```java
@Cacheable(value = "matches", unless = "#result == null")
public List<Game> getUpcomingMatches() {
    // This will cache results and reduce database queries
    return gameRepository.findUpcomingGames();
}
```

### 6. Logging
```java
log.info("Starting scraper");           // Important events
log.debug("Processing match: {}", id);  // Detailed info
log.warn("Missing data for match: {}", id);  // Warnings
log.error("Failed to scrape: {}", url, e);   // Errors
```

---

## Troubleshooting

### Issue: Scraper not running
**Check:**
1. Is `@EnableScheduling` added to main application class?
2. Is `scraper.enabled=true` in properties?
3. Check logs for errors during startup

### Issue: Cannot find elements
**Solution:**
1. Inspect website HTML structure
2. Update CSS selectors to match actual structure
3. Check if site uses JavaScript (may need Selenium)

### Issue: Getting blocked/rate limited
**Solution:**
1. Increase delay between requests
2. Rotate user agents
3. Use proxies (paid service)
4. Consider using official API instead

### Issue: Parsing errors
**Solution:**
```java
// Add defensive null checks
Element oddsElement = doc.select(".odds").first();
if (oddsElement != null) {
    String odds = oddsElement.text();
} else {
    log.warn("Could not find odds element");
}
```

### Issue: Database connection issues
**Solution:**
1. Check connection pool settings
2. Add timeout configuration
3. Implement retry logic

### Debugging Tips

**Enable verbose Jsoup logging:**
```java
Document doc = Jsoup.connect(url)
    .userAgent("...")
    .timeout(10000)
    .execute()
    .parse();

// Log the HTML to see what you're getting
log.debug("HTML: {}", doc.html());
```

**Test selectors manually:**
```java
@GetMapping("/test-scrape")
public String testScrape() throws IOException {
    Document doc = Jsoup.connect(url).get();

    // Test different selectors
    Elements matches = doc.select(".match");
    return "Found " + matches.size() + " matches\n" +
           "HTML: " + doc.html();
}
```

---

## Alternatives

### If Scraping Becomes Too Complex

#### 1. Use Official APIs (Recommended)

**PandaScore (eSports data):**
```java
@Service
public class PandaScoreService {

    @Value("${pandascore.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Match> getUpcomingMatches() {
        String url = "https://api.pandascore.co/lol/matches/upcoming?token=" + apiKey;
        Match[] matches = restTemplate.getForObject(url, Match[].class);
        return Arrays.asList(matches);
    }
}
```

**The Odds API:**
```java
public OddsResponse getOdds() {
    String url = "https://api.the-odds-api.com/v4/sports/lol_esports/odds" +
                 "?apiKey=" + apiKey + "&regions=us";
    return restTemplate.getForObject(url, OddsResponse.class);
}
```

#### 2. Selenium for JavaScript-heavy sites

```java
@Service
public class SeleniumScraperService {

    private WebDriver driver;

    @PostConstruct
    public void init() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    public void scrapeDynamic() {
        driver.get("https://example.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("match-item")
        ));

        List<WebElement> matches = driver.findElements(By.className("match-item"));
        // Process matches
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

#### 3. Python Microservice

Deploy separate Python service for complex scraping:
- Use Playwright/Scrapy
- Deploy to Render/Railway (free tier)
- Java backend calls Python service via REST API

#### 4. GitHub Actions Cron

Free scheduled scraping that writes directly to database:
```yaml
name: Scrape Odds
on:
  schedule:
    - cron: '*/5 * * * *'
jobs:
  scrape:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run scraper
        run: python scraper.py
```

---

## Testing

### Manual Testing Endpoint

```java
@RestController
@RequestMapping("/api/scraper")
public class ScraperTestController {

    @Autowired
    private MatchScraperService matchScraper;

    @Autowired
    private OddsScraperService oddsScraper;

    @GetMapping("/test-matches")
    public ResponseEntity<String> testMatchScraper() {
        try {
            matchScraper.scrapeUpcomingMatches();
            return ResponseEntity.ok("Match scraping triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test-odds")
    public ResponseEntity<String> testOddsScraper() {
        try {
            oddsScraper.scrapeLiveOdds();
            return ResponseEntity.ok("Odds scraping triggered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
```

### Unit Tests

```java
@SpringBootTest
class MatchScraperServiceTests {

    @Autowired
    private MatchScraperService scraperService;

    @Test
    void testParseMatchTime() {
        String timeStr = "2025-10-22T18:00:00Z";
        LocalDateTime result = scraperService.parseMatchTime(timeStr);
        assertNotNull(result);
    }

    @Test
    void testGetOrCreateTeam() {
        Team team = scraperService.getOrCreateTeam("T1");
        assertNotNull(team);
        assertEquals("T1", team.getName());
    }
}
```

---

## Monitoring

### Add Health Check

```java
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OddsRepository oddsRepository;

    @GetMapping("/scraper-status")
    public Map<String, Object> getScraperStatus() {
        Map<String, Object> status = new HashMap<>();

        long totalGames = gameRepository.count();
        long upcomingGames = gameRepository.findUpcomingGames().size();
        long totalOdds = oddsRepository.count();

        status.put("total_games", totalGames);
        status.put("upcoming_games", upcomingGames);
        status.put("total_odds_records", totalOdds);
        status.put("last_updated", LocalDateTime.now());

        return status;
    }
}
```

---

## Resources

### Useful Links
- [Jsoup Documentation](https://jsoup.org/cookbook/)
- [Spring Scheduling Guide](https://spring.io/guides/gs/scheduling-tasks/)
- [PandaScore API](https://developers.pandascore.co/)
- [The Odds API](https://the-odds-api.com/)
- [robots.txt Checker](https://www.google.com/webmasters/tools/robots-testing-tool)

### CSS Selector Reference
```
.class-name          → Select by class
#id-name             → Select by ID
tag-name             → Select by tag
[attribute]          → Select by attribute
.parent .child       → Descendant selector
.parent > .child     → Direct child
:first-child         → First child element
:nth-child(2)        → Second child element
```

### Common Scraping Patterns

**Extract text:**
```java
String text = element.select(".class").text();
```

**Extract attribute:**
```java
String href = element.select("a").attr("href");
```

**Extract multiple elements:**
```java
Elements elements = doc.select(".match");
for (Element el : elements) {
    // process each element
}
```

**Check if element exists:**
```java
if (!doc.select(".odds").isEmpty()) {
    // element exists
}
```

---

## Next Steps

1. ✅ Identify target bookmaker website
2. ✅ Inspect HTML structure and identify selectors
3. ✅ Update CSS selectors in scraper services
4. ✅ Test scrapers manually using test endpoints
5. ✅ Deploy to production (Render/Railway)
6. ✅ Monitor scraper logs and performance
7. ✅ Optimize based on real-world usage

---

**Last Updated:** 2025-10-22
**Version:** 1.0
