# Neon PostgreSQL Setup Guide - LeagueOpinion

This guide will walk you through setting up Neon PostgreSQL for your LeagueOpinion application.

## Why Neon?

✅ **5GB total storage** (10 projects × 0.5GB each)
✅ **Permanent free tier** - No time limits
✅ **Serverless** - Scales to zero when not in use
✅ **100 compute hours/month** per project
✅ **No credit card required**
✅ **Modern developer experience**

---

## Step 1: Create Neon Account (2 minutes)

1. **Go to Neon website:**
   - Visit: https://neon.tech/
   - Click "Sign Up" or "Get Started"

2. **Sign up with GitHub (recommended):**
   - Click "Continue with GitHub"
   - Authorize Neon to access your GitHub account
   - Complete the signup process

   *Alternative: Sign up with email*

3. **Verify your email** (if using email signup)

---

## Step 2: Create Your First Project (1 minute)

1. **After signing in**, you'll see the Neon Console
2. Click **"Create Project"** or **"New Project"**
3. **Configure your project:**
   - **Project Name**: `LeagueOpinion` (or any name you prefer)
   - **Region**: Choose closest to you or your users
     - `US East (N. Virginia)` - For North America
     - `Europe (Frankfurt)` - For Europe
     - `Asia Pacific (Singapore)` - For Asia
   - **PostgreSQL Version**: Keep default (16 or latest)
   - **Compute Size**: Keep default (0.25 vCPU - perfect for free tier)

4. Click **"Create Project"**

5. **Wait ~10 seconds** for the project to be created

---

## Step 3: Get Connection Details (1 minute)

Once your project is created, you'll see the connection details screen.

### Connection String Format

You'll see a connection string like this:
```
postgresql://username:password@ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require
```

### Break Down the Connection String:

```
postgresql://[USERNAME]:[PASSWORD]@[HOST]/[DATABASE]?sslmode=require
```

- **Username**: Usually `username` or your GitHub username
- **Password**: Auto-generated password (looks like: `ep-abc123xyz`)
- **Host**: Something like `ep-cool-name-123456.us-east-2.aws.neon.tech`
- **Database**: Default is `neondb`

### Save These Details:

**Option 1: Copy Full Connection String**
- Click the copy icon next to the connection string
- Save it in a secure location

**Option 2: Note Individual Components**
```
Host: ep-cool-name-123456.us-east-2.aws.neon.tech
Database: neondb
Username: username
Password: ep-abc123xyz
```

---

## Step 4: Initialize Database Schema (3 minutes)

### Method 1: Using Neon SQL Editor (Recommended)

1. **In Neon Console**, click on your project
2. Go to **"SQL Editor"** tab (left sidebar)
3. You'll see a SQL query editor

4. **Copy the PostgreSQL schema**:
   - On your computer, open: `database-schema-postgresql.sql`
   - Copy ALL the contents (Cmd+A, Cmd+C on Mac / Ctrl+A, Ctrl+C on Windows)

5. **Paste into Neon SQL Editor**:
   - Click in the SQL Editor
   - Paste the schema (Cmd+V / Ctrl+V)

6. **Run the schema**:
   - Click **"Run"** button (or press Cmd+Enter / Ctrl+Enter)
   - Wait for execution (~5-10 seconds)

7. **Verify tables were created**:
   - In the left sidebar, click **"Tables"**
   - You should see: `regions`, `users`, `teams`, `players`, `games`, `opinions`, etc.

### Method 2: Using psql Command Line

If you prefer command line:

```bash
# Install psql if you don't have it
# Mac: brew install postgresql
# Ubuntu: sudo apt-get install postgresql-client

# Connect to Neon
psql "postgresql://username:password@ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require"

# Run the schema
\i database-schema-postgresql.sql

# List tables to verify
\dt

# Exit
\q
```

---

## Step 5: Configure Spring Boot Backend (2 minutes)

### Update `application-cloud.properties`

1. **Open**: `src/main/resources/application-cloud.properties`

2. **Replace the placeholder values** with your Neon connection details:

```properties
### Database - NEON POSTGRESQL CONFIGURATION
spring.datasource.url=jdbc:postgresql://ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=username
spring.datasource.password=ep-abc123xyz
spring.datasource.driver-class-name=org.postgresql.Driver
```

**Example with real values:**
```properties
spring.datasource.url=jdbc:postgresql://ep-cool-sunset-a5b3c2d1.us-east-2.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=todorov_github
spring.datasource.password=ep-Vc7KjW9mZx2Pq4Rt8Ny5
spring.datasource.driver-class-name=org.postgresql.Driver
```

3. **Save the file**

---

## Step 6: Build and Run Your Backend (2 minutes)

### Install Dependencies

```bash
# Install PostgreSQL driver
./mvnw clean install
```

This will download the PostgreSQL JDBC driver.

### Run with Cloud Profile

```bash
# Run Spring Boot with cloud profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud
```

Or in IntelliJ IDEA:
1. Right-click your main application class
2. Select "Modify Run Configuration"
3. In "Active profiles", enter: `cloud`
4. Click "Apply" and "Run"

### Check the Logs

You should see:
```
Hibernate:
    create table if not exists regions (...)

Successfully acquired change log lock
...
Started DemoApplication in 3.456 seconds
```

✅ **Backend is now connected to Neon!**

---

## Step 7: Test Your API (1 minute)

### Test in Browser

Open: http://localhost:8081/regions

You should see JSON:
```json
[
  {
    "id": 1,
    "name": "North America",
    "code": "NA"
  },
  {
    "id": 2,
    "name": "Europe",
    "code": "EU"
  },
  ...
]
```

