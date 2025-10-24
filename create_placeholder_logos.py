from PIL import Image, ImageDraw, ImageFont
import os

# Directory where logos will be saved
output_dir = "/Users/todortodorov/IdeaProjects/LeagueOpinion/uploads/images/leagues"

# League names and their colors
leagues = {
    "worlds.png": {"name": "WORLDS", "color": "#0A6FA7"},
    "msi.png": {"name": "MSI", "color": "#C28F2C"},
    "lck.png": {"name": "LCK", "color": "#ED1C24"},
    "lpl.png": {"name": "LPL", "color": "#FF4655"},
    "lec.png": {"name": "LEC", "color": "#1C4587"},
    "lta.png": {"name": "LTA", "color": "#1565C0"},
    "lcp.png": {"name": "LCP", "color": "#0097A7"},
    "emea.png": {"name": "EMEA", "color": "#5E35B1"}
}

print("Creating placeholder league logos...")

for filename, data in leagues.items():
    try:
        # Create 400x400 image
        img = Image.new('RGB', (400, 400), color='#0F1923')
        draw = ImageDraw.Draw(img)

        # Draw colored circle background
        margin = 50
        draw.ellipse([margin, margin, 400-margin, 400-margin], fill=data["color"])

        # Try to use a nice font, fall back to default if not available
        try:
            font = ImageFont.truetype("/System/Library/Fonts/Helvetica.ttc", 80)
        except:
            try:
                font = ImageFont.truetype("/Library/Fonts/Arial.ttf", 80)
            except:
                font = ImageFont.load_default()

        # Get text size and center it
        text = data["name"]
        bbox = draw.textbbox((0, 0), text, font=font)
        text_width = bbox[2] - bbox[0]
        text_height = bbox[3] - bbox[1]

        position = ((400 - text_width) / 2, (400 - text_height) / 2 - 10)

        # Draw white text
        draw.text(position, text, fill='#FFFFFF', font=font)

        # Save image
        file_path = os.path.join(output_dir, filename)
        img.save(file_path, 'PNG')

        print(f"✓ Created {filename}")

    except Exception as e:
        print(f"✗ Error creating {filename}: {str(e)}")

print("\n" + "="*50)
print("Placeholder logos created! Checking files...")
print("="*50)

# Verify all files
for filename in leagues.keys():
    file_path = os.path.join(output_dir, filename)
    if os.path.exists(file_path):
        size = os.path.getsize(file_path)
        print(f"✓ {filename}: {size} bytes")
    else:
        print(f"✗ {filename}: NOT FOUND")
