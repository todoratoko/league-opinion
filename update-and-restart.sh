#!/bin/bash

# Script to update team logos in Neon database and restart Spring Boot
# For teams: FLY, G2, IG, TSW, VKS

set -e

echo "================================================"
echo "  Updating Team Logos & Restarting Server"
echo "================================================"
echo ""

# Database connection
DB_CONN="postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require"

# Teams to update
TEAMS=("FLY" "G2" "IG" "TSW" "VKS")

echo "📋 Teams to update:"
for team in "${TEAMS[@]}"; do
    team_lower=$(echo "$team" | tr '[:upper:]' '[:lower:]')
    if [ -f "src/main/resources/static/images/teams/${team_lower}.png" ]; then
        size=$(ls -lh "src/main/resources/static/images/teams/${team_lower}.png" | awk '{print $5}')
        echo "  ✓ $team - ${team_lower}.png ($size)"
    else
        echo "  ⚠️  $team - ${team_lower}.png (FILE NOT FOUND!)"
    fi
done

echo ""
read -p "Continue with database update? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cancelled."
    exit 0
fi

# Update database for each team
echo ""
echo "💾 Updating Neon database..."
echo ""

for team in "${TEAMS[@]}"; do
    team_lower=$(echo "$team" | tr '[:upper:]' '[:lower:]')
    db_path="/images/teams/${team_lower}.png"

    echo "Updating $team..."
    /opt/homebrew/opt/postgresql@14/bin/psql "$DB_CONN" \
        -c "UPDATE teams SET image = '$db_path' WHERE tag = '$team';"

    echo "✓ $team updated to $db_path"
done

echo ""
echo "✅ All team logos updated in database!"

# Rebuild and restart
echo ""
echo "🔄 Rebuilding and restarting Spring Boot..."
echo ""

echo "🛑 Stopping current Spring Boot instance..."
lsof -ti:8081 | xargs kill -9 2>/dev/null || echo "No running instance found"

echo ""
echo "🔨 Building project (packaging new images)..."
./mvnw clean package -DskipTests

echo ""
echo "🚀 Starting Spring Boot..."
./mvnw spring-boot:run > spring-boot-neon.log 2>&1 &

echo ""
echo "⏳ Waiting for server to start (15 seconds)..."
sleep 15

# Verify images are accessible
echo ""
echo "✅ Verifying images are accessible..."
echo ""

for team in "${TEAMS[@]}"; do
    team_lower=$(echo "$team" | tr '[:upper:]' '[:lower:]')
    url="http://localhost:8081/images/teams/${team_lower}.png"

    if curl -s -I "$url" | grep -q "200"; then
        size=$(curl -s "$url" | wc -c)
        echo "✓ $team - Accessible (${size} bytes)"
    else
        echo "❌ $team - NOT accessible yet"
    fi
done

echo ""
echo "================================================"
echo "  ✓ Complete!"
echo "================================================"
echo ""
echo "Updated teams: FLY, G2, IG, TSW, VKS"
echo ""
echo "🌐 Opening test page to verify..."
sleep 2
open test-images.html

echo ""
echo "Server is running. Check the test page!"
echo "Logs: tail -f spring-boot-neon.log"
