# Region Filtering Implementation Guide

## Overview
This document describes the region filtering functionality that allows filtering games/matches and opinions by region.

## Database Schema

### Region Table
A new `regions` table has been created with the following structure:
- `id` (BIGINT, Primary Key, Auto Increment)
- `name` (VARCHAR, Unique, Not Null) - e.g., "North America", "Europe"
- `code` (VARCHAR, Unique, Not Null) - e.g., "NA", "EU"

### Game Table Update
The `games` table now includes:
- `region_id` (BIGINT, Foreign Key) - references `regions.id`

## Database Setup

### 1. Create Regions Table
```sql
CREATE TABLE regions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE
);
```

### 2. Add Region Column to Games Table
```sql
ALTER TABLE games
ADD COLUMN region_id BIGINT,
ADD CONSTRAINT fk_game_region
FOREIGN KEY (region_id) REFERENCES regions(id);
```

### 3. Seed Initial Region Data
```sql
INSERT INTO regions (name, code) VALUES
('North America', 'NA'),
('Europe', 'EU'),
('Korea', 'KR'),
('China', 'CN'),
('International', 'INT'),
('Brazil', 'BR'),
('Latin America', 'LAN'),
('Oceania', 'OCE'),
('Japan', 'JP'),
('Turkey', 'TR'),
('CIS', 'CIS'),
('Southeast Asia', 'SEA');
```

## API Endpoints

### Region Endpoints

#### Get All Regions
```
GET /regions
```
Returns a list of all available regions.

**Response:**
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
  }
]
```

#### Get Region by ID
```
GET /region/{id}
```
Returns a specific region by ID.

**Response:**
```json
{
  "id": 1,
  "name": "North America",
  "code": "NA"
}
```

#### Get Region by Code
```
GET /region/code/{code}
```
Returns a specific region by code.

**Example:**
```
GET /region/code/NA
```

### Game Endpoints

#### Get All Games (with optional region filter)
```
GET /games?regionId={regionId}
```
- If `regionId` parameter is provided, returns games for that region
- If `regionId` is not provided, returns all games

**Examples:**
```
GET /games                  # Returns all games
GET /games?regionId=1       # Returns games in North America region
```

#### Get Games by Region (alternative endpoint)
```
GET /games/region/{regionId}
```
Returns all games for a specific region.

**Example:**
```
GET /games/region/1         # Returns games in North America region
```

### Opinion Endpoints

#### Get All Opinions (with optional region filter)
```
GET /opinions?regionId={regionId}
```
- If `regionId` parameter is provided, returns opinions for games in that region
- If `regionId` is not provided, returns all opinions

**Examples:**
```
GET /opinions               # Returns all opinions
GET /opinions?regionId=1    # Returns opinions for games in North America region
```

#### Get Opinions by Region (alternative endpoint)
```
GET /opinions/region/{regionId}
```
Returns all opinions for games in a specific region.

**Example:**
```
GET /opinions/region/1      # Returns opinions for games in North America region
```

## Frontend Implementation Example

### Fetching Regions for Filter Dropdown
```javascript
// Fetch all regions
const response = await fetch('http://your-api/regions');
const regions = await response.json();

// Display in dropdown
regions.forEach(region => {
  // Add to select element
  // <option value={region.id}>{region.name}</option>
});
```

### Filtering Games by Region
```javascript
// When user selects a region from dropdown
const selectedRegionId = 1; // From user selection

// Fetch games for that region
const response = await fetch(`http://your-api/games?regionId=${selectedRegionId}`);
const games = await response.json();

// Display filtered games
```

### Filtering Opinions by Region
```javascript
// When user selects a region from dropdown
const selectedRegionId = 1; // From user selection

// Fetch opinions for that region
const response = await fetch(`http://your-api/opinions?regionId=${selectedRegionId}`);
const opinions = await response.json();

// Display filtered opinions
```

## Implementation Details

### Files Created
1. **Region.java** - Entity representing a region
2. **RegionRepository.java** - Repository for region database operations
3. **RegionService.java** - Service layer for region business logic
4. **RegionController.java** - REST controller for region endpoints
5. **RegionDTO.java** - Data Transfer Object for region data

### Files Modified
1. **Game.java** - Added `region` relationship
2. **GameRepository.java** - Added `findByRegion()` and `findByRegionId()` methods
3. **GameService.java** - Added `getGamesByRegionId()` and `getAllGames()` methods
4. **GameController.java** - Added filtering endpoints
5. **OpinionRepository.java** - Added `findByGameRegion()` and `findByGameRegionId()` methods
6. **OpinionService.java** - Added `getOpinionsByRegionId()` and `getAllOpinions()` methods
7. **OpinionController.java** - Added filtering endpoints

## Testing

### Test the Implementation
1. Start your Spring Boot application
2. Use Postman or curl to test the endpoints

**Example curl commands:**
```bash
# Get all regions
curl http://localhost:8080/regions

# Get games for region ID 1
curl http://localhost:8080/games?regionId=1

# Get opinions for region ID 1
curl http://localhost:8080/opinions?regionId=1
```

## Notes
- Each game can belong to only one region
- Opinions are filtered by the region of their associated game
- The filtering is done at the database level for optimal performance
- Spring Data JPA automatically generates the query methods based on method names
