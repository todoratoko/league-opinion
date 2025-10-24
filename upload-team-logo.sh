#!/bin/bash

# Helper script to upload a team logo
# Usage: ./upload-team-logo.sh <image-file> <team-tag>
# Example: ./upload-team-logo.sh ~/Downloads/t1-logo.png T1

set -e

if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <image-file> <team-tag>"
    echo "Example: $0 ~/Downloads/t1-logo.png T1"
    exit 1
fi

IMAGE_FILE="$1"
TEAM_TAG="$2"
TEAM_TAG_LOWER=$(echo "$TEAM_TAG" | tr '[:upper:]' '[:lower:]')

# Check if image file exists
if [ ! -f "$IMAGE_FILE" ]; then
    echo "❌ Error: Image file '$IMAGE_FILE' not found!"
    exit 1
fi

# Get file extension
EXT="${IMAGE_FILE##*.}"
if [ "$EXT" != "png" ] && [ "$EXT" != "jpg" ] && [ "$EXT" != "jpeg" ]; then
    echo "⚠️  Warning: Image is not PNG/JPG. Recommended format is PNG."
fi

TARGET_FILE="src/main/resources/static/images/teams/${TEAM_TAG_LOWER}.png"
DB_PATH="/images/teams/${TEAM_TAG_LOWER}.png"

echo "================================================"
echo "  Team Logo Upload Helper"
echo "================================================"
echo ""
echo "Team Tag: $TEAM_TAG"
echo "Source File: $IMAGE_FILE"
echo "Target File: $TARGET_FILE"
echo "Database Path: $DB_PATH"
echo ""

# Create directory if it doesn't exist
mkdir -p src/main/resources/static/images/teams

# Copy the file
echo "📁 Copying image file..."
cp "$IMAGE_FILE" "$TARGET_FILE"
echo "✓ Image copied to $TARGET_FILE"

# Update database
echo ""
echo "💾 Updating Neon database..."
/opt/homebrew/opt/postgresql@14/bin/psql \
  "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require" \
  -c "UPDATE teams SET image = '$DB_PATH' WHERE tag = '$TEAM_TAG';"

echo "✓ Database updated"

# Ask if user wants to rebuild
echo ""
read -p "🔄 Rebuild and restart Spring Boot? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "🛑 Stopping Spring Boot..."
    lsof -ti:8081 | xargs kill -9 2>/dev/null || echo "No running instance found"

    echo "🔨 Rebuilding..."
    ./mvnw clean package -DskipTests

    echo "🚀 Starting Spring Boot..."
    ./mvnw spring-boot:run > spring-boot-neon.log 2>&1 &

    echo "⏳ Waiting for startup (10 seconds)..."
    sleep 10

    echo ""
    echo "✅ Testing image accessibility..."
    if curl -s -I "http://localhost:8081$DB_PATH" | grep -q "200"; then
        echo "✓ Image is accessible at: http://localhost:8081$DB_PATH"
    else
        echo "⚠️  Warning: Image may not be accessible yet. Wait a bit longer."
    fi

    echo ""
    echo "🌐 Opening test page..."
    open test-images.html
fi

echo ""
echo "================================================"
echo "  ✓ Done!"
echo "================================================"
echo ""
echo "Your team logo has been uploaded successfully!"
echo "If you didn't rebuild, remember to run:"
echo "  ./mvnw clean package -DskipTests"
echo "  ./mvnw spring-boot:run"
