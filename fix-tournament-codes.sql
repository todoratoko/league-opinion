-- Fix tournament references in teams table to use tournament codes instead of short names
-- This ensures frontend filtering works correctly

-- Replace "Worlds 2025" with "worlds-2025"
UPDATE teams
SET tournaments = array_replace(tournaments, 'Worlds 2025', 'worlds-2025')
WHERE 'Worlds 2025' = ANY(tournaments);

-- Replace "MSI 2025" with "msi-2025"
UPDATE teams
SET tournaments = array_replace(tournaments, 'MSI 2025', 'msi-2025')
WHERE 'MSI 2025' = ANY(tournaments);

-- Replace "First Stand" with "first-stand"
UPDATE teams
SET tournaments = array_replace(tournaments, 'First Stand', 'first-stand')
WHERE 'First Stand' = ANY(tournaments);

-- Verify the changes
SELECT tag, name, tournaments
FROM teams
WHERE tournaments IS NOT NULL
ORDER BY tag;
