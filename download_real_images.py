#!/usr/bin/env python3
"""
Download real team logos and player photos from Leaguepedia (Fandom Wiki)
"""

import os
import urllib.request
import urllib.error
import psycopg2
import time
import re
from html.parser import HTMLParser

# Database connection string
DB_CONNECTION = "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require"

# Local paths
TEAMS_DIR = "src/main/resources/static/images/teams"
PLAYERS_DIR = "src/main/resources/static/images/players"

# Leaguepedia player page mapping (player name -> wiki page name)
PLAYER_PAGES = {
    # T1
    'Doran': 'Doran_(Choi_Hyeon-joon)',
    'Oner': 'Oner',
    'Faker': 'Faker',
    'Gumayusi': 'Gumayusi',
    'Keria': 'Keria',
    # Gen.G
    'Kiin': 'Kiin',
    'Canyon': 'Canyon',
    'Chovy': 'Chovy',
    'Ruler': 'Ruler',
    'Duro': 'Duro',
    # HLE
    'Zeus': 'Zeus',
    'Peanut': 'Peanut',
    'Zeka': 'Zeka_(Kim_Geon-woo)',
    'Viper': 'Viper_(Park_Do-hyeon)',
    'Delight': 'Delight',
    # KT
    'PerfecT': 'PerfecT_(Kim_Dae-yeop)',
    'Cuzz': 'Cuzz',
    'Bdd': 'Bdd',
    'deokdam': 'Deokdam',
    'Peter': 'Peter_(Jeong_Yoon-su)',
    # BLG
    'Bin': 'Bin_(Chen_Ze-Bin)',
    'Beichuan': 'XUN',  # Beichuan's game name
    'Knight': 'Knight_(Zhuo_Ding)',
    'Elk': 'Elk',
    'ON': 'ON',
    # AL
    'Flandre': 'Flandre',
    'Tarzan': 'Tarzan_(Lee_Seung-yong)',
    'Shanks': 'Shanks',
    'Hope': 'Hope_(Wang_Jie)',
    'Kael': 'Kael_(Kim_Jin-hong)',
    # TES
    '369': '369',
    'Kanavi': 'Kanavi',
    'Creme': 'Creme',
    'JackeyLove': 'JackeyLove',
    'Hang': 'Hang',
    # IG
    'TheShy': 'TheShy',
    'Wei': 'Wei_(Yan_Yang-Wei)',
    'Rookie': 'Rookie',
    'GALA': 'GALA',
    'Meiko': 'Meiko',
    # G2
    'BrokenBlade': 'BrokenBlade',
    'SkewMond': 'SkewMond',
    'Caps': 'Caps',
    'Hans Sama': 'Hans_Sama',
    'Labrov': 'Labrov',
    # KOI
    'Myrwn': 'Myrwn',
    'Elyoya': 'Elyoya',
    'Jojopyun': 'Jojopyun',
    'Supa': 'Supa',
    'Alvaro': 'Alvaro_(Álvaro_Fernández)',
    # FNC
    'Oscarinin': 'Oscarinin',
    'Razork': 'Razork',
    'Poby': 'Poby',
    'Upset': 'Upset',
    'Mikyx': 'Mikyx',
    # FLY
    'Bwipo': 'Bwipo',
    'Inspired': 'Inspired',
    'Quad': 'Quad',
    'Massu': 'Massu',
    'Busio': 'Busio',
    # 100T
    'Dhokla': 'Dhokla',
    'River': 'River_(Kim_Dong-woo)',
    'Quid': 'Quid',
    'FBI': 'FBI',
    'Eyla': 'Eyla',
    # PSG
    'Azhi': 'Azhi',
    'Karsa': 'Karsa',
    'Maple': 'Maple_(Huang_Yi-Tang)',
    'Betty': 'Betty',
    'Woody': 'Woody',
    # GAM
    'Kiaya': 'Kiaya',
    'Levi': 'Levi',
    'Emo': 'Emo',
    'Aress': 'Aress',
    'Artemis': 'Artemis_(Trần_Quốc_Hưng)',
    # CFO
    'Driver': 'Driver',
    'Rest': 'Rest',
    'JunJia': 'JunJia',
    'HongQ': 'HongQ',
    'Doggo': 'Doggo',
    # VKS (Vivo Keyd Stars)
    'Boal': 'Boal',
    'Disamis': 'Disamis',
    'Mireu': 'Mireu_(Vinícius_Fernandes)',
    # TSW (Team Secret Whales)
    'Steller': 'Steller',
    'Hizto': 'Hizto',
    'Dire': 'Dire',
    'Eddie': 'Eddie',
    'Taki': 'Taki_(Đinh_Anh_Tài)',
    # FUR (FURIA)
    'Guigo': 'Guigo',
    'Tatu': 'Tatu_(Pedro_Seixas)',
    'Tutsz': 'Tutsz',
    'Ayu': 'Ayu_(Andrey_Saraiva)',
    'JoJo': 'JoJo_(Gabriel_Dzelme)'
}

