# Kora-Rwanda: Smart Artisan Heritage & E-Commerce

**Kora-Rwanda** is a specialized e-commerce and authentication platform designed to empower Rwandan artisans. It combines traditional craft sales with digital "Heritage Tags" to ensure authenticity and support the local artisan economy.

---

## 🌐 Live Deployment
The platform is deployed on Render and can be accessed here:
**[View Live Demo](https://smart-artisan-heritage-and-e-commerce.onrender.com)**

---

## 📚 Project Documentation
We have prepared a set of comprehensive documents to help you navigate and manage the system:

1.  **[User Manual](USER_MANUAL.md)**: A complete guide on how to register, buy, and manage products.
2.  **[Roles & Functions](ROLES_AND_FUNCTIONS.md)**: A detailed breakdown of the permissions for Admins, Artisans, and Customers.
3.  **[Test Credentials](TEST_CREDENTIALS.md)**: Pre-configured accounts for testing each role immediately.

---

## 🚀 Technical Architecture
- **Backend**: Spring Boot 3.2.3 (Java 17)
- **Database**: MySQL 5.7+ (hosted on FreeSQLDatabase)
- **Authentication**: JWT (JSON Web Tokens) with Spring Security
- **Frontend**: HTML5, Vanilla CSS, and JavaScript
- **Deployment**: Dockerized container running on Render
- **Authenticity**: QR-code based Heritage Tag system for product verification

---

## 🛠️ Local Development Setup
If you wish to run this project locally:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/nabayo123/smart-artisan-heritage-and-e-commerce-aunthentication-system.git
    ```
2.  **Configure Database**:
    Update `kora-rwanda/src/main/resources/application.properties` with your local MySQL credentials.
3.  **Build & Run**:
    ```bash
    cd kora-rwanda
    mvn clean spring-boot:run
    ```
4.  **Access**:
    Navigate to `http://localhost:8080/login.html`

---

## 📄 License
This project is developed for the Smart Artisan Heritage initiative. All rights reserved.
