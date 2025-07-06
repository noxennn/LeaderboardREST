import http from 'k6/http';
import { check, sleep, fail } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const BASE_URL = 'http://127.0.0.1:8080/leaderboard';
const TOTAL_PLAYERS = 10000;
const READ_COUNT = 100;

// CONSTANT READERS (10,000 VUs)
export let options = {
  scenarios: {
    readers: {
      executor: 'constant-vus',
      exec: 'readScenario',
      vus: 1000,
      duration: '2m',
    },
    writers: {
      executor: 'ramping-vus',
      exec: 'writeScenario',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 100 },
        { duration: '10s', target: 300 },
        { duration: '10s', target: 500 },
        { duration: '10s', target: 800 },
        { duration: '10s', target: 1000 },
        { duration: '20s', target: 1000 }, // Hold
        { duration: '10s', target: 0 },    // Ramp-down
      ],
      gracefulStop: '5s',
    },
  },
  thresholds: {
    'http_req_failed{scenario:writers}': ['rate<0.50'], // Stop if >50% write fail
    'http_req_failed{scenario:readers}': ['rate<0.05'],
    'http_req_duration{scenario:readers}': ['p(95)<1000'],
    'http_req_duration{scenario:writers}': ['p(95)<1000'],
  },
};

// Setup once: generate 10,000 players before the test
export function setup() {
  console.log(`Generating ${TOTAL_PLAYERS} players...`);
  const res = http.post(`${BASE_URL}/players`, JSON.stringify({ count: TOTAL_PLAYERS }), {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'player generation succeeded': (r) => r.status === 200,
  });

  sleep(3); // brief wait
}

// READERS: 10,000 VUs constantly requesting top 100 players
export function readScenario() {
  const res = http.get(`${BASE_URL}/top?n=${READ_COUNT}`, {
    tags: { scenario: 'readers' },
  });

  check(res, {
    'read success': (r) => r.status === 200,
  });

  sleep(1);
}

// WRITERS: ramping up, randomly incrementing players
export function writeScenario() {
  const playerId = randomIntBetween(1, TOTAL_PLAYERS);
  const increment = randomIntBetween(1, 100);

  const res = http.post(
    `${BASE_URL}/${playerId}/score/incrementBy`,
    JSON.stringify({ incrementBy: increment }),
    {
      headers: { 'Content-Type': 'application/json' },
      tags: { scenario: 'writers' },
    }
  );

  const ok = check(res, {
    'write success': (r) => r.status === 200,
  });

  if (!ok) {
    console.error(`❌ Failed to write to player ${playerId}`);
  }

  sleep(1);
}
