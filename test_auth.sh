#!/bin/bash

# Test script for portfolio authentication fix

# Step 1: Login and save JWT token
echo "=== Step 1: Login ==="
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' \
  -i)

echo "$LOGIN_RESPONSE"

# Extract JWT token from Authorization header
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "^Authorization:" | sed 's/Authorization: Bearer //I' | tr -d '\r\n ')

if [ -z "$TOKEN" ]; then
    echo "Failed to get token. Trying different password..."
    LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/login \
      -H "Content-Type: application/json" \
      -d '{"username":"testuser","password":"test123"}' \
      -i)

    echo "$LOGIN_RESPONSE"
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "^Authorization:" | sed 's/Authorization: Bearer //I' | tr -d '\r\n ')
fi

echo "Token: $TOKEN"

# Step 2: Try to update portfolio with JWT token
echo -e "\n=== Step 2: Update Portfolio ==="
curl -v -X PUT http://localhost:8081/users/1/portfolio \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"portfolioSize":1000,"minEdge":5,"minWin":2}' \
  2>&1 | grep -A 20 "< HTTP"

# Step 3: Try GET profile to confirm token works
echo -e "\n=== Step 3: GET Profile (should work) ==="
curl -s -X GET http://localhost:8081/users/1/profile \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
