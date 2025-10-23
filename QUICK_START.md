# Quick Start: Connect Frontend to Backend

## ✅ What's Been Set Up

1. **CORS Configuration** - Your backend now accepts requests from frontend
2. **Region Filtering** - API endpoints for filtering games and opinions by region
3. **Cloud Database Template** - Ready to use with Railway, Aiven, or PlanetScale
4. **Database Schema** - Complete SQL script to initialize your database

## 🚀 Quick Setup (5 Steps)

### Step 1: Create Cloud Database (5 minutes)

**Recommended: Railway**
1. Go to https://railway.app/
2. Sign up with GitHub
3. Click "New Project" → "Provision MySQL"
4. Wait 30 seconds

**Copy these values from Railway:**
- Host (MYSQLHOST)
- Port (MYSQLPORT)
- Database (MYSQLDATABASE)
- Username (MYSQLUSER)
- Password (MYSQLPASSWORD)

### Step 2: Initialize Database (2 minutes)

**In Railway Dashboard:**
1. Click on MySQL service
2. Go to "Data" tab
3. Click "Query"
4. Copy/paste content from `database-schema.sql`
5. Execute

### Step 3: Configure Backend (1 minute)

Edit `src/main/resources/application-cloud.properties`:

```properties
spring.datasource.url=jdbc:mysql://YOUR_HOST:YOUR_PORT/YOUR_DATABASE?useSSL=true&serverTimezone=UTC
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Replace YOUR_HOST, YOUR_PORT, YOUR_DATABASE, YOUR_USERNAME, YOUR_PASSWORD with values from Step 1.

### Step 4: Run Backend (1 minute)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=cloud
```

Backend will start on http://localhost:8081

**Test it:**
```bash
curl http://localhost:8081/regions
```

You should see JSON with regions!

### Step 5: Update Frontend (2 minutes)

In your React app, create `src/config/api.js`:

```javascript
export const API_BASE_URL = 'http://localhost:8081';
```

Update your components to use real API:

```javascript
import { API_BASE_URL } from './config/api';

// Fetch regions
const regions = await fetch(`${API_BASE_URL}/regions`).then(r => r.json());

// Fetch games (all)
const games = await fetch(`${API_BASE_URL}/games`).then(r => r.json());

// Fetch games by region
const games = await fetch(`${API_BASE_URL}/games?regionId=1`).then(r => r.json());

// Fetch opinions by region
const opinions = await fetch(`${API_BASE_URL}/opinions?regionId=1`).then(r => r.json());
```

## 🎯 Available API Endpoints

### Regions
- `GET /regions` - Get all regions
- `GET /region/{id}` - Get region by ID
- `GET /region/code/NA` - Get region by code

### Games/Matches
- `GET /games` - Get all games
- `GET /games?regionId=1` - Get games by region
- `GET /game/{id}` - Get specific game

### Opinions
- `GET /opinions` - Get all opinions
- `GET /opinions?regionId=1` - Get opinions by region
- `GET /opinions/{id}` - Get specific opinion
- `POST /match/opinions/{matchId}` - Add opinion (requires auth)

### Users
- `POST /register` - Register new user
- `POST /login` - Login
- `GET /user/{id}` - Get user profile

## 🔧 Update CORS for Your Frontend URL

Edit `src/main/java/com/example/demo/config/CorsConfig.java`:

```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",      // Your local React
    "http://localhost:5173",      // Your local Vite
    "https://your-app.vercel.app" // Your Vercel deployment
));
```

## 📋 Common Frontend Integration Examples

### React Hook for Regions

```javascript
import { useState, useEffect } from 'react';
import { API_BASE_URL } from './config/api';

function useRegions() {
  const [regions, setRegions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${API_BASE_URL}/regions`)
      .then(res => res.json())
      .then(data => {
        setRegions(data);
        setLoading(false);
      });
  }, []);

  return { regions, loading };
}

// Usage:
function RegionFilter() {
  const { regions, loading } = useRegions();

  if (loading) return <div>Loading...</div>;

  return (
    <select onChange={(e) => handleRegionChange(e.target.value)}>
      <option value="">All Regions</option>
      {regions.map(region => (
        <option key={region.id} value={region.id}>
          {region.name}
        </option>
      ))}
    </select>
  );
}
```

### Fetch Games with Region Filter

```javascript
async function fetchGames(regionId = null) {
  const url = regionId
    ? `${API_BASE_URL}/games?regionId=${regionId}`
    : `${API_BASE_URL}/games`;

  const response = await fetch(url);
  return response.json();
}

// Usage:
const allGames = await fetchGames();           // All games
const naGames = await fetchGames(1);           // North America games
```

### Complete Component Example

```javascript
import { useState, useEffect } from 'react';
import { API_BASE_URL } from './config/api';

function GamesList() {
  const [games, setGames] = useState([]);
  const [regions, setRegions] = useState([]);
  const [selectedRegion, setSelectedRegion] = useState(null);

  // Fetch regions on mount
  useEffect(() => {
    fetch(`${API_BASE_URL}/regions`)
      .then(res => res.json())
      .then(setRegions);
  }, []);

  // Fetch games when region changes
  useEffect(() => {
    const url = selectedRegion
      ? `${API_BASE_URL}/games?regionId=${selectedRegion}`
      : `${API_BASE_URL}/games`;

    fetch(url)
      .then(res => res.json())
      .then(setGames);
  }, [selectedRegion]);

  return (
    <div>
      <select onChange={(e) => setSelectedRegion(e.target.value || null)}>
        <option value="">All Regions</option>
        {regions.map(r => (
          <option key={r.id} value={r.id}>{r.name}</option>
        ))}
      </select>

      <div>
        {games.map(game => (
          <div key={game.id}>
            {/* Render game details */}
          </div>
        ))}
      </div>
    </div>
  );
}
```

## 🐛 Troubleshooting

### Backend won't start
```bash
# Check if MySQL is configured correctly
# Verify credentials in application-cloud.properties
# Check logs for detailed error message
```

### CORS Error in Browser
```
Access to fetch at 'http://localhost:8081/regions' from origin
'http://localhost:3000' has been blocked by CORS policy
```

**Solution:**
1. Check `CorsConfig.java` includes your frontend URL
2. Restart backend
3. Clear browser cache

### Empty Data
**Solution:**
1. Check database has data: Run `SELECT * FROM regions;` in Railway
2. Verify backend logs show successful queries
3. Check browser network tab for API responses

## 📚 Documentation Files

- `CLOUD_DATABASE_SETUP.md` - Detailed setup guide
- `REGION_FILTERING_GUIDE.md` - API documentation
- `database-schema.sql` - Database initialization script
- `SCRAPING_GUIDE.md` - Web scraping implementation

## 🎉 You're Ready!

Your backend is configured and ready to connect to your React frontend!

**Next steps:**
1. Start backend with cloud profile
2. Update frontend API calls
3. Test locally
4. Deploy to Railway/Render (backend) and Vercel (frontend)
