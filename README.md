# ⚡ Charge Point – EV Charging Request Service

A Spring Boot microservice written in Kotlin that handles **electric vehicle (EV) charging requests**. It receives charging requests from clients, enriches them with contextual data, persists them in the database, and publishes the request to Kafka for asynchronous processing.

---

## 📌 Core Features

- ✅ Accept and validate charging requests  
- 🧠 Enrich requests with system metadata  
- 🗃️ Persist requests in the database using Spring Data JPA  
- 🚀 Publish enriched requests to Kafka asynchronously  
- 🔁 Receive status updates via callback  
- 🧪 Dev utility to pre-load system data  

---

## 🛠️ Tech Stack

| Layer         | Technology                    |
|--------------|-------------------------------|
| Language      | Kotlin (JVM 17)               |
| Framework     | Spring Boot 3.x               |
| Build Tool    | Gradle                        |
| DB Access     | Spring Data JPA               |
| Messaging     | Apache Kafka (Producer)       |
| Validation    | Jakarta Bean Validation       |
| Concurrency   | Kotlin Coroutines             |
| Configuration | `application.properties`      |
| DB (Dev)      | H2 (in-memory)                |

---

## 🚀 Getting Started

### ✅ Prerequisites

- JDK 17+
- Kafka (optional, for full functionality)
- Gradle
- Docker (optional for local Kafka)

### 🧪 Clone & Build

```bash
git clone https://github.com/heshawa/charge-point.git
cd charge-point
./gradlew clean build
```

### ▶️ Run the App

```bash
./gradlew bootRun
```

App will be accessible at:  
```
http://localhost:8080
```

---

## 🔗 API Endpoints

Base path: `/chargepoint/v1/api`

### 🔋 POST `/charge`

Initiates a new vehicle charging request.

**Request Body** (`ChargingRequest.kt`)
```json
{
    "driverId": "c85ac7bc-1d92-4b5a-8884-a05990966d55",
    "requestedStationId": "23dcc323-6a07-4320-8368-d809db1f2b1f",
    "callbackUrl": "http://localhost:8080/chargepoint/v1/api/callback"
}
```

**Response** (`ChargingResponse.kt`)
```json
{
  "status": "RECEIVED",
  "message": "Charging request received and processing started"
}
```

✅ Internally enriched and published to Kafka

---

### 🔁 POST `/callback`

Receives completion information from an external service or system.

**Request Body** (`CallbackRequestBody.kt`)
```json
{
    "driver_token":"c85ac7bc-1d92-4b5a-8884-a05990966d55",
    "station_id":"23dcc323-6a07-4320-8368-d809db1f2b1f",
    "status":"allowed"
}
```

**Response**
```json
{
  "success": true
}
```

---

### 🧪 GET `/populateData`

Utility endpoint to preload mock drivers, vehicles, and stations for testing purposes.

---

## 📁 Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── org/chargepoint/charging/v1/api/
│   │       ├── controller/ChargePointServiceAPI.kt
│   │       ├── service/ChargingService.kt
│   │       ├── service/ChargingServiceImpl.kt
│   │       ├── dto/
│   │       │   ├── ChargingRequest.kt
│   │       │   ├── ChargingResponse.kt
│   │       │   ├── CallbackRequestBody.kt
│   │       │   ├── RequestStatus.kt
│   │       │   └── ServiceRequestContext.kt
│   └── resources/
│       └── application.properties
```

---

## 🧠 Service Behavior

### 🔄 Charging Flow

1. `POST /charge`:  
   - Validates and enriches the request  
   - Saves it to the database  
   - Publishes to Kafka  
   - Updates status to `PUBLISHED`

2. `POST /callback`:  
   - Updates the request status to `COMPLETED` or other states

3. `GET /populateData`:  
   - Seeds test data (stations, vehicles, drivers)

---

## ⚙️ Configuration Highlights

`src/main/resources/application.properties`
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:chargingdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

---

## 🧪 Example Curl

```bash
curl -X POST http://localhost:8080/chargepoint/v1/api/charge   -H "Content-Type: application/json"   -d '{
    "driverId": "c85ac7bc-1d92-4b5a-8884-a05990966d55",
    "requestedStationId": "23dcc323-6a07-4320-8368-d809db1f2b1f",
    "callbackUrl": "http://localhost:8080/chargepoint/v1/api/callback"
}'
```

---

## 🔐 TODO & Improvements

- Add Swagger/OpenAPI documentation
- Integrate Kafka consumer for processing
- Add authentication (Spring Security)
- Add Dockerfile and Docker Compose for local stack
- Write unit/integration tests

---

## 👨‍💻 Maintainer

Developed and maintained by [@heshawa](https://github.com/heshawa)

---

## 📄 License

MIT License. See [LICENSE](./LICENSE) for more.
