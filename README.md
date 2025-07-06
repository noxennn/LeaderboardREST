running k6 test:
$Env:K6_PROMETHEUS_RW_SERVER_URL = "http://localhost:9090/api/v1/write"
k6 run --out experimental-prometheus-rw loadtest.js
