import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080';

// Define load stages for quick ramp-up to 1000 users
export let options = {
  stages: [
    { duration: '30s', target: 100 },   // Ramp up to 100 users in 30s
    { duration: '30s', target: 500 },   // Ramp up to 300 users in 30s
    { duration: '1m', target: 2000 },   // Ramp up to 1000 users in 1m
    { duration: '3m', target: 2000 },   // Stay at 1000 users for 3m (peak load)
    { duration: '1m', target: 500 },    // Ramp down to 500 users in 1m
    { duration: '30s', target: 100 },   // Ramp down to 100 users in 30s
    { duration: '30s', target: 0 },     // Ramp down to 0 users in 30s
  ],
  thresholds: {
    http_req_duration: ['p(95)<1000'], // 95% of requests should be below 1000ms
    http_req_failed: ['rate<0.05'],    // Error rate should be less than 5%
  },
};

// Array to store created cart IDs for realistic testing
let createdCartIds = [];

export default function () {
  // 20% create cart, 80% view existing carts
  if (Math.random() < 0.2) {
    // Create cart
    const payload = JSON.stringify({
      userId: `user-${Math.random().toString(36).substring(7)}`,
      items: []
    });
    
    const createResponse = http.post(`${BASE_URL}/carts`, payload, {
      headers: { 'Content-Type': 'application/json' },
    });
    
    check(createResponse, {
      'create cart status is 200': (r) => r.status === 200,
      'create cart response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    // Store cart ID for later use
    if (createResponse.status === 200) {
      const cartId = createResponse.json('cartId');
      if (cartId) {
        createdCartIds.push(cartId);
        // Keep only the last 100 cart IDs to prevent memory issues
        if (createdCartIds.length > 100) {
          createdCartIds = createdCartIds.slice(-100);
        }
      }
    }
  } else {
    // View existing cart (80% of requests)
    if (createdCartIds.length > 0) {
      const randomCartId = createdCartIds[Math.floor(Math.random() * createdCartIds.length)];
      const viewResponse = http.get(`${BASE_URL}/carts/${randomCartId}`);
      check(viewResponse, {
        'view existing cart status is 200': (r) => r.status === 200,
        'view existing cart response time < 300ms': (r) => r.timings.duration < 300,
      });
    } else {
      // If no carts exist yet, create one
      const payload = JSON.stringify({
        userId: `user-${Math.random().toString(36).substring(7)}`,
        items: []
      });
      
      const createResponse = http.post(`${BASE_URL}/carts`, payload, {
        headers: { 'Content-Type': 'application/json' },
      });
      
      check(createResponse, {
        'create cart status is 200': (r) => r.status === 200,
        'create cart response time < 500ms': (r) => r.timings.duration < 500,
      });
      
      if (createResponse.status === 200) {
        const cartId = createResponse.json('cartId');
        if (cartId) {
          createdCartIds.push(cartId);
        }
      }
    }
  }
  
  sleep(1);
}