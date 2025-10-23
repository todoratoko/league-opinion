# Cloud Database Setup Guide

This guide will help you set up a cloud database and connect your React frontend to the Java backend.

## Step 1: Create Cloud Database

### Option A: Railway (Recommended)

1. **Sign Up**
   - Go to https://railway.app/
   - Sign up with GitHub
   - You get $5 free credit per month

2. **Create MySQL Database**
   - Click "New Project"
   - Select "Provision MySQL"
   - Wait 30 seconds for deployment

3. **Get Connection Details**
   - Click on the MySQL service
   - Go to "Variables" or "Connect" tab
   - You'll see these values:
     - `MYSQLHOST` (e.g., containers-us-west-123.railway.app)
     - `MYSQLPORT` (e.g., 6789)
     - `MYSQLDATABASE` (e.g., railway)
     - `MYSQLUSER` (e.g., root)
     - `MYSQLPASSWORD` (e.g., abc123xyz)

### Option B: Aiven

1. **Sign Up**
   - Go to https://aiven.io/
   - Create free account
   - Verify email

2. **Create MySQL Service**
   - Click "Create Service"
   - Select MySQL
   - Choose free tier
   - Select region closest to you
   - Create service

3. **Get Connection Details**
   - Wait for service to start
   - Click on the service
   - Go to "Connection information"
   - Copy the connection details

## Step 2: Initialize Database Schema

### Option 1: Using Railway Web Console
1. In Railway, click on your MySQL service
2. Go to "Data" tab
3. Click "Query"
4. Copy and paste the content of `database-schema.sql`
5. Click "Execute"

### Option 2: Using MySQL Workbench
1. Download MySQL Workbench
2. Create new connection using your cloud database credentials
3. Open `database-schema.sql`
4. Execute the script

### Option 3: Using Command Line
```bash
mysql -h YOUR_HOST -P YOUR_PORT -u YOUR_USER -p YOUR_DATABASE < database-schema.sql
```

## Step 3: Configure Backend

### Update Application Properties

1. **Open** `src/main/resources/application-cloud.properties`

2. **Replace the placeholder values** with your cloud database credentials:

```properties
# Example with Railway credentials:
spring.datasource.url=jdbc:mysql://containers-us-west-123.railway.app:6789/railway?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=abc123xyz
```

3. **Update CORS Configuration**

Open `src/main/java/com/example/demo/config/CorsConfig.java` and update the allowed origins:

```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",                    // Local React dev
    "http://localhost:5173",                    // Local Vite dev
    "https://your-actual-frontend.vercel.app"   // Your Vercel URL
));
```

## Step 4: Run Backend with Cloud Database

### Using IntelliJ IDEA
1. Right-click on your main application class
2. Select "Modify Run Configuration"
3. In "Active profiles", enter: `cloud`
4. Run the application

### Using Command Line
```bash
# Run with cloud profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud

# OR set environment variable
export SPRING_PROFILES_ACTIVE=cloud
./mvnw spring-boot:run
```

### Verify Backend is Running
```bash
# Test health endpoint
curl http://localhost:8081/regions

# You should see the list of regions
```

## Step 5: Deploy Backend (Optional)

### Deploy to Railway
1. In Railway dashboard, click "New"
2. Select "Deploy from GitHub repo"
3. Select your backend repository
4. Railway will detect it's a Spring Boot app
5. Add environment variable: `SPRING_PROFILES_ACTIVE=cloud`
6. Add your MySQL service to the project
7. Deploy

### Deploy to Render
1. Go to https://render.com/
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Configure:
   - **Build Command**: `./mvnw clean install`
   - **Start Command**: `java -jar target/demo-0.0.1-SNAPSHOT.jar`
5. Add environment variables:
   - `SPRING_PROFILES_ACTIVE=cloud`
   - `SPRING_DATASOURCE_URL=jdbc:mysql://...`
   - `SPRING_DATASOURCE_USERNAME=...`
   - `SPRING_DATASOURCE_PASSWORD=...`

## Step 6: Connect Frontend to Backend

### Update Frontend API Base URL

