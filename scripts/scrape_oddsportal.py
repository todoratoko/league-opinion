#!/usr/bin/env python3
"""
OddsPortal League of Legends Odds Scraper
Scrapes match odds from OddsPortal for League of Legends esports matches
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

# Team name normalization map (OddsPortal name -> Database name/tag)
TEAM_NAME_MAP = {
    "Top Esports": "TES",
    "Bilibili Gaming": "BLG",
    "FlyQuest": "FLY",
    "Gen.G Esports": "GEN",
    "Gen.G": "GEN",
    "GEN": "GEN",
    "GENG": "GEN",
    "Hanwha Life": "HLE",
    "Hanwha Life Esports": "HLE",
    "HLE": "HLE",
    "LNG Esports": "LNG",
    "Weibo Gaming": "WBG",
    "MAD Lions KOI": "MDK",
    "KOI": "KOI",
    "Movistar KOI": "KOI",
    "Vikings Esports": "VKE",
    "Keyd Stars": "VKS",
    "PSG Talon": "PSG",
    "paiN Gaming": "PNG",
    "Rainbow7": "R7",
    "GAM Esports": "GAM",
    "100 Thieves": "100",
    "100T": "100",
    "SoftBank HAWKS": "SHG",
    "Cloud9": "C9",
    "Fnatic": "FNC",
    "G2 Esports": "G2",
    "G2": "G2",
    "T1": "T1",
    "SK Telecom T1": "T1",
    "Team Liquid": "TL",
    "TL": "TL",
    "Team SoloMid": "TSM",
    "TSM": "TSM",
    "CTBC Flying Oyster": "CFO",
    "Secret Whales": "TSW",
    "Anyone's Legend": "AL",
    "KT Rolster": "KT",
    "KT": "KT",
}

def normalize_team_name(team_name):
    """Normalize team name to match database names"""
    # First try direct mapping
    if team_name in TEAM_NAME_MAP:
        return TEAM_NAME_MAP[team_name]

    # Try case-insensitive matching
    for key, value in TEAM_NAME_MAP.items():
        if key.lower() == team_name.lower():
            return value

    # Return original name if no mapping found
    return team_name

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

def scrape_lol_odds():
    """
    Scrape League of Legends odds from OddsPortal
    Returns a list of matches with odds
    """
    driver = None
    matches = []

    # Tournament URLs to scrape
    tournament_urls = [
        "https://www.oddsportal.com/esports/league-of-legends/league-of-legends-world-championship/",
        "https://www.oddsportal.com/esports/league-of-legends/league-of-legends-emea-masters/"
    ]

    try:
        driver = setup_driver()

        for url in tournament_urls:
            print(f"Scraping {url}...", file=sys.stderr)

            driver.get(url)

            # Wait for page to load
            wait = WebDriverWait(driver, 10)
            time.sleep(5)  # Additional wait for dynamic content

            # Try to find match containers
            # Note: OddsPortal structure may change, these selectors might need updates
            match_elements = driver.find_elements(By.CSS_SELECTOR, ".eventRow, .event-row, [class*='match'], [class*='event']")

            print(f"Found {len(match_elements)} potential match elements on this page", file=sys.stderr)

            for match_elem in match_elements[:20]:  # Limit to first 20 matches per tournament
                try:
                    # Extract team names
                    teams = match_elem.find_elements(By.CSS_SELECTOR, ".participant-name, .team-name, [class*='participant']")

                    if len(teams) >= 2:
                        team1_name = teams[0].text.strip()
                        team2_name = teams[1].text.strip()

                        # Extract odds
                        odds_elements = match_elem.find_elements(By.CSS_SELECTOR, ".odds-value, [class*='odds']")

                        if len(odds_elements) >= 2:
                            try:
                                team1_odds = float(odds_elements[0].text.strip())
                                team2_odds = float(odds_elements[1].text.strip())

                                # Try to get match date/time
                                date_elem = match_elem.find_elements(By.CSS_SELECTOR, ".date, .time, [class*='date']")
                                match_date = date_elem[0].text.strip() if date_elem else "Unknown"

                                # Normalize team names to match database
                                normalized_team1 = normalize_team_name(team1_name)
                                normalized_team2 = normalize_team_name(team2_name)

                                matches.append({
                                    "team1": normalized_team1,
                                    "team2": normalized_team2,
                                    "team1Odds": team1_odds,
                                    "team2Odds": team2_odds,
                                    "date": match_date,
                                    "source": "oddsportal",
                                    "scrapedAt": datetime.now().isoformat()
                                })

                                print(f"Scraped: {team1_name} ({normalized_team1}) vs {team2_name} ({normalized_team2}) - {team1_odds}/{team2_odds}", file=sys.stderr)

                            except (ValueError, IndexError) as e:
                                print(f"Error parsing odds: {e}", file=sys.stderr)
                                continue

                except Exception as e:
                    print(f"Error processing match element: {e}", file=sys.stderr)
                    continue

    except Exception as e:
        print(f"Error scraping OddsPortal: {e}", file=sys.stderr)

    finally:
        if driver:
            driver.quit()

    return matches

def main():
    """Main function"""
    try:
        print("Starting OddsPortal scraper...", file=sys.stderr)
        matches = scrape_lol_odds()

        # Output JSON to stdout
        print(json.dumps({
            "success": True,
            "matches": matches,
            "count": len(matches),
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
