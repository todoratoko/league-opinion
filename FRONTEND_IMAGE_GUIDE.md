# Frontend Image Display Guide

## How Images Work in Your App

### Backend Setup ✅
- Images stored in: `src/main/resources/static/images/`
- Spring Boot automatically serves them at: `http://localhost:8081/images/`
- API returns image paths like: `/images/teams/t1.png`

### Image URLs
When your backend is running on `http://localhost:8081`:
- Team logos: `http://localhost:8081/images/teams/{tag}.png`
- Player photos: `http://localhost:8081/images/players/{tag}-{name}.png`

---

## Frontend Examples

### 1. React/Next.js Example

```jsx
// TeamCard.jsx - Display a team with logo
function TeamCard({ team }) {
  const baseUrl = 'http://localhost:8081';

  return (
    <div className="team-card">
      <img
        src={`${baseUrl}${team.image}`}
        alt={team.name}
        className="team-logo"
        onError={(e) => {
          e.target.src = '/placeholder-team.png'; // Fallback
        }}
      />
      <h3>{team.fullName}</h3>
      <p>{team.region}</p>
    </div>
  );
}

// PlayerCard.jsx - Display a player with photo
function PlayerCard({ player }) {
  const baseUrl = 'http://localhost:8081';

  return (
    <div className="player-card">
      <img
        src={`${baseUrl}${player.image}`}
        alt={player.name}
        className="player-photo"
      />
      <h4>{player.name}</h4>
      <span className="role">{player.role}</span>
    </div>
  );
}

// TeamsList.jsx - Fetch and display all teams
import { useState, useEffect } from 'react';

function TeamsList() {
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch('http://localhost:8081/teams')
      .then(res => res.json())
      .then(data => {
        setTeams(data);
        setLoading(false);
      })
      .catch(error => console.error('Error:', error));
  }, []);

  if (loading) return <div>Loading teams...</div>;

  return (
    <div className="teams-grid">
      {teams.map(team => (
        <TeamCard key={team.id} team={team} />
      ))}
    </div>
  );
}

// TeamDetail.jsx - Display team with all players
function TeamDetail({ teamId }) {
  const [team, setTeam] = useState(null);
  const baseUrl = 'http://localhost:8081';

  useEffect(() => {
    fetch(`http://localhost:8081/teams/${teamId}`)
      .then(res => res.json())
      .then(data => setTeam(data));
  }, [teamId]);

  if (!team) return <div>Loading...</div>;

  return (
    <div className="team-detail">
      <div className="team-header">
        <img
          src={`${baseUrl}${team.image}`}
          alt={team.name}
          className="team-logo-large"
        />
        <div>
          <h1>{team.fullName}</h1>
          <p>{team.region} - {team.country}</p>
        </div>
      </div>

      <div className="roster">
        <h2>Roster</h2>
        <div className="players-grid">
          {team.players.map(player => (
            <div key={player.id} className="player">
              <img
                src={`${baseUrl}${player.image}`}
                alt={player.name}
              />
              <h3>{player.name}</h3>
              <p className="role">{player.role}</p>
              <p className="nickname">{player.nickname}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
```

### 2. Vanilla JavaScript Example

```html
<!DOCTYPE html>
<html>
<head>
    <title>League Opinion - Teams</title>
    <style>
        .teams-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 20px;
            padding: 20px;
        }

        .team-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
        }

        .team-logo {
            width: 100px;
            height: 100px;
            object-fit: contain;
            margin-bottom: 10px;
        }

        .player-photo {
            width: 80px;
            height: 80px;
            object-fit: cover;
            border-radius: 50%;
        }
    </style>
</head>
<body>
    <h1>League of Legends Teams</h1>
    <div id="teams-container" class="teams-container"></div>

    <script>
        const API_BASE = 'http://localhost:8081';

        // Fetch and display all teams
        async function loadTeams() {
            try {
                const response = await fetch(`${API_BASE}/teams`);
                const teams = await response.json();

                const container = document.getElementById('teams-container');
                container.innerHTML = teams.map(team => `
                    <div class="team-card" onclick="showTeamDetail(${team.id})">
                        <img
                            src="${API_BASE}${team.image}"
                            alt="${team.name}"
                            class="team-logo"
                            onerror="this.src='/placeholder-team.png'"
                        />
                        <h3>${team.name}</h3>
                        <p>${team.region}</p>
                    </div>
                `).join('');
            } catch (error) {
                console.error('Error loading teams:', error);
            }
        }

        // Show team detail with players
        async function showTeamDetail(teamId) {
            try {
                const response = await fetch(`${API_BASE}/teams/${teamId}`);
                const team = await response.json();

                const html = `
                    <div class="team-detail">
                        <button onclick="loadTeams()">← Back to Teams</button>
                        <div class="team-header">
                            <img src="${API_BASE}${team.image}" class="team-logo" />
                            <h1>${team.fullName}</h1>
                            <p>${team.region} - ${team.country}</p>
                        </div>
                        <h2>Roster</h2>
                        <div class="players-grid">
                            ${team.players.map(player => `
                                <div class="player-card">
                                    <img
                                        src="${API_BASE}${player.image}"
                                        alt="${player.name}"
                                        class="player-photo"
                                    />
                                    <h4>${player.name}</h4>
                                    <p>${player.role}</p>
                                </div>
                            `).join('')}
                        </div>
                    </div>
                `;

                document.getElementById('teams-container').innerHTML = html;
            } catch (error) {
                console.error('Error loading team detail:', error);
            }
        }

        // Load teams on page load
        loadTeams();
    </script>
</body>
</html>
```

### 3. Vue.js Example

```vue
<!-- TeamsList.vue -->
<template>
  <div class="teams-list">
    <h1>Teams</h1>
    <div class="teams-grid">
      <div
        v-for="team in teams"
        :key="team.id"
        class="team-card"
        @click="selectTeam(team)"
      >
        <img
          :src="`${baseUrl}${team.image}`"
          :alt="team.name"
          class="team-logo"
        />
        <h3>{{ team.fullName }}</h3>
        <p>{{ team.region }}</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TeamsList',
  data() {
    return {
      teams: [],
      baseUrl: 'http://localhost:8081'
    }
  },
  mounted() {
    this.loadTeams();
  },
  methods: {
    async loadTeams() {
      try {
        const response = await fetch(`${this.baseUrl}/teams`);
        this.teams = await response.json();
      } catch (error) {
        console.error('Error loading teams:', error);
      }
    },
    selectTeam(team) {
      this.$router.push(`/teams/${team.id}`);
    }
  }
}
</script>

<!-- TeamDetail.vue -->
<template>
  <div v-if="team" class="team-detail">
    <div class="team-header">
      <img
        :src="`${baseUrl}${team.image}`"
        :alt="team.name"
        class="team-logo-large"
      />
      <div>
        <h1>{{ team.fullName }}</h1>
        <p>{{ team.region }} - {{ team.country }}</p>
      </div>
    </div>

    <div class="roster">
      <h2>Roster</h2>
      <div class="players-grid">
        <div
          v-for="player in team.players"
          :key="player.id"
          class="player-card"
        >
          <img
            :src="`${baseUrl}${player.image}`"
            :alt="player.name"
            class="player-photo"
          />
          <h3>{{ player.name }}</h3>
          <p class="role">{{ player.role }}</p>
          <p class="nickname">{{ player.nickname }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TeamDetail',
  props: ['teamId'],
  data() {
    return {
      team: null,
      baseUrl: 'http://localhost:8081'
    }
  },
  mounted() {
    this.loadTeam();
  },
  methods: {
    async loadTeam() {
      try {
        const response = await fetch(`${this.baseUrl}/teams/${this.teamId}`);
        this.team = await response.json();
      } catch (error) {
        console.error('Error loading team:', error);
      }
    }
  }
}
</script>
```

### 4. Angular Example

```typescript
// team.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Team {
  id: number;
  name: string;
  fullName: string;
  tag: string;
  region: string;
  country: string;
  image: string;
  twitter: string;
  players: Player[];
}

