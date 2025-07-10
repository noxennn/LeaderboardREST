import { check, sleep, group } from 'k6';
import http from 'k6/http';

// Define load test options
export const options = {
  vus: 5000, // 100 virtual users
  duration: '60s', // Test duration
};

export default function () {
  group('Increment Player Score', function () {
    // Generate a random player ID between 1 and 10000
    const playerId = Math.floor(Math.random() * 1000)+1 ;

    // Simulate an increment score request
    let res = http.post(`http://localhost:8080/leaderboard/${playerId}/score/incrementBy`, JSON.stringify({
      incrementBy: Math.floor(Math.random() * 50) + 1,  // The increment value for the score
    }), { headers: { 'Content-Type': 'application/json' } });

    // Check that the response is 200 OK
    check(res, {
      'Increment score status is 200': (r) => r.status === 200,
    });

    
  });
}
