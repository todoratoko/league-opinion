-- ================================================================
-- CREATE LEAGUES AND TOURNAMENTS TABLES
-- ================================================================

-- Create leagues table
CREATE TABLE IF NOT EXISTS leagues (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,  -- LCK, LPL, LEC, LTA, LCP
    name VARCHAR(100) NOT NULL,         -- League of Legends Champions Korea
    short_name VARCHAR(50),             -- LCK
    region VARCHAR(50),                 -- Korea, China, Europe, Americas, Pacific
    logo VARCHAR(255),                  -- /images/leagues/lck.png
    description TEXT,
    website VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create tournaments table
CREATE TABLE IF NOT EXISTS tournaments (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,   -- worlds-2025, msi-2025, first-stand
    name VARCHAR(100) NOT NULL,         -- 2025 World Championship
    short_name VARCHAR(50),             -- Worlds 2025
    year INTEGER,                       -- 2025
    logo VARCHAR(255),                  -- /images/tournaments/worlds-2025.png
    start_date DATE,
    end_date DATE,
    location VARCHAR(100),              -- China
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert league data
INSERT INTO leagues (code, name, short_name, region, logo, description) VALUES
('LCK', 'League of Legends Champions Korea', 'LCK', 'Korea', '/images/leagues/lck.png', 'The premier professional League of Legends league in South Korea'),
('LPL', 'League of Legends Pro League', 'LPL', 'China', '/images/leagues/lpl.png', 'The premier professional League of Legends league in China'),
('LEC', 'League of Legends European Championship', 'LEC', 'Europe', '/images/leagues/lec.png', 'The premier professional League of Legends league in Europe, Middle East, and Africa'),
('LTA', 'League of Legends The Americas', 'LTA', 'Americas', '/images/leagues/lta.png', 'The premier professional League of Legends league covering North and South America'),
('LCP', 'League of Legends Championship Pacific', 'LCP', 'Pacific', '/images/leagues/lcp.png', 'The premier professional League of Legends league in the Asia-Pacific region')
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    short_name = EXCLUDED.short_name,
    region = EXCLUDED.region,
    logo = EXCLUDED.logo,
    description = EXCLUDED.description;

-- Insert tournament data
INSERT INTO tournaments (code, name, short_name, year, logo, start_date, end_date, location, description) VALUES
('worlds-2025', '2025 Season World Championship', 'Worlds 2025', 2025, '/images/tournaments/worlds-2025.png', '2025-10-14', '2025-11-09', 'China (Beijing, Shanghai, Chengdu)', 'The 15th iteration of the League of Legends World Championship'),
('msi-2025', '2025 Mid-Season Invitational', 'MSI 2025', 2025, '/images/tournaments/msi-2025.png', '2025-06-27', '2025-07-12', 'Vancouver, Canada', 'Mid-Season Invitational featuring the top teams from each region'),
('first-stand', '2025 First Stand', 'First Stand', 2025, '/images/tournaments/first-stand.png', '2025-03-01', '2025-03-15', 'Seoul, South Korea', 'The inaugural First Stand tournament featuring regional champions and Fearless Draft')
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
    short_name = EXCLUDED.short_name,
    year = EXCLUDED.year,
    logo = EXCLUDED.logo,
    start_date = EXCLUDED.start_date,
    end_date = EXCLUDED.end_date,
    location = EXCLUDED.location,
    description = EXCLUDED.description;

-- Verification queries
SELECT 'Leagues:' as info;
SELECT code, short_name, region, logo FROM leagues ORDER BY code;

SELECT '' as blank;
SELECT 'Tournaments:' as info;
SELECT code, short_name, year, location, logo FROM tournaments ORDER BY year, start_date;
