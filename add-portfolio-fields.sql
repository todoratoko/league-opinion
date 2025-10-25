-- Migration: Add portfolio management fields to users table
-- Date: 2025-10-25
-- Description: Adds portfolio_size, min_edge, and min_win columns to support betting portfolio management

-- Add portfolio_size column (amount of money in user's account)
ALTER TABLE users ADD COLUMN IF NOT EXISTS portfolio_size DOUBLE PRECISION;

-- Add min_edge column (minimum edge threshold as percentage, e.g., 5.0 for 5%)
ALTER TABLE users ADD COLUMN IF NOT EXISTS min_edge DOUBLE PRECISION;

-- Add min_win column (minimum win as percentage of portfolio, e.g., 2.0 for 2%)
ALTER TABLE users ADD COLUMN IF NOT EXISTS min_win DOUBLE PRECISION;

-- Add comments for documentation
COMMENT ON COLUMN users.portfolio_size IS 'Amount of money in user''s betting account';
COMMENT ON COLUMN users.min_edge IS 'Minimum edge threshold as percentage (e.g., 5.0 for 5%)';
COMMENT ON COLUMN users.min_win IS 'Minimum win as percentage of portfolio (e.g., 2.0 for 2%)';

-- Optional: Set default values for existing users (NULL means not configured yet)
-- UPDATE users SET portfolio_size = NULL WHERE portfolio_size IS NULL;
-- UPDATE users SET min_edge = NULL WHERE min_edge IS NULL;
-- UPDATE users SET min_win = NULL WHERE min_win IS NULL;
