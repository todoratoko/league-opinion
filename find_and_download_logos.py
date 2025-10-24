import requests
import os

# Create directories
os.makedirs('src/main/resources/static/images/leagues', exist_ok=True)
os.makedirs('src/main/resources/static/images/tournaments', exist_ok=True)

# Known working URLs from WebFetch
CONFIRMED_URLS = {
    'tournaments/worlds-2025.png': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/2/22/Worlds_2025.png',
    'tournaments/msi-2025.png': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/b/bc/Mid-Season_Invitational_2025.png',
    'tournaments/first-stand.png': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/a/a2/First_Stand_logo.png',
    'leagues/lta.png': 'https://lol.fandom.com/wiki/Special:Redirect/file/LTA_logo.png'  # This one worked before
}

# Try multiple possible URL patterns for each league
LEAGUE_URL_PATTERNS = {
    'lck': [
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/2/2e/LCKlogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/2/2e/LCKlogo_square.png/220px-LCKlogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/f/f9/LCK_2023.png',
    ],
    'lpl': [
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/0/0a/LPLlogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/0/0a/LPLlogo_square.png/220px-LPLlogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/d/d3/LPL_2024.png',
    ],
    'lec': [
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/6/69/LEClogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/6/69/LEClogo_square.png/220px-LEClogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/b/b7/LEC_2023.png',
    ],
    'lcp': [
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/1/14/LCPlogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/1/14/LCPlogo_square.png/220px-LCPlogo_square.png',
        'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/3/3c/LCP_logo.png',
    ]
}

def download_image(url, filepath):
    """Download image from URL to filepath"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, allow_redirects=True, timeout=10)
        response.raise_for_status()

        # Check if it's actually an image
        content_type = response.headers.get('content-type', '')
        if 'image' not in content_type and len(response.content) < 1000:
            return False

        with open(filepath, 'wb') as f:
            f.write(response.content)

        file_size = os.path.getsize(filepath)
        print(f"✓ Downloaded {filepath} ({file_size:,} bytes) from {url}")
        return True
    except Exception as e:
        return False

print("Downloading confirmed tournament logos...")
for path, url in CONFIRMED_URLS.items():
    filepath = f'src/main/resources/static/images/{path}'
    download_image(url, filepath)

print("\nTrying to find league logos...")
for league_code, url_list in LEAGUE_URL_PATTERNS.items():
    filepath = f'src/main/resources/static/images/leagues/{league_code}.png'
    print(f"\nSearching for {league_code.upper()} logo...")

    success = False
    for url in url_list:
        print(f"  Trying: {url}")
        if download_image(url, filepath):
            success = True
            break

    if not success:
        print(f"  ✗ Could not find working URL for {league_code.upper()}")

print("\nChecking all downloaded files:")
for folder in ['leagues', 'tournaments']:
    folder_path = f'src/main/resources/static/images/{folder}'
    if os.path.exists(folder_path):
        for filename in sorted(os.listdir(folder_path)):
            if filename.endswith('.png'):
                filepath = os.path.join(folder_path, filename)
                size = os.path.getsize(filepath)
                print(f"  {folder}/{filename}: {size:,} bytes")
