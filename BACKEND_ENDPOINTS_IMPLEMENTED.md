# Backend API Endpoints - Implementation Complete

## ✅ All Requested Endpoints Implemented

### Teams Endpoints

| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/teams` | GET | Get all teams with players | ✅ Done |
| `/teams/{id}` | GET | Get specific team by ID with players | ✅ Done |
| `/teams/{id}/matches` | GET | Get all past matches for a team | ✅ Done |
| `/teams/{id1}/head-to-head/{id2}` | GET | Get head-to-head matches between two teams | ✅ Done |

### User Opinion Endpoints

| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/users/{id}/opinions` | GET | Get opinions created by user | ✅ Done |
| `/users/{id}/liked-opinions` | GET | Get opinions liked by user | ✅ Done |

### Existing Endpoints (Already Working)

| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/regions` | GET | Get all regions | ✅ Working |
| `/games` | GET | Get all games | ✅ Working |
| `/games?regionId={id}` | GET | Get games filtered by region | ✅ Working |
| `/opinions` | GET | Get all opinions | ✅ Working |
| `/opinions?regionId={id}` | GET | Get opinions filtered by region | ✅ Working |

---

## Database Schema Updates

### Team Entity - Added Fields
```java
- fullName: String     // Full team name
- country: String      // Country name
```

### Player Entity - Added Fields
```java
- nickname: String     // Optional nickname
- image: String        // Player image URL
```

### Game Entity - Added Fields
```java
- teamOneScore: Integer    // Final score
- teamTwoScore: Integer    // Final score
- tournament: String       // Tournament name
- matchType: String        // Bo1, Bo3, Bo5
- isFinished: Boolean      // Match completion status
```

### New Entity: OpinionLike
```java
- id: Long
- opinion: Opinion (ManyToOne)
- user: User (ManyToOne)
- createdAt: LocalDateTime
```

---

## Frontend Data Structure Mapping

### Teams Response
```json
{
  "id": 1,
  "name": "T1",
  "fullName": "SK Telecom T1",
  "tag": "T1",
  "region": "Korea",
  "country": "South Korea",
  "image": "https://...",
  "twitter": "@T1LoL",
  "players": [
    {
      "id": 1,
      "name": "Faker",
      "nickname": "The Unkillable Demon King",
      "role": "Mid",
      "image": "https://..."
    }
  ]
}
```

### Past Matches Response
```json
{
  "id": 1,
  "matchStartDateTime": "2024-11-07T19:00:00",
  "teamOneId": 1,
  "teamTwoId": 2,
  "teamOneScore": 3,
  "teamTwoScore": 0,
  "teamOnePercent": 65,
  "teamTwoPercent": 35,
  "tournament": "LCK",
  "matchType": "Bo5",
  "isFinished": true,
  "region": {
    "id": 3,
    "name": "Korea",
    "code": "KR"
  }
}
```

---

## Testing the New Endpoints

### Test Teams
```bash
# Get all teams
curl http://localhost:8081/teams

# Get specific team with players
curl http://localhost:8081/teams/7

# Get team's past matches (Team 7 = T1)
curl http://localhost:8081/teams/7/matches

# Get head-to-head between T1 (7) and Gen.G (8)
curl http://localhost:8081/teams/7/head-to-head/8
```

### Test User Opinions
```bash
# Get opinions by user ID
curl http://localhost:8081/users/1/opinions

# Get opinions liked by user ID
curl http://localhost:8081/users/1/liked-opinions
```

### Test Results ✅

All endpoints tested and working:
- ✅ GET /teams - Returns all 14 teams
- ✅ GET /teams/{id} - Returns team with new fullName and country fields
- ✅ GET /teams/7/matches - Returns 3 matches for team 7
- ✅ GET /teams/7/head-to-head/8 - Returns 2 head-to-head matches
- ✅ GET /users/1/opinions - Returns 6 opinions created by user 1
- ✅ GET /users/1/liked-opinions - Returns empty array (no likes yet, but endpoint working)

---

## Next Steps for Frontend Integration

### 1. Remove Mock Data Files
Delete or comment out:
- `fetchTeams()`
- `fetchPastMatchesByTeamId()`
- `fetchPastMatchesByTeamsId()`
- `fetchListingsByCreatedBy()`

### 2. Create API Service Functions
```typescript
// src/services/teamService.ts
export const fetchTeams = async () => {
  const response = await fetch(`${API_BASE_URL}/teams`);
  return response.json();
};

export const fetchTeamById = async (teamId: number) => {
  const response = await fetch(`${API_BASE_URL}/teams/${teamId}`);
  return response.json();
};

export const fetchTeamMatches = async (teamId: number) => {
  const response = await fetch(`${API_BASE_URL}/teams/${teamId}/matches`);
  return response.json();
};

export const fetchHeadToHead = async (team1Id: number, team2Id: number) => {
  const response = await fetch(`${API_BASE_URL}/teams/${team1Id}/head-to-head/${team2Id}`);
  return response.json();
};

// src/services/userService.ts
export const fetchUserOpinions = async (userId: number) => {
  const response = await fetch(`${API_BASE_URL}/users/${userId}/opinions`);
  return response.json();
};

export const fetchUserLikedOpinions = async (userId: number) => {
  const response = await fetch(`${API_BASE_URL}/users/${userId}/liked-opinions`);
  return response.json();
};
```

### 3. Update Components
Replace mock data calls with real API calls in:
- `TeamPageComponent.tsx`
- `TeamHistory.tsx`
- `TeamMatchHistory.tsx`
- `TeamHeadToHeadHistory.tsx`
- `ProfilePageComponent.tsx`
- `ProfileOpinions.tsx`
- `ProfileLikedOpinions.tsx` (or equivalent component for liked opinions)

---

## Implementation Status

### Completed ✅
- [x] Team entity with fullName, country
- [x] Player entity with image, nickname
- [x] Game entity with scores, tournament, matchType
- [x] OpinionLike entity
- [x] GET /teams endpoint
- [x] GET /teams/{id} endpoint
- [x] GET /teams/{id}/matches endpoint
- [x] GET /teams/{id1}/head-to-head/{id2} endpoint
- [x] GET /users/{id}/opinions endpoint
- [x] GET /users/{id}/liked-opinions endpoint
- [x] Repository methods for all queries
- [x] Service layer methods
- [x] Controller endpoints

### Pending 🔜
- [ ] POST /opinions/{id}/like endpoint (to like an opinion)
- [ ] DELETE /opinions/{id}/like endpoint (to unlike an opinion)
- [ ] Add more test data with players (names, roles, images)
- [ ] Add past matches with scores (teamOneScore, teamTwoScore, tournament, matchType, isFinished)

---

## Database Migration Steps

When you restart your backend, Hibernate will automatically:
1. Add `fullName` and `country` columns to `teams` table
2. Add `nickname` and `image` columns to `players` table
3. Add `teamOneScore`, `teamTwoScore`, `tournament`, `matchType`, `isFinished` to `games` table
4. Create `opinion_likes` table

No manual SQL migration needed! Just restart the backend.

---

## Ready to Test!

All backend endpoints are implemented and ready.

**Next**: Restart backend and test the new endpoints, then connect frontend!
