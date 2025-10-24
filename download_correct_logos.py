import requests
import os

# Create directories
os.makedirs('src/main/resources/static/images/leagues', exist_ok=True)
os.makedirs('src/main/resources/static/images/tournaments', exist_ok=True)

# Direct image URLs from Leaguepedia - these are the correct logo files
LOGOS = {
    'leagues': {
        'lck': 'https://lol.fandom.com/wiki/Special:Redirect/file/LCK_2024_logo.png',
        'lpl': 'https://lol.fandom.com/wiki/Special:Redirect/file/LPL_2023_logo.png',
        'lec': 'https://lol.fandom.com/wiki/Special:Redirect/file/LEC_2023_logo.png',
        'lta': 'https://lol.fandom.com/wiki/Special:Redirect/file/LTA_logo.png',
        'lcp': 'https://lol.fandom.com/wiki/Special:Redirect/file/LCP_logo.png'
    },
    'tournaments': {
        'worlds-2025': 'https://lol.fandom.com/wiki/Special:Redirect/file/Worlds_2024_logo.png',
        'msi-2025': 'https://lol.fandom.com/wiki/Special:Redirect/file/MSI_2024_logo.png',
        'first-stand': 'https://lol.fandom.com/wiki/Special:Redirect/file/First_Stand_2025_logo.png'
    }
}

def download_image(url, filepath):
    """Download image from URL to filepath"""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        response = requests.get(url, headers=headers, allow_redirects=True)
        response.raise_for_status()

        with open(filepath, 'wb') as f:
            f.write(response.content)

        file_size = os.path.getsize(filepath)
        print(f"✓ Downloaded {filepath} ({file_size} bytes)")
        return True
    except Exception as e:
        print(f"✗ Failed to download {url}: {e}")
        return False

print("Downloading league logos...")
for code, url in LOGOS['leagues'].items():
    filepath = f'src/main/resources/static/images/leagues/{code}.png'
    download_image(url, filepath)

print("\nDownloading tournament logos...")
for code, url in LOGOS['tournaments'].items():
    filepath = f'src/main/resources/static/images/tournaments/{code}.png'
    download_image(url, filepath)

print("\nDone! All logos downloaded.")
