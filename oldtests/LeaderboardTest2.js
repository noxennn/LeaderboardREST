import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  scenarios: {
    mixed: {
      executor: 'per-vu-iterations',
      vus: 200,
      iterations: 100,
      exec: 'mixedScenario',
    },
    read_all: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: __ENV.READ_VUS || 100 },
        { duration: __ENV.READ_DURATION || '30s', target: __ENV.READ_VUS || 200 },
        { duration: '30s', target: 0 },
      ],
      exec: 'readAll',
    },
  },
  // no custom metrics; just built-in
};

const BASE_URL         = __ENV.BASE_URL || 'http://localhost:8080';
const PLAYER_POOL_SIZE = parseInt(__ENV.PLAYER_POOL_SIZE, 10) || 100;
const GENERATE_COUNT   = parseInt(__ENV.GENERATE_COUNT, 10) || 100000;

export function setup() {
  // 1) Bulk-generate players
  let res = http.post(
    `${BASE_URL}/leaderboard/players`,
    JSON.stringify({ count: GENERATE_COUNT }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { name: 'generatePlayers' },
      timeout: '120s',
    }
  );
  check(res, { 'generatePlayers status was 200': (r) => r.status === 200 });

  // 2) Fetch initial pool of IDs
  res = http.get(
    `${BASE_URL}/leaderboard/top?n=${PLAYER_POOL_SIZE}`,
    { tags: { name: 'fetchPool' }, timeout: '120s' }
  );
  check(res, { 'fetchPool status was 200': (r) => r.status === 200 });

  return res.json().map((e) => e.playerId);
}

export function mixedScenario(playerIds) {
  const playerId = playerIds[Math.floor(Math.random() * playerIds.length)];

  group('incrementScore', () => {
    let res = http.post(
      `${BASE_URL}/leaderboard/${playerId}/score/incrementBy`,
      JSON.stringify({ incrementBy: Math.random() * 10 }),
      {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'incrementScore' },
        timeout: '120s',
      }
    );
    check(res, { 'incrementScore status was 200': (r) => r.status === 200 });
    sleep(Math.random());
  });

  group('getTopPlayers', () => {
    let res = http.get(
      `${BASE_URL}/leaderboard/top?n=10`,
      { tags: { name: 'getTopPlayers' }, timeout: '120s' }
    );
    check(res, { 'getTopPlayers status was 200': (r) => r.status === 200 });
    sleep(Math.random());
  });

  group('getEntry', () => {
    let res = http.get(
      `${BASE_URL}/leaderboard/${playerId}`,
      { tags: { name: 'getEntry' }, timeout: '120s' }
    );
    check(res, { 'getEntry status was 200': (r) => r.status === 200 });
    sleep(Math.random());
  });

  group('getRank', () => {
    let res = http.get(
      `${BASE_URL}/leaderboard/${playerId}/rank`,
      { tags: { name: 'getRank' }, timeout: '120s' }
    );
    check(res, { 'getRank status was 200': (r) => r.status === 200 });
    sleep(Math.random());
  });

  group('addPlayer', () => {
    let newId = `user-${__VU}-${Date.now()}`;
    let dto   = JSON.stringify({ playerId: newId, username: newId, level: 1, score: 0 });
    let res = http.post(
      `${BASE_URL}/leaderboard/player`,
      dto,
      {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: 'addPlayer' },
        timeout: '120s',
      }
    );
    check(res, { 'addPlayer status was 200': (r) => r.status === 200 });
    sleep(Math.random());
  });


}

export function readAll() {
  let res = http.get(
    `${BASE_URL}/leaderboard/top?n=${PLAYER_POOL_SIZE}`,
    { tags: { name: 'readAll' }, timeout: '120s' }
  );
  check(res, { 'readAll status was 200': (r) => r.status === 200 });
}
