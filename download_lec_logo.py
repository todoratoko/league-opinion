import requests
import os

# Try multiple possible URLs for LEC logo
LEC_URL_PATTERNS = [
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/8/86/LEClogo_square.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/8/86/LEClogo_square.png/220px-LEClogo_square.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/1/1f/LEC_2024_Winter.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/1/1f/LEC_2024_Winter.png/220px-LEC_2024_Winter.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/2/2e/LEC_logo.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/d/d5/LEC_2023.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/thumb/d/d5/LEC_2023.png/220px-LEC_2023.png',
    'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/3/30/LEC_Logo.png',
    # Try LoL Esports official CDN paths
    'https://am-a.akamaihd.net/image?resize=200:&f=http://static.lolesports.com/leagues/LEC-Logo-Regular.png',
    'https://static.lolesports.com/leagues/LEC-Logo-Regular.png',
    'https://lolstatic-a.akamaihd.net/esports-assets/production/league/lec-icon-8kjdsn29.png'
]

def download_image(url, filepath):
    """Download image from URL to filepath"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, allow_redirects=True, timeout=15)
        response.raise_for_status()

        # Check if it's actually an image
        content_type = response.headers.get('content-type', '')
        if 'image' not in content_type and len(response.content) < 1000:
            return False

        with open(filepath, 'wb') as f:
            f.write(response.content)

        file_size = os.path.getsize(filepath)
        print(f"✓ SUCCESS! Downloaded {filepath} ({file_size:,} bytes)")
        print(f"  URL: {url}")
        return True
    except Exception as e:
        return False

print("Trying to find LEC logo...\n")

filepath = 'src/main/resources/static/images/leagues/lec.png'
success = False

for i, url in enumerate(LEC_URL_PATTERNS, 1):
    print(f"[{i}/{len(LEC_URL_PATTERNS)}] Trying: {url[:80]}...")
    if download_image(url, filepath):
        success = True
        break

if not success:
    print("\n✗ Could not find working URL for LEC logo")
    print("\nLEC logo may need to be manually downloaded from:")
    print("- https://lol.fandom.com/wiki/File:LEClogo_square.png")
    print("- https://lolesports.com/ (check their media assets)")
else:
    print("\n" + "="*70)
    print("LEC logo downloaded successfully!")
    print("="*70)
