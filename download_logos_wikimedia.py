import requests
import os

# Create directories
os.makedirs('src/main/resources/static/images/leagues', exist_ok=True)
os.makedirs('src/main/resources/static/images/tournaments', exist_ok=True)

# URLs from Wikimedia Commons and other sources
LOGO_URLS = {
    # Leagues - from Wikimedia Commons
    'leagues/lck.png': 'https://upload.wikimedia.org/wikipedia/commons/thumb/1/13/League_of_Legends_Champions_Korea_logo.svg/1024px-League_of_Legends_Champions_Korea_logo.svg.png',
    'leagues/lcp.png': 'https://upload.wikimedia.org/wikipedia/commons/thumb/a/a3/League_of_Legends_Championship_Pacific_logo.svg/1024px-League_of_Legends_Championship_Pacific_logo.svg.png',
    'leagues/lpl.png': 'https://upload.wikimedia.org/wikipedia/pt/thumb/b/b4/League_of_legends_pro_league_logo.svg/640px-League_of_legends_pro_league_logo.svg.png',

    # Tournaments - already found from Leaguepedia
    'tournaments/worlds-2025.png': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/2/22/Worlds_2025.png',
    'tournaments/msi-2025.png': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/b/bc/Mid-Season_Invitational_2025.png',
    'tournaments/first-stand.png': 'https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/a/a2/First_Stand_logo.png',

    # LTA - already found working
    'leagues/lta.png': 'https://lol.fandom.com/wiki/Special:Redirect/file/LTA_logo.png'
}

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
            print(f"  ✗ Not an image: {content_type}")
            return False

        with open(filepath, 'wb') as f:
            f.write(response.content)

        file_size = os.path.getsize(filepath)
        print(f"  ✓ Downloaded {filepath} ({file_size:,} bytes)")
        return True
    except Exception as e:
        print(f"  ✗ Failed: {e}")
        return False

print("Downloading logos from Wikimedia Commons and other sources...\n")

for path, url in LOGO_URLS.items():
    filepath = f'src/main/resources/static/images/{path}'
    print(f"{path}:")
    download_image(url, filepath)

print("\n" + "="*70)
print("Summary of downloaded files:")
print("="*70)

for folder in ['leagues', 'tournaments']:
    folder_path = f'src/main/resources/static/images/{folder}'
    if os.path.exists(folder_path):
        print(f"\n{folder.upper()}:")
        for filename in sorted(os.listdir(folder_path)):
            if filename.endswith('.png'):
                filepath = os.path.join(folder_path, filename)
                size = os.path.getsize(filepath)
                status = "✓" if size > 10000 else "⚠"
                print(f"  {status} {filename}: {size:,} bytes")

print("\nNote: Files with ⚠ may need to be re-downloaded")
