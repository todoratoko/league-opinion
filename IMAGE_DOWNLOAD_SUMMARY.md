# Team Logos and Player Photos - Download Summary

## ✅ Mission Accomplished!

Successfully downloaded **real team logos and player photos** from Leaguepedia for your League of Legends opinion website.

## 📊 Final Statistics

### Images Downloaded
- **Team Logos**: 18/19 (95% real images from Leaguepedia)
- **Player Photos**: 66/95 (69% real images from Leaguepedia)
- **Total Storage**: 11MB

### Database Status
- All 19 teams have image paths configured
- All 95 players have image paths configured
- Images are served from local paths (`/images/teams/` and `/images/players/`)

## 🎯 What Was Done

### 1. Research Phase
- Investigated Riot Games official APIs (no longer publicly available for esports)
- Discovered Leaguepedia (Fandom Wiki) as the best community source
- Found image URL patterns: `static.wikia.nocookie.net/lolesports_gamepedia_en/images/`

### 2. Image Sources
All images were scraped from:
- **Leaguepedia Team Pages** - Official team logos
- **Leaguepedia Player Pages** - 2025 season player photos

### 3. Download Process
Created `download_real_images.py` that:
- Fetches Leaguepedia pages for each team and player
- Extracts image URLs using HTML parsing
- Downloads images to local directories
- Updates database with local image paths
- Respects rate limits (1 second delay between requests)

## 📁 Image Locations

### Directory Structure
```
src/main/resources/static/images/
├── teams/          (19 team logos)
│   ├── t1.png
│   ├── gen.png
│   ├── g2.png
│   └── ...
└── players/        (95 player photos)
    ├── t1-faker.png
    ├── gen-chovy.png
    ├── g2-caps.png
    └── ...
```

### Database Paths
- Teams: `/images/teams/{tag}.png`
- Players: `/images/players/{tag}-{playername}.png`

## 🖼️ Image Quality

### Real Images (From Leaguepedia)
**18 Team Logos:**
- T1, Gen.G, HLE, KT Rolster (Korea)
- BLG, AL, TES, IG (China)
- G2, KOI, FNC (Europe)
- FLY, 100T, FUR, VKS (Americas)
- CFO, GAM, PSG (Asia Pacific)

**66 Player Photos:**
Major players from top regions including:
- **LCK**: Faker, Chovy, Canyon, Kiin, Ruler, Zeus, Peanut, Viper, etc.
- **LPL**: JackeyLove, Knight, Bin, TheShy, Rookie, GALA, 369, Kanavi, etc.
- **LEC**: Caps, BrokenBlade, Upset, Mikyx, Razork, etc.
- **LTA**: Bwipo, Inspired, FBI, Quid, Dhokla, etc.
- **LCP**: Levi, Karsa, Betty, Doggo, etc.

### Placeholder Images (29 players)
Players without 2025 photos on Leaguepedia have color-coded placeholders showing:
- Player name
- Role (Top, Jungle, Mid, ADC, Support)
- Unique color per player

Mainly from:
- FURIA (5 players)
- Vivo Keyd Stars (5 players)
- Team Secret Whales (5 players)
- Some individual players with missing/outdated wiki pages

## 🔧 Scripts Available

### `download_real_images.py`
Main script that downloads images from Leaguepedia.
- Maps 65+ players to their Leaguepedia pages
- Scrapes team pages for logos
- Extracts image URLs from HTML
- Downloads and saves locally
- Updates database automatically

**Usage:**
```bash
source venv/bin/activate
python download_real_images.py
```

### `create_placeholders.py`
Creates colored placeholder images for missing photos.
- Already run - placeholders created for all players
- Can be re-run if needed

### `fetch_leaguepedia_images.py`
Earlier version with hardcoded URLs (superseded by `download_real_images.py`)

## 🌐 Image Sources

### Leaguepedia (Fandom Wiki)
- **Base URL**: `https://lol.fandom.com/wiki/`
- **Image CDN**: `https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/`
- **License**: Community-maintained, publicly accessible
- **Update Frequency**: Updated with each split/tournament

### Player Photo Naming Convention
`{Team}_{Player}_{Year}_Split_{Number}.png`

Examples:
- `T1_Faker_2025_Split_3.png`
- `GEN_Chovy_2025_Split_1.png`
- `G2_Caps_2025_Split_3.png`

### Team Logo Naming Convention
`{TeamName}logo_profile.png`

Examples:
- `T1logo_profile.png`
- `Gen.Glogo_profile.png`
- `G2_Esportslogo_profile.png`

## 🚀 Future Updates

To download updated images in the future:

### Option 1: Re-run the Script
```bash
source venv/bin/activate
python download_real_images.py
```

### Option 2: Add New Player Mappings
Edit `download_real_images.py` and add to `PLAYER_PAGES`:
```python
'NewPlayer': 'NewPlayer',  # or 'NewPlayer_(Disambiguation)'
```

### Option 3: Manual Download
1. Find player on Leaguepedia: `https://lol.fandom.com/wiki/{PlayerName}`
2. Right-click player photo → Save image
3. Rename to `{team}-{player}.png` (lowercase)
4. Place in `src/main/resources/static/images/players/`

## ⚠️ Important Notes

### Copyright
- Images from Leaguepedia are community-contributed
- Used for a non-commercial educational project
- Original image credits belong to Riot Games and team organizations

### Rate Limiting
- Script includes 1-second delays between requests
- Respects Fandom/Wikia servers
- Don't run too frequently to avoid being blocked

### Missing Images
29 players still have placeholders because:
- No Leaguepedia page exists
- 2025 photos not uploaded yet
- Player from very small region with limited coverage
- Name disambiguation issues

These will remain as colored placeholders until:
1. Leaguepedia pages are created/updated
2. Manual images are added

## 📝 Maintenance

### Keeping Images Up-to-Date

**Before Each Split:**
- Re-run `download_real_images.py` to get latest photos
- Check for roster changes in database
- Update player mappings if players changed names/teams

**Adding New Teams:**
1. Add to `TEAM_PAGES` mapping in script
2. Add players to `PLAYER_PAGES` mapping
3. Run download script

## ✨ Summary

Your League Opinion website now has:
- ✅ 18 professional team logos from official sources
- ✅ 66 professional player photos from 2025 season
- ✅ 29 stylish placeholder images for remaining players
- ✅ All images stored locally (11MB total)
- ✅ Database fully configured with image paths
- ✅ Automated download script for future updates

The images are ready to display on your website! 🎉

---

*Images sourced from Leaguepedia (lol.fandom.com)*
*Last updated: October 23, 2025*
