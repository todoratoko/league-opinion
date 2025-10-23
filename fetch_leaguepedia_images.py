#!/usr/bin/env python3
"""
Fetch team and player images from Leaguepedia
"""

import os
import urllib.request
import urllib.error
import psycopg2
import time
import re

# Database connection string
DB_CONNECTION = "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require"

# Local paths
TEAMS_DIR = "src/main/resources/static/images/teams"
PLAYERS_DIR = "src/main/resources/static/images/players"

# Leaguepedia base URL for images
LEAGUEPEDIA_IMG_BASE = "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images"

# Team logo mappings (from Leaguepedia)
TEAM_LOGOS = {
    'T1': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/7/78/T1logo_profile.png',
    'GEN': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/b/bb/Gen.Glogo_profile.png',
    'HLE': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/d/d0/Hanwha_Life_Esportslogo_profile.png',
    'KT': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/8/89/KT_Rolsterlogo_profile.png',
    'BLG': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/c/c4/Bilibili_Gaminglogo_profile.png',
    'AL': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/e/e3/Anyone%27s_Legendlogo_profile.png',
    'TES': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/3/3e/Top_Esportslogo_profile.png',
    'IG': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/e/e9/Invictus_Gaminglogo_profile.png',
    'G2': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/d/df/G2_Esportslogo_profile.png',
    'KOI': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/fd/KOIlogo_profile.png',
    'FNC': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/1/10/Fnaticlogo_profile.png',
    'FLY': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/0/0b/FlyQuestlogo_profile.png',
    '100T': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/b/b7/100_Thieveslogo_profile.png',
    'FUR': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/f8/FURIAlogo_profile.png',
    'VKS': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/47/Vivo_Keyd_Starslogo_profile.png',
    'CFO': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/5/51/CTBC_Flying_Oysterlogo_profile.png',
    'GAM': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/1/1e/GAM_Esportslogo_profile.png',
    'PSG': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/0/01/PSG_Talonlogo_profile.png',
    'TSW': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/1/19/Team_Secretlogo_profile.png'
}

# Player photo mappings (format: playername -> image URL)
PLAYER_PHOTOS = {
    # T1
    'Doran': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/9/96/T1_Doran_2025_Split_1.png',
    'Oner': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/2/2e/T1_Oner_2025_Split_1.png',
    'Faker': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/3/37/T1_Faker_2025_Split_1.png',
    'Gumayusi': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/fb/T1_Gumayusi_2025_Split_1.png',
    'Keria': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/b/bd/T1_Keria_2025_Split_1.png',

    # Gen.G
    'Kiin': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/2/29/Gen.G_Kiin_2025_Split_1.png',
    'Canyon': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/7/7c/Gen.G_Canyon_2025_Split_1.png',
    'Chovy': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/9/90/Gen.G_Chovy_2025_Split_1.png',
    'Ruler': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/e/e7/Gen.G_Ruler_2025_Split_1.png',
    'Duro': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/0/03/Gen.G_Duro_2025_Split_1.png',

    # HLE
    'Zeus': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/9/9a/HLE_Zeus_2025_Split_1.png',
    'Peanut': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/3/3c/HLE_Peanut_2025_Split_1.png',
    'Zeka': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4b/HLE_Zeka_2025_Split_1.png',
    'Viper': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/d/d7/HLE_Viper_2025_Split_1.png',
    'Delight': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/f5/HLE_Delight_2025_Split_1.png',

    # G2
    'BrokenBlade': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/5/54/G2_BrokenBlade_2025_Split_1.png',
    'SkewMond': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4e/G2_SkewMond_2025_Split_1.png',
    'Caps': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/d/dc/G2_Caps_2025_Split_1.png',
    'Hans Sama': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/0/03/G2_Hans_sama_2025_Split_1.png',
    'Labrov': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/5/59/G2_Labrov_2025_Split_1.png'
}

def download_image(url, save_path):
    """Download an image from URL and save it locally."""
    try:
        req = urllib.request.Request(
            url,
            headers={
                'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
            }
        )

        with urllib.request.urlopen(req, timeout=10) as response:
            with open(save_path, 'wb') as out_file:
                out_file.write(response.read())
        print(f"✓ Downloaded: {os.path.basename(save_path)}")
        return True
    except Exception as e:
        print(f"✗ Error downloading {os.path.basename(save_path)}: {str(e)}")
        return False

def main():
    print("=" * 70)
    print("Downloading Team Logos and Player Images from Leaguepedia")
    print("=" * 70)

    # Connect to database
    conn = psycopg2.connect(DB_CONNECTION)
    cursor = conn.cursor()

    # Download team logos
    print("\n📁 Downloading Team Logos...")
    print("-" * 70)
    cursor.execute("SELECT id, tag, name FROM teams ORDER BY name")
    teams = cursor.fetchall()

    team_updates = []
    for team_id, tag, name in teams:
        if tag in TEAM_LOGOS:
            url = TEAM_LOGOS[tag]
            filename = f"{tag.lower()}.png"
            save_path = os.path.join(TEAMS_DIR, filename)

            if download_image(url, save_path):
                local_path = f"/images/teams/{filename}"
                team_updates.append((local_path, team_id))
                time.sleep(0.5)
        else:
            print(f"⚠ No logo URL for {name} ({tag})")

    # Download player images (only for players we have URLs for)
    print("\n📁 Downloading Player Images...")
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
        if player_name in PLAYER_PHOTOS:
            url = PLAYER_PHOTOS[player_name]
            filename = f"{team_tag.lower()}-{player_name.lower().replace(' ', '-')}.png"
            save_path = os.path.join(PLAYERS_DIR, filename)

            if download_image(url, save_path):
                local_path = f"/images/players/{filename}"
                player_updates.append((local_path, player_id))
                time.sleep(0.5)
        else:
            print(f"⚠ No photo URL for {player_name} ({team_tag})")

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
    print("✓ Images downloaded successfully!")
    print(f"  - Team logos: {len(team_updates)}/{len(teams)}")
    print(f"  - Player photos: {len(player_updates)}/{len(players)}")
    print("=" * 70)

if __name__ == "__main__":
    main()
