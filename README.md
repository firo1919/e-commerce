# E-Commerce Platform

A comprehensive, full-stack e-commerce platform built with Spring Boot, featuring a robust RESTful API backend with modern security, payment integration, and comprehensive business logic.

## 🚀 Features

### Core Functionality

-   **User Management**: Complete user registration, authentication, and profile management
-   **Product Catalog**: Product management with categories, images, and inventory tracking
-   **Shopping Cart**: Persistent cart functionality with real-time updates
-   **Order Processing**: Complete order lifecycle management with status tracking
-   **Payment Integration**: Secure payment processing via Chapa payment gateway
-   **Review System**: Product reviews and ratings
-   **Address Management**: Multiple shipping addresses per user

### Security & Authentication

-   **JWT Authentication**: Secure token-based authentication with refresh tokens
-   **Role-Based Access Control**: Admin and user role management
-   **Email Verification**: Account activation via email confirmation
-   **Password Security**: BCrypt password hashing
-   **CORS Configuration**: Cross-origin resource sharing setup

### Technical Features

-   **API Versioning**: Structured API versioning with v1 package organization
-   **RESTful API**: Well-structured REST endpoints with proper HTTP methods
-   **Data Validation**: Comprehensive input validation using Bean Validation
-   **Exception Handling**: Global exception handling with custom exceptions
-   **File Upload**: Image upload and management for products
-   **Email Service**: Automated email notifications
-   **Database Support**: Multi-database support (MariaDB, PostgreSQL, H2)
-   **API Documentation**: Versioned Swagger/OpenAPI documentation
-   **Docker Support**: Containerized deployment with Docker Compose
-   **CI/CD Pipeline**: Automated deployment with GitHub Actions

## 🛠️ Technology Stack

### Backend

-   **Java 21**: Latest LTS version with modern language features
-   **Spring Boot 3.5.6**: Enterprise-grade application framework
-   **Spring Security**: Authentication and authorization
-   **Spring Data JPA**: Data persistence layer
-   **Spring Mail**: Email service integration
-   **Maven**: Dependency management and build tool
-   **Lombok**: Boilerplate code reduction

### Database

-   **MariaDB**: Primary production database
-   **PostgreSQL**: Alternative production database
-   **H2**: In-memory database for testing

### Security & Authentication

-   **JWT (JSON Web Tokens)**: Stateless authentication
-   **BCrypt**: Password hashing
-   **Spring Security**: Security framework

### Payment & External Services

-   **Chapa Payment Gateway**: Payment processing integration
-   **SMTP Email Service**: Email notifications

### Development & Deployment

-   **Docker**: Containerization
-   **Docker Compose**: Multi-container orchestration
-   **Maven Wrapper**: Consistent build environment
-   **Spring Boot DevTools**: Development productivity

## 📁 Project Structure

```
e-commerce/
├── .github/
│   └── workflows/           # GitHub Actions CI/CD
│       ├── deploy-backend-dev.yml
│       └── deploy-backend-prod.yml
├── Backend/
│   ├── src/main/java/com/firomsa/ecommerce/
│   │   ├── config/           # Configuration classes
│   │   │   └── OpenApiConfig.java  # API versioning & Swagger config
│   │   ├── exception/        # Custom exception handling
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Data access layer
│   │   ├── security/         # Security configuration
│   │   └── v1/               # API Version 1
│   │       ├── controller/   # REST API controllers
│   │       ├── dto/          # Data Transfer Objects
│   │       ├── mapper/       # Entity-DTO mappers
│   │       └── service/      # Business logic layer
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql         # Initial data
│   ├── docker/              # Docker configuration
│   ├── pom.xml              # Maven dependencies
│   └── Dockerfile           # Container image
└── README.md
```

## 🚀 Quick Start

### Prerequisites

-   Java 21 or higher
-   Maven 3.6+
-   Docker and Docker Compose (optional)
-   MariaDB or PostgreSQL database

### Environment Setup

1. **Clone the repository**

    ```bash
    git clone https://github.com/firo1919/e-commerce.git
    cd e-commerce
    ```

