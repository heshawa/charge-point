# Charging Service Request Station

Spring boot project to expose API for customers to submit requests to allocate charging station

## 📦 Technologies Used

- Kotlin 1.9
- Java 17
- Spring Boot 3.x
- Kafka
- Gradle (or Maven)
- H2 database

## 🏁 Getting Started

### ✅ Prerequisites

Make sure you have:

- Java 17+
- Gradle installed (`./gradlew` wrapper is also included)
- Add Lombok plugin to your IDE to avoid Lombok IDE error indications


### 🔧 Running the App

- If your default java version is not Java 17
    - Update .project-env file with your java 17 path
    - Execute command
        ```bash 
        source .project-env
        ```
- Execute following commands
     ```bash
     ./gradlew clean build
     ./gradlew bootRun
    ```

- Execute in debug mode
     ```bash
     ./gradlew clean build
     ./gradlew bootRun --debug-jvm
    ```

### 📖 API Guide

#### order API
- **POST** `/chargepoint/v1/api/charge` - HTTP Response Code: **200**
  Validate the request and acknowledge the request
    - Request sample 1 - success
      ```javascript
      Request Body
      {
        "driverId": "0ce0a920-a32f-48de-b7e3-0d4467383562",
        "requestedStationId": "b8e8c31c-6f87-47da-b329-698e06a99747",
        "callbackUrl": "http://localhost:8080/chargepoint/mobile/notification/0ce0a920-a32f-48de-b7e3-0d4467383562"
      }
      ```
    - Request sample 2 - no content
      ```javascript
      Request Body
      {}
      ```
      ```javascript
      HTTP/1.1 204
      ```
- **GET** `/inventory/get/{itemName}` - HTTP Response Code: **200**
  Search an inventory items by its name. Returns the item details.
    - Request sample 1 - success
      ```javascript
      Request
      http://localhost:8080/inventory/hammer
      ```
      ```javascript
      Response - HTTP/1.1 200
      Content-Type: application/json
      [
        {
            "name": "Sledge Hammer",
            "description": "3cm head, Wooden handle and 1Kg weight",
            "price": "35.25",
            "quantity": "15"
        },
        {
            "name": "Nail Hammer",
            "description": "2cm head, Wooden handle and 750g weight",
            "price": "27.00",
            "quantity": "15"
        }
      ]
      ```
    - Request sample 2 - no results
      ```javascript
      Request
      http://localhost:8080/inventory/aaa
      ```
      ```javascript
      Response - HTTP/1.1 200
      Content-Type: application/json
      {
        "message": "No items with the given name",
        "description": ""
      }
      ```

### 📖 DB Guide
Following instructions are based on values in `application.properties` file
- Visit `http://localhost:8080/h2-console` to view data inserted to DB
- Configuration values are as follow
    - Driver Class: `org.h2.Driver`
    - JDBC URL: `jdbc:h2:file:./data/inventoryDB`
    - Username: `adminInventory`
    - Password:
- Press test connectivity button to check if connection is successful
- Press connect button to view data in DB
- Write SQL queries in the SQL command box and press run button to execute the query

### 🧪 Running Unit Tests

Unit tests are written using JUnit 5 and Mockito. To execute the tests, run the following command:

```bash
./gradlew test
```
To run in debug mode
```bash
./gradlew test --debug-jvm
```

To view the test report
```javascript
http://localhost:63342/inventoryControl/build/reports/tests/test/index.html
```

Clone the repository:

```bash
git clone https://github.com/heshawa/inventory-control.git
cd your-repo
