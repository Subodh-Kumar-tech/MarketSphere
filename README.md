# 🛒 MarketSphere – Multi-Vendor E-Commerce Marketplace

A full-stack multi-vendor e-commerce marketplace built using **Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, MySQL, and React.js**.

MarketSphere provides a platform where multiple vendors can manage their stores and products, customers can explore products, and administrators can manage users and vendor approvals through a centralized system.

---

## 🚀 Project Overview

MarketSphere is designed to simplify multi-vendor online shopping by bringing multiple sellers onto a single platform.

The application follows a layered backend architecture and uses RESTful APIs to connect the Spring Boot backend with the React frontend.

### ✨ Key Features

**🔐 Authentication & Security**
- User authentication using Spring Security.
- JWT-based authentication.
- Role-based authorization for Admin, Vendor, and Customer.
- Secured REST API endpoints.

**👤 User Management**
- User registration and management.
- User role management.
- Admin access to user information.

**🏪 Vendor Management**
- Vendor registration and store management.
- Vendor approval and rejection by administrators.
- Vendor status management (Pending, Approved, Rejected, Suspended).
- Vendor-specific store and product management.

**📦 Product Management**
- Create, read, update, and delete products.
- Product and category management.
- Vendor-product association.
- Product status management.
- Pagination and sorting.

**⚙️ Backend Architecture**
- RESTful API development using Spring Boot.
- Controller-Service-Repository architecture.
- Database operations using Spring Data JPA and Hibernate.
- Entity relationships and MySQL integration.
- Centralized exception handling.

**💻 Frontend**
- React.js-based user interface.
- API integration with the Spring Boot backend.
- Admin and vendor management interfaces.

---

## 🛠️ Technology Stack

| Category | Technologies |
|---|---|
| Programming Language | Java |
| Backend Framework | Spring Boot |
| Security | Spring Security, JWT |
| ORM | Hibernate |
| Data Access | Spring Data JPA |
| Database | MySQL |
| API Architecture | REST APIs |
| Frontend | React.js |
| Build Tool | Maven |
| Package Manager | npm |
| API Testing | Postman |
| Development Tools | STS, VS Code, Git, GitHub |

---

## 🏗️ Project Architecture

```text
                 ┌─────────────────────────┐
                 │       React.js          │
                 │       Frontend          │
                 └────────────┬────────────┘
                              │
                         REST APIs
                              │
                 ┌────────────▼────────────┐
                 │     Spring Boot         │
                 │     REST Controllers   │
                 └────────────┬────────────┘
                              │
                 ┌────────────▼────────────┐
                 │     Service Layer       │
                 │     Business Logic      │
                 └────────────┬────────────┘
                              │
                 ┌────────────▼────────────┐
                 │    Repository Layer     │
                 │     Spring Data JPA     │
                 └────────────┬────────────┘
                              │
                 ┌────────────▼────────────┐
                 │       MySQL             │
                 │       Database         │
                 └─────────────────────────┘

          Security: Spring Security + JWT
```

---

## 📂 Project Structure

```text
MarketSphere/
│
├── marketSphereBackend/
│   └── marketsphere/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/flipkart/marketsphere/
│       │   │   │       ├── config/
│       │   │   │       ├── controller/
│       │   │   │       ├── service/
│       │   │   │       ├── repository/
│       │   │   │       ├── entity/
│       │   │   │       ├── dto/
│       │   │   │       ├── security/
│       │   │   │       └── exception/
│       │   │   │
│       │   │   └── resources/
│       │   │       └── application.properties
│       │   │
│       │   └── test/
│       │
│       └── pom.xml
│
├── marketSphereProjectFrontend/
│   └── marketsphere-frontend/
│       ├── public/
│       ├── src/
│       ├── package.json
│       └── vite.config.js
│
├── .gitignore
└── README.md
```

*Note: Package and folder names above represent the intended organization; adjust them to match the actual folders in your repository.*

---

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/MarketSphere.git
cd MarketSphere
```

### 2. Backend Setup

**Prerequisites:**
- Java JDK 17 or compatible version.
- Maven.
- MySQL Server.
- STS / IntelliJ IDEA / Eclipse.

**Step 1: Create the Database**

Open MySQL and execute:

```sql
CREATE DATABASE marketsphere2;
```

**Step 2: Configure Database Credentials**

Update your backend `application.properties` with your local database configuration.

Use environment variables for sensitive credentials rather than committing passwords or JWT secrets.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/marketsphere2
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Configure your JWT secret using the environment variable expected by your security configuration.

**Step 3: Run the Backend**

Navigate to the backend project directory:

```bash
cd marketSphereBackend/marketsphere
```

Run using Maven:

```bash
mvn spring-boot:run
```

Alternatively, import the project into STS as an existing Maven project and run the main Spring Boot application class.

Backend runs on the configured application port (currently 1610 in the development setup).

### 3. Frontend Setup

**Prerequisites:**
- Node.js
- npm

Navigate to the frontend directory:

```bash
cd marketSphereProjectFrontend/marketsphere-frontend
```

Install dependencies:

```bash
npm install
```

Configure the frontend environment variables using `.env.example` and set the backend API base URL according to your local setup.

Start the development server:

```bash
npm run dev
```

Open the local URL displayed by Vite in your terminal.

---

## 🔐 Security Implementation

MarketSphere uses Spring Security and JWT to secure backend REST APIs.

The security implementation includes:

- Authentication using JWT.
- Token-based access to protected endpoints.
- Authorization based on user roles.
- Secured access to administrative and vendor operations.
- Integration of authentication with the frontend.

**Note:** Never publish actual JWT secrets, database passwords, or private API keys in the repository.

---

## 🧪 API Testing

Backend REST APIs can be tested using Postman.

The API modules include:

- User Management
- Vendor Management
- Category Management
- Product Management
- Admin Management
- Authentication and Authorization

Example vendor API endpoints:

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/vendors/register/{userId}` | Register a vendor |
| GET | `/api/vendors` | Retrieve vendors |
| GET | `/api/vendors/{id}` | Retrieve vendor details |
| GET | `/api/vendors/admin/pending` | View pending vendors |
| PUT | `/api/vendors/admin/{id}/approve` | Approve a vendor |
| PUT | `/api/vendors/admin/{id}/reject` | Reject a vendor |

Protected endpoints require valid authentication according to the configured security rules.

---

## 🎯 Learning Outcomes

Through this project, I gained practical experience in:

- Building RESTful backend applications using Spring Boot.
- Implementing authentication and authorization using Spring Security and JWT.
- Developing CRUD operations using Spring Data JPA and Hibernate.
- Designing relational database relationships using MySQL.
- Implementing pagination and sorting.
- Integrating React.js with Spring Boot REST APIs.
- Testing APIs using Postman.
- Structuring applications using layered architecture.

---

## 🔮 Future Enhancements

- Shopping cart and checkout workflow.
- Order tracking and management.
- Payment gateway integration.
- Product reviews and ratings.
- Wishlist functionality.
- Advanced product search and filtering.
- Deployment using cloud platforms.

---

## 👨‍💻 Developer

**Subodh Kumar**

Java Full Stack Developer | Spring Boot | React.js | MySQL

- GitHub: https://github.com/Subodh-Kumar-tech
- LinkedIn: https://www.linkedin.com/in/subodh-kumar-3a34402aa

---

⭐ If you find this project interesting, consider giving the repository a star!

**MarketSphere – One Marketplace, Multiple Vendors, Endless Possibilities.**
