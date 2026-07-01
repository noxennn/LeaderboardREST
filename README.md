# LeaderboardREST

> A Spring Boot leaderboard REST API that benchmarks **three interchangeable in-memory backends** — Plain Java, Caffeine, and Redis — behind a single interface, with full load testing and live observability.

![Java](https://img.shields.io/badge/Java-17-007396)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-6DB33F)
![Redis](https://img.shields.io/badge/Redis-DC382D)
![Docker](https://img.shields.io/badge/Docker-2496ED)
![Grafana](https://img.shields.io/badge/Grafana-F46800)

Developed during a backend engineering internship at ASELSAN (2025).

---

## 🎯 What & Why
The same leaderboard API is implemented against **three storage strategies behind one interface**, so their latency and throughput can be compared apples-to-apples under identical load:
- **Plain Java** — in-process data structures
- **Caffeine** — in-process cache
- **Redis** — external in-memory store

## 📊 Results
Load-tested with **k6** up to **~10,000 ops/s**, scraped by **Prometheus**, visualized in **Grafana**:

| Backend | p99 latency | Notes |
|--------|-------------|-------|
| Plain Java | ≈ 0.004 ms (µs-level) | fastest, single-node only |
| Caffeine | <!-- fill from your measurements --> | in-process cache |
| Redis | ms-level | network hop, horizontally scalable |

<!-- Embed the images from "LeaderboardLoadTest Screenshots/" here — this is your selling point. -->
<!-- ![k6 + Grafana](docs/grafana.png) -->

## 🧱 Tech Stack
Spring Boot · Redis · Caffeine · Docker Compose · Prometheus · Grafana · k6 · Maven

## 🚀 Run it
```bash
docker compose up -d          # app + Redis + Prometheus + Grafana

# run the load test
$Env:K6_PROMETHEUS_RW_SERVER_URL = "http://localhost:9090/api/v1/write"
k6 run --out experimental-prometheus-rw loadtest.js
```

## 📁 Structure
- `src/` — Spring Boot application (single interface, three implementations)
- `LeaderboardDiagrams/` — architecture & flow diagrams
- `LeaderboardLoadTest Screenshots/` — k6 / Grafana results
- `grafana/`, `prometheus.yml`, `redis.conf` — observability & infra config
- `loadtest.js` — k6 scenario
- `docs/` — internship report & presentation

## 📄 License
<!-- Add MIT (or similar) if you want this to read as open source. -->
