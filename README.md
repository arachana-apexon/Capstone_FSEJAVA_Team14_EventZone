# EventZone Backend

A robust and scalable backend API for event management and booking system built with Spring Boot 3.5.x and Java 21.

## 📋 Project Overview

EventZone Backend is a comprehensive RESTful API service designed to manage events, bookings, and user authentication. The system provides role-based access control (RBAC) with distinct functionalities for administrators, organizers, and customers. It enables event organizers to create and manage events, while customers can browse, book tickets, and manage their bookings seamlessly.

## ✨ Features

- **Authentication & Authorization**
  - User registration and login with JWT-based authentication
  - Role-Based Access Control (RBAC) supporting ADMIN, ORGANISER, and CUSTOMER roles
  - Secure password encryption using BCrypt

- **Event Category Management**
  - Create, read, update, and delete event categories
  - Categorize events for better organization and discovery

- **Event Management**
  - Create and manage events with detailed information
  - Upload event images and manage event status
  - Admin-controlled event activation and deactivation
  - Event filtering and search capabilities

- **Ticket Category Management**
  - Define multiple ticket categories per event
  - Set pricing and availability for each category
  - Manage ticket inventory in real-time

- **Booking Management**
  - Customer ticket booking with seat allocation
  - Real-time availability checking
  - Booking confirmation and tracking
  - Booking cancellation and refund processing
  - Organizer booking summary and analytics

- **Admin Controls**
  - User role management
  - Event approval and moderation
  - System-wide monitoring and control

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 3.5.15 | Application Framework |
| **Spring Data JPA** | 3.5.x | ORM and Data Access |
| **Spring Security** | 6.x | Authentication & Authorization |
| **PostgreSQL** | Latest | Relational Database |
| **Maven** | 3.x | Build & Dependency Management |
| **SpringDoc OpenAPI** | 2.8.8 | API Documentation (Swagger) |
| **Lombok** | Latest | Boilerplate Code Reduction |
| **Jakarta Validation** | 3.x | Input Validation |

## 📦 Prerequisites

Before setting up the project, ensure you have the following installed:

