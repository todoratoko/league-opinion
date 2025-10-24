import requests
import os

# Directory where logos will be saved
output_dir = "/Users/todortodorov/IdeaProjects/LeagueOpinion/uploads/images/leagues"

# League logo URLs from LoL Fandom Wiki (static.wikia.nocookie.net)
league_logos = {
    "worlds.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/3/30/Worlds_2024_Logo.png",
    "msi.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/0/0e/MSI_2024_Logo.png",
    "lck.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/4/4a/LCK_2024_Logo.png",
    "lpl.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/9/98/LPL_2024_Logo.png",
    "lec.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/1/1e/LEC_2024_Logo.png",
    "lta.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/c/ce/LTA_2024_Logo.png",
    "lcp.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/8/8a/LCP_2024_Logo.png",
    "emea.png": "https://static.wikia.nocookie.net/lolesports_gamepedia_en/images/6/6a/EMEA_Masters_2024_Spring_Logo.png"
}

print("Starting league logo download...")

for filename, url in league_logos.items():
    try:
        print(f"\nDownloading {filename} from {url}")

        headers = {
            'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        }

        response = requests.get(url, headers=headers, timeout=30)

        if response.status_code == 200:
            file_path = os.path.join(output_dir, filename)
            with open(file_path, 'wb') as f:
                f.write(response.content)
            print(f"✓ Successfully downloaded {filename} ({len(response.content)} bytes)")
        else:
            print(f"✗ Failed to download {filename}: HTTP {response.status_code}")

    except Exception as e:
        print(f"✗ Error downloading {filename}: {str(e)}")

print("\n" + "="*50)
print("Download complete! Checking files...")
print("="*50)

# Verify all files
for filename in league_logos.keys():
    file_path = os.path.join(output_dir, filename)
    if os.path.exists(file_path):
        size = os.path.getsize(file_path)
        print(f"✓ {filename}: {size} bytes")
    else:
        print(f"✗ {filename}: NOT FOUND")
