#!/usr/bin/env python3
"""
OddsPortal League of Legends Results Scraper
Scrapes finished match results from OddsPortal for League of Legends esports matches
"""

import sys
import json
import time
from datetime import datetime
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException

def setup_driver():
    """Setup Chrome driver with headless options"""
    chrome_options = Options()
    chrome_options.add_argument('--headless')
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    chrome_options.add_argument('--disable-blink-features=AutomationControlled')
    chrome_options.add_argument('user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36')

    driver = webdriver.Chrome(options=chrome_options)
    return driver

def scrape_lol_results():
    """
    Scrape finished League of Legends match results from OddsPortal
    Returns a list of matches with results (scores)
    """
    driver = None
    results = []

    # Results page URLs to scrape
    results_urls = [
        "https://www.oddsportal.com/esports/league-of-legends/results/",
        "https://www.oddsportal.com/esports/league-of-legends/league-of-legends-world-championship/results/",
    ]

    try:
        driver = setup_driver()

        for url in results_urls:
            print(f"Scraping results from {url}...", file=sys.stderr)

            driver.get(url)

            # Wait for page to load
            wait = WebDriverWait(driver, 10)
            time.sleep(5)  # Additional wait for dynamic content

            # Try to find match containers
            # OddsPortal shows finished matches with scores
            match_elements = driver.find_elements(By.CSS_SELECTOR, ".eventRow, .event-row, [class*='match'], [class*='event']")

            print(f"Found {len(match_elements)} potential match elements on results page", file=sys.stderr)

            for match_elem in match_elements[:30]:  # Get last 30 finished matches
                try:
                    # Extract team names
                    teams = match_elem.find_elements(By.CSS_SELECTOR, ".participant-name, .team-name, [class*='participant']")

                    if len(teams) >= 2:
                        team1_name = teams[0].text.strip()
                        team2_name = teams[1].text.strip()

                        # Skip if no team names
                        if not team1_name or not team2_name:
                            continue

                        # Try to extract score - OddsPortal shows scores in different formats
                        # Common formats: "2:1", "3-0", etc.
                        score_elem = match_elem.find_elements(By.CSS_SELECTOR, ".score, [class*='score'], [class*='result']")

                        team1_score = None
                        team2_score = None

                        if score_elem:
                            score_text = score_elem[0].text.strip()
                            print(f"Found score text: {score_text} for {team1_name} vs {team2_name}", file=sys.stderr)

                            # Parse score (formats: "2:1", "2-1", "2 - 1")
                            for separator in [':', '-', ' - ']:
                                if separator in score_text:
                                    parts = score_text.split(separator)
                                    if len(parts) == 2:
                                        try:
                                            team1_score = int(parts[0].strip())
                                            team2_score = int(parts[1].strip())
                                            break
                                        except ValueError:
                                            continue

                        # Only add if we found valid scores
                        if team1_score is not None and team2_score is not None:
                            # Try to get match date
                            date_elem = match_elem.find_elements(By.CSS_SELECTOR, ".date, .time, [class*='date']")
                            match_date = date_elem[0].text.strip() if date_elem else "Unknown"

                            results.append({
                                "team1": team1_name,
                                "team2": team2_name,
                                "team1Score": team1_score,
                                "team2Score": team2_score,
                                "date": match_date,
                                "source": "oddsportal",
                                "scrapedAt": datetime.now().isoformat()
                            })

                            print(f"Scraped result: {team1_name} {team1_score}-{team2_score} {team2_name}", file=sys.stderr)

                except Exception as e:
                    print(f"Error processing match element: {e}", file=sys.stderr)
                    continue

    except Exception as e:
        print(f"Error scraping OddsPortal results: {e}", file=sys.stderr)

    finally:
        if driver:
            driver.quit()

    return results

def main():
    """Main function"""
    try:
        print("Starting OddsPortal results scraper...", file=sys.stderr)
        results = scrape_lol_results()

        # Output JSON to stdout
        print(json.dumps({
            "success": True,
            "results": results,
            "count": len(results),
            "timestamp": datetime.now().isoformat()
        }))

    except Exception as e:
        print(json.dumps({
            "success": False,
            "error": str(e),
            "timestamp": datetime.now().isoformat()
        }))
        sys.exit(1)

if __name__ == "__main__":
    main()
