# Region Filter Feature Guide

## Overview

The teams page now includes a dynamic region filter that allows users to view teams by their competitive league (LCK, LPL, LEC, etc.).

## Features

### Filter Buttons

The page displays filter buttons with:
- **League emoji** (🇰🇷 for LCK, 🇨🇳 for LPL, etc.)
- **League name** (LCK, LPL, LEC, LCS, PCS, CBLOL)
- **Team count** in each region

### Available Filters

| Filter | League | Region | Teams |
|--------|--------|--------|-------|
| 🌍 All Teams | International | All | 19 |
| 🇰🇷 LCK | League of Legends Champions Korea | Korea | 4 |
| 🇨🇳 LPL | League of Legends Pro League | China | 4 |
| 🇪🇺 LEC | League of Legends EMEA Championship | Europe | 3 |
| 🌏 PCS | Pacific Championship Series | Southeast Asia | 4 |
| 🇺🇸 LCS | League of Legends Championship Series | North America | 2 |
| 🇧🇷 CBLOL | Campeonato Brasileiro de LoL | Brazil | 2 |

### Team Distribution

**LCK (Korea) - 4 teams:**
- T1
- Gen.G
- HLE (Hanwha Life Esports)
- KT Rolster

**LPL (China) - 4 teams:**
- BLG (Bilibili Gaming)
- TES (Top Esports)
- IG (Invictus Gaming)
- AL (Anyone's Legend)

**LEC (Europe) - 3 teams:**
- G2 Esports
- Fnatic
- Movistar KOI

**PCS (Southeast Asia) - 4 teams:**
- PSG Talon (Hong Kong)
- GAM Esports (Vietnam)
- CTBC Flying Oyster (Taiwan)
- Team Secret Whales (Philippines)

**LCS (North America) - 2 teams:**
- 100 Thieves
- FlyQuest

**CBLOL (Brazil) - 2 teams:**
- FURIA Esports
- Vivo Keyd Stars

## How It Works

### International vs Regional View

As you requested, teams appear in **both** contexts:

1. **"All Teams" filter** - Shows all 19 teams (International tournaments view)
   - This represents teams at MSI 2025 & Worlds 2025

2. **Regional filters (LCK, LPL, etc.)** - Show only teams from that region
   - Example: Click "🇰🇷 LCK" → See only T1, Gen.G, HLE, KT
   - Example: Click "🇨🇳 LPL" → See only BLG, TES, IG, AL

### User Interaction

1. **Click a filter button** to show only teams from that region
2. **Active filter** is highlighted in purple
3. **Team count** updates to show how many teams match the filter
4. **Team cards** display both:
   - League name (e.g., "LCK")
   - Full region (e.g., "Korea")

### Visual Indicators

- **Active button**: Purple background with white text
- **Inactive buttons**: White background with purple border
- **Hover effect**: Buttons lift slightly and change color
- **Team count badge**: Shows number of teams in each category

## Technical Implementation

### Region Mapping

The system automatically maps database regions to league names:

```javascript
const REGION_MAP = {
    'Korea': 'LCK',
    'China': 'LPL',
    'Europe': 'LEC',
    'North America': 'LCS',
    'Southeast Asia': 'PCS',
    'Brazil': 'CBLOL'
};
```

### Filter Logic

- **All Teams**: No filtering, shows all 19 teams
- **Regional Filter**: Filters teams where `team.region` matches the selected league's region

### Team Card Display

Each team card now shows:
```
[Team Logo]
Team Name
LCK • Korea    ← League • Region
[TAG]
```

## Usage Examples

### Example 1: View Korean Teams
1. Open the page
2. Click "🇰🇷 LCK (4)"
3. See only: T1, Gen.G, HLE, KT

### Example 2: View All International Teams
1. Click "🌍 All Teams (19)"
2. See all 19 teams from all regions

### Example 3: Compare Regions
1. Click "🇨🇳 LPL (4)" → See Chinese teams
2. Click "🇰🇷 LCK (4)" → See Korean teams
3. Notice: Both have 4 teams at international tournaments

## Benefits

✅ **Easy Navigation** - Find teams by region quickly
✅ **Regional Context** - Understand which league teams come from
✅ **International View** - See all tournament participants at once
✅ **Visual Clarity** - Color coding and emojis make filtering intuitive
✅ **Team Counts** - Know how many teams from each region

## Future Enhancements

Possible additions:
- Multi-region filter (show LCK + LPL at same time)
- Search by team name
- Sort by region, name, or tag
- Filter by player count or other attributes

## File Location

The filter is implemented in:
```
test-images.html
```

To integrate into your actual frontend:
1. Copy the filter CSS (lines 245-300)
2. Copy the filter HTML section
3. Copy the JavaScript filter functions
4. Adjust styling to match your design

---

**Enjoy exploring your teams by region!** 🎉
