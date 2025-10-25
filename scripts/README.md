# OddsPortal Scraper for League of Legends

This directory contains a Python web scraper that fetches betting odds for League of Legends matches from OddsPortal.com.

## Setup

### Quick Setup
Run the setup script to create a virtual environment and install dependencies:

```bash
cd scripts
./setup.sh
```

### Manual Setup
If you prefer to set up manually:

```bash
cd scripts
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

## Requirements

- Python 3.7+
- Chrome browser (for Selenium WebDriver)
- Internet connection

## Dependencies

- `selenium>=4.15.0` - Web browser automation
- `webdriver-manager>=4.0.1` - Automatic WebDriver management

## Usage

### From Command Line

```bash
cd scripts
source venv/bin/activate
python3 scrape_oddsportal.py
```

The scraper will output JSON to stdout with the following structure:

```json
{
  "success": true,
  "matches": [
    {
      "team1": "T1",
      "team2": "Gen.G",
      "team1Odds": 1.85,
      "team2Odds": 1.95,
      "date": "25 Oct, 15:00",
      "source": "oddsportal",
      "scrapedAt": "2025-10-24T14:30:00"
    }
  ],
  "count": 1,
  "timestamp": "2025-10-24T14:30:00"
}
```

### From Java Application

The scraper is automatically called by the Spring Boot application:

1. **Scheduled Task**: Runs every 4 hours automatically
2. **Manual Trigger**: POST to `/odds/scrape` to run manually

## How It Works

1. The scraper launches a headless Chrome browser
2. Navigates to OddsPortal's League of Legends section
3. Extracts match information and betting odds
4. Outputs structured JSON data
5. Java service matches teams by name and updates the database

## Important Notes

### Legal & Ethical Considerations

⚠️ **WARNING**: Web scraping may violate OddsPortal's Terms of Service. This scraper is for educational purposes only.

- **Rate Limiting**: The scraper includes delays to avoid overloading the server
- **User-Agent**: Uses a realistic browser user-agent
- **Robots.txt**: Please check OddsPortal's robots.txt before using
- **Terms of Service**: Review their ToS - they may prohibit scraping

### Maintenance

OddsPortal may change their website structure at any time, which could break the scraper. If the scraper stops working:

1. Check if the website structure has changed
2. Update CSS selectors in `scrape_oddsportal.py`
3. Test with manual runs before relying on scheduled tasks

### Limitations

- Only scrapes upcoming matches (not historical data)
- Limited to first 20 matches to avoid long execution times
- Team name matching is fuzzy - may miss some matches
- Requires Chrome browser to be installed

## Troubleshooting

### "Chrome not found" Error
Install Chrome browser or update the WebDriver settings.

### "No matches found" Error
- OddsPortal may have changed their HTML structure
- Check if League of Legends section is still at the same URL
- Verify internet connection

### "Module not found" Error
Make sure you've activated the virtual environment:
```bash
source venv/bin/activate
```

### Java Application Can't Find Python
The Java service looks for `scripts/venv/bin/python3` first, then falls back to system `python3`. Make sure the venv is set up correctly.

## Development

To modify the scraper:

1. Edit `scrape_oddsportal.py`
2. Test changes manually: `python3 scrape_oddsportal.py`
3. Check output JSON format
4. Restart Spring Boot application to use updated scraper

## Alternative: Community Odds

If web scraping is not suitable for your needs, consider implementing community-calculated odds based on user predictions instead. This would be:
- Free and sustainable
- Legal and ethical
- A unique feature
- More engaging for users
