#!/usr/bin/env python3
"""
Create placeholder images for teams and players with their names/tags
Also download available images from public APIs
"""

import os
import psycopg2
from PIL import Image, ImageDraw, ImageFont
import hashlib

# Database connection string
DB_CONNECTION = "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require"

# Local paths
TEAMS_DIR = "src/main/resources/static/images/teams"
PLAYERS_DIR = "src/main/resources/static/images/players"

def get_color_from_text(text):
    """Generate a consistent color based on text hash"""
    hash_value = int(hashlib.md5(text.encode()).hexdigest(), 16)
    hue = hash_value % 360

    # Convert HSL to RGB (keeping saturation and lightness moderate)
    h = hue / 360.0
    s = 0.6
    l = 0.5

    def hue_to_rgb(p, q, t):
        if t < 0: t += 1
        if t > 1: t -= 1
        if t < 1/6: return p + (q - p) * 6 * t
        if t < 1/2: return q
        if t < 2/3: return p + (q - p) * (2/3 - t) * 6
        return p

    if s == 0:
        r = g = b = l
    else:
        q = l * (1 + s) if l < 0.5 else l + s - l * s
        p = 2 * l - q
        r = hue_to_rgb(p, q, h + 1/3)
        g = hue_to_rgb(p, q, h)
        b = hue_to_rgb(p, q, h - 1/3)

    return (int(r * 255), int(g * 255), int(b * 255))

def create_team_logo(tag, name, filename):
    """Create a placeholder team logo with team tag"""
    size = (200, 200)
    img = Image.new('RGB', size, color=get_color_from_text(tag))
    draw = ImageDraw.Draw(img)

    try:
        # Try to use a nice font
        font_large = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 60)
        font_small = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 20)
    except:
        font_large = ImageFont.load_default()
        font_small = ImageFont.load_default()

    # Draw team tag
    bbox = draw.textbbox((0, 0), tag, font=font_large)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    x = (size[0] - text_width) // 2
    y = (size[1] - text_height) // 2 - 10

    # Add shadow
    draw.text((x+2, y+2), tag, font=font_large, fill=(0, 0, 0, 128))
    draw.text((x, y), tag, font=font_large, fill='white')

    img.save(filename)
    print(f"✓ Created logo: {os.path.basename(filename)}")

def create_player_photo(player_name, team_tag, role, filename):
    """Create a placeholder player photo"""
    size = (150, 150)
    img = Image.new('RGB', size, color=get_color_from_text(player_name))
    draw = ImageDraw.Draw(img)

    try:
        font_name = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 30)
        font_role = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 16)
    except:
        font_name = ImageFont.load_default()
        font_role = ImageFont.load_default()

    # Draw player name
    bbox = draw.textbbox((0, 0), player_name, font=font_name)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    x = (size[0] - text_width) // 2
    y = (size[1] - text_height) // 2 - 15

    # Add shadow for name
    draw.text((x+2, y+2), player_name, font=font_name, fill=(0, 0, 0, 128))
    draw.text((x, y), player_name, font=font_name, fill='white')

    # Draw role
    bbox = draw.textbbox((0, 0), role, font=font_role)
    text_width = bbox[2] - bbox[0]
    x = (size[0] - text_width) // 2
    y = size[1] - 35

    draw.text((x+1, y+1), role, font=font_role, fill=(0, 0, 0, 128))
    draw.text((x, y), role, font=font_role, fill='#FFD700')

    img.save(filename)
    print(f"✓ Created photo: {os.path.basename(filename)}")

def main():
    print("=" * 70)
    print("Creating Placeholder Images for Teams and Players")
    print("=" * 70)

    # Connect to database
    conn = psycopg2.connect(DB_CONNECTION)
    cursor = conn.cursor()

    # Create team logos
    print("\n📁 Creating Team Logos...")
    print("-" * 70)
    cursor.execute("SELECT id, tag, name FROM teams ORDER BY name")
    teams = cursor.fetchall()

    team_updates = []
    for team_id, tag, name in teams:
        filename = f"{tag.lower()}.png"
        save_path = os.path.join(TEAMS_DIR, filename)

        create_team_logo(tag, name, save_path)
        local_path = f"/images/teams/{filename}"
        team_updates.append((local_path, team_id))

    # Create player photos
    print("\n📁 Creating Player Photos...")
    print("-" * 70)
    cursor.execute("""
        SELECT p.id, p.name, p.role, t.tag
        FROM players p
        JOIN teams t ON p.team_id = t.id
        ORDER BY t.name, p.name
    """)
    players = cursor.fetchall()

    player_updates = []
    for player_id, player_name, role, team_tag in players:
        filename = f"{team_tag.lower()}-{player_name.lower().replace(' ', '-')}.png"
        save_path = os.path.join(PLAYERS_DIR, filename)

        create_player_photo(player_name, team_tag, role, save_path)
        local_path = f"/images/players/{filename}"
        player_updates.append((local_path, player_id))

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
    print("✓ All placeholder images created successfully!")
    print(f"  - Team logos: {len(team_updates)}")
    print(f"  - Player photos: {len(player_updates)}")
    print("\n📝 Note: Replace these placeholder images with real ones later")
    print("=" * 70)

if __name__ == "__main__":
    main()
