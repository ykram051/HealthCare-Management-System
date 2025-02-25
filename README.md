# University Health Center App

## Introduction

The **University Health Center App** is designed to streamline healthcare services for students and staff at universities. It provides functionalities such as appointment scheduling, prescription management, and health data tracking, ensuring efficient and secure access to healthcare resources.

---

## Features and Design Principles

### **SOLID Principles in Practice**

1. **Single Responsibility Principle**:  
   Each class has a single responsibility, as evident in our layered architecture:
   - **Controllers** handle HTTP requests and delegate tasks to services.
   - **Services** encapsulate business logic.
   - **Repositories** manage database interactions.

   Example: `AppointmentController` only manages appointment-related HTTP requests, while `AppointmentService` handles the associated business logic.

2. **Open/Closed Principle**:  
   The app is open for extension but closed for modification. For instance:
   - New email strategies can be added by implementing the `EmailStrategy` interface without modifying the existing email logic.

3. **Liskov Substitution Principle**:  
   All services and repository implementations respect this principle. Any `ServiceImpl` can replace its corresponding `Service` interface in the app without breaking functionality.

4. **Interface Segregation Principle**:  
   Interfaces such as `EmailStrategy` and `Repository` are specific and focused, ensuring classes only implement what they use.

5. **Dependency Inversion Principle**:  
   High-level modules depend on abstractions, not concrete implementations. For example:
   - Controllers depend on service interfaces, not implementations.

---

### **Design Patterns Used**

1. **Strategy Pattern**:  
   - Implemented for sending emails through different strategies (`AppointmentConfirmationEmailStrategy`, `MedicineReminderEmailStrategy`).
   - Benefits: Easy to add or modify email strategies without affecting other parts of the system.

2. **Observer Pattern**:  
   - Used in the `observers/appointment` package to notify health staff and students about appointment updates.
   - Benefits: Ensures real-time notifications for relevant parties.

3. **Factory Pattern** (implicit in repository usage):  
   - Spring Data JPA repositories act as factories, providing implementations at runtime for CRUD operations.

---

## Project Architecture

The project follows a **layered architecture**:
- **Controllers**: Handle incoming HTTP requests (`AppointmentController`, `AuthController`).
- **Services**: Implement business logic (`AppointmentService`, `ReminderService`).
- **Repositories**: Interact with the database (`AppointmentRepository`, `UserRepository`).
- **Entities**: Represent data models (`Appointment`, `User`).
- **Security**: Manage authentication and authorization (`TokenInterceptor`, `TokenStore`).
- **Utils**: Provide utility functions (`PrescriptionPDFGenerator`, `PermissionChecker`).

---
## Running the Project

### Backend Configuration

1. **Clone the Repository**:  
   Start by cloning the project repository:
   ```bash
   git clone https://github.com/m-elhamlaoui/se-project-celes.git
   cd se-project-celes/health-center-app-project

# Database Configuration

Ensure you have PostgreSQL installed and configured. Use the following settings:

- **Database Name**: `health_center_db`
- **Username**: `postgres`
- **Password**: `root`
- **Port**: `5432`

To create the database and user in PostgreSQL, run the following commands:

```sql
CREATE DATABASE health_center_db;
CREATE USER postgres WITH PASSWORD 'root';
GRANT ALL PRIVILEGES ON DATABASE health_center_db TO postgres;

# Run the Backend

Run the backend application with the following commands:

```bash
mvn clean install
mvn spring-boot:run
```

# Frontend Configuration

## Navigate and Setup

```bash
# Navigate to Frontend Directory
cd se-project-celes/healthcenter-app-frontend

# Install Dependencies
npm install

# Run the Frontend
npm run serve
```

Frontend URL: [http://localhost:8080](http://localhost:8080)

# Documentation Folder

## Project Resources

### Demo Materials
- **Demo Video**: A walkthrough of the application functionality

### Requirements Documentation

### Technical Diagrams
- **Sequence Diagrams**
- **Class Diagrams**
- **Use Case Diagrams**

### Development Process
- **Agile Report**: Details of the agile methodology followed during development
