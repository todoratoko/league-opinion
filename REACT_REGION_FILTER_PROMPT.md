# Prompt for Claude Code: React Region Filter Implementation

Copy and paste this prompt to Claude Code:

---

## Task: Add Region Filter to Teams Page in React Frontend

I need you to add a region filter feature to my React Teams page. The backend API is already working and returns all teams with their regions.

## Backend API Details

**Endpoint:** `http://localhost:8081/teams`

**Response Format:**
```json
[
  {
    "id": 1,
    "name": "T1",
    "fullName": "T1 Esports",
    "tag": "T1",
    "region": "Korea",
    "country": "South Korea",
    "image": "/images/teams/t1.png",
    "twitter": "@T1LoL",
    "players": [...]
  },
  ...
]
```

## Region to League Mapping

Map the database regions to competitive league names:

```javascript
const REGION_MAP = {
    'Korea': 'LCK',           // League of Legends Champions Korea
    'China': 'LPL',           // League of Legends Pro League
    'Europe': 'LEC',          // League of Legends EMEA Championship
    'North America': 'LCS',   // League of Legends Championship Series
    'Southeast Asia': 'PCS',  // Pacific Championship Series
    'Brazil': 'CBLOL'         // Campeonato Brasileiro de LoL
};
```

## Current Team Distribution

- **LCK (Korea):** 4 teams - T1, Gen.G, HLE, KT
- **LPL (China):** 4 teams - BLG, TES, IG, AL
- **LEC (Europe):** 3 teams - G2, FNC, KOI
- **PCS (Southeast Asia):** 4 teams - PSG, GAM, CFO, TSW
- **LCS (North America):** 2 teams - 100T, FLY
- **CBLOL (Brazil):** 2 teams - FUR, VKS
- **Total:** 19 teams

## Requirements

### 1. Filter Component
Create a filter section ABOVE the teams grid with these buttons:
- **🌍 All Teams (19)** - Default, shows all teams
- **🇰🇷 LCK (4)** - Shows only Korean teams
- **🇨🇳 LPL (4)** - Shows only Chinese teams
- **🇪🇺 LEC (3)** - Shows only European teams
- **🌏 PCS (4)** - Shows only Southeast Asian teams
- **🇺🇸 LCS (2)** - Shows only North American teams
- **🇧🇷 CBLOL (2)** - Shows only Brazilian teams

Each button should show:
- League emoji (flag or regional symbol)
- League name (LCK, LPL, etc.)
- Team count in that region

### 2. Visual Design

**Filter Buttons:**
- Display horizontally in a row (flex-wrap for mobile)
- Active button: Purple/blue background with white text
- Inactive buttons: White background with purple border
- Hover effect: Slight scale up and background color change
- Team count badge displayed on each button

**Team Cards:**
Display both league AND region:
```
[Team Logo]
Gen.G
LCK • Korea    ← Show "LCK • Korea" instead of just "Korea"
GEN
```

### 3. Functionality

**Filter Behavior:**
1. On page load, show "All Teams" (all 19 teams)
2. When clicking a filter button:
   - Update the active button styling
   - Filter teams where `team.region` maps to the selected league
   - Re-render the teams grid with filtered results
   - If no teams match, show "No teams found" message

**State Management:**
- Store all teams in state (from API)
- Store current filter selection (default: "All")
- Dynamically calculate team counts per region

**Important:** Teams should appear in BOTH contexts:
- "All Teams" = International view (all 19 teams)
- Regional filter (e.g., "LCK") = Only teams from that region

### 4. Implementation Approach

**Suggested Component Structure:**
```
TeamsPage
  ├── RegionFilter (new component)
  │     ├── Filter buttons with counts
  │     └── Click handlers
  └── TeamsGrid
        └── TeamCard (update to show league • region)
```

**Filter Logic:**
```javascript
const filterTeams = (teams, selectedFilter) => {
  if (selectedFilter === 'All') return teams;

  return teams.filter(team => {
    const league = REGION_MAP[team.region] || team.region;
    return league === selectedFilter;
  });
};
```

**Dynamic Count Calculation:**
```javascript
const getRegionCounts = (teams) => {
  const counts = { 'All': teams.length };

  teams.forEach(team => {
    const league = REGION_MAP[team.region] || team.region;
    counts[league] = (counts[league] || 0) + 1;
  });

  return counts;
};
```

## Reference Implementation

I have a working vanilla JavaScript implementation in `test-images.html` at these locations:
- **CSS Styles:** Lines 245-300 (filter-section, filter-btn classes)
- **Filter Creation:** Lines 383-426 (createFilterButtons function)
- **Filter Logic:** Lines 428-449 (filterTeams function)
- **Display Update:** Lines 451-477 (displayTeams function with league mapping)

You can reference this implementation but convert it to React patterns (useState, props, JSX, etc.).

## Expected User Experience

1. User opens Teams page
2. Sees all 19 teams with filter buttons above
3. Clicks "🇰🇷 LCK (4)" button
4. Page shows only 4 Korean teams: T1, Gen.G, HLE, KT
5. Button shows active state (purple background)
6. Team cards show "LCK • Korea"
7. Clicks "🌍 All Teams (19)" to see all teams again

## Styling Preferences

- Use Tailwind CSS if available, otherwise CSS modules or styled-components
- Purple/blue accent color (#667eea) for active states
- Clean, modern design matching the existing UI
- Responsive: Stack filter buttons vertically on mobile
- Smooth transitions (0.3s) for hover and active states

## Additional Notes

- Cache the full teams list to avoid re-fetching when filtering
- Sort filter buttons by team count (descending)
- Make filter buttons keyboard accessible (tab navigation)
- Consider adding a search bar in the future (leave space for it)

## Testing

After implementation, verify:
1. All 19 teams load on initial page load
2. Each filter button shows correct count
3. Clicking LCK shows only 4 Korean teams
4. Clicking All Teams shows all 19 teams again
5. Active button styling updates correctly
6. Team cards display "LCK • Korea" format
7. Works on mobile (responsive)

---

Please implement this feature in the Teams page/component of my React frontend. Let me know if you need any clarification about the API response format or design requirements.