# Team page mapping
TEAM_PAGES = {
    'T1': 'T1',
    'GEN': 'Gen.G',
    'HLE': 'Hanwha_Life_Esports',
    'KT': 'KT_Rolster',
    'BLG': 'Bilibili_Gaming',
    'AL': 'Anyone%27s_Legend',
    'TES': 'Top_Esports',
    'IG': 'Invictus_Gaming',
    'G2': 'G2_Esports',
    'KOI': 'Movistar_KOI',
    'FNC': 'Fnatic',
    'FLY': 'FlyQuest',
    '100T': '100_Thieves',
    'FUR': 'FURIA_Esports',
    'VKS': 'Vivo_Keyd_Stars',
    'CFO': 'CTBC_Flying_Oyster',
    'GAM': 'GAM_Esports',
    'PSG': 'PSG_Talon',
    'TSW': 'Team_Secret'
}

class ImageExtractor(HTMLParser):
    """Extract image URLs from HTML"""
    def __init__(self):
        super().__init__()
        self.image_url = None
        self.looking_for_logo = False

    def handle_starttag(self, tag, attrs):
        attrs_dict = dict(attrs)
        if tag == 'img' or tag == 'a':
            # Look for image with specific patterns
            if 'src' in attrs_dict:
                src = attrs_dict['src']
                # Check if it's a player photo or team logo
                if 'lolesports_gamepedia_en/images' in src:
                    if ('logo_profile.png' in src or
                        '_2025_Split' in src or
                        '_2024_Split' in src):
                        if not self.image_url:  # Take first match
                            self.image_url = src
            elif 'href' in attrs_dict:
                href = attrs_dict['href']
                if 'lolesports_gamepedia_en/images' in href and '.png' in href:
                    if not self.image_url:
                        self.image_url = href

def fetch_page(url):
    """Fetch a webpage"""
    try:
        # URL encode the path to handle Unicode characters
        from urllib.parse import quote
        # Split URL and encode only the path part
        if '/wiki/' in url:
            base = url.split('/wiki/')[0] + '/wiki/'
            path = url.split('/wiki/')[1]
            url = base + quote(path, safe='()_-')

        req = urllib.request.Request(
            url,
            headers={'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'}
        )
        with urllib.request.urlopen(req, timeout=15) as response:
            return response.read().decode('utf-8')
    except Exception as e:
        print(f"✗ Error fetching page {url}: {str(e)}")
        return None

def extract_image_from_page(html):
    """Extract image URL from HTML page"""
    if not html:
        return None

    parser = ImageExtractor()
    parser.feed(html)

    if parser.image_url:
        # Make sure it's a full URL
        url = parser.image_url
        if url.startswith('//'):
            url = 'https:' + url
        elif url.startswith('/'):
            url = 'https://static.wikia.nocookie.net' + url

        # Get the latest revision
        if '/revision/latest' not in url:
            # Extract base URL and add revision/latest
            base_url = url.split('/revision/')[0] if '/revision/' in url else url
            url = base_url + '/revision/latest'

        return url
    return None

