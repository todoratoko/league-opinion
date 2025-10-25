package com.example.demo.services;

import com.example.demo.model.entities.User;
import com.example.demo.model.repositories.UserRepository;
import com.example.demo.services.pandascore.PandaScoreSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CornJobs {
    private static final Logger logger = LoggerFactory.getLogger(CornJobs.class);

    @Autowired
    EmailService emailService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PandaScoreSyncService pandaScoreSyncService;
    @Autowired
    OddsScraperService oddsScraperService;

    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
    public void checkInactiveUsers() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        List<User> inactiveUsers = userRepository.findUserByLastLoginBefore(thirtyDaysAgo);
        for (User user: inactiveUsers) {
            emailService.sendEmailNotLoggedForMonth(user.getEmail());
        }
    }

    /**
     * Sync upcoming matches from PandaScore
     * Runs every 6 hours
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 6)  // Every 6 hours
    public void syncUpcomingMatches() {
        logger.info("Starting scheduled sync of upcoming matches from PandaScore");
        try {
            int synced = pandaScoreSyncService.syncUpcomingMatches(14);  // Sync next 14 days
            logger.info("Scheduled sync completed: {} matches synced", synced);
        } catch (Exception e) {
            logger.error("Error during scheduled match sync", e);
        }
    }

    /**
     * Scrape and update odds from OddsPortal
     * Runs every 4 hours
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 4)  // Every 4 hours
    public void scrapeOdds() {
        logger.info("Starting scheduled odds scraping from OddsPortal");
        try {
            int updated = oddsScraperService.scrapeAndUpdateOdds();
            logger.info("Scheduled odds scraping completed: {} matches updated", updated);
        } catch (Exception e) {
            logger.error("Error during scheduled odds scraping", e);
        }
    }

    /**
     * Sync currently running (live) matches from PandaScore
     * Runs every 2 minutes to keep live game status up to date
     */
    @Scheduled(fixedDelay = 1000 * 60 * 2)  // Every 2 minutes
    public void syncRunningMatches() {
        logger.info("Starting scheduled sync of running matches from PandaScore");
        try {
            int synced = pandaScoreSyncService.syncRunningMatches();
            logger.info("Scheduled running matches sync completed: {} matches synced", synced);
        } catch (Exception e) {
            logger.error("Error during scheduled running matches sync", e);
        }
    }

    //ToDo

//        @Scheduled(fixedDelay = 1000 * 60 * 60 * 24)
//        public static void checkIfMatchHasFinished () {
//            //  select matches where matchStartDateTime < now();
//            // set match to finishedMatch = true; ??
//        }




    }

