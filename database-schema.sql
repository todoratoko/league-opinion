-- LeagueOpinion Database Schema
-- Run this script on your cloud database after creation

-- Create regions table
CREATE TABLE IF NOT EXISTS regions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    profile_image VARCHAR(500),
    created_at DATETIME,
    last_login DATE,
    login_attempts INT DEFAULT 0,
    is_enabled BOOLEAN DEFAULT TRUE,
    INDEX idx_email (email),
    INDEX idx_username (username)
);

-- Create teams table
CREATE TABLE IF NOT EXISTS teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tag VARCHAR(50),
    region VARCHAR(100),
    image VARCHAR(500),
    twitter VARCHAR(255),
    INDEX idx_name (name),
    INDEX idx_region (region)
);

-- Create players table
CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    role VARCHAR(100),
    image VARCHAR(500),
    team_id BIGINT,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE SET NULL,
    INDEX idx_team (team_id)
);

-- Create championships table
CREATE TABLE IF NOT EXISTS championships (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tag VARCHAR(50),
    region VARCHAR(100),
    date DATE
);

-- Create games table
CREATE TABLE IF NOT EXISTS games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_start_date_time VARCHAR(255),
    team_one_id INT NOT NULL,
    team_two_id INT NOT NULL,
    team_one_percent INT DEFAULT 0,
    team_two_percent INT DEFAULT 0,
    region_id BIGINT,
    FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE SET NULL,
    INDEX idx_region (region_id),
    INDEX idx_match_date (match_start_date_time)
);

-- Create opinions table
CREATE TABLE IF NOT EXISTS opinions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME,
    team_one_percent INT NOT NULL,
    team_two_percent INT NOT NULL,
    comment TEXT,
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_game (game_id),
    INDEX idx_created (created_at)
);

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
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    confirmed_at DATETIME,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token)
);

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
ON DUPLICATE KEY UPDATE name=name;

-- Sample teams (optional - for testing)
INSERT INTO teams (name, tag, region, image) VALUES
('T1', 'T1', 'Korea', 'https://example.com/t1.png'),
('G2 Esports', 'G2', 'Europe', 'https://example.com/g2.png'),
('Cloud9', 'C9', 'North America', 'https://example.com/c9.png')
ON DUPLICATE KEY UPDATE name=name;
