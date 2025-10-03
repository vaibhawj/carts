#!/bin/bash

# Load Test Script

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}🚀 Cart API Load Test${NC}"
echo "================================"

# Check if app is running
echo -e "${BLUE}Checking if app is running...${NC}"
if ! curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "❌ App is not running on http://localhost:8080"
    echo "Please start your app first: ./gradlew bootRun"
    exit 1
fi
echo -e "${GREEN}✅ App is running${NC}"

# Check if K6 is installed
if ! command -v k6 &> /dev/null; then
    echo "❌ K6 is not installed"
    echo "Install it: brew install k6"
    exit 1
fi
echo -e "${GREEN}✅ K6 is installed${NC}"

# Run K6 with web dashboard
echo -e "${BLUE}Starting load test with web dashboard...${NC}"
echo "📊 Dashboard will open at: http://localhost:5665"
echo "Press Ctrl+C to stop"

# Run K6 with external script
K6_WEB_DASHBOARD=true k6 run ./load-test.js

echo -e "${GREEN}✅ Test completed!${NC}"
echo "Check the output above for results."