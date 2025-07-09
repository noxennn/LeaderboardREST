import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const BASE_URL = 'http://127.0.0.1:8080/leaderboard';
const TOTAL_PLAYERS = 10000;
const READ_COUNT = 100;

export let options = {
  scenarios: {
    readers: {
      executor: 'ramping-vus',
      exec: 'readScenario',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 6250 },
        { duration: '50s', target: 6250 },
      ],
       gracefulStop: '5s',
   },
   writers: {
         executor: 'ramping-vus',
         exec: 'writeScenario',
         startVUs: 0,
         stages: [
           { duration: '5s', target: 500 },
           { duration: '45s', target: 500 },
           { duration: '10s', target: 0},
         ],
         gracefulStop: '5s',
   },

//      vus: 2000,
//      duration: '90s',
    },

};
//  thresholds: {
//    'http_req_failed{scenario:writers}': ['rate<0.50'],
//    'http_req_failed{scenario:readers}': ['rate<0.05'],
//    'http_req_duration{scenario:readers}': ['p(95)<1000'],
//    'http_req_duration{scenario:writers}': ['p(95)<1000'],
//  },
//};

export function setup() {
  console.log(`Generating ${TOTAL_PLAYERS} players...`);
  const res = http.post(`${BASE_URL}/players`, JSON.stringify({ count: TOTAL_PLAYERS }), {
    headers: { 'Content-Type': 'application/json' },
  });

  check(res, {
    'player generation succeeded': (r) => r.status === 200,
  });

  sleep(3);
}

export function teardown() {
  console.log('Deleting all players...');
  const res = http.del(`${BASE_URL}/players`);

  const ok = check(res, {
    'all players deleted': (r) => r.status === 200,
  });

  if (ok) {
    console.log('✅ All players deleted after test');
  } else {
    console.error(`❌ Failed to delete players. Status: ${res.status}`);
  }
}

export function readScenario() {
  const res = http.get(`${BASE_URL}/top?n=${READ_COUNT}`, {
    tags: { scenario: 'readers' },
  });

  check(res, {
    'read success': (r) => r.status === 200,
  });

  sleep(0.5);
}

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
