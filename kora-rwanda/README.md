# Kora Rwanda - Smart Artisan Heritage & E-commerce System

## 🏗️ Project Structure

```
src/main/java/com/korarwandasystem/korarwanda/
├── KoraRwandaApplication.java          # Main Spring Boot application
├── config/                             # Configuration classes
│   ├── SecurityConfig.java            # Security and JWT configuration
│   └── WebConfig.java                 # Web MVC configuration
├── controller/                         # REST Controllers
│   ├── AuthController.java            # Authentication endpoints
│   └── UserController.java            # User management endpoints
├── dto/                               # Data Transfer Objects
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   └── RegisterRequest.java
├── model/                             # JPA Entities
│   ├── User.java
│   └── UserType.java
├── repository/                        # Data Access Layer
│   └── UserRepository.java
├── security/                          # Security Components
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   └── UserPrincipal.java
└── service/                           # Business Logic
    ├── AuthService.java
    └── UserService.java
```

## 🚀 Features

### Authentication & Authorization
- **JWT-based authentication** with refresh token support
- **Role-based access control** (ADMIN, ARTISAN, CUSTOMER)
- **Secure password hashing** using BCrypt
- **User registration and login** endpoints
- **Profile management** for all user types

### User Management
- **Multi-user support**: Artisans, Customers, and Administrators
- **Artisan profiles** with business information and descriptions
- **Customer profiles** for shopping and order management
- **Admin dashboard** for platform management

### Security Features
- **CORS configuration** for cross-origin requests
- **Input validation** using Jakarta Bean Validation
- **SQL injection prevention** through JPA/Hibernate
- **XSS protection** through Spring Security

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: H2 (Development), MySQL (Production)
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **Validation**: Jakarta Bean Validation

## 📋 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - User logout

### User Management
- `GET /api/users/profile` - Get current user profile
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user profile
- `DELETE /api/users/{id}` - Delete user account
- `GET /api/users/artisans` - Get all artisans
- `GET /api/users/customers` - Get all customers (Admin only)

## 🔧 Configuration

### Development Environment
- **Database**: H2 in-memory database
- **Console**: Available at `http://localhost:8080/api/h2-console`
- **Profile**: `application-dev.properties`

### Production Environment
- **Database**: MySQL
- **Profile**: `application-prod.properties`
- **Logging**: File-based logging with INFO level

## 🏃‍♂️ Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Development Setup
1. Clone the repository
2. Navigate to project directory
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the application at `http://localhost:8080/api`

### Production Build
```bash
mvn clean package
java -jar target/kora-rwanda-1.0-SNAPSHOT.jar
```

## 📊 Database Schema

### Users Table
- `id` - Primary key
- `first_name`, `last_name` - User's name
- `email` - Unique email address
- `phone_number` - Contact number
- `password` - Encrypted password
- `user_type` - Enum (ARTISAN, CUSTOMER, ADMIN)
- `business_name` - For artisans
- `description` - Artisan profile description
- `profile_image` - Profile picture URL
- `enabled` - Account status
- `created_at`, `updated_at` - Timestamps

## 🔐 Security Configuration

### JWT Configuration
- **Secret**: Configurable via `jwt.secret` property
- **Expiration**: 24 hours (configurable)
- **Algorithm**: HS256

### Role-Based Access
- **ADMIN**: Full system access
- **ARTISAN**: Can manage products and orders
- **CUSTOMER**: Can browse and purchase products

## 🧪 Testing

### Running Tests
```bash
mvn test
```

### Test Coverage
- Unit tests for service layer
- Integration tests for controllers
- Security tests for authentication

## 📝 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | JWT signing secret | `mySecretKey` |
| `JWT_EXPIRATION` | Token expiration (ms) | `86400000` |
| `DB_URL` | Database URL | H2 in-memory |
| `DB_USERNAME` | Database username | `sa` |
| `DB_PASSWORD` | Database password | `password` |

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/kora-rwanda-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Setup
1. Set production database configuration
2. Configure JWT secret key
3. Set up file storage for product images
4. Configure email service for notifications

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Contact

For support and inquiries:
- Email: support@kora-rwanda.rw
- Website: www.kora-rwanda.rw

---

**Kora Rwanda** - Preserving Heritage, Enabling Commerce 🇷🇼