export interface Player {
  id: number;
  name: string;
  nickname: string;
  role: string;
  image: string;
}

@Injectable({
  providedIn: 'root'
})
export class TeamService {
  private apiUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) {}

  getAllTeams(): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.apiUrl}/teams`);
  }

  getTeamById(id: number): Observable<Team> {
    return this.http.get<Team>(`${this.apiUrl}/teams/${id}`);
  }

  getImageUrl(imagePath: string): string {
    return `${this.apiUrl}${imagePath}`;
  }
}

// teams-list.component.ts
import { Component, OnInit } from '@angular/core';
import { TeamService, Team } from './team.service';

@Component({
  selector: 'app-teams-list',
  template: `
    <div class="teams-grid">
      <div
        *ngFor="let team of teams"
        class="team-card"
        (click)="selectTeam(team)"
      >
        <img
          [src]="getImageUrl(team.image)"
          [alt]="team.name"
          class="team-logo"
        />
        <h3>{{ team.fullName }}</h3>
        <p>{{ team.region }}</p>
      </div>
    </div>
  `
})
export class TeamsListComponent implements OnInit {
  teams: Team[] = [];

  constructor(private teamService: TeamService) {}

  ngOnInit() {
    this.teamService.getAllTeams().subscribe(
      teams => this.teams = teams
    );
  }

  getImageUrl(imagePath: string): string {
    return this.teamService.getImageUrl(imagePath);
  }

  selectTeam(team: Team) {
    // Navigate to team detail
  }
}
```

