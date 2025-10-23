// Example React Component: Homepage with Region Filtering
// Copy this into your React frontend and customize as needed

import { useState, useEffect } from 'react';

// Config - Update with your backend URL
const API_BASE_URL = 'http://localhost:8081';

function Homepage() {
  const [regions, setRegions] = useState([]);
  const [games, setGames] = useState([]);
  const [opinions, setOpinions] = useState([]);
  const [selectedRegion, setSelectedRegion] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState('games'); // 'games' or 'opinions'

  // Fetch regions on component mount
  useEffect(() => {
    fetchRegions();
  }, []);

  // Fetch games or opinions when region or filter changes
  useEffect(() => {
    if (activeFilter === 'games') {
      fetchGames(selectedRegion);
    } else {
      fetchOpinions(selectedRegion);
    }
  }, [selectedRegion, activeFilter]);

  // API Calls
  const fetchRegions = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/regions`);
      const data = await response.json();
      setRegions(data);
    } catch (error) {
      console.error('Error fetching regions:', error);
    }
  };

  const fetchGames = async (regionId) => {
    setLoading(true);
    try {
      const url = regionId
        ? `${API_BASE_URL}/games?regionId=${regionId}`
        : `${API_BASE_URL}/games`;

      const response = await fetch(url);
      const data = await response.json();
      setGames(data);
    } catch (error) {
      console.error('Error fetching games:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchOpinions = async (regionId) => {
    setLoading(true);
    try {
      const url = regionId
        ? `${API_BASE_URL}/opinions?regionId=${regionId}`
        : `${API_BASE_URL}/opinions`;

      const response = await fetch(url);
      const data = await response.json();
      setOpinions(data);
    } catch (error) {
      console.error('Error fetching opinions:', error);
    } finally {
      setLoading(false);
    }
  };

  // Event Handlers
  const handleRegionChange = (regionId) => {
    setSelectedRegion(regionId || null);
  };

  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
  };

  return (
    <div className="homepage">
      <h1>League Opinion</h1>

      {/* Region Filter */}
      <div className="filters">
        <label>Filter by Region:</label>
        <select
          value={selectedRegion || ''}
          onChange={(e) => handleRegionChange(e.target.value)}
        >
          <option value="">All Regions</option>
          {regions.map((region) => (
            <option key={region.id} value={region.id}>
              {region.name} ({region.code})
            </option>
          ))}
        </select>

        {/* Toggle between Games and Opinions */}
        <div className="filter-tabs">
          <button
            className={activeFilter === 'games' ? 'active' : ''}
            onClick={() => handleFilterChange('games')}
          >
            Matches
          </button>
          <button
            className={activeFilter === 'opinions' ? 'active' : ''}
            onClick={() => handleFilterChange('opinions')}
          >
            Opinions
          </button>
        </div>
      </div>

      {/* Loading State */}
      {loading && <div>Loading...</div>}

      {/* Games List */}
      {!loading && activeFilter === 'games' && (
        <div className="games-list">
          <h2>Matches</h2>
          {games.length === 0 ? (
            <p>No matches found for this region.</p>
          ) : (
            games.map((game) => (
              <div key={game.id} className="game-card">
                <p>Match ID: {game.id}</p>
                <p>Team 1 vs Team 2</p>
                <p>Start Time: {game.matchStartDateTime}</p>
                <p>
                  Predictions: Team 1: {game.teamOnePercent}% | Team 2:{' '}
                  {game.teamTwoPercent}%
                </p>
              </div>
            ))
          )}
        </div>
      )}

      {/* Opinions List */}
      {!loading && activeFilter === 'opinions' && (
        <div className="opinions-list">
          <h2>Opinions</h2>
          {opinions.length === 0 ? (
            <p>No opinions found for this region.</p>
          ) : (
            opinions.map((opinion) => (
              <div key={opinion.id} className="opinion-card">
                <p>Opinion by: User {opinion.owner?.username || 'Anonymous'}</p>
                <p>
                  Prediction: Team 1: {opinion.teamOnePercent}% | Team 2:{' '}
                  {opinion.teamTwoPercent}%
                </p>
                <p>Comment: {opinion.comment}</p>
                <p>Posted: {new Date(opinion.createdAt).toLocaleDateString()}</p>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}

export default Homepage;

/*
CSS Example (add to your CSS file):

.homepage {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filters {
  margin: 20px 0;
  display: flex;
  gap: 20px;
  align-items: center;
}

.filters select {
  padding: 10px;
  font-size: 16px;
  border-radius: 5px;
  border: 1px solid #ccc;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tabs button {
  padding: 10px 20px;
  font-size: 16px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  border-radius: 5px;
}

.filter-tabs button.active {
  background: #007bff;
  color: white;
}

.game-card,
.opinion-card {
  border: 1px solid #ddd;
  padding: 15px;
  margin: 10px 0;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

*/
