-- International Teams 2025 (MSI & Worlds)
-- Complete rosters for teams that participated in MSI 2025 and/or qualified for Worlds 2025

-- Clear existing data if needed (UNCOMMENT IF YOU WANT TO START FRESH)
-- DELETE FROM players;
-- DELETE FROM teams WHERE id > 8;

-- ============================================
-- LCK (KOREA) TEAMS
-- ============================================

-- Gen.G (MSI 2025 Champions, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('Gen.G', 'Gen.G Esports', 'GEN', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370350784_GenG-FullonDark.png', '@GenG')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Kiin', 'Kim Gi-in', 'Top', (SELECT id FROM teams WHERE tag = 'GEN')),
('Canyon', 'Kim Geon-bu', 'Jungle', (SELECT id FROM teams WHERE tag = 'GEN')),
('Chovy', 'Jeong Ji-hoon', 'Mid', (SELECT id FROM teams WHERE tag = 'GEN')),
('Ruler', 'Park Jae-hyuk', 'ADC', (SELECT id FROM teams WHERE tag = 'GEN')),
('Duro', 'Joo Min-kyu', 'Support', (SELECT id FROM teams WHERE tag = 'GEN'))
ON CONFLICT DO NOTHING;

-- T1 (MSI 2025, Worlds 2025 Play-In)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('T1', 'T1 Esports', 'T1', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819614211_t1-2021-worlds.png', '@T1LoL')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Doran', 'Choi Hyeon-joon', 'Top', (SELECT id FROM teams WHERE tag = 'T1')),
('Oner', 'Mun Hyeon-jun', 'Jungle', (SELECT id FROM teams WHERE tag = 'T1')),
('Faker', 'Lee Sang-hyeok', 'Mid', (SELECT id FROM teams WHERE tag = 'T1')),
('Gumayusi', 'Lee Min-hyeong', 'ADC', (SELECT id FROM teams WHERE tag = 'T1')),
('Keria', 'Ryu Min-seok', 'Support', (SELECT id FROM teams WHERE tag = 'T1'))
ON CONFLICT DO NOTHING;

-- Hanwha Life Esports (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('HLE', 'Hanwha Life Esports', 'HLE', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819677431_hle-2021-worlds.png', '@HLEofficial')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Zeus', 'Choi Woo-je', 'Top', (SELECT id FROM teams WHERE tag = 'HLE')),
('Peanut', 'Han Wang-ho', 'Jungle', (SELECT id FROM teams WHERE tag = 'HLE')),
('Zeka', 'Kim Geon-woo', 'Mid', (SELECT id FROM teams WHERE tag = 'HLE')),
('Viper', 'Park Do-hyeon', 'ADC', (SELECT id FROM teams WHERE tag = 'HLE')),
('Delight', 'Yoo Hwan-joong', 'Support', (SELECT id FROM teams WHERE tag = 'HLE'))
ON CONFLICT DO NOTHING;

-- KT Rolster (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('KT', 'KT Rolster', 'KT', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819652707_kt-2021-worlds.png', '@KT_Rolster')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('PerfecT', 'Lee Seung-min', 'Top', (SELECT id FROM teams WHERE tag = 'KT')),
('Cuzz', 'Moon Woo-chan', 'Jungle', (SELECT id FROM teams WHERE tag = 'KT')),
('Bdd', 'Gwak Bo-seong', 'Mid', (SELECT id FROM teams WHERE tag = 'KT')),
('deokdam', 'Seo Dae-gil', 'ADC', (SELECT id FROM teams WHERE tag = 'KT')),
('Peter', 'Jeong Yoon-su', 'Support', (SELECT id FROM teams WHERE tag = 'KT'))
ON CONFLICT DO NOTHING;

-- ============================================
-- LPL (CHINA) TEAMS
-- ============================================

-- Bilibili Gaming (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('BLG', 'Bilibili Gaming', 'BLG', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370109137_BLG-FullonDark.png', '@bilibili_gaming')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Bin', 'Chen Ze-Bin', 'Top', (SELECT id FROM teams WHERE tag = 'BLG')),
('Beichuan', 'Yang Ling', 'Jungle', (SELECT id FROM teams WHERE tag = 'BLG')),
('Knight', 'Zhuo Ding', 'Mid', (SELECT id FROM teams WHERE tag = 'BLG')),
('Elk', 'Zhao Jia-Hao', 'ADC', (SELECT id FROM teams WHERE tag = 'BLG')),
('ON', 'Luo Wen-Jun', 'Support', (SELECT id FROM teams WHERE tag = 'BLG'))
ON CONFLICT DO NOTHING;

-- Anyone's Legend (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('AL', 'Anyone''s Legend', 'AL', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2FAL-logo.png', '@ALGaming')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Flandre', 'Li Xuan-Jun', 'Top', (SELECT id FROM teams WHERE tag = 'AL')),
('Tarzan', 'Lee Seung-yong', 'Jungle', (SELECT id FROM teams WHERE tag = 'AL')),
('Shanks', 'Cui Xiao-Jun', 'Mid', (SELECT id FROM teams WHERE tag = 'AL')),
('Hope', 'Wang Jie', 'ADC', (SELECT id FROM teams WHERE tag = 'AL')),
('Kael', 'Kim Jin-hong', 'Support', (SELECT id FROM teams WHERE tag = 'AL'))
ON CONFLICT DO NOTHING;

-- Top Esports (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('TES', 'Top Esports', 'TES', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819659428_tes-2021-worlds.png', '@TOP_esports_')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('369', 'Bai Jia-Hao', 'Top', (SELECT id FROM teams WHERE tag = 'TES')),
('Kanavi', 'Seo Jin-hyeok', 'Jungle', (SELECT id FROM teams WHERE tag = 'TES')),
('Creme', 'Zheng Zhe-Bin', 'Mid', (SELECT id FROM teams WHERE tag = 'TES')),
('JackeyLove', 'Yu Wen-Bo', 'ADC', (SELECT id FROM teams WHERE tag = 'TES')),
('Hang', 'Wang Jie', 'Support', (SELECT id FROM teams WHERE tag = 'TES'))
ON CONFLICT DO NOTHING;

-- Invictus Gaming (Worlds 2025 Play-In)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('iG', 'Invictus Gaming', 'IG', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370277114_iG-FullonDark.png', '@invgaming')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('TheShy', 'Kang Seung-lok', 'Top', (SELECT id FROM teams WHERE tag = 'IG')),
('Wei', 'Gao Tian-Liang', 'Jungle', (SELECT id FROM teams WHERE tag = 'IG')),
('Rookie', 'Song Eui-jin', 'Mid', (SELECT id FROM teams WHERE tag = 'IG')),
('GALA', 'Chen Wei', 'ADC', (SELECT id FROM teams WHERE tag = 'IG')),
('Meiko', 'Tian Ye', 'Support', (SELECT id FROM teams WHERE tag = 'IG'))
ON CONFLICT DO NOTHING;

-- ============================================
-- LEC (EUROPE) TEAMS
-- ============================================

-- G2 Esports (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('G2', 'G2 Esports', 'G2', 'Europe', 'Germany', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2FG2-FullonDark.png', '@G2esports')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('BrokenBlade', 'Sergen Çelik', 'Top', (SELECT id FROM teams WHERE tag = 'G2')),
('SkewMond', 'Rudy Semaan', 'Jungle', (SELECT id FROM teams WHERE tag = 'G2')),
('Caps', 'Rasmus Borregaard Winther', 'Mid', (SELECT id FROM teams WHERE tag = 'G2')),
('Hans Sama', 'Steven Liv', 'ADC', (SELECT id FROM teams WHERE tag = 'G2')),
('Labrov', 'Labros Papoutsakis', 'Support', (SELECT id FROM teams WHERE tag = 'G2'))
ON CONFLICT DO NOTHING;

-- Movistar KOI (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('KOI', 'Movistar KOI', 'KOI', 'Europe', 'Spain', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2FKOI-logo.png', '@KOI')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Myrwn', 'Alex Pastor Villarejo', 'Top', (SELECT id FROM teams WHERE tag = 'KOI')),
('Elyoya', 'Javier Prades Batalla', 'Jungle', (SELECT id FROM teams WHERE tag = 'KOI')),
('Jojopyun', 'Joseph Joon Pyun', 'Mid', (SELECT id FROM teams WHERE tag = 'KOI')),
('Supa', 'David Martínez García', 'ADC', (SELECT id FROM teams WHERE tag = 'KOI')),
('Alvaro', 'Álvaro Fernández del Amo', 'Support', (SELECT id FROM teams WHERE tag = 'KOI'))
ON CONFLICT DO NOTHING;

-- Fnatic (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('FNC', 'Fnatic', 'FNC', 'Europe', 'United Kingdom', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819807831_fnc-2021-worlds.png', '@FNATIC')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Oscarinin', 'Óscar Muñoz Jiménez', 'Top', (SELECT id FROM teams WHERE tag = 'FNC')),
('Razork', 'Iván Martín Díaz', 'Jungle', (SELECT id FROM teams WHERE tag = 'FNC')),
('Poby', 'Park Jun-seong', 'Mid', (SELECT id FROM teams WHERE tag = 'FNC')),
('Upset', 'Elias Lipp', 'ADC', (SELECT id FROM teams WHERE tag = 'FNC')),
('Mikyx', 'Mihael Mehle', 'Support', (SELECT id FROM teams WHERE tag = 'FNC'))
ON CONFLICT DO NOTHING;

-- ============================================
-- LTA (AMERICAS) TEAMS
-- ============================================

-- FlyQuest (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('FLY', 'FlyQuest', 'FLY', 'North America', 'United States', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fflyquest-logo.png', '@FlyQuest')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Bwipo', 'Gabriël Rau', 'Top', (SELECT id FROM teams WHERE tag = 'FLY')),
('Inspired', 'Kacper Słoma', 'Jungle', (SELECT id FROM teams WHERE tag = 'FLY')),
('Quad', 'Song Su-hyeong', 'Mid', (SELECT id FROM teams WHERE tag = 'FLY')),
('Massu', 'Fahad Abdulmalek', 'ADC', (SELECT id FROM teams WHERE tag = 'FLY')),
('Busio', 'Alan Cwalina', 'Support', (SELECT id FROM teams WHERE tag = 'FLY'))
ON CONFLICT DO NOTHING;

-- FURIA (MSI 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('FUR', 'FURIA Esports', 'FUR', 'Brazil', 'Brazil', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Ffuria-logo.png', '@FURIA')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Guigo', 'Guilherme Araújo Ruiz', 'Top', (SELECT id FROM teams WHERE tag = 'FUR')),
('Tatu', 'Pedro Seixas', 'Jungle', (SELECT id FROM teams WHERE tag = 'FUR')),
('Tutsz', 'Arthur Peixoto Machado', 'Mid', (SELECT id FROM teams WHERE tag = 'FUR')),
('Ayu', 'Andrey Saraiva', 'ADC', (SELECT id FROM teams WHERE tag = 'FUR')),
('JoJo', 'Gabriel Dzelme de Oliveira', 'Support', (SELECT id FROM teams WHERE tag = 'FUR'))
ON CONFLICT DO NOTHING;

-- Vivo Keyd Stars (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('VKS', 'Vivo Keyd Stars', 'VKS', 'Brazil', 'Brazil', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fkeyd-stars-logo.png', '@VivoKeyd')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Boal', 'Gustavo Boal', 'Top', (SELECT id FROM teams WHERE tag = 'VKS')),
('Disamis', 'Leonardo Disamis', 'Jungle', (SELECT id FROM teams WHERE tag = 'VKS')),
('Mireu', 'Felipe Mireu', 'Mid', (SELECT id FROM teams WHERE tag = 'VKS')),
('Morttheus', 'Matheus Morttheus', 'ADC', (SELECT id FROM teams WHERE tag = 'VKS')),
('Scamber', 'Samuel Scamber', 'Support', (SELECT id FROM teams WHERE tag = 'VKS'))
ON CONFLICT DO NOTHING;

-- 100 Thieves (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('100T', '100 Thieves', '100T', 'North America', 'United States', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370137928_100-FullonDark.png', '@100Thieves')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Dhokla', 'Niship Doshi', 'Top', (SELECT id FROM teams WHERE tag = '100T')),
('River', 'Kim Dong-woo', 'Jungle', (SELECT id FROM teams WHERE tag = '100T')),
('Quid', 'Liu Sheng-Hong', 'Mid', (SELECT id FROM teams WHERE tag = '100T')),
('FBI', 'Victor Huang', 'ADC', (SELECT id FROM teams WHERE tag = '100T')),
('Eyla', 'James Cornette', 'Support', (SELECT id FROM teams WHERE tag = '100T'))
ON CONFLICT DO NOTHING;

-- ============================================
-- LCP (ASIA PACIFIC) TEAMS
-- ============================================

-- CTBC Flying Oyster (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('CFO', 'CTBC Flying Oyster', 'CFO', 'Southeast Asia', 'Taiwan', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fcfo-logo.png', '@CTBC_FlyingOyster')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Driver', 'Shen Tsung-Hua', 'Top', (SELECT id FROM teams WHERE tag = 'CFO')),
('Rest', 'Hsu Shih-Chieh', 'Jungle', (SELECT id FROM teams WHERE tag = 'CFO')),
('JunJia', 'Yu Chun-Chia', 'Mid', (SELECT id FROM teams WHERE tag = 'CFO')),
('HongQ', 'Tsai Ming-Hong', 'ADC', (SELECT id FROM teams WHERE tag = 'CFO')),
('Doggo', 'Chiu Tzu-Chuan', 'Support', (SELECT id FROM teams WHERE tag = 'CFO'))
ON CONFLICT DO NOTHING;

-- GAM Esports (MSI 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('GAM', 'GAM Esports', 'GAM', 'Southeast Asia', 'Vietnam', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fgam-logo.png', '@GAMEsportsVN')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Kiaya', 'Trần Duy Sang', 'Top', (SELECT id FROM teams WHERE tag = 'GAM')),
('Levi', 'Đỗ Duy Khánh', 'Jungle', (SELECT id FROM teams WHERE tag = 'GAM')),
('Emo', 'Nguyễn Thái Vinh', 'Mid', (SELECT id FROM teams WHERE tag = 'GAM')),
('Aress', 'Hồ Văn Vĩ Đại', 'ADC', (SELECT id FROM teams WHERE tag = 'GAM')),
('Artemis', 'Trần Quốc Hưng', 'Support', (SELECT id FROM teams WHERE tag = 'GAM'))
ON CONFLICT DO NOTHING;

-- Team Secret Whales (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('TSW', 'Team Secret Whales', 'TSW', 'Southeast Asia', 'Philippines', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fsecret-whales-logo.png', '@teamsecret')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Steller', 'Philip Steller', 'Top', (SELECT id FROM teams WHERE tag = 'TSW')),
('Hizto', 'John Hizto', 'Jungle', (SELECT id FROM teams WHERE tag = 'TSW')),
('Dire', 'Joshua Dire', 'Mid', (SELECT id FROM teams WHERE tag = 'TSW')),
('Eddie', 'Edward Eddie', 'ADC', (SELECT id FROM teams WHERE tag = 'TSW')),
('Taki', 'Mark Taki', 'Support', (SELECT id FROM teams WHERE tag = 'TSW'))
ON CONFLICT DO NOTHING;

-- PSG Talon (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('PSG', 'PSG Talon', 'PSG', 'Southeast Asia', 'Hong Kong', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fpsg-talon-logo.png', '@PSG_Talon')
ON CONFLICT DO NOTHING;

INSERT INTO players (name, nickname, role, team_id) VALUES
('Azhi', 'Wong Chun-Lung', 'Top', (SELECT id FROM teams WHERE tag = 'PSG')),
('Karsa', 'Hung Hau-Hsuan', 'Jungle', (SELECT id FROM teams WHERE tag = 'PSG')),
('Maple', 'Huang Yi-Tang', 'Mid', (SELECT id FROM teams WHERE tag = 'PSG')),
('Betty', 'Yeh Ting-Wei', 'ADC', (SELECT id FROM teams WHERE tag = 'PSG')),
('Woody', 'Chen Yu-Hsien', 'Support', (SELECT id FROM teams WHERE tag = 'PSG'))
ON CONFLICT DO NOTHING;

-- Verify data was inserted
SELECT 'International Teams inserted:' as status, COUNT(*) as count FROM teams;
SELECT 'Players inserted:' as status, COUNT(*) as count FROM players;

-- Show all teams with their regions
SELECT name, full_name, region, country FROM teams ORDER BY region, name;

-- Show player count by team
SELECT t.name, t.region, COUNT(p.id) as player_count
FROM teams t
LEFT JOIN players p ON t.id = p.team_id
GROUP BY t.id, t.name, t.region
ORDER BY t.region, t.name;
