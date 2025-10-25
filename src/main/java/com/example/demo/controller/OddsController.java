package com.example.demo.controller;

import com.example.demo.model.dto.ResponseMessage;
import com.example.demo.services.OddsScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OddsController {

    @Autowired
    private OddsScraperService oddsScraperService;

    /**
     * Manual endpoint to trigger odds scraping
     * POST /odds/scrape
     */
    @PostMapping("/odds/scrape")
    public ResponseEntity<ResponseMessage> scrapeOdds() {
        try {
            int updated = oddsScraperService.scrapeAndUpdateOdds();
            return ResponseEntity.ok(new ResponseMessage(
                    "Odds scraping completed successfully. " + updated + " matches updated."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ResponseMessage("Error scraping odds: " + e.getMessage()));
        }
    }
}
