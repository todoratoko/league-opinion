-- ================================================================
-- UPDATE DATABASE WITH PROPER 2025 ESPORTS STRUCTURE
-- ================================================================
--
-- 2025 Tier 1 Structure:
-- 5 Major Regions: LCK, LPL, LEC, LTA, LCP
-- 3 International Tournaments: First Stand, MSI, Worlds
--
-- ================================================================

-- Add new columns to teams table (if they don't exist)
ALTER TABLE teams ADD COLUMN IF NOT EXISTS league VARCHAR(10);
ALTER TABLE teams ADD COLUMN IF NOT EXISTS league_full_name VARCHAR(100);
ALTER TABLE teams ADD COLUMN IF NOT EXISTS sub_region VARCHAR(50);
ALTER TABLE teams ADD COLUMN IF NOT EXISTS tournaments TEXT[];

-- ================================================================
-- LCK (LEAGUE OF LEGENDS CHAMPIONS KOREA)
-- ================================================================

UPDATE teams SET
    league = 'LCK',
    league_full_name = 'League of Legends Champions Korea',
    sub_region = 'Korea',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'T1';

UPDATE teams SET
    league = 'LCK',
    league_full_name = 'League of Legends Champions Korea',
    sub_region = 'Korea',
    tournaments = ARRAY['First Stand', 'MSI 2025', 'Worlds 2025']
WHERE tag = 'GEN';

UPDATE teams SET
    league = 'LCK',
    league_full_name = 'League of Legends Champions Korea',
    sub_region = 'Korea',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'HLE';

UPDATE teams SET
    league = 'LCK',
    league_full_name = 'League of Legends Champions Korea',
    sub_region = 'Korea',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'KT';

-- ================================================================
-- LPL (LEAGUE OF LEGENDS PRO LEAGUE - CHINA)
-- ================================================================

UPDATE teams SET
    league = 'LPL',
    league_full_name = 'League of Legends Pro League',
    sub_region = 'China',
    tournaments = ARRAY['MSI 2025', 'Worlds 2025']
WHERE tag = 'BLG';

UPDATE teams SET
    league = 'LPL',
    league_full_name = 'League of Legends Pro League',
    sub_region = 'China',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'TES';

UPDATE teams SET
    league = 'LPL',
    league_full_name = 'League of Legends Pro League',
    sub_region = 'China',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'IG';

UPDATE teams SET
    league = 'LPL',
    league_full_name = 'League of Legends Pro League',
    sub_region = 'China',
    tournaments = ARRAY['MSI 2025', 'Worlds 2025']
WHERE tag = 'AL';

-- ================================================================
-- LEC (LEAGUE OF LEGENDS EUROPEAN CHAMPIONSHIP)
-- ================================================================

UPDATE teams SET
    league = 'LEC',
    league_full_name = 'League of Legends European Championship',
    sub_region = 'Europe',
    tournaments = ARRAY['MSI 2025', 'Worlds 2025']
WHERE tag = 'G2';

UPDATE teams SET
    league = 'LEC',
    league_full_name = 'League of Legends European Championship',
    sub_region = 'Europe',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'FNC';

UPDATE teams SET
    league = 'LEC',
    league_full_name = 'League of Legends European Championship',
    sub_region = 'Europe',
    tournaments = ARRAY['MSI 2025', 'Worlds 2025']
WHERE tag = 'KOI';

-- ================================================================
-- LTA (LEAGUE OF LEGENDS THE AMERICAS)
-- North America + Latin America + Brazil
-- ================================================================

UPDATE teams SET
    league = 'LTA',
    league_full_name = 'League of Legends The Americas',
    sub_region = 'North America',
    tournaments = ARRAY['MSI 2025', 'Worlds 2025']
WHERE tag = 'FLY';

UPDATE teams SET
    league = 'LTA',
    league_full_name = 'League of Legends The Americas',
    sub_region = 'North America',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = '100T';

UPDATE teams SET
    league = 'LTA',
    league_full_name = 'League of Legends The Americas',
    sub_region = 'South America (Brazil)',
    tournaments = ARRAY['MSI 2025']
WHERE tag = 'FUR';

UPDATE teams SET
    league = 'LTA',
    league_full_name = 'League of Legends The Americas',
    sub_region = 'South America (Brazil)',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'VKS';

-- ================================================================
-- LCP (LEAGUE OF LEGENDS CHAMPIONSHIP PACIFIC)
-- Southeast Asia + Vietnam + Japan + Oceania
-- ================================================================

UPDATE teams SET
    league = 'LCP',
    league_full_name = 'League of Legends Championship Pacific',
    sub_region = 'Taiwan',
    tournaments = ARRAY['MSI 2025', 'Worlds 2025']
WHERE tag = 'CFO';

UPDATE teams SET
    league = 'LCP',
    league_full_name = 'League of Legends Championship Pacific',
    sub_region = 'Vietnam',
    tournaments = ARRAY['MSI 2025']
WHERE tag = 'GAM';

UPDATE teams SET
    league = 'LCP',
    league_full_name = 'League of Legends Championship Pacific',
    sub_region = 'Hong Kong',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'PSG';

UPDATE teams SET
    league = 'LCP',
    league_full_name = 'League of Legends Championship Pacific',
    sub_region = 'Philippines',
    tournaments = ARRAY['Worlds 2025']
WHERE tag = 'TSW';

-- ================================================================
-- VERIFICATION QUERIES
-- ================================================================

-- View teams by league
SELECT league, COUNT(*) as team_count
FROM teams
GROUP BY league
ORDER BY team_count DESC;

-- View all teams with proper structure
SELECT
    tag,
    name,
    league,
    sub_region,
    array_to_string(tournaments, ', ') as tournaments
FROM teams
ORDER BY league, tag;

-- View teams by tournament participation
SELECT
    unnest(tournaments) as tournament,
    COUNT(*) as team_count
FROM teams
GROUP BY tournament
ORDER BY team_count DESC;
