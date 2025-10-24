# Manual Image Upload Guide

## For Team Logos

### Step 1: Download the Image
Download your team logo from any source (Leaguepedia, official team website, etc.)

### Step 2: Format & Naming

**Image Format:** PNG (recommended) or JPG
**Recommended size:** 200x200px to 500x500px
**File naming convention:** `{team-tag-lowercase}.png`

**Examples:**
- Team tag "T1" → filename: `t1.png`
- Team tag "GEN" → filename: `gen.png`
- Team tag "100T" → filename: `100t.png`
- Team tag "TSW" → filename: `tsw.png`

### Step 3: Save the File

Save the image to:
```
src/main/resources/static/images/teams/{team-tag-lowercase}.png
```

**Example:**
```bash
# If you downloaded t1-new-logo.png, rename and move it:
mv ~/Downloads/t1-new-logo.png src/main/resources/static/images/teams/t1.png
```

### Step 4: Update Database (Neon PostgreSQL)

The database needs to know the path to your image. Run this SQL command:

```sql
UPDATE teams
SET image = '/images/teams/{team-tag-lowercase}.png'
WHERE tag = '{TEAM-TAG}';
```

**Example for T1:**
```bash
psql "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require" \
  -c "UPDATE teams SET image = '/images/teams/t1.png' WHERE tag = 'T1';"
```

**Quick command template:**
```bash
/opt/homebrew/opt/postgresql@14/bin/psql "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require" -c "UPDATE teams SET image = '/images/teams/{filename}.png' WHERE tag = '{TAG}';"
```

### Step 5: Rebuild & Restart Spring Boot

The images are bundled into the JAR, so you need to rebuild:

```bash
# Stop current instance
lsof -ti:8081 | xargs kill -9

# Rebuild with new images
./mvnw clean package -DskipTests

# Restart
./mvnw spring-boot:run > spring-boot-neon.log 2>&1 &
```

### Step 6: Verify

Check if the image is accessible:
```bash
curl -I http://localhost:8081/images/teams/t1.png
```

Should return: `HTTP/1.1 200`

---

## For Player Photos

### Step 1: Download the Image
Download player photo (headshot or profile picture)

### Step 2: Format & Naming

**Image Format:** PNG (recommended) or JPG
**Recommended size:** 150x150px to 300x300px
**File naming convention:** `{team-tag-lowercase}-{player-name-lowercase}.png`

**Examples:**
- T1 player "Faker" → filename: `t1-faker.png`
- BLG player "Bin" → filename: `blg-bin.png`
- Gen.G player "Chovy" → filename: `gen-chovy.png`
- Player name with spaces: "Hans Sama" → `g2-hans-sama.png`

### Step 3: Save the File

Save the image to:
```
src/main/resources/static/images/players/{team-tag-lowercase}-{player-name-lowercase}.png
```

### Step 4: Update Database

```sql
UPDATE players
SET image = '/images/players/{team-tag-lowercase}-{player-name-lowercase}.png'
WHERE name = '{PLAYER-NAME}'
  AND team_id = (SELECT id FROM teams WHERE tag = '{TEAM-TAG}');
```

**Example for Faker:**
```bash
/opt/homebrew/opt/postgresql@14/bin/psql "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require" -c "UPDATE players SET image = '/images/players/t1-faker.png' WHERE name = 'Faker' AND team_id = (SELECT id FROM teams WHERE tag = 'T1');"
```

### Step 5: Rebuild & Restart

Same as team logos - rebuild and restart Spring Boot.

---

## Quick Reference: Current Teams

| Team Tag | Current Logo | Database Path |
|----------|--------------|---------------|
| 100T | 100t.png | /images/teams/100t.png |
| AL | al.png | /images/teams/al.png |
| BLG | blg.png | /images/teams/blg.png |
| CFO | cfo.png | /images/teams/cfo.png |
| FLY | fly.png | /images/teams/fly.png |
| FNC | fnc.png | /images/teams/fnc.png |
| FUR | fur.png | /images/teams/fur.png |
| G2 | g2.png | /images/teams/g2.png |
| GAM | gam.png | /images/teams/gam.png |
| GEN | gen.png | /images/teams/gen.png |
| HLE | hle.png | /images/teams/hle.png |
| IG | ig.png | /images/teams/ig.png |
| KOI | koi.png | /images/teams/koi.png |
| KT | kt.png | /images/teams/kt.png |
| PSG | psg.png | /images/teams/psg.png |
| T1 | t1.png | /images/teams/t1.png |
| TES | tes.png | /images/teams/tes.png |
| TSW | tsw.png | /images/teams/tsw.png |
| VKS | vks.png | /images/teams/vks.png |

---

## Common Issues

**Issue:** Image shows but looks blurry
- **Solution:** Use a higher resolution image (at least 200x200px)

**Issue:** Image doesn't show after uploading
- **Solution:** Make sure you rebuilt and restarted Spring Boot

**Issue:** 404 error when accessing image
- **Solution:** Check filename matches exactly (lowercase, correct extension)

**Issue:** Old image still showing in browser
- **Solution:** Hard refresh browser (Cmd+Shift+R on Mac, Ctrl+Shift+R on Windows)

---

## Example: Replacing T1 Logo

Complete example of replacing T1's logo:

```bash
# 1. Download new logo (let's say it's called new-t1-logo.png)

# 2. Rename and move to correct location
cp ~/Downloads/new-t1-logo.png src/main/resources/static/images/teams/t1.png

# 3. Update database
/opt/homebrew/opt/postgresql@14/bin/psql "postgresql://neondb_owner:npg_Zp2Y8UmTSeJB@ep-quiet-resonance-ag50l6u2-pooler.c-2.eu-central-1.aws.neon.tech/neondb?sslmode=require" -c "UPDATE teams SET image = '/images/teams/t1.png' WHERE tag = 'T1';"

# 4. Rebuild Spring Boot
lsof -ti:8081 | xargs kill -9
./mvnw clean package -DskipTests
./mvnw spring-boot:run > spring-boot-neon.log 2>&1 &

# 5. Test (wait 10 seconds for startup)
sleep 10
curl -I http://localhost:8081/images/teams/t1.png

# 6. Open test page
open test-images.html
```

Done! Your new T1 logo should now appear.
