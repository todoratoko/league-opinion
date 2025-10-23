#!/usr/bin/env python3
"""
Download team logos and player images from the database URLs
and save them locally in the project resources.
"""

import os
import urllib.request
import urllib.error
import psycopg2
from urllib.parse import urlparse
import time

# Database connection string
DB_CONNECTION = "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require"

# Local paths
TEAMS_DIR = "src/main/resources/static/images/teams"
PLAYERS_DIR = "src/main/resources/static/images/players"

def download_image(url, save_path):
    """Download an image from URL and save it locally."""
    try:
        # Add headers to mimic a browser request
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
    except urllib.error.HTTPError as e:
        print(f"✗ HTTP Error {e.code} for {url}")
        return False
    except urllib.error.URLError as e:
        print(f"✗ URL Error for {url}: {e.reason}")
        return False
    except Exception as e:
        print(f"✗ Error downloading {url}: {str(e)}")
        return False

def get_file_extension(url):
    """Extract file extension from URL."""
    parsed = urlparse(url)
    path = parsed.path
    ext = os.path.splitext(path)[1]
    if not ext or ext not in ['.png', '.jpg', '.jpeg', '.gif', '.webp']:
        return '.png'  # default to png
    return ext

def main():
    print("=" * 60)
    print("Downloading Team Logos and Player Images")
    print("=" * 60)

    # Connect to database
    conn = psycopg2.connect(DB_CONNECTION)
    cursor = conn.cursor()

    # Download team logos
    print("\n📁 Downloading Team Logos...")
    print("-" * 60)
    cursor.execute("SELECT id, tag, name, image FROM teams WHERE image IS NOT NULL ORDER BY name")
    teams = cursor.fetchall()

    team_updates = []
    for team_id, tag, name, image_url in teams:
        if not image_url:
            continue

        ext = get_file_extension(image_url)
        filename = f"{tag.lower()}{ext}"
        save_path = os.path.join(TEAMS_DIR, filename)

        if download_image(image_url, save_path):
            local_path = f"/images/teams/{filename}"
            team_updates.append((local_path, team_id))
            time.sleep(0.5)  # Be nice to the server

    # Download player images
    print("\n📁 Downloading Player Images...")
    print("-" * 60)
    cursor.execute("""
        SELECT p.id, p.name, p.image, t.tag
        FROM players p
        JOIN teams t ON p.team_id = t.id
        WHERE p.image IS NOT NULL
        ORDER BY t.name, p.name
    """)
    players = cursor.fetchall()

    player_updates = []
    for player_id, player_name, image_url, team_tag in players:
        if not image_url:
            continue

        ext = get_file_extension(image_url)
        # Create filename: teamtag-playername.ext (e.g., t1-faker.png)
        filename = f"{team_tag.lower()}-{player_name.lower().replace(' ', '-')}{ext}"
        save_path = os.path.join(PLAYERS_DIR, filename)

        if download_image(image_url, save_path):
            local_path = f"/images/players/{filename}"
            player_updates.append((local_path, player_id))
            time.sleep(0.5)  # Be nice to the server

    # Update database with local paths
    print("\n💾 Updating database with local paths...")
    print("-" * 60)

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

    print("\n" + "=" * 60)
    print("✓ All images downloaded successfully!")
    print(f"  - Teams: {len(team_updates)} logos")
    print(f"  - Players: {len(player_updates)} photos")
    print("=" * 60)

if __name__ == "__main__":
    main()
