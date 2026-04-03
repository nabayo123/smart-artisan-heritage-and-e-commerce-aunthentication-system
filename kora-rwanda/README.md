# 🇷🇼 Kora-Rwanda — Smart Artisan Heritage & E-Commerce Authentication System

**Created by NABAYO Clementine**

A Spring Boot backend that connects Rwandan artisans with verified buyers while protecting authentic handmade heritage through cryptographic **Heritage Tags** (QR-based Certificates of Authenticity).

---

## 🚀 Quick Start — Running in IntelliJ IDEA

### Prerequisites
| Tool | Version |
|------|---------|
| Java JDK | 17 or higher |
| Maven | 3.8+ (or use IntelliJ built-in) |
| MySQL | 8.0+ |
| IntelliJ IDEA | Any edition (Community works fine) |

---

### Step 1 — Set up MySQL Database

Open MySQL Workbench or any MySQL client and run:

```sql
CREATE DATABASE kora_rwanda_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The schema tables are created **automatically** by Spring Boot on first run (JPA `ddl-auto=update`).

---

### Step 2 — Configure Database Credentials

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/kora_rwanda_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
```

---

### Step 3 — Open in IntelliJ

1. Open IntelliJ IDEA
2. Click **File → Open** and select the `kora-rwanda` folder
3. IntelliJ will detect the `pom.xml` — click **Load Maven Project** when prompted
4. Wait for all dependencies to download (first time may take 2-3 minutes)

---

### Step 4 — Run the Application

1. Navigate to `src/main/java/com/korarwanda/kora/KoraApplication.java`
2. Click the **green ▶ Run button** next to the `main` method
3. Or use the top toolbar: **Run → Run 'KoraApplication'**

**On first startup you will see in the console:**
```
============================================================
  DEFAULT ADMIN ACCOUNT CREATED
  Email   : admin@kora-rwanda.rw
  Password: Admin@2024
  IMPORTANT: Change this password after first login!
============================================================
```

---

### Step 5 — Open Swagger UI

Once running, open your browser:

```
http://localhost:8080/swagger-ui.html
```

You will see all API endpoints grouped by feature.

---

## 🔐 Authentication Flow

### 1. Register as Artisan
```
POST /api/auth/register/artisan
```
```json
{
  "fullName": "Uwimana Marie",
  "email": "marie@example.com",
  "password": "password123",
  "phoneNumber": "0788000001",
  "districtVillage": "Nyanza, Southern Province",
  "bio": "Master weaver specializing in Agaseke baskets.",
  "momoNumber": "0788000001"
}
```

### 2. Register as Customer
```
POST /api/auth/register/customer
```

### 3. Login (any role)
```
POST /api/auth/login
```
```json
{
  "email": "admin@kora-rwanda.rw",
  "password": "Admin@2024",
  "role": "ADMIN"
}
```
→ Returns a **JWT token**. Copy it.

### 4. Authorize in Swagger
Click the 🔒 **Authorize** button in Swagger UI and enter:
```
Bearer <your-jwt-token>
```

---

## 📦 Core API Endpoints

### 🛒 Products (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/public` | Browse all available products |
| GET | `/api/products/public/{id}` | View product details |
| GET | `/api/products/public/search?keyword=agaseke` | Search products |

### 🎨 Artisan (requires ARTISAN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/artisan/products?artisanId=1` | Create product + auto-generate Heritage Tag |
| GET | `/api/artisan/products/{artisanId}` | Get my products |

### ✅ Heritage Verification (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/certificates/verify/{heritageHash}` | Scan QR code to verify authenticity |
| GET | `/api/certificates/product/{productId}` | Get certificate details |

### 🛍️ Orders (requires CUSTOMER role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders/customer/{customerId}` | Place an order |
| GET | `/api/orders/customer/{customerId}` | View my orders |

### 💳 Payments (requires CUSTOMER role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/initiate` | Initiate MTN MoMo / Airtel payment |
| POST | `/api/payments/{paymentId}/confirm` | Simulate payment confirmation |

### 🔧 Admin Dashboard (requires ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/dashboard/stats` | Platform statistics |
| GET | `/api/admin/artisans` | All artisans |
| GET | `/api/admin/artisans/pending` | Pending verifications |
| PATCH | `/api/admin/artisans/{id}/verify?status=APPROVED` | Approve/reject artisan |
| GET | `/api/admin/products` | All products |
| GET | `/api/orders` | All orders |
| GET | `/api/payments` | All payments |

---

## 🏷️ How the Heritage Tag Works

When an artisan creates a product, the system **automatically**:

1. Generates a unique `SHA-256` Heritage Hash (e.g., `KR-abc123xyz...`)
2. Encodes it as a QR code image (Base64 PNG)
3. Creates a `Certificate of Authenticity` linked to the product

When a buyer scans the QR code:
- The embedded URL hits `/api/certificates/verify/{heritageHash}`
- The system returns the artisan biography, product origin, and authenticity status
- `✅ AUTHENTIC` or `⚠️ WARNING: Possible counterfeit`

---

## 🗃️ Database Schema

Tables created automatically:
- `cooperative` — Registered artisan groups
- `artisan` — Individual craft producers
- `customer` — Buyers
- `product` — Listed craft items
- `certificate` — Heritage Tags (one per product)
- `orders` — Purchase transactions
- `order_items` — Products within an order
- `payment` — MoMo/Airtel payment records
- `admin` — Platform administrators

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|------------|
| Backend Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt) |
| QR Code | ZXing (Google) |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build Tool | Maven |

---

## 📁 Project Structure

```
kora-rwanda/
├── src/main/java/com/korarwanda/kora/
│   ├── KoraApplication.java          ← Entry point
│   ├── config/
│   │   ├── SecurityConfig.java       ← JWT + CORS + role rules
│   │   ├── OpenApiConfig.java        ← Swagger setup
│   │   └── DataSeeder.java           ← Default admin creation
│   ├── controller/                   ← REST API endpoints
│   ├── dto/                          ← Request/Response objects
│   ├── entity/                       ← Database tables (JPA)
│   ├── enums/                        ← Status enumerations
│   ├── exception/                    ← Error handling
│   ├── repository/                   ← Database queries
│   ├── security/                     ← JWT filter, UserDetails
│   ├── service/                      ← Business logic interfaces
│   │   └── impl/                     ← Business logic implementations
│   └── util/
│       └── HeritageTagUtil.java      ← QR + Hash generation
└── src/main/resources/
    └── application.properties
```

---

## ⚠️ Troubleshooting

**"Access denied" on all endpoints?**
→ Make sure you copy the full JWT token and use `Bearer <token>` format in the Authorization header.

**MySQL connection error?**
→ Check your MySQL service is running and credentials in `application.properties` are correct.

**Port 8080 already in use?**
→ Change `server.port=8080` to `server.port=8081` in `application.properties`.

**Lombok not working?**
→ In IntelliJ: `File → Settings → Build Tools → Annotation Processors → Enable annotation processing`.

---

*Built with ❤️ for Rwandan artisans — Kora-Rwanda, 2026*