2. **Configure environment variables**

    Copy the example environment file and customize it:

    ```bash
    cd Backend
    cp example.env .env
    ```

    Then edit the `.env` file with your actual values:

    ```env
    # Database Configuration (for Docker Compose)
    SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/ecommerce
    SPRING_DATASOURCE_USERNAME=admin
    SPRING_DATASOURCE_PASSWORD=root

    # API Documentation
    SPRINGDOC_APIDOCS_ENABLED=true
    SPRING_SQL_INIT_MODE=always
    SPRING_JPA_DEFERDATASOURCEINITIALIZATION=true
    SPRING_SQL_INIT_CONTINUEONERROR=true

    # Email Configuration
    SPRING_MAIL_USERNAME=your-email@gmail.com
    SPRING_MAIL_PASSWORD=your-app-password
    COMPANY_MAIL=your-email@gmail.com

    # Authentication
    AUTH_SECRET=your-jwt-secret-key

    # Admin Configuration
    ADMIN_USERNAME=admin
    ADMIN_PASSWORD=admin123
    ADMIN_EMAIL=admin@example.com
    ADMIN_FIRSTNAME=Admin
    ADMIN_LASTNAME=User

    # Payment Configuration
    CHAPA_SECRET_KEY=your-chapa-secret-key
    CHAPA_ENCRIPTION_KEY=your-chapa-encryption-key
    ```

    Also create the `.env.db` file with the following content
   ```
   MARIADB_DATABASE=ecommerce
   MARIADB_PASSWORD=your-password
   MARIADB_ROOT_PASSWORD=your-root-password
   MARIADB_USER=admin
   ```

### Running the Application

#### Using Docker Compose (Recommended for Local Development)

```bash
cd Backend
docker compose -f docker/compose.yaml up -d
```

This will start both the application and MariaDB database automatically.

The application will be available at `http://localhost:8080`

## 📚 API Documentation

The application features **API versioning** with comprehensive Swagger/OpenAPI documentation.

### API Access Points

-   **Swagger UI**: `http://localhost:8080/docs`
-   **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### API Versioning Structure

-   **Version 1 (v1)**: Current stable API under `/api/v1/` prefix
-   **Future versions**: Ready for v2, v3, etc. without breaking changes

## 🧪 Testing

Run the test suite:

```bash
cd Backend
./mvnw test
```

The project includes comprehensive tests for:

-   Controllers (API endpoints)
-   Services (Business logic)
-   Repositories (Data access)
-   Mappers (Entity-DTO conversion)

## 🚀 CI/CD Pipeline

The project includes automated CI/CD pipelines using GitHub Actions for both development and production environments.

### GitHub Actions Workflows

#### Development Deployment (`deploy-backend-dev.yml`)

-   **Trigger**: Push/PR to `dev` branch
-   **Process**:
    1. Builds Docker image with `dev` tag
    2. Pushes to Docker Hub (`firo1919/ecommerce-backend:${API_VERSION}-dev`)
    3. Deploys to development environment on Render

#### Production Deployment (`deploy-backend-prod.yml`)

-   **Trigger**: Push/PR to `main` branch
-   **Process**:
    1. Builds Docker image with `prod` tag
    2. Pushes to Docker Hub (`firo1919/ecommerce-backend:${API_VERSION}-prod`)
    3. Deploys to production environment on Render

### Required Secrets

Configure these secrets in your GitHub repository:

-   `DOCKERHUB_USERNAME` - Docker Hub username
-   `DOCKERHUB_TOKEN` - Docker Hub access token
-   `RENDER_API_KEY` - Render.com API key
-   `RENDER_SERVICE_ID_DEV` - Development service ID
-   `RENDER_SERVICE_ID_PROD` - Production service ID

### Required Variables

-   `API_VERSION` - Version tag for Docker images

## 🔧 Configuration

### Database Configuration

The application supports multiple databases:

-   **MariaDB** (default production)
-   **PostgreSQL** (alternative production)
-   **H2** (testing)

### Security Configuration

-   JWT token expiration: Configurable
-   Password strength requirements
-   CORS origins configuration
-   Rate limiting (configurable)

### Email Configuration

-   SMTP server configuration
-   Email templates for notifications
-   Confirmation token management

## 📊 Database Schema

### Core Entities

-   **Users**: User accounts with role-based access
-   **Products**: Product catalog with categories and images
-   **Orders**: Order management with status tracking
-   **Cart**: Shopping cart persistence
-   **Reviews**: Product reviews and ratings
-   **Addresses**: User shipping addresses
-   **Categories**: Product categorization
-   **Images**: Product image management

### Relationships

-   Users have multiple addresses, orders, and reviews
-   Products belong to multiple categories
-   Orders contain multiple order items
-   Products have multiple images and reviews

## 🚀 Deployment

### Production Deployment

1. Build the Docker image:

    ```bash
    docker build -t ecommerce-backend .
    ```

2. Deploy with Docker Compose:

    ```bash
    docker-compose -f docker/compose.yaml up -d
    ```

3. Configure reverse proxy (Nginx) for SSL termination

4. Set up monitoring and logging

### Environment Variables

Ensure all required environment variables are set:

-   Database credentials
-   Email service credentials
-   Payment gateway keys
-   JWT signing keys
-   Admin account details

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
