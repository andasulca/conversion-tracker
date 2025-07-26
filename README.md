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

```http
GET /sales-data?fromDate=YYYY-MM-DD&toDate=YYYY-MM-DD
```

### Required Query Parameters:

- `fromDate` — Start date in ISO format (e.g., `2025-01-01`)
- `toDate` — End date in ISO format (e.g., `2025-01-10`)

### Example Request:

```http
GET /sales-data?fromDate=2025-01-01&toDate=2025-01-10
```

### Expected JSON Response:

```json
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

### What happens after fetching:

- On **startup**, historical data is fetched and stored locally.
- Every **5 minutes**, the app polls for new data and filters it by known tracking IDs.
- Only records matching your owned tracking prefixes (`ABB`, `TBS`, `EKW`) are stored in the local database.
- The number of records fetched, filtered, and stored is logged clearly for visibility.

If the external API is not available at startup, the app will continue running and attempt to fetch data later.

---

## 7. 📡 Available APIs

This application exposes the following RESTful APIs under the base path `/api`.

---

### ➤ 1. Get Conversion Rate for a Landing Page

**Endpoint:**

```http
GET /api/conversion-rate
```

**Query Parameters:**

- `landingPageCode` – Landing page identifier (e.g. `abc123`)
- `start` – Start date (format: `YYYY-MM-DD`)
- `end` – End date (format: `YYYY-MM-DD`)

**Example Request:**

```bash
curl "http://localhost:8080/api/conversion-rate?landingPageCode=abc123&start=2025-07-01&end=2025-07-25"
```

**Example Response:**

```json
{
  "landingPageCode": "abc123",
  "conversionRate": 0.15
}
```

---

### ➤ 2. Get Total Commission for a Landing Page

**Endpoint:**

```http
GET /api/commission
```

**Query Parameters:**

- `landingPageCode` – Landing page identifier
- `start` – Start date
- `end` – End date

**Example Request:**

```bash
curl "http://localhost:8080/api/commission?landingPageCode=abc123&start=2025-07-01&end=2025-07-25"
```

**Example Response:**

```json
{
  "landingPageCode": "abc123",
  "totalCommission": 42.50
}
```

---

### ➤ 3. Get Conversion Rates per Product

**Endpoint:**

```http
GET /api/product-conversions
```

**Query Parameters:**

- `start` – Start date
- `end` – End date

**Example Request:**

```bash
curl "http://localhost:8080/api/product-conversions?start=2025-07-01&end=2025-07-25"
```

**Example Response:**

```json
[
  {
    "productId": "prod-001",
    "conversionRate": 0.12
  },
  {
    "productId": "prod-002",
    "conversionRate": 0.33
  }
]
```
## 8. 🔍 Inspect the Database (No GUI Required)

You can connect directly to the PostgreSQL database **inside the Docker container** using the built-in `psql` CLI — no external tools needed.

### Connect to PostgreSQL with `psql`

1. Open a terminal and run:

   ```bash
   docker exec -it conversion-tracker-db-1 psql -U postgres -d conversion_tracker
   ```

   > Replace `conversion-tracker-db-1` with your actual container name if different (check with `docker ps`).

2. You’ll see something like this:

   ```
   psql (16.x)
   Type "help" for help.

   conversion_tracker=#
   ```

3. Run SQL queries like:

   ```sql
   SELECT COUNT(*) FROM sales_data;
   SELECT * FROM sales_data ORDER BY id DESC LIMIT 5;
   ```

4. To exit the session:

   ```
   \q
   ```

### Example Output

```bash
PS C:\Users\Anda\IdeaProjects\conversion-tracker> docker exec -it conversion-tracker-db-1 psql -U postgres -d conversion_tracker
psql (16.9)
Type "help" for help.

conversion_tracker=# SELECT COUNT(*) FROM sales_data;
 count 
-------
  6689
(1 row)
```


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
