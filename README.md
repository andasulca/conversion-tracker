# Conversion Tracker

Track and store sales data locally using Spring Boot + PostgreSQL.

---

## Local Setup Guide

This app uses Docker to simplify local development. Follow these steps to run everything without installing PostgreSQL or Java locally.

---

## Prerequisites

* [Docker Desktop](https://www.docker.com/products/docker-desktop) — must be installed and running
* Git (to clone this project)

Optional (for manual builds):

* Java 21
* Gradle

---

## 1. Start Docker

Make sure Docker is running before continuing.

---

## 2. Set Up Environment Variables

Copy the example:

```bash
cp .env.example .env
```

You can leave the values as-is for local testing.

---

## 3. Build & Run with Docker (Recommended)

This builds the app and runs both the app and PostgreSQL container:

```bash
docker compose up --build
```

To stop everything:

```bash
docker compose down
```

---

## 4. (Optional) Manual Run

If you'd rather run the app manually:

1. Start the DB container:

```bash
docker compose up db
```

2. Build and run the app:

```bash
./gradlew bootJar
java -jar build/libs/conversion-tracker-0.0.1-SNAPSHOT.jar
```

---

## 5. Connect to External Sales Data Provider

This application depends on an external HTTP API that provides sales data. It does **not** generate data itself. Instead, it fetches historical and real-time sales records and stores them in a local PostgreSQL database.

If the external service is running **outside Docker** (on your local machine), use this special hostname to access it **from within the container:**

```text
host.docker.internal
```

Do **not** use localhost, as that points to the container itself — not your host machine.

Update your .env file accordingly:

```text
EXTERNAL_API_URL=http://host.docker.internal:8081/sales-data
```
---
## 6. External API Details
The application periodically calls the following endpoint to retrieve sales data:
```text
GET /sales-data?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD
```

Required query parameters:
fromDate — Start date in ISO format (e.g., 2025-01-01)
toDate — End date in ISO format (e.g., 2025-01-10)

Example request:
```text
GET /sales-data?fromDate=2025-01-01&toDate=2025-01-10
```

Expected JSON response:
```text
[
  {
    "id": 1,
    "trackingId": "ABC123",
    "visitDate": "2025-01-01T12:30:00",
    "saleDate": "2025-01-01T14:00:00",
    "salePrice": 149.99,
    "product": "Deluxe Widget",
    "commissionAmount": 15.00
  },
  {
    "id": 2,
    "trackingId": "DEF456",
    "visitDate": "2025-01-02T10:00:00",
    "saleDate": null,
    "salePrice": null,
    "product": "Basic Gadget",
    "commissionAmount": null
  }
]
```
If the external API is not available at startup, the app will continue running and attempt to fetch data later.

## Project Structure

```text
src/main/java/io/github/andasulca/conversiontracker
├── ConversionTrackerApplication.java
├── client/
├── controller/
├── dto/
├── entity/
├── repository/
├── scheduler/
└── service/
```

---

## Stack

* Java 21
* Spring Boot 3.2
* PostgreSQL 16 (via Docker)
* Docker Compose

---

## License

MIT License — basically, do whatever you want. Copy it, break it, fix it, ship it.
Just don’t sue me if your laptop catches fire.