def download_image(url, save_path):
    """Download an image from URL and save it locally."""
    try:
        req = urllib.request.Request(
            url,
            headers={'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'}
        )
        with urllib.request.urlopen(req, timeout=15) as response:
            with open(save_path, 'wb') as out_file:
                out_file.write(response.read())
        print(f"✓ Downloaded: {os.path.basename(save_path)}")
        return True
    except Exception as e:
        print(f"✗ Error downloading {os.path.basename(save_path)}: {str(e)}")
        return False

def main():
    print("=" * 70)
    print("Downloading Real Team Logos and Player Photos from Leaguepedia")
    print("=" * 70)

    # Connect to database
    conn = psycopg2.connect(DB_CONNECTION)
    cursor = conn.cursor()

    # Download team logos
    print("\n📁 Downloading Team Logos from Leaguepedia...")
    print("-" * 70)
    cursor.execute("SELECT id, tag, name FROM teams ORDER BY name")
    teams = cursor.fetchall()

    team_updates = []
    for team_id, tag, name in teams:
        if tag in TEAM_PAGES:
            wiki_page = TEAM_PAGES[tag]
            url = f"https://lol.fandom.com/wiki/{wiki_page}"

            print(f"Fetching {name} ({tag})...")
            html = fetch_page(url)
            image_url = extract_image_from_page(html)

            if image_url:
                filename = f"{tag.lower()}.png"
                save_path = os.path.join(TEAMS_DIR, filename)

                if download_image(image_url, save_path):
                    local_path = f"/images/teams/{filename}"
                    team_updates.append((local_path, team_id))
                    time.sleep(1)  # Be nice to the server
            else:
                print(f"⚠ Could not find logo for {name}")
        else:
            print(f"⚠ No wiki page mapping for {name} ({tag})")

    # Download player images (for players we have wiki pages for)
    print("\n📁 Downloading Player Images from Leaguepedia...")
    print("-" * 70)
    cursor.execute("""
        SELECT p.id, p.name, t.tag
        FROM players p
        JOIN teams t ON p.team_id = t.id
        ORDER BY t.name, p.name
    """)
    players = cursor.fetchall()

    player_updates = []
    for player_id, player_name, team_tag in players:
        if player_name in PLAYER_PAGES:
            wiki_page = PLAYER_PAGES[player_name]
            url = f"https://lol.fandom.com/wiki/{wiki_page}"

            print(f"Fetching {player_name} ({team_tag})...")
            html = fetch_page(url)
            image_url = extract_image_from_page(html)

            if image_url:
                filename = f"{team_tag.lower()}-{player_name.lower().replace(' ', '-')}.png"
                save_path = os.path.join(PLAYERS_DIR, filename)

                if download_image(image_url, save_path):
                    local_path = f"/images/players/{filename}"
                    player_updates.append((local_path, player_id))
                    time.sleep(1)  # Be nice to the server
            else:
                print(f"⚠ Could not find photo for {player_name}")
        else:
            print(f"⚠ No wiki page mapping for {player_name} ({team_tag})")

    # Update database with local paths
    print("\n💾 Updating database with local paths...")
    print("-" * 70)

    for local_path, team_id in team_updates:
        cursor.execute("UPDATE teams SET image = %s WHERE id = %s", (local_path, team_id))
    print(f"✓ Updated {len(team_updates)} team logos in database")

    for local_path, player_id in player_updates:
        cursor.execute("UPDATE players SET image = %s WHERE id = %s", (local_path, player_id))
    print(f"✓ Updated {len(player_updates)} player images in database")

    # Commit changes
    conn.commit()
    cursor.close()
    conn.close()

    print("\n" + "=" * 70)
    print("✓ Real images downloaded successfully!")
    print(f"  - Team logos: {len(team_updates)}/{len(teams)}")
    print(f"  - Player photos: {len(player_updates)}/{len(players)}")
    print("=" * 70)

if __name__ == "__main__":
    main()
