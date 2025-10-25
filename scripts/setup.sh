#!/bin/bash

# Setup script for OddsPortal scraper
# This script creates a virtual environment and installs dependencies

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

echo "Setting up OddsPortal scraper..."

# Check if Python 3 is installed
if ! command -v python3 &> /dev/null; then
    echo "Error: Python 3 is not installed"
    exit 1
fi

echo "Python version: $(python3 --version)"

# Create virtual environment if it doesn't exist
if [ ! -d "$SCRIPT_DIR/venv" ]; then
    echo "Creating virtual environment..."
    python3 -m venv "$SCRIPT_DIR/venv"
else
    echo "Virtual environment already exists"
fi

# Activate virtual environment and install dependencies
echo "Installing dependencies..."
source "$SCRIPT_DIR/venv/bin/activate"
pip install --upgrade pip
pip install -r "$SCRIPT_DIR/requirements.txt"

echo "Setup complete!"
echo ""
echo "To test the scraper, run:"
echo "  cd $SCRIPT_DIR"
echo "  source venv/bin/activate"
echo "  python3 scrape_oddsportal.py"
