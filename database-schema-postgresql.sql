-- LeagueOpinion Database Schema for PostgreSQL (Neon)
-- Run this script on your Neon database after creation

-- Create regions table
CREATE TABLE IF NOT EXISTS regions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_image VARCHAR(500),
    created_at TIMESTAMP,
    last_login DATE,
    login_attempts INTEGER DEFAULT 0,
    is_enabled BOOLEAN DEFAULT TRUE
);

-- Create indexes for users
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Create teams table
CREATE TABLE IF NOT EXISTS teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tag VARCHAR(50),
    region VARCHAR(100),
    image VARCHAR(500),
    twitter VARCHAR(255)
);

-- Create indexes for teams
CREATE INDEX IF NOT EXISTS idx_teams_name ON teams(name);
CREATE INDEX IF NOT EXISTS idx_teams_region ON teams(region);

-- Create players table
CREATE TABLE IF NOT EXISTS players (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    role VARCHAR(100),
    image VARCHAR(500),
    team_id BIGINT,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL
);

-- Create index for players
CREATE INDEX IF NOT EXISTS idx_players_team ON players(team_id);

-- Create championships table
CREATE TABLE IF NOT EXISTS championships (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tag VARCHAR(50),
    region VARCHAR(100),
    date DATE
);

-- Create games table
CREATE TABLE IF NOT EXISTS games (
    id BIGSERIAL PRIMARY KEY,
    match_start_date_time VARCHAR(255),
    team_one_id INTEGER NOT NULL,
    team_two_id INTEGER NOT NULL,
    team_one_percent INTEGER DEFAULT 0,
    team_two_percent INTEGER DEFAULT 0,
    region_id BIGINT,
    FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE SET NULL
);

-- Create indexes for games
CREATE INDEX IF NOT EXISTS idx_games_region ON games(region_id);
CREATE INDEX IF NOT EXISTS idx_games_match_date ON games(match_start_date_time);

-- Create opinions table
CREATE TABLE IF NOT EXISTS opinions (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    team_one_percent INTEGER NOT NULL,
    team_two_percent INTEGER NOT NULL,
    comment TEXT,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

-- Create indexes for opinions
CREATE INDEX IF NOT EXISTS idx_opinions_user ON opinions(user_id);
CREATE INDEX IF NOT EXISTS idx_opinions_game ON opinions(game_id);
CREATE INDEX IF NOT EXISTS idx_opinions_created ON opinions(created_at);

-- Create user follow relationships table
CREATE TABLE IF NOT EXISTS users_follow_users (
    user_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, following_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create confirmation tokens table (for email verification)
CREATE TABLE IF NOT EXISTS confirmation_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create index for confirmation tokens
CREATE INDEX IF NOT EXISTS idx_confirmation_tokens_token ON confirmation_tokens(token);

-- Seed initial region data
INSERT INTO regions (name, code) VALUES
('North America', 'NA'),
('Europe', 'EU'),
('Korea', 'KR'),
('China', 'CN'),
('International', 'INT'),
('Brazil', 'BR'),
('Latin America', 'LAN'),
('Oceania', 'OCE'),
('Japan', 'JP'),
('Turkey', 'TR'),
('CIS', 'CIS'),
('Southeast Asia', 'SEA')
ON CONFLICT (code) DO NOTHING;

-- Sample teams (optional - for testing)
INSERT INTO teams (name, tag, region, image) VALUES
('T1', 'T1', 'Korea', 'https://example.com/t1.png'),
('G2 Esports', 'G2', 'Europe', 'https://example.com/g2.png'),
('Cloud9', 'C9', 'North America', 'https://example.com/c9.png')
ON CONFLICT DO NOTHING;