- **Java Development Kit (JDK) 21** - [Download Here](https://www.oracle.com/java/technologies/downloads/#java21)
- **Apache Maven 3.9+** - [Download Here](https://maven.apache.org/download.cgi)
- **PostgreSQL 15+** - [Download Here](https://www.postgresql.org/download/)
- **Git** - [Download Here](https://git-scm.com/downloads)
- **IDE** - IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## 🚀 Project Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd eventzone-backend
```

### 2. PostgreSQL Database Setup

#### Create Database

Connect to PostgreSQL and create the database:

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database
CREATE DATABASE eventzone;

-- Verify database creation
\l
```

#### Configure Database User (Optional)

```sql
-- Create dedicated user (if needed)
CREATE USER eventzone_user WITH PASSWORD 'your_secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE eventzone TO eventzone_user;
```

### 3. Application Configuration

Update the `src/main/resources/application.properties` file with your database credentials:

```properties
# Application Name
spring.application.name=eventzone-backend

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/eventzone
spring.datasource.username=postgres
spring.datasource.password=your_password_here

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# SpringDoc OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.api-docs.enabled=true
springdoc.swagger-ui.operationsSorter=method
```

**Security Note:** Never commit sensitive credentials to version control. Use environment variables or application-{profile}.properties for different environments.

### 4. Install Dependencies

```bash
mvn clean install
```

## ▶️ Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

### Using JAR

```bash
# Build the project
mvn clean package

# Run the JAR file
java -jar target/eventzone-backend-0.0.1-SNAPSHOT.jar
```

### Using IDE

- Import the project as a Maven project
- Run the `EventzoneBackendApplication` class

The application will start on **http://localhost:8080**

## 📚 Swagger URL

Access the interactive API documentation:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

Swagger provides a user-friendly interface to explore and test all available endpoints with request/response schemas.

## 🔌 API Modules

### 1. Authentication Module (`/api/auth`)

| Endpoint | Method | Description | Access |
|----------|--------|-------------|--------|
| `/api/auth/register` | POST | User registration | Public |
| `/api/auth/login` | POST | User login | Public |

### 2. Admin Module (`/api/admin`)

| Endpoint | Method | Description | Access |
|----------|--------|-------------|--------|
| `/api/admin/users` | GET | Get all users | Admin |
| `/api/admin/users/{id}/role` | PUT | Update user role | Admin |
| `/api/admin/events/{id}/activate` | PUT | Activate event | Admin |
| `/api/admin/events/{id}/deactivate` | PUT | Deactivate event | Admin |

### 3. Category Module (`/api/categories`)

| Endpoint | Method | Description | Access |
|----------|--------|-------------|--------|
| `/api/categories` | GET | Get all categories | Public |
| `/api/categories/{id}` | GET | Get category by ID | Public |
| `/api/categories` | POST | Create category | Admin/Organiser |
| `/api/categories/{id}` | PUT | Update category | Admin/Organiser |
| `/api/categories/{id}` | DELETE | Delete category | Admin |

### 4. Event Module (`/api/events`)

| Endpoint | Method | Description | Access |
|----------|--------|-------------|--------|
| `/api/events` | GET | Get all events | Public |
| `/api/events/{id}` | GET | Get event by ID | Public |
| `/api/events` | POST | Create event | Organiser |
| `/api/events/{id}` | PUT | Update event | Organiser |
| `/api/events/{id}` | DELETE | Delete event | Organiser |
| `/api/events/organiser/{id}` | GET | Get events by organiser | Organiser |

### 5. Ticket Category Module (`/api/ticket-categories`)

| Endpoint | Method | Description | Access |
|----------|--------|-------------|--------|
| `/api/ticket-categories/event/{eventId}` | GET | Get ticket categories | Public |
| `/api/ticket-categories` | POST | Create ticket category | Organiser |
| `/api/ticket-categories/{id}` | PUT | Update ticket category | Organiser |
| `/api/ticket-categories/{id}` | DELETE | Delete ticket category | Organiser |

### 6. Booking Module (`/api/bookings`)

| Endpoint | Method | Description | Access |
|----------|--------|-------------|--------|
| `/api/bookings` | POST | Create booking | Customer |
| `/api/bookings/user/{userId}` | GET | Get user bookings | Customer |
| `/api/bookings/{id}` | GET | Get booking details | Customer |
| `/api/bookings/{id}/cancel` | PUT | Cancel booking | Customer |
| `/api/bookings/event/{eventId}/summary` | GET | Get booking summary | Organiser |

## 📁 Project Structure

```
eventzone-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── eventzone/
│   │   │           └── eventzone_backend/
│   │   │               ├── EventzoneBackendApplication.java
│   │   │               ├── config/
│   │   │               │   ├── OpenApiConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── constants/
│   │   │               │   └── Roles.java
│   │   │               ├── controller/
│   │   │               │   ├── AdminController.java
│   │   │               │   ├── AuthController.java
│   │   │               │   ├── BookingController.java
│   │   │               │   ├── CategoryController.java
│   │   │               │   ├── EventController.java
│   │   │               │   └── TicketCategoryController.java
│   │   │               ├── dto/
│   │   │               │   ├── BookingRequest.java
│   │   │               │   ├── BookingResponse.java
│   │   │               │   ├── EventRequest.java
│   │   │               │   ├── EventResponse.java
│   │   │               │   └── ... (other DTOs)
│   │   │               ├── entity/
│   │   │               │   ├── Booking.java
│   │   │               │   ├── Event.java
│   │   │               │   ├── EventCategory.java
│   │   │               │   ├── TicketCategory.java
│   │   │               │   └── User.java
│   │   │               ├── exception/
│   │   │               │   └── GlobalExceptionHandler.java
│   │   │               ├── repository/
│   │   │               │   └── ... (JPA Repositories)
│   │   │               ├── security/
│   │   │               │   └── ... (Security components)
│   │   │               └── service/
│   │   │                   └── ... (Business logic services)
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/
│               └── eventzone/
│                   └── eventzone_backend/
│                       └── EventzoneBackendApplicationTests.java
├── target/
├── pom.xml
├── README.md
└── mvnw
```

### Key Packages

- **config/** - Application configuration classes (Security, OpenAPI)
- **constants/** - Application constants and enums
- **controller/** - REST API endpoints
- **dto/** - Data Transfer Objects for API requests/responses
- **entity/** - JPA entities representing database tables
- **exception/** - Global exception handling
- **repository/** - Data access layer using Spring Data JPA
- **security/** - Security filters, JWT utilities, and authentication
- **service/** - Business logic and service layer

## 🔮 Future Enhancements

- **Payment Integration**
  - Integrate payment gateways (Stripe, Razorpay)
  - Support multiple payment methods
  - Automated refund processing

- **Email Notifications**
  - Booking confirmations via email
  - Event reminders and updates
  - Password reset functionality

- **Advanced Search & Filtering**
  - Elasticsearch integration for full-text search
  - Advanced filtering by location, date, price range
  - Personalized event recommendations

- **Analytics Dashboard**
  - Real-time booking analytics
  - Revenue reports and insights
  - Customer behavior analytics

- **Mobile App Support**
  - Dedicated mobile API endpoints
  - Push notification support
  - QR code-based ticket validation

- **Social Features**
  - Event reviews and ratings
  - Social media sharing
  - User wishlist and favorites

- **Multi-tenancy Support**
  - Support for multiple event organizations
  - Custom branding per tenant
  - Isolated data per tenant

## 👤 Author

**Nagarajan Balaguru**

---

## 📄 License

This project is developed as a capstone project.

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

## 💬 Support

For support and queries, please open an issue in the repository.

---

**Built with ❤️ using Spring Boot and Java 21**