### Test with curl

```bash
# Get all regions
curl http://localhost:8081/regions

# Get all games
curl http://localhost:8081/games

# Get games by region
curl http://localhost:8081/games?regionId=1
```

✅ **Everything is working!**

---

## Step 8: Verify Data in Neon Console

1. **Go back to Neon Console**
2. Click **"Tables"** in left sidebar
3. Click on **"regions"** table
4. You should see 12 rows with all regions

You can also run queries:
```sql
SELECT * FROM regions;
SELECT * FROM games;
SELECT * FROM opinions;
```

---

## Important Notes

### Free Tier Limits

- **Storage**: 0.5 GB per project
- **Total Storage**: 5 GB across 10 projects
- **Compute**: 100 hours/month per project
- **Connections**: Default connection limit
- **Branches**: Not available on free tier

### Scale to Zero

Neon automatically **suspends compute** after 5 minutes of inactivity. This saves your compute hours!

**What this means:**
- First request after inactivity: ~1-2 second delay (cold start)
- Subsequent requests: Fast as normal
- Your data is never lost

### Monitoring Usage

**Check your usage:**
1. Go to Neon Console
2. Click on **"Settings"** → **"Usage"**
3. See storage and compute hours used

---

## Troubleshooting

### Error: "Connection refused"

**Solution:**
- Verify connection string is correct
- Check that `?sslmode=require` is at the end
- Ensure no extra spaces in the URL

### Error: "Password authentication failed"

**Solution:**
- Double-check username and password
- Get a fresh connection string from Neon Console:
  - Go to your project
  - Click "Connection Details"
  - Copy the connection string again

### Error: "SSL connection is required"

**Solution:**
- Ensure your connection URL ends with `?sslmode=require`
- Neon requires SSL connections

### Tables not showing up

**Solution:**
1. Verify schema was executed successfully
2. Check Neon Console → SQL Editor → History
3. Look for errors in the execution history
4. Re-run `database-schema-postgresql.sql`

### "Too many connections" error

**Solution:**
- Free tier has connection limits
- Make sure to close connections properly
- Restart your Spring Boot app
- Check for connection leaks in your code

---

## Environment Variables (Optional - Better Security)

Instead of putting credentials in `application-cloud.properties`, use environment variables:

### Create `.env` file (don't commit to Git):

```bash
NEON_DB_URL=jdbc:postgresql://ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require
NEON_DB_USERNAME=username
NEON_DB_PASSWORD=ep-abc123xyz
```

### Update `application-cloud.properties`:

```properties
spring.datasource.url=${NEON_DB_URL}
spring.datasource.username=${NEON_DB_USERNAME}
spring.datasource.password=${NEON_DB_PASSWORD}
```

### Set environment variables:

**Mac/Linux:**
```bash
export NEON_DB_URL="jdbc:postgresql://..."
export NEON_DB_USERNAME="username"
export NEON_DB_PASSWORD="ep-abc123xyz"
```

**Windows:**
```cmd
set NEON_DB_URL=jdbc:postgresql://...
set NEON_DB_USERNAME=username
set NEON_DB_PASSWORD=ep-abc123xyz
```

---

## Deploying to Production

When deploying your backend (Railway, Render, etc.):

1. **Add environment variables** in your hosting platform:
   - `SPRING_PROFILES_ACTIVE=cloud`
   - `NEON_DB_URL=jdbc:postgresql://...`
   - `NEON_DB_USERNAME=...`
   - `NEON_DB_PASSWORD=...`

2. **Update CORS** in `CorsConfig.java`:
   ```java
   config.setAllowedOrigins(Arrays.asList(
       "http://localhost:3000",
       "https://your-frontend.vercel.app"
   ));
   ```

3. **Deploy and test**

---

## Neon Features You Can Use

### Branching (Paid tier)

Create database branches for testing:
- Like Git branches for your database
- Test schema changes safely
- Not available on free tier

### Metrics

View database metrics:
- Query performance
- Connection stats
- Storage usage

### Backups

Free tier includes:
- 6 hours of restore history
- Point-in-time recovery within that window

---

## Connection Pooling Best Practices

In `application-cloud.properties`, add:

```properties
# Connection pool settings
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

This optimizes connections for Neon's free tier limits.

---

## Next Steps

✅ **You're all set!** Your backend is now running on Neon PostgreSQL.

**What to do next:**

1. ✅ Test all API endpoints locally
2. ✅ Connect your React frontend
3. ✅ Add more data via your API
4. ✅ Deploy backend to Railway/Render
5. ✅ Deploy frontend to Vercel

---

## Helpful Commands

```bash
# Build project
./mvnw clean install

# Run with cloud profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud

# Run tests
./mvnw test

# Package for deployment
./mvnw clean package -DskipTests

# Connect to Neon with psql
psql "postgresql://username:password@host/database?sslmode=require"
```

---

## Resources

- **Neon Dashboard**: https://console.neon.tech/
- **Neon Documentation**: https://neon.tech/docs
- **Neon Discord**: https://discord.gg/neon
- **PostgreSQL Docs**: https://www.postgresql.org/docs/

---

## Support

If you encounter issues:

1. Check Neon Console for connection status
2. Review Spring Boot logs for detailed errors
3. Verify schema was executed correctly
4. Test connection with `psql` directly
5. Check Neon Discord for community help

---

**Congratulations! 🎉 You're now using Neon PostgreSQL with 5GB of free storage!**
