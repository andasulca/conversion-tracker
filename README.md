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

## 5. Connect to Other Local Services

If you are running a separate service (like a "Juno" app) locally **outside of Docker**, you must use this hostname **inside your Docker container**:

```text
host.docker.internal
```

So instead of using `http://localhost:8081/sales-data` in your app, use:

```text
http://host.docker.internal:8081/sales-data
```

This tells the container to connect to your host machine’s local services.

---

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
