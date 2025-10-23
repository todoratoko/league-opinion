# Team and Player Images

## Current Status

All teams and players now have **placeholder images** stored locally in the project.

### Image Locations

- **Team Logos**: `src/main/resources/static/images/teams/`
- **Player Photos**: `src/main/resources/static/images/players/`

### Database Paths

All image paths in the database point to the local images:
- Teams: `/images/teams/{tag}.png` (e.g., `/images/teams/t1.png`)
- Players: `/images/players/{tag}-{playername}.png` (e.g., `/images/players/t1-faker.png`)

## Image Inventory

### Teams (19 total)
- **Korea**: Gen.G, T1, HLE, KT Rolster
- **China**: BLG, Anyone's Legend, Top Esports, Invictus Gaming
- **Europe**: G2 Esports, Movistar KOI, Fnatic
- **Americas**: FlyQuest, 100 Thieves, FURIA, Vivo Keyd Stars
- **Asia Pacific**: CTBC Flying Oyster, GAM Esports, PSG Talon, Team Secret Whales

### Players (95 total)
5 players per team (Top, Jungle, Mid, ADC, Support)

## Replacing Placeholder Images

To replace placeholder images with real team logos and player photos:

### Option 1: Manual Replacement

1. Download real images from sources like:
   - [Leaguepedia](https://lol.fandom.com/)
   - [Liquipedia](https://liquipedia.net/leagueoflegends/)
   - Official team websites
   - Riot Games LoL Esports assets

2. Save with the correct filename:
   - Teams: `{tag}.png` (lowercase, e.g., `t1.png`, `gen.png`)
   - Players: `{tag}-{name}.png` (lowercase, e.g., `t1-faker.png`)

3. Replace the file in the appropriate directory
   - No database updates needed - paths stay the same!

### Option 2: Batch Download Script

Use the provided Python scripts to download images:

```bash
# Activate virtual environment
source venv/bin/activate

# Edit the script to add real image URLs
nano fetch_leaguepedia_images.py

# Run the script
python fetch_leaguepedia_images.py
```

### Option 3: Use Public APIs

Some public APIs that may have LoL Esports images:
- Riot Games Data Dragon API
- PandaScore API
- Leaguepedia/Gamepedia APIs

## Image Specifications

### Recommended Sizes
- **Team Logos**: 200x200px (square)
- **Player Photos**: 150x150px or 150x200px (portrait)

### Supported Formats
- PNG (preferred for transparency)
- JPG/JPEG
- WebP

## Current Placeholder Design

The placeholders are color-coded:
- Each team/player has a unique color based on their name
- Team logos show the team tag (e.g., "T1", "GEN")
- Player photos show the player name and role

## Need Help?

If you need help finding or downloading specific images, check:
1. Leaguepedia team pages: `https://lol.fandom.com/wiki/{Team_Name}`
2. Leaguepedia player pages: `https://lol.fandom.com/wiki/{Player_Name}`
3. Official team Twitter/social media accounts

## Scripts Available

- `create_placeholders.py` - Creates colored placeholder images
- `fetch_leaguepedia_images.py` - Downloads from Leaguepedia (needs URL updates)
- `download_images.py` - Generic download script

All scripts are in the project root directory.
