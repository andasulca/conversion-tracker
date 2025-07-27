# Conversion Tracker

Track and store sales data locally using Spring Boot + PostgreSQL.

---

## Local Setup Guide

This app uses Docker to simplify local development. Follow these steps to run everything without installing PostgreSQL or Java locally.

---

## Prerequisites

* [Docker Desktop](https://www.docker.com/products/docker-desktop) — must be installed and running
* Git (to clone this project)

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

## 3. Build & Run with Docker

This builds the app and runs both the app and PostgreSQL container:

```bash
docker compose up --build
```

To stop everything:

```bash
docker compose down
```

### Debugging Guide

- Port `5005` is always exposed in this setup for development
- This is safe for local dev, but should be disabled in production builds
#### Create a Remote Debug Configuration in IntelliJ

1. Go to **Run > Edit Configurations**
2. Click the ➕ icon and select **Remote JVM Debug**
3. Use the following settings:
   - **Name:** Docker Remote Debug
   - **Host:** `localhost`
   - **Port:** `5005`
   - (Leave other settings as default)
4. Click **OK** to save
#### Start Debugging

- Set breakpoints in your code
- Go to **Run > Debug 'Docker Remote Debug'** or use `Shift + F9`
- You should see:

```text
Connected to the target VM at 'localhost:5005' using socket transport.
```
---

## 4. Connect to External Sales Data Provider

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

## 5. External API Details

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
    "trackingId": "ABB001",
    "visitDate": "2025-01-01T12:30:00",
    "saleDate": "2025-01-01T14:00:00",
    "salePrice": 149.99,
    "product": "Deluxe Widget",
    "commissionAmount": 15.00
  },
  {
    "id": 2,
    "trackingId": "ABB002",
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

## 6. 📡 Available APIs

This application exposes the following RESTful APIs under the base path `/api`.

---

### ➤ 1. Get Conversion Rate for a Landing Page

**Endpoint:**

```http
GET /api/conversion-rate
```

**Query Parameters:**

- `landingPageCode` – Landing page identifier (e.g. `ABB001`)
- `start` – Start date (format: `YYYY-MM-DD`)
- `end` – End date (format: `YYYY-MM-DD`)

**Example Request:**

```bash
curl "http://localhost:8080/api/conversion-rate?landingPageCode=ABB001&fromDate=2025-07-01&toDate=2025-07-25"
```

**Example Response:**

```json
{
  "landingPageCode": "ABB001",
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
curl "http://localhost:8080/api/commission?landingPageCode=ABB001&fromDate=2025-07-01&toDate=2025-07-25"
```

**Example Response:**

```json
{
  "landingPageCode": "ABB001",
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
curl "http://localhost:8080/api/products/conversions?fromDate=2025-07-01&toDate=2025-07-25"
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
## 7. 🔍 Inspect the Database (No GUI Required)

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
`Note: This section is written from the perspective of a developer currently learning Spring Boot and the MVC pattern. It serves as a personal reference and may help future contributors understand the high-level layout.`

This project follows a standard Spring Boot application structure aligned with the MVC (Model-View-Controller) architectural pattern. Here's a breakdown of each main package:

* `ConversionTrackerApplication.java`
Entry point of the application. Annotated with @SpringBootApplication, it bootstraps the entire Spring context.

* `client/`
Contains components responsible for interacting with external services or APIs (e.g., fetching exchange rates).

* `controller/`
Handles incoming HTTP requests. Maps endpoints and delegates business logic to the service layer.

* `dto/ (Data Transfer Objects)`
Defines objects used for transferring data between layers, especially between client requests and internal logic. Helps isolate external input/output models from domain models.

* `entity/`
Represents the core domain models and database entities. These classes are typically annotated with JPA annotations like @Entity.

* `repository/`
Interfaces extending Spring Data JPA repositories, providing easy CRUD operations and database queries with minimal boilerplate.

* `scheduler/`
Contains components for scheduled or time-based tasks. Uses Spring’s @Scheduled to automate background jobs.

* `service/`
Implements the core business logic of the application. Services receive data from controllers, process it, and delegate database operations to repositories.
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
