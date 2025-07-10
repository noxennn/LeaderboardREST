import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Trend, Rate } from 'k6/metrics';

// Custom metrics for each operation
const incTrend = new Trend('increment_score_latency');
const topTrend = new Trend('get_top_players_latency');
const playerTrend = new Trend('get_player_latency');
const rankTrend = new Trend('get_player_rank_latency');
const addTrend = new Trend('add_player_latency');
const delTrend = new Trend('delete_player_latency');
const errors = new Rate('errors');

export let options = {
  scenarios: {
    ramping: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '2m', target: 50 },    // ramp-up to 50 VUs in 2m
        { duration: '5m', target: 50 },    // sustain 50 VUs for 5m
        { duration: '2m', target: 0 },     // ramp-down to 0 VUs in 2m
      ],
    },
  },
  thresholds: {
    'http_req_duration': ['p(95)<500', 'p(99)<1500'],
    'errors': ['rate<0.01'],             // <1% errors
  },
};

// Environment variables
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const GENERATE_COUNT = __ENV.GENERATE_COUNT ? parseInt(__ENV.GENERATE_COUNT) : 100000;
const PLAYER_POOL_SIZE = __ENV.PLAYER_POOL_SIZE ? parseInt(__ENV.PLAYER_POOL_SIZE) : 1000;

/**
 * setup(): generate initial players and fetch a pool of player IDs
 */
export function setup() {
  // 1. Generate players in bulk
  const genRes = http.post(
    `${BASE_URL}/leaderboard/players`,
    JSON.stringify({ count: GENERATE_COUNT }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  check(genRes, { 'generate status is 200': (r) => r.status === 200 });
  sleep(1);

  // 2. Fetch a subset of players to use in tests
  const listRes = http.get(`${BASE_URL}/leaderboard/top?n=${PLAYER_POOL_SIZE}`);
  check(listRes, { 'fetch pool status is 200': (r) => r.status === 200 });
  const entries = listRes.json();  // array of { playerId, username, level, score }
  const players = entries.map(e => e.playerId);

  return { players };
}

/**
 * default(): simulate mixed traffic against leaderboard endpoints
 */
export default function (data) {
  const players = data.players;
  const rand = Math.random();

  if (rand < 0.5) {
    group('Increment Score', () => {
      const id = players[Math.floor(Math.random() * players.length)];
      const payload = JSON.stringify({ incrementBy: Math.random() * 10 });
      const res = http.post(
        `${BASE_URL}/leaderboard/${id}/score/incrementBy`,
        payload,
        { headers: { 'Content-Type': 'application/json' } }
      );
      incTrend.add(res.timings.duration);
      errors.add(res.status !== 200);
      check(res, { 'increment 200': (r) => r.status === 200 });
    });

  } else if (rand < 0.7) {
    group('Get Top Players', () => {
      const res = http.get(`${BASE_URL}/leaderboard/top?n=10`);
      topTrend.add(res.timings.duration);
      errors.add(res.status !== 200);
      check(res, { 'top 200': (r) => r.status === 200 });
    });

  } else if (rand < 0.85) {
    group('Get Player', () => {
      const id = players[Math.floor(Math.random() * players.length)];
      const res = http.get(`${BASE_URL}/leaderboard/${id}`);
      playerTrend.add(res.timings.duration);
      errors.add(res.status !== 200);
      check(res, { 'get player 200': (r) => r.status === 200 });
    });

  } else if (rand < 0.95) {
    group('Get Player Rank', () => {
      const id = players[Math.floor(Math.random() * players.length)];
      const res = http.get(`${BASE_URL}/leaderboard/${id}/rank`);
      rankTrend.add(res.timings.duration);
      errors.add(res.status !== 200);
      check(res, { 'get rank 200': (r) => r.status === 200 });
    });

  } else {
    group('Add & Delete Player', () => {
      const unique = `vu${__VU}-ts${Date.now()}`;
      // Add
      const addPayload = JSON.stringify({
        playerId: unique,
        username: unique,
        level: Math.floor(Math.random() * 100),
        score: 0
      });
      const addRes = http.post(
        `${BASE_URL}/leaderboard/player`,
        addPayload,
        { headers: { 'Content-Type': 'application/json' } }
      );
      addTrend.add(addRes.timings.duration);
      errors.add(addRes.status !== 200);
      check(addRes, { 'add 200': (r) => r.status === 200 });
      sleep(0.1);
      // Delete
      const delRes = http.del(
        `${BASE_URL}/leaderboard/player?playerId=${unique}`
      );
      delTrend.add(delRes.timings.duration);
      errors.add(delRes.status !== 200);
      check(delRes, { 'delete 200': (r) => r.status === 200 });
    });
  }

  // Simulate think-time
  sleep(Math.random());
}