In your React app, create or update your API configuration:

**For Local Development:**
```javascript
// src/config/api.js
export const API_BASE_URL = 'http://localhost:8081';
```

**For Production:**
```javascript
// src/config/api.js
export const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8081';
```

Then add to your `.env.production`:
```
REACT_APP_API_URL=https://your-backend.railway.app
```

### Example API Calls

**Fetch Regions:**
```javascript
import { API_BASE_URL } from './config/api';

async function fetchRegions() {
  const response = await fetch(`${API_BASE_URL}/regions`);
  const regions = await response.json();
  return regions;
}
```

**Fetch Games by Region:**
```javascript
async function fetchGamesByRegion(regionId) {
  const response = await fetch(`${API_BASE_URL}/games?regionId=${regionId}`);
  const games = await response.json();
  return games;
}
```

**Fetch Opinions by Region:**
```javascript
async function fetchOpinionsByRegion(regionId) {
  const response = await fetch(`${API_BASE_URL}/opinions?regionId=${regionId}`);
  const opinions = await response.json();
  return opinions;
}
```

## Step 7: Test End-to-End

### Testing Checklist

1. **Backend Health Check**
   ```bash
   curl http://localhost:8081/regions
   ```

2. **Test from Browser Console**
   ```javascript
   fetch('http://localhost:8081/regions')
     .then(r => r.json())
     .then(console.log);
   ```

3. **Test Region Filtering**
   ```bash
   # Get all games
   curl http://localhost:8081/games

   # Get games for region 1
   curl http://localhost:8081/games?regionId=1

   # Get opinions for region 1
   curl http://localhost:8081/opinions?regionId=1
   ```

4. **Test from React App**
   - Start your React app
   - Open browser console
   - Check for CORS errors (should be none)
   - Verify data loads correctly

## Troubleshooting

### CORS Errors
**Error**: "No 'Access-Control-Allow-Origin' header"

**Solution**:
1. Verify `CorsConfig.java` includes your frontend URL
2. Restart backend server
3. Clear browser cache

### Database Connection Errors
**Error**: "Communications link failure"

**Solutions**:
1. Verify database credentials in `application-cloud.properties`
2. Check if database host/port is correct
3. Ensure SSL parameters are correct
4. Check if your IP is whitelisted (some providers require this)

### 404 Errors
**Error**: "404 Not Found" for API endpoints

**Solutions**:
1. Verify backend is running on port 8081
2. Check endpoint URL spelling
3. Verify controller mappings

### Session/Cookie Issues
**Error**: Sessions not persisting

**Solution**: In `CorsConfig.java`, ensure:
```java
config.setAllowCredentials(true);
```

## Environment Variables (Optional)

For better security, use environment variables instead of hardcoding:

**In `application-cloud.properties`:**
```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USER}
spring.datasource.password=${DATABASE_PASSWORD}
```

**Set environment variables:**
```bash
export DATABASE_URL="jdbc:mysql://..."
export DATABASE_USER="root"
export DATABASE_PASSWORD="password"
```

## Security Notes

1. **Never commit credentials** to Git
2. **Add to `.gitignore`:**
   ```
   **/application-cloud.properties
   .env
   .env.local
   ```

3. **Use environment variables** in production

4. **Rotate JWT secrets** before production deployment

## Next Steps

1. ✅ Set up cloud database
2. ✅ Initialize schema
3. ✅ Configure backend
4. ✅ Test backend locally
5. ✅ Update frontend API calls
6. ✅ Test end-to-end locally
7. 🚀 Deploy backend to Railway/Render
8. 🚀 Deploy frontend to Vercel
9. 🚀 Update production environment variables

## Helpful Commands

```bash
# Build backend
./mvnw clean install

# Run backend with cloud profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud

# Run tests
./mvnw test

# Package for deployment
./mvnw clean package -DskipTests
```

## Support

If you encounter issues:
1. Check the logs: Backend will show detailed error messages
2. Verify database connection in Railway/Aiven dashboard
3. Test API endpoints with Postman or curl first
4. Check browser console for frontend errors
