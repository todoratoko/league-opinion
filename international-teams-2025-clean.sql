-- International Teams 2025 (MSI & Worlds) - CLEAN IMPORT
-- This script removes ALL existing teams and adds only international teams with logos and player photos

-- ============================================
-- CLEAN UP EXISTING DATA
-- ============================================
DELETE FROM players;
DELETE FROM teams;

-- Reset sequences
ALTER SEQUENCE teams_id_seq RESTART WITH 1;
ALTER SEQUENCE players_id_seq RESTART WITH 1;

-- ============================================
-- LCK (KOREA) TEAMS
-- ============================================

-- Gen.G (MSI 2025 Champions, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('Gen.G', 'Gen.G Esports', 'GEN', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370350784_GenG-FullonDark.png', '@GenG');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Kiin', 'Kim Gi-in', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkiin-2025.png', (SELECT id FROM teams WHERE tag = 'GEN' LIMIT 1)),
('Canyon', 'Kim Geon-bu', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcanyon-2025.png', (SELECT id FROM teams WHERE tag = 'GEN' LIMIT 1)),
('Chovy', 'Jeong Ji-hoon', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fchovy-2025.png', (SELECT id FROM teams WHERE tag = 'GEN' LIMIT 1)),
('Ruler', 'Park Jae-hyuk', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fruler-2025.png', (SELECT id FROM teams WHERE tag = 'GEN' LIMIT 1)),
('Duro', 'Joo Min-kyu', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fduro-2025.png', (SELECT id FROM teams WHERE tag = 'GEN' LIMIT 1));

-- T1 (MSI 2025, Worlds 2025 Play-In)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('T1', 'T1 Esports', 'T1', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819614211_t1-2021-worlds.png', '@T1LoL');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Doran', 'Choi Hyeon-joon', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdoran-2025.png', (SELECT id FROM teams WHERE tag = 'T1' LIMIT 1)),
('Oner', 'Mun Hyeon-jun', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Foner-2025.png', (SELECT id FROM teams WHERE tag = 'T1' LIMIT 1)),
('Faker', 'Lee Sang-hyeok', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ffaker-2025.png', (SELECT id FROM teams WHERE tag = 'T1' LIMIT 1)),
('Gumayusi', 'Lee Min-hyeong', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fgumayusi-2025.png', (SELECT id FROM teams WHERE tag = 'T1' LIMIT 1)),
('Keria', 'Ryu Min-seok', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkeria-2025.png', (SELECT id FROM teams WHERE tag = 'T1' LIMIT 1));

-- Hanwha Life Esports (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('HLE', 'Hanwha Life Esports', 'HLE', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819677431_hle-2021-worlds.png', '@HLEofficial');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Zeus', 'Choi Woo-je', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fzeus-2025.png', (SELECT id FROM teams WHERE tag = 'HLE' LIMIT 1)),
('Peanut', 'Han Wang-ho', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fpeanut-2025.png', (SELECT id FROM teams WHERE tag = 'HLE' LIMIT 1)),
('Zeka', 'Kim Geon-woo', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fzeka-2025.png', (SELECT id FROM teams WHERE tag = 'HLE' LIMIT 1)),
('Viper', 'Park Do-hyeon', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fviper-2025.png', (SELECT id FROM teams WHERE tag = 'HLE' LIMIT 1)),
('Delight', 'Yoo Hwan-joong', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdelight-2025.png', (SELECT id FROM teams WHERE tag = 'HLE' LIMIT 1));

-- KT Rolster (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('KT', 'KT Rolster', 'KT', 'Korea', 'South Korea', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819652707_kt-2021-worlds.png', '@KT_Rolster');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('PerfecT', 'Lee Seung-min', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fperfect-2025.png', (SELECT id FROM teams WHERE tag = 'KT' LIMIT 1)),
('Cuzz', 'Moon Woo-chan', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcuzz-2025.png', (SELECT id FROM teams WHERE tag = 'KT' LIMIT 1)),
('Bdd', 'Gwak Bo-seong', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbdd-2025.png', (SELECT id FROM teams WHERE tag = 'KT' LIMIT 1)),
('deokdam', 'Seo Dae-gil', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdeokdam-2025.png', (SELECT id FROM teams WHERE tag = 'KT' LIMIT 1)),
('Peter', 'Jeong Yoon-su', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fpeter-2025.png', (SELECT id FROM teams WHERE tag = 'KT' LIMIT 1));

-- ============================================
-- LPL (CHINA) TEAMS
-- ============================================

-- Bilibili Gaming (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('BLG', 'Bilibili Gaming', 'BLG', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370109137_BLG-FullonDark.png', '@bilibili_gaming');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Bin', 'Chen Ze-Bin', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbin-2025.png', (SELECT id FROM teams WHERE tag = 'BLG' LIMIT 1)),
('Beichuan', 'Yang Ling', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbeichuan-2025.png', (SELECT id FROM teams WHERE tag = 'BLG' LIMIT 1)),
('Knight', 'Zhuo Ding', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fknight-2025.png', (SELECT id FROM teams WHERE tag = 'BLG' LIMIT 1)),
('Elk', 'Zhao Jia-Hao', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Felk-2025.png', (SELECT id FROM teams WHERE tag = 'BLG' LIMIT 1)),
('ON', 'Luo Wen-Jun', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fon-2025.png', (SELECT id FROM teams WHERE tag = 'BLG' LIMIT 1));

-- Anyone's Legend (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('AL', 'Anyone''s Legend', 'AL', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fal-logo-2025.png', '@ALGaming');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Flandre', 'Li Xuan-Jun', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fflandre-2025.png', (SELECT id FROM teams WHERE tag = 'AL' LIMIT 1)),
('Tarzan', 'Lee Seung-yong', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ftarzan-2025.png', (SELECT id FROM teams WHERE tag = 'AL' LIMIT 1)),
('Shanks', 'Cui Xiao-Jun', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fshanks-2025.png', (SELECT id FROM teams WHERE tag = 'AL' LIMIT 1)),
('Hope', 'Wang Jie', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhope-2025.png', (SELECT id FROM teams WHERE tag = 'AL' LIMIT 1)),
('Kael', 'Kim Jin-hong', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkael-2025.png', (SELECT id FROM teams WHERE tag = 'AL' LIMIT 1));

-- Top Esports (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('TES', 'Top Esports', 'TES', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819659428_tes-2021-worlds.png', '@TOP_esports_');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('369', 'Bai Jia-Hao', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2F369-2025.png', (SELECT id FROM teams WHERE tag = 'TES' LIMIT 1)),
('Kanavi', 'Seo Jin-hyeok', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkanavi-2025.png', (SELECT id FROM teams WHERE tag = 'TES' LIMIT 1)),
('Creme', 'Zheng Zhe-Bin', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcreme-2025.png', (SELECT id FROM teams WHERE tag = 'TES' LIMIT 1)),
('JackeyLove', 'Yu Wen-Bo', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fjackeylove-2025.png', (SELECT id FROM teams WHERE tag = 'TES' LIMIT 1)),
('Hang', 'Wang Jie', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhang-2025.png', (SELECT id FROM teams WHERE tag = 'TES' LIMIT 1));

-- Invictus Gaming (Worlds 2025 Play-In)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('iG', 'Invictus Gaming', 'IG', 'China', 'China', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370277114_iG-FullonDark.png', '@invgaming');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('TheShy', 'Kang Seung-lok', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ftheshy-2025.png', (SELECT id FROM teams WHERE tag = 'IG' LIMIT 1)),
('Wei', 'Gao Tian-Liang', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fwei-2025.png', (SELECT id FROM teams WHERE tag = 'IG' LIMIT 1)),
('Rookie', 'Song Eui-jin', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Frookie-2025.png', (SELECT id FROM teams WHERE tag = 'IG' LIMIT 1)),
('GALA', 'Chen Wei', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fgala-2025.png', (SELECT id FROM teams WHERE tag = 'IG' LIMIT 1)),
('Meiko', 'Tian Ye', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmeiko-2025.png', (SELECT id FROM teams WHERE tag = 'IG' LIMIT 1));

-- ============================================
-- LEC (EUROPE) TEAMS
-- ============================================

-- G2 Esports (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('G2', 'G2 Esports', 'G2', 'Europe', 'Germany', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2FG2-FullonDark.png', '@G2esports');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('BrokenBlade', 'Sergen Çelik', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbrokenblade-2025.png', (SELECT id FROM teams WHERE tag = 'G2' LIMIT 1)),
('SkewMond', 'Rudy Semaan', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fskewmond-2025.png', (SELECT id FROM teams WHERE tag = 'G2' LIMIT 1)),
('Caps', 'Rasmus Borregaard Winther', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fcaps-2025.png', (SELECT id FROM teams WHERE tag = 'G2' LIMIT 1)),
('Hans Sama', 'Steven Liv', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhanssama-2025.png', (SELECT id FROM teams WHERE tag = 'G2' LIMIT 1)),
('Labrov', 'Labros Papoutsakis', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Flabrov-2025.png', (SELECT id FROM teams WHERE tag = 'G2' LIMIT 1));

-- Movistar KOI (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('KOI', 'Movistar KOI', 'KOI', 'Europe', 'Spain', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fkoi-logo-2025.png', '@KOI');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Myrwn', 'Alex Pastor Villarejo', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmyrwn-2025.png', (SELECT id FROM teams WHERE tag = 'KOI' LIMIT 1)),
('Elyoya', 'Javier Prades Batalla', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Felyoya-2025.png', (SELECT id FROM teams WHERE tag = 'KOI' LIMIT 1)),
('Jojopyun', 'Joseph Joon Pyun', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fjojopyun-2025.png', (SELECT id FROM teams WHERE tag = 'KOI' LIMIT 1)),
('Supa', 'David Martínez García', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fsupa-2025.png', (SELECT id FROM teams WHERE tag = 'KOI' LIMIT 1)),
('Alvaro', 'Álvaro Fernández del Amo', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Falvaro-2025.png', (SELECT id FROM teams WHERE tag = 'KOI' LIMIT 1));

-- Fnatic (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('FNC', 'Fnatic', 'FNC', 'Europe', 'United Kingdom', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1631819807831_fnc-2021-worlds.png', '@FNATIC');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Oscarinin', 'Óscar Muñoz Jiménez', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Foscarinin-2025.png', (SELECT id FROM teams WHERE tag = 'FNC' LIMIT 1)),
('Razork', 'Iván Martín Díaz', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Frazork-2025.png', (SELECT id FROM teams WHERE tag = 'FNC' LIMIT 1)),
('Poby', 'Park Jun-seong', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fpoby-2025.png', (SELECT id FROM teams WHERE tag = 'FNC' LIMIT 1)),
('Upset', 'Elias Lipp', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fupset-2025.png', (SELECT id FROM teams WHERE tag = 'FNC' LIMIT 1)),
('Mikyx', 'Mihael Mehle', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmikyx-2025.png', (SELECT id FROM teams WHERE tag = 'FNC' LIMIT 1));

-- ============================================
-- LTA (AMERICAS) TEAMS
-- ============================================

-- FlyQuest (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('FLY', 'FlyQuest', 'FLY', 'North America', 'United States', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fflyquest-logo-2025.png', '@FlyQuest');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Bwipo', 'Gabriël Rau', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbwipo-2025.png', (SELECT id FROM teams WHERE tag = 'FLY' LIMIT 1)),
('Inspired', 'Kacper Słoma', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Finspired-2025.png', (SELECT id FROM teams WHERE tag = 'FLY' LIMIT 1)),
('Quad', 'Song Su-hyeong', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fquad-2025.png', (SELECT id FROM teams WHERE tag = 'FLY' LIMIT 1)),
('Massu', 'Fahad Abdulmalek', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmassu-2025.png', (SELECT id FROM teams WHERE tag = 'FLY' LIMIT 1)),
('Busio', 'Alan Cwalina', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbusio-2025.png', (SELECT id FROM teams WHERE tag = 'FLY' LIMIT 1));

-- FURIA (MSI 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('FUR', 'FURIA Esports', 'FUR', 'Brazil', 'Brazil', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Ffuria-logo-2025.png', '@FURIA');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Guigo', 'Guilherme Araújo Ruiz', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fguigo-2025.png', (SELECT id FROM teams WHERE tag = 'FUR' LIMIT 1)),
('Tatu', 'Pedro Seixas', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ftatu-2025.png', (SELECT id FROM teams WHERE tag = 'FUR' LIMIT 1)),
('Tutsz', 'Arthur Peixoto Machado', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ftutsz-2025.png', (SELECT id FROM teams WHERE tag = 'FUR' LIMIT 1)),
('Ayu', 'Andrey Saraiva', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fayu-2025.png', (SELECT id FROM teams WHERE tag = 'FUR' LIMIT 1)),
('JoJo', 'Gabriel Dzelme de Oliveira', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fjojo-2025.png', (SELECT id FROM teams WHERE tag = 'FUR' LIMIT 1));

-- Vivo Keyd Stars (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('VKS', 'Vivo Keyd Stars', 'VKS', 'Brazil', 'Brazil', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fkeyd-stars-logo-2025.png', '@VivoKeyd');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Boal', 'Gustavo Boal', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fboal-2025.png', (SELECT id FROM teams WHERE tag = 'VKS' LIMIT 1)),
('Disamis', 'Leonardo Disamis', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdisamis-2025.png', (SELECT id FROM teams WHERE tag = 'VKS' LIMIT 1)),
('Mireu', 'Felipe Mireu', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmireu-2025.png', (SELECT id FROM teams WHERE tag = 'VKS' LIMIT 1)),
('Morttheus', 'Matheus Morttheus', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmorttheus-2025.png', (SELECT id FROM teams WHERE tag = 'VKS' LIMIT 1)),
('Scamber', 'Samuel Scamber', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fscamber-2025.png', (SELECT id FROM teams WHERE tag = 'VKS' LIMIT 1));

-- 100 Thieves (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('100T', '100 Thieves', '100T', 'North America', 'United States', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2F1641370137928_100-FullonDark.png', '@100Thieves');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Dhokla', 'Niship Doshi', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdhokla-2025.png', (SELECT id FROM teams WHERE tag = '100T' LIMIT 1)),
('River', 'Kim Dong-woo', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Friver-2025.png', (SELECT id FROM teams WHERE tag = '100T' LIMIT 1)),
('Quid', 'Liu Sheng-Hong', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fquid-2025.png', (SELECT id FROM teams WHERE tag = '100T' LIMIT 1)),
('FBI', 'Victor Huang', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ffbi-2025.png', (SELECT id FROM teams WHERE tag = '100T' LIMIT 1)),
('Eyla', 'James Cornette', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Feyla-2025.png', (SELECT id FROM teams WHERE tag = '100T' LIMIT 1));

-- ============================================
-- LCP (ASIA PACIFIC) TEAMS
-- ============================================

-- CTBC Flying Oyster (MSI 2025, Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('CFO', 'CTBC Flying Oyster', 'CFO', 'Southeast Asia', 'Taiwan', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fcfo-logo-2025.png', '@CTBC_FlyingOyster');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Driver', 'Shen Tsung-Hua', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdriver-2025.png', (SELECT id FROM teams WHERE tag = 'CFO' LIMIT 1)),
('Rest', 'Hsu Shih-Chieh', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Frest-2025.png', (SELECT id FROM teams WHERE tag = 'CFO' LIMIT 1)),
('JunJia', 'Yu Chun-Chia', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fjunjia-2025.png', (SELECT id FROM teams WHERE tag = 'CFO' LIMIT 1)),
('HongQ', 'Tsai Ming-Hong', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhongq-2025.png', (SELECT id FROM teams WHERE tag = 'CFO' LIMIT 1)),
('Doggo', 'Chiu Tzu-Chuan', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdoggo-2025.png', (SELECT id FROM teams WHERE tag = 'CFO' LIMIT 1));

-- GAM Esports (MSI 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('GAM', 'GAM Esports', 'GAM', 'Southeast Asia', 'Vietnam', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fgam-logo-2025.png', '@GAMEsportsVN');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Kiaya', 'Trần Duy Sang', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkiaya-2025.png', (SELECT id FROM teams WHERE tag = 'GAM' LIMIT 1)),
('Levi', 'Đỗ Duy Khánh', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Flevi-2025.png', (SELECT id FROM teams WHERE tag = 'GAM' LIMIT 1)),
('Emo', 'Nguyễn Thái Vinh', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Femo-2025.png', (SELECT id FROM teams WHERE tag = 'GAM' LIMIT 1)),
('Aress', 'Hồ Văn Vĩ Đại', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Faress-2025.png', (SELECT id FROM teams WHERE tag = 'GAM' LIMIT 1)),
('Artemis', 'Trần Quốc Hưng', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fartemis-2025.png', (SELECT id FROM teams WHERE tag = 'GAM' LIMIT 1));

-- Team Secret Whales (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('TSW', 'Team Secret Whales', 'TSW', 'Southeast Asia', 'Philippines', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fsecret-whales-logo-2025.png', '@teamsecret');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Steller', 'Philip Steller', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fsteller-2025.png', (SELECT id FROM teams WHERE tag = 'TSW' LIMIT 1)),
('Hizto', 'John Hizto', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fhizto-2025.png', (SELECT id FROM teams WHERE tag = 'TSW' LIMIT 1)),
('Dire', 'Joshua Dire', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fdire-2025.png', (SELECT id FROM teams WHERE tag = 'TSW' LIMIT 1)),
('Eddie', 'Edward Eddie', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Feddie-2025.png', (SELECT id FROM teams WHERE tag = 'TSW' LIMIT 1)),
('Taki', 'Mark Taki', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Ftaki-2025.png', (SELECT id FROM teams WHERE tag = 'TSW' LIMIT 1));

-- PSG Talon (Worlds 2025)
INSERT INTO teams (name, full_name, tag, region, country, image, twitter) VALUES
('PSG', 'PSG Talon', 'PSG', 'Southeast Asia', 'Hong Kong', 'https://am-a.akamaihd.net/image?resize=130:&f=http%3A%2F%2Fstatic.lolesports.com%2Fteams%2Fpsg-talon-logo-2025.png', '@PSG_Talon');

INSERT INTO players (name, nickname, role, image, team_id) VALUES
('Azhi', 'Wong Chun-Lung', 'Top', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fazhi-2025.png', (SELECT id FROM teams WHERE tag = 'PSG' LIMIT 1)),
('Karsa', 'Hung Hau-Hsuan', 'Jungle', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fkarsa-2025.png', (SELECT id FROM teams WHERE tag = 'PSG' LIMIT 1)),
('Maple', 'Huang Yi-Tang', 'Mid', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fmaple-2025.png', (SELECT id FROM teams WHERE tag = 'PSG' LIMIT 1)),
('Betty', 'Yeh Ting-Wei', 'ADC', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fbetty-2025.png', (SELECT id FROM teams WHERE tag = 'PSG' LIMIT 1)),
('Woody', 'Chen Yu-Hsien', 'Support', 'https://am-a.akamaihd.net/image?resize=100:&f=http%3A%2F%2Fstatic.lolesports.com%2Fplayers%2Fwoody-2025.png', (SELECT id FROM teams WHERE tag = 'PSG' LIMIT 1));

-- ============================================
-- VERIFICATION QUERIES
-- ============================================

-- Verify data was inserted
SELECT 'Teams inserted:' as status, COUNT(*) as count FROM teams;
SELECT 'Players inserted:' as status, COUNT(*) as count FROM players;

-- Show all teams with their regions
SELECT id, name, full_name, tag, region, country FROM teams ORDER BY region, name;

-- Show player count by team
SELECT t.name, t.tag, t.region, COUNT(p.id) as player_count
FROM teams t
LEFT JOIN players p ON t.id = p.team_id
GROUP BY t.id, t.name, t.tag, t.region
ORDER BY t.region, t.name;
