#!/usr/bin/env python3
"""
Download league and tournament logos from Leaguepedia
"""

import os
import urllib.request
import urllib.error
import time
from html.parser import HTMLParser

# Directories
LEAGUES_DIR = "src/main/resources/static/images/leagues"
TOURNAMENTS_DIR = "src/main/resources/static/images/tournaments"

# League mappings (league code -> wiki page name)
LEAGUE_PAGES = {
    'lck': 'LCK',
    'lpl': 'LPL',
    'lec': 'LEC',
    'lta': 'LTA',
    'lcp': 'LCP'
}

# Tournament mappings
TOURNAMENT_PAGES = {
    'worlds-2025': '2025_Season_World_Championship',
    'msi-2025': '2025_Mid-Season_Invitational',
    'first-stand': '2025_First_Stand'
}

class ImageExtractor(HTMLParser):
    """Extract image URLs from HTML"""
    def __init__(self):
        super().__init__()
        self.image_url = None

    def handle_starttag(self, tag, attrs):
        attrs_dict = dict(attrs)
        if tag == 'img' or tag == 'a':
            # Look for logo images
            if 'src' in attrs_dict:
                src = attrs_dict['src']
                if 'lolesports_gamepedia_en/images' in src or 'leaguepedia' in src.lower():
                    if 'logo' in src.lower() or 'icon' in src.lower():
                        if not self.image_url:
                            self.image_url = src
            elif 'href' in attrs_dict:
                href = attrs_dict['href']
                if 'lolesports_gamepedia_en/images' in href and '.png' in href:
                    if 'logo' in href.lower():
                        if not self.image_url:
                            self.image_url = href

def fetch_page(url):
    """Fetch a webpage"""
    try:
        from urllib.parse import quote
        # URL encode the path to handle special characters
        if '/wiki/' in url:
            base = url.split('/wiki/')[0] + '/wiki/'
            path = url.split('/wiki/')[1]
            url = base + quote(path, safe='()_-/')

        req = urllib.request.Request(
            url,
            headers={'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'}
        )
        with urllib.request.urlopen(req, timeout=15) as response:
            return response.read().decode('utf-8')
    except Exception as e:
        print(f"✗ Error fetching page {url}: {str(e)}")
        return None

def extract_image_from_page(html):
    """Extract logo URL from HTML page"""
    if not html:
        return None

    parser = ImageExtractor()
    parser.feed(html)

    if parser.image_url:
        url = parser.image_url
        if url.startswith('//'):
            url = 'https:' + url
        elif url.startswith('/'):
            url = 'https://static.wikia.nocookie.net' + url

        # Get the latest revision
        if '/revision/latest' not in url:
            base_url = url.split('/revision/')[0] if '/revision/' in url else url
            url = base_url + '/revision/latest'

        return url
    return None

def download_image(url, save_path):
    """Download an image from URL and save it locally."""
    try:
        req = urllib.request.Request(
            url,
            headers={'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'}
        )
        with urllib.request.urlopen(req, timeout=15) as response:
            with open(save_path, 'wb') as out_file:
                out_file.write(response.read())
        print(f"✓ Downloaded: {os.path.basename(save_path)}")
        return True
    except Exception as e:
        print(f"✗ Error downloading {os.path.basename(save_path)}: {str(e)}")
        return False

def main():
    print("=" * 70)
    print("Downloading League and Tournament Logos from Leaguepedia")
    print("=" * 70)

    # Download league logos
    print("\n📁 Downloading League Logos...")
    print("-" * 70)

    for filename, wiki_page in LEAGUE_PAGES.items():
        url = f"https://lol.fandom.com/wiki/{wiki_page}"
        print(f"Fetching {filename.upper()} logo from {wiki_page}...")

        html = fetch_page(url)
        image_url = extract_image_from_page(html)

        if image_url:
            save_path = os.path.join(LEAGUES_DIR, f"{filename}.png")
            if download_image(image_url, save_path):
                time.sleep(1)  # Be nice to the server
        else:
            print(f"⚠ Could not find logo for {filename.upper()}")

    # Download tournament logos
    print("\n📁 Downloading Tournament Logos...")
    print("-" * 70)

    for filename, wiki_page in TOURNAMENT_PAGES.items():
        url = f"https://lol.fandom.com/wiki/{wiki_page}"
        print(f"Fetching {filename} logo from {wiki_page}...")

        html = fetch_page(url)
        image_url = extract_image_from_page(html)

        if image_url:
            save_path = os.path.join(TOURNAMENTS_DIR, f"{filename}.png")
            if download_image(image_url, save_path):
                time.sleep(1)
        else:
            print(f"⚠ Could not find logo for {filename}")

    print("\n" + "=" * 70)
    print("✓ Logo download complete!")
    print(f"  - League logos: {LEAGUES_DIR}/")
    print(f"  - Tournament logos: {TOURNAMENTS_DIR}/")
    print("=" * 70)

if __name__ == "__main__":
    main()