---

## CSS Styling Examples

```css
/* Team Logos */
.team-logo {
  width: 100px;
  height: 100px;
  object-fit: contain;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 10px;
}

.team-logo-large {
  width: 150px;
  height: 150px;
}

/* Player Photos */
.player-photo {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
  border: 3px solid #667eea;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.player-photo:hover {
  transform: scale(1.05);
  transition: transform 0.2s;
}

/* Grid Layouts */
.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
}

.players-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

@media (max-width: 768px) {
  .players-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* Cards */
.team-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.team-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.player-card {
  text-align: center;
  padding: 15px;
  background: #f9fafb;
  border-radius: 8px;
}

.role {
  display: inline-block;
  padding: 4px 12px;
  background: #667eea;
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
}
```

---

## Testing the Images

### 1. Start Your Backend
```bash
./mvnw spring-boot:run
```

### 2. Test Image URLs Directly
Open in browser:
- Team logo: `http://localhost:8081/images/teams/t1.png`
- Player photo: `http://localhost:8081/images/players/t1-faker.png`

### 3. Test API Response
```bash
curl http://localhost:8081/teams | json_pp
```

You should see image paths like:
```json
{
  "image": "/images/teams/t1.png",
  "players": [
    {
      "image": "/images/players/t1-faker.png"
    }
  ]
}
```

---

## CORS Configuration (if needed)

If your frontend is on a different port (e.g., React on 3000, backend on 8081), add CORS:

```java
// src/main/java/com/example/demo/config/WebConfig.java
package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

---

## Quick Start HTML (Test Page)

Save this as `test-images.html` and open in browser:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Test Team Images</title>
    <style>
        body { font-family: Arial; padding: 20px; }
        .team {
            display: inline-block;
            margin: 15px;
            text-align: center;
            border: 1px solid #ddd;
            padding: 15px;
            border-radius: 8px;
        }
        img {
            width: 100px;
            height: 100px;
            object-fit: contain;
        }
    </style>
</head>
<body>
    <h1>Team Logos Test</h1>
    <div id="teams"></div>

    <script>
        fetch('http://localhost:8081/teams')
            .then(r => r.json())
            .then(teams => {
                document.getElementById('teams').innerHTML = teams.map(t => `
                    <div class="team">
                        <img src="http://localhost:8081${t.image}" alt="${t.name}">
                        <p><strong>${t.name}</strong></p>
                        <p>${t.region}</p>
                    </div>
                `).join('');
            });
    </script>
</body>
</html>
```

---

## Troubleshooting

**Images not showing?**
1. ✅ Check backend is running: `http://localhost:8081`
2. ✅ Test image URL directly: `http://localhost:8081/images/teams/t1.png`
3. ✅ Check browser console for CORS errors
4. ✅ Verify image path in API response starts with `/images/`

**CORS errors?**
- Add WebConfig.java (see above)
- Or use a proxy in your frontend dev server

**Images slow to load?**
- Normal for first load (11MB total)
- Consider lazy loading for better performance
- Images are cached by browser after first load

---

Your images are ready to use! Just fetch from your API and display them. 🎉
