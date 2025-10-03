# Load Testing with K6

Simple and effective load testing for the Cart API using K6 with web dashboard.

## 📁 File Structure

```
load-testing/
├── test.sh          # Main test runner script
├── load-test.js     # K6 load test configuration and scenarios
└── README.md        # This documentation
```

## 🚀 Quick Start

### Prerequisites
- K6 installed: `brew install k6` (macOS) or see [K6 installation guide](https://k6.io/docs/getting-started/installation/)
- Cart API running on `http://localhost:8080`
- MongoDB running on `localhost:27017`

⚠️ **High-Scale Warning**: This test scales up to 1000 concurrent users over 25 minutes. Make sure your system can handle this load!

### Simple Test (Recommended)

```bash
./test.sh
```

**What you get:**
- ✅ 25-minute high-scale load test (up to 1000 users)
- ✅ Real-time web dashboard at `http://localhost:5665`
- ✅ Tests create cart (20%) and view existing cart (80%) scenarios
- ✅ Realistic user behavior patterns with gradual ramp-up
- ✅ Automatic health checks
- ✅ Clean console output
- ✅ Stop with Ctrl+C

### Customize Test Parameters

Edit `load-test.js` to change:
- **Stages**: Modify the `stages` array for different load patterns
- **Thresholds**: Adjust performance thresholds
- **Scenarios**: Modify the test scenarios

## 📊 K6 Web Dashboard

The web dashboard provides:
- **Real-time metrics**: Response times, throughput, error rates
- **Interactive charts**: Visual representation of performance
- **Live updates**: See metrics as the test runs
- **Detailed breakdowns**: Per-endpoint performance analysis

**Access**: `http://localhost:5665` (opens automatically)

## 📁 Report Locations

- **Web Dashboard**: `http://localhost:5665` (live during test)
- **Console Output**: Real-time metrics in terminal

## 🎯 Test Scenarios

### Default Test
- **Total Duration**: 25 minutes
- **Load Pattern**: Realistic ramp-up and ramp-down
- **Peak Users**: 1000 concurrent users
- **Create Cart**: 20% of requests
- **View Existing Cart**: 80% of requests (realistic user behavior)

### Load Stages
- **0-2m**: Ramp up to 50 users
- **2m-5m**: Ramp up to 200 users  
- **5m-10m**: Ramp up to 500 users
- **10m-13m**: Ramp up to 1000 users
- **13m-18m**: Stay at 1000 users (peak load)
- **18m-21m**: Ramp down to 500 users
- **21m-23m**: Ramp down to 200 users
- **23m-24m**: Ramp down to 50 users
- **24m-25m**: Ramp down to 0 users

### Customizable
- **Stages**: Modify the `stages` array in `load-test.js` for different load patterns
- **Thresholds**: Adjust performance thresholds in `load-test.js`
- **Scenarios**: Edit the test scenarios in `load-test.js`

## 🛑 Stopping Tests

Press `Ctrl+C` to stop any running test gracefully.

## 🔧 Customization

### Modify Test Parameters

Edit `simple-test.sh`:
```bash
# Change duration and users
K6_WEB_DASHBOARD=true k6 run --duration=5m --vus=50 - << 'EOF'
```

### Modify Test Scenarios

Edit the JavaScript section in `simple-test.sh` to change:
- Request ratios (create vs view)
- Request payloads
- Response time thresholds
- Custom metrics

## 📈 Understanding Results

### Key Metrics
- **Response Time**: Average, P95, P99 percentiles
- **Throughput**: Requests per second
- **Error Rate**: Percentage of failed requests
- **Status Codes**: Distribution of HTTP responses

### Thresholds
- Create cart: < 500ms response time
- View cart: < 300ms response time
- Error rate: < 20%

## 🚨 Troubleshooting

### App Not Running
```
❌ App is not running on http://localhost:8080
```
**Solution**: Start your app with `./gradlew bootRun`

### K6 Not Installed
```
❌ K6 is not installed
```
**Solution**: Install K6 with `brew install k6`

### Dashboard Not Opening
- Check if port 5665 is available
- Try accessing `http://localhost:5665` manually
- Restart the test if needed

## 🎉 Benefits

- **Simple**: One command to run everything
- **Visual**: Real-time web dashboard
- **Effective**: Tests realistic user behavior
- **Clean**: No complex scripts or configuration
- **Fast**: Quick setup and execution