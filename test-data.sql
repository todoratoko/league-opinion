-- Test Data for LeagueOpinion
-- Run this in Neon SQL Editor to add sample data

-- Insert some teams
INSERT INTO teams (name, tag, region, image, twitter) VALUES
('T1', 'T1', 'Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819614211_t1-2021-worlds.png', '@T1LoL'),
('Gen.G', 'GEN', 'Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370350784_GenG-FullonDark.png', '@GenG'),
('G2 Esports', 'G2', 'Europe', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2FG2-FullonDark.png', '@G2esports'),
('Fnatic', 'FNC', 'Europe', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819807831_fnc-2021-worlds.png', '@FNATIC'),
('Cloud9', 'C9', 'North America', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819640922_c9-2021-worlds.png', '@Cloud9'),
('Team Liquid', 'TL', 'North America', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819669096_tl-2021-worlds.png', '@TeamLiquid'),
('JD Gaming', 'JDG', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819624910_jdg-2021-worlds.png', '@JDGaming'),
('Bilibili Gaming', 'BLG', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370109137_BLG-FullonDark.png', '@bilibili_gaming')
ON CONFLICT DO NOTHING;

-- Create a test user for opinions
INSERT INTO users (username, email, password, is_enabled, created_at) VALUES
('testuser', 'test@leagueopinion.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', true, NOW()),
('faker_fan', 'faker@leagueopinion.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', true, NOW()),
('lec_viewer', 'lec@leagueopinion.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', true, NOW())
ON CONFLICT (email) DO NOTHING;

-- Insert some games with regions
-- Get team IDs first, then insert games
INSERT INTO games (match_start_date_time, team_one_id, team_two_id, team_one_percent, team_two_percent, region_id) VALUES
-- Korea Region (T1 vs Gen.G)
('2025-10-25T18:00:00', 1, 2, 65, 35, 3),
-- Europe Region (G2 vs Fnatic)
('2025-10-26T20:00:00', 3, 4, 55, 45, 2),
-- North America Region (C9 vs TL)
('2025-10-26T22:00:00', 5, 6, 48, 52, 1),
-- China Region (JDG vs BLG)
('2025-10-27T14:00:00', 7, 8, 60, 40, 4),
-- Korea Region (Gen.G vs T1 - rematch)
('2025-10-28T18:00:00', 2, 1, 45, 55, 3),
-- International match (T1 vs G2)
('2025-10-29T20:00:00', 1, 3, 70, 30, 5)
ON CONFLICT DO NOTHING;

-- Insert opinions for these games
-- Note: user_id will be 1, 2, 3 based on the users we created above
INSERT INTO opinions (team_one_percent, team_two_percent, comment, created_at, user_id, game_id) VALUES
-- Opinions for Game 1 (T1 vs Gen.G)
(70, 30, 'T1 looking unstoppable this season! Faker is in peak form.', NOW() - INTERVAL '2 hours', 1, 1),
(65, 35, 'Gen.G has been struggling lately, T1 should take this easily.', NOW() - INTERVAL '1 hour', 2, 1),
(60, 40, 'I think T1 wins but Gen.G will put up a fight in early game.', NOW() - INTERVAL '30 minutes', 3, 1),

-- Opinions for Game 2 (G2 vs Fnatic)
(50, 50, 'This is going to be so close! Classic EU rivalry.', NOW() - INTERVAL '3 hours', 1, 2),
(55, 45, 'G2 has better macro, I give them the edge.', NOW() - INTERVAL '2 hours', 2, 2),
(45, 55, 'Fnatic bot lane is looking really strong, I favor them.', NOW() - INTERVAL '1 hour', 3, 2),

-- Opinions for Game 3 (C9 vs TL)
(40, 60, 'Team Liquid has been on fire! CoreJJ is playing like a monster.', NOW() - INTERVAL '4 hours', 1, 3),
(45, 55, 'C9 has potential but TL is more consistent right now.', NOW() - INTERVAL '3 hours', 2, 3),
(50, 50, 'NA is so unpredictable, this could go either way.', NOW() - INTERVAL '2 hours', 3, 3),

-- Opinions for Game 4 (JDG vs BLG)
(65, 35, 'JDG are the defending champions, they got this.', NOW() - INTERVAL '5 hours', 1, 4),
(60, 40, 'BLG has upset potential but JDG should win.', NOW() - INTERVAL '4 hours', 2, 4),
(55, 45, 'LPL is so competitive, but JDG has more experience.', NOW() - INTERVAL '3 hours', 3, 4),

-- Opinions for Game 5 (Gen.G vs T1 rematch)
(40, 60, 'T1 again! They are just better in every role.', NOW() - INTERVAL '1 hour', 1, 5),
(35, 65, 'Gen.G needs to figure something out, T1 is too good.', NOW() - INTERVAL '45 minutes', 2, 5),

-- Opinions for Game 6 (T1 vs G2 - International)
(75, 25, 'LCK vs LEC, and LCK always wins. T1 dominates.', NOW() - INTERVAL '6 hours', 1, 6),
(70, 30, 'G2 is good but T1 is on another level internationally.', NOW() - INTERVAL '5 hours', 2, 6),
(65, 35, 'East vs West, and East wins. T1 by a mile.', NOW() - INTERVAL '4 hours', 3, 6)
ON CONFLICT DO NOTHING;

-- Verify data was inserted
SELECT 'Teams inserted:' as status, COUNT(*) as count FROM teams;
SELECT 'Games inserted:' as status, COUNT(*) as count FROM games;
SELECT 'Opinions inserted:' as status, COUNT(*) as count FROM opinions;
SELECT 'Users inserted:' as status, COUNT(*) as count FROM users;
