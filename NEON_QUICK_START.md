# 🚀 Neon Quick Start - 5 Minutes to Running

Follow these steps to get your backend connected to Neon PostgreSQL.

---

## Step 1: Create Neon Account (2 min)

1. Go to: **https://neon.tech/**
2. Click **"Sign Up"**
3. Choose **"Continue with GitHub"**
4. Authorize Neon

✅ Done!

---

## Step 2: Create Project (1 min)

1. Click **"Create Project"**
2. Name: `LeagueOpinion`
3. Region: Choose closest to you
4. Click **"Create Project"**

You'll see a connection string like:
```
postgresql://user:pass@ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require
```

**Copy this entire string!** 📋

---

## Step 3: Initialize Database (1 min)

1. In Neon Console, click **"SQL Editor"** (left sidebar)
2. On your computer, open: `database-schema-postgresql.sql`
3. Copy ALL contents (Cmd+A, Cmd+C)
4. Paste into Neon SQL Editor
5. Click **"Run"** button
6. Wait 10 seconds

✅ Tables created!

---

## Step 4: Configure Backend (1 min)

1. Open: `src/main/resources/application-cloud.properties`
2. Find these lines:
   ```properties
   spring.datasource.url=jdbc:postgresql://YOUR_NEON_HOST/YOUR_DATABASE_NAME?sslmode=require
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```

3. Replace with YOUR Neon details:

**Example:**
```properties
# From connection string:
# postgresql://myuser:mypass@ep-abc-123.us-east-2.aws.neon.tech/neondb?sslmode=require

# Becomes:
spring.datasource.url=jdbc:postgresql://ep-abc-123.us-east-2.aws.neon.tech/neondb?sslmode=require
spring.datasource.username=myuser
spring.datasource.password=mypass
```

4. Save file

✅ Configured!

---

## Step 5: Run Backend (1 min)

```bash
# In terminal:
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud
```

**Or in IntelliJ:**
1. Right-click main application class
2. "Modify Run Configuration"
3. Active profiles: `cloud`
4. Run

Wait for:
```
Started DemoApplication in X.XXX seconds
```

✅ Backend is running!

---

## Test It! (30 seconds)

Open in browser:
```
http://localhost:8081/regions
```

You should see:
```json
[
  {
    "id": 1,
    "name": "North America",
    "code": "NA"
  },
  ...
]
```

✅ **SUCCESS!** Your backend is connected to Neon! 🎉

---

## What You Get

✅ **5GB storage** (10 projects × 0.5GB)
✅ **100 compute hours/month** per project
✅ **Permanent free tier** - No time limits
✅ **No credit card** required
✅ **Serverless** - Scales to zero automatically

---

## Quick Commands

```bash
# Run with Neon (cloud)
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud

# Run with local MySQL
./mvnw spring-boot:run

# Test API
curl http://localhost:8081/regions
curl http://localhost:8081/games
curl http://localhost:8081/games?regionId=1
```

---

## Connection String Breakdown

```
postgresql://[user]:[pass]@[host]/[db]?sslmode=require
           ↓      ↓      ↓        ↓
         username password  host  database
```

**In application-cloud.properties:**
```properties
spring.datasource.url=jdbc:postgresql://[host]/[db]?sslmode=require
spring.datasource.username=[user]
spring.datasource.password=[pass]
```

---

## Troubleshooting

### ❌ "Connection refused"
- Check connection string is correct
- Ensure `?sslmode=require` at the end

### ❌ "Authentication failed"
- Double-check username and password
- Get fresh connection string from Neon

### ❌ "Tables don't exist"
- Re-run `database-schema-postgresql.sql` in Neon SQL Editor
- Check for errors in execution history

---

## Next Steps

1. ✅ Backend is running on Neon
2. 🔜 Connect your React frontend
3. 🔜 Test region filtering
4. 🔜 Deploy to production

---

## Need More Help?

📚 **Detailed Guide:** `NEON_SETUP_GUIDE.md`
📊 **Why Neon:** `DATABASE_PROVIDERS_COMPARISON.md`
📝 **Migration Summary:** `MIGRATION_SUMMARY.md`

---

**You're all set! Your backend is now running on 5GB of free PostgreSQL storage! 🚀**
