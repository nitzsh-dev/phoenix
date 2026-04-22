# Phoenix Insurance System 🛡️

A Spring Boot-based backend system designed to manage user registration, Two-Factor Authentication (OTP), and insurance product purchases. This project demonstrates a clean layered architecture, the use of Value Objects for domain validation, and in-memory data management.

## 🚀 Key Features
* **User Management:** Register and manage users with specific roles (`USER`, `ADMIN`).
* **OTP Identification:** A secure identification flow via Email or SMS.
* **Security Logic:** Includes automatic session expiration (5 minutes) and account blocking after 3 failed OTP attempts.
* **Product Catalog:** Browse available insurance products and manage them (Admin only).
* **In-Memory Storage:** Utilizes `ConcurrentHashMap` for high-performance, thread-safe data handling.
* **Domain-Driven Validation:** Uses Java Records to ensure valid email formats and phone numbers.

## 🏗️ Architecture & Project Structure
The project follows a standard layered architecture:
* `controller`: REST API endpoints.
* `service`: Business logic and orchestration.
* `repository`: Data access layer (In-memory implementation).
* `model`: Domain entities, Enums, and Value Objects.
* `dto`: Data Transfer Objects for API requests.
* `config`: Application configuration and data initialization.

## 🛠️ Tech Stack
* **Java 17** | **Spring Boot 3.x** | **Lombok** | **JUnit 5**

## 📝 Getting Started
1. Clone the repository: `git clone https://github.com/nitzsh-dev/phoenix.git`
2. Navigate to directory: `cd phoenix`
3. Run the application: `mvn spring-boot:run`

## 🚦 Main API Endpoints

| Action | Method | Path |
| :--- | :--- | :--- |
| **Register** | `POST` | `/api/users/register` |
| **Login** | `POST` | `/api/users/login` |
| **Verify OTP** | `POST` | `/api/auth/verify` |
| **Purchase** | `POST` | `/api/users/{id}/purchase/{pId}` |
| **Update Product** | `PUT` | `/api/products/update` |

*Note: For protected actions, provide the session ID in the `X-OTP-Session-Id` header.*

## 🧪 Testing
To run the test suite: `mvn test`

---
### Author's Note
This system was built with **Clean Code** principles in mind, ensuring that each layer has a single responsibility.
