#!/usr/bin/env python3
"""
Liquipedia League of Legends Results Scraper
Scrapes finished match results from Liquipedia for League of Legends tournaments
"""

import sys
import json
import requests
from datetime import datetime
from bs4 import BeautifulSoup

# Team name normalization map (Liquipedia name -> Database name/tag)
TEAM_NAME_MAP = {
    "Top Esports": "TES",
    "Bilibili Gaming": "BLG",
    "FlyQuest": "FLY",
    "Gen.G Esports": "GEN",
    "Gen.G": "GEN",
    "Hanwha Life": "HLE",
    "Hanwha Life Esports": "HLE",
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
    "T1": "T1",
    "SK Telecom T1": "T1",
    "Team Liquid": "TL",
    "Team SoloMid": "TSM",
    "TSM": "TSM",
    "CTBC Flying Oyster": "CFO",
    "Secret Whales": "TSW",
    "Anyone's Legend": "AL",
    "KT Rolster": "KT",
}

def normalize_team_name(team_name):
    """Normalize team name to match database names"""
    return TEAM_NAME_MAP.get(team_name, team_name)

def scrape_liquipedia_results():
    """
    Scrape finished League of Legends match results from Liquipedia
    Returns a list of matches with results (scores)
    """
    results = []

    # Liquipedia URLs to scrape
    urls = [
        "https://liquipedia.net/leagueoflegends/World_Championship/2025",
        "https://liquipedia.net/leagueoflegends/World_Championship/2025/Swiss_Stage",
        "https://liquipedia.net/leagueoflegends/Main_Page",  # For recent matches
    ]

    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
    }

    for url in urls:
        try:
            print(f"Scraping results from {url}...", file=sys.stderr)

            response = requests.get(url, headers=headers, timeout=10)
            response.raise_for_status()

            soup = BeautifulSoup(response.text, 'html.parser')

            # Find all match containers (using matchlist format)
            matches = soup.find_all('div', class_='brkts-matchlist-match')
            print(f"Found {len(matches)} matches on {url}", file=sys.stderr)

            for match in matches:
                try:
                    # Find team names - there should be 2 team name elements
                    team_names = match.find_all('span', class_='name')

                    # Find scores - look for score cells
                    score_cells = match.find_all('div', class_='brkts-matchlist-score')

                    if len(team_names) < 2 or len(score_cells) < 2:
                        continue

                    # Extract team 1 name (prefer non-mobile version)
                    team1_name = None
                    for span in team_names:
                        classes = span.get('class', [])
                        # Skip mobile abbreviations (visible-xs)
                        if 'visible-xs' not in classes:
                            team1_name = span.get_text(strip=True)
                            break
                    if not team1_name and len(team_names) > 0:
                        team1_name = team_names[0].get_text(strip=True)

                    # Extract team 2 name (prefer non-mobile version)
                    team2_name = None
                    remaining_names = team_names[1:] if team1_name else team_names
                    for span in remaining_names:
                        classes = span.get('class', [])
                        if 'visible-xs' not in classes:
                            team2_name = span.get_text(strip=True)
                            break
                    if not team2_name and len(team_names) > 1:
                        team2_name = team_names[-1].get_text(strip=True)

                    if not team1_name or not team2_name:
                        continue

                    # Extract scores from score cells
                    team1_score_elem = score_cells[0].find('div', class_='brkts-matchlist-cell-content')
                    team2_score_elem = score_cells[1].find('div', class_='brkts-matchlist-cell-content')

                    if not team1_score_elem or not team2_score_elem:
                        continue

                    team1_score_text = team1_score_elem.get_text(strip=True)
                    team2_score_text = team2_score_elem.get_text(strip=True)

                    # Parse scores - scores may be wrapped in <b> tags or plain text
                    try:
                        team1_score = int(team1_score_text)
                        team2_score = int(team2_score_text)
                    except ValueError:
                        # Skip if scores aren't valid integers
                        continue

                    # Only add matches with actual scores (finished matches)
                    if team1_score > 0 or team2_score > 0:
                        # Try to extract match timestamp
                        match_datetime = None
                        try:
                            # Find timer-object element with timestamp
                            timer = match.find('span', class_='timer-object')
                            if timer and timer.get('data-timestamp'):
                                unix_timestamp = int(timer.get('data-timestamp'))
                                match_datetime = datetime.fromtimestamp(unix_timestamp).isoformat()
                                print(f"Found timestamp {unix_timestamp} -> {match_datetime}", file=sys.stderr)
                        except Exception as e:
                            print(f"Could not extract timestamp: {e}", file=sys.stderr)

                        # Check if this result already exists in our list (avoid duplicates)
                        duplicate = False
                        for existing in results:
                            if (existing['team1'] == team1_name and existing['team2'] == team2_name and
                                existing['team1Score'] == team1_score and existing['team2Score'] == team2_score):
                                duplicate = True
                                break

                        if not duplicate:
                            # Normalize team names to match database
                            normalized_team1 = normalize_team_name(team1_name)
                            normalized_team2 = normalize_team_name(team2_name)

                            result_entry = {
                                "team1": normalized_team1,
                                "team2": normalized_team2,
                                "team1Score": team1_score,
                                "team2Score": team2_score,
                                "source": "liquipedia",
                                "scrapedAt": datetime.now().isoformat()
                            }

                            # Add match datetime if found
                            if match_datetime:
                                result_entry["matchDateTime"] = match_datetime

                            results.append(result_entry)

                            print(f"Scraped result: {team1_name} ({normalized_team1}) {team1_score}-{team2_score} {team2_name} ({normalized_team2}) at {match_datetime}", file=sys.stderr)

                except Exception as e:
                    print(f"Error processing match: {e}", file=sys.stderr)
                    continue

        except Exception as e:
            print(f"Error scraping {url}: {e}", file=sys.stderr)
            continue

    return results

def main():
    """Main function"""
    try:
        print("Starting Liquipedia results scraper...", file=sys.stderr)
        results = scrape_liquipedia_results()

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
