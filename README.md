# 🕯️ Cozy Creations - Backend Service

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg?style=flat-square&logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Firebase](https://img.shields.io/badge/Firebase-Admin%20SDK-ffca28.svg?style=flat-square&logo=firebase)](https://firebase.google.com/)
[![Razorpay](https://img.shields.io/badge/Razorpay-SDK-blue.svg?style=flat-square&logo=razorpay)](https://razorpay.com/)
[![Twilio](https://img.shields.io/badge/Twilio-WhatsApp-red.svg?style=flat-square&logo=twilio)](https://www.twilio.com/)
[![Playwright](https://img.shields.io/badge/Playwright-PDF%20Generation-2e8b57.svg?style=flat-square&logo=playwright)](https://playwright.dev/java/)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-blue.svg?style=flat-square&logo=docker)](https://www.docker.com/)

Welcome to the backend service for **Cozy Creations**, an elegant e-commerce brand specializing in handpoured soy candles, corporate gifting, and luxury curated gift boxes. 

This repository houses a high-performance **Spring Boot REST API** built with Java 21, designed to handle checkout workflows, online/offline payment processing, WhatsApp notifications, automated PDF catalogue rendering, real-time logistics integrations, and dynamic administration controls.

---

## 🌟 Key Features

*   **🔒 Secure Payment Orchestration**: Integrates with the **Razorpay SDK** to process secure online payments. Includes robust cryptographic signature verification (`razorpay_signature`) and automated Cash on Delivery (COD) workflows.
*   **🚚 Automated Logistics**: Leverages **Shiprocket API** to query real-time delivery charges, verify pin code serviceability, generate active shipping orders, and monitor transit updates via tracking webhooks.
*   **📱 Instant Notifications**: Employs **Twilio WhatsApp Business API** to trigger automatic order confirmations, shipping updates, and real-time alerts directly to both customers and system administrators.
*   **📑 High-Fidelity Catalogues (Playwright)**: Features a dynamic PDF catalogue generator. Headless **Microsoft Playwright** compiles custom-styled **Thymeleaf HTML templates** containing dynamic Firestore product details and highly optimized Cloudinary images into clean, downloadable PDFs.
*   **🔥 Serverless Real-time Database**: Uses the **Firebase Admin SDK** for transactional-grade interactions with Google Cloud Firestore, managing user authentication profiles, product listings, orders, and configuration rules.
*   **📊 Integrated Dashboard Metrics**: Exposes comprehensive statistical endpoints for store admins, calculating real-time revenue, product catalogs, status-based order distribution lists, and active registration counts.
*   **⚙️ Dynamic Storefront Settings**: Allows administrators to toggle storefront modes, alter platform fees, set baseline delivery charges, and manage active discount coupons/offers.
*   **☁️ Optimized Media Hosting**: Integrates with **Cloudinary** for image hosting, applying on-the-fly resizing and compression parameters to maintain fast load times and optimized layouts inside PDF catalogues.

---

## 🛠️ Technology Stack

*   **Framework**: Spring Boot 4.0.6 (Spring WebMVC, Thymeleaf, Webflux)
*   **Language**: Java 21 (JDK 21)
*   **Database**: Google Cloud Firestore (Firebase Admin SDK 9.4.1)
*   **Payments**: Razorpay Java SDK (1.4.6)
*   **Communications**: Twilio Java SDK (10.1.2) for WhatsApp, Resend API (via custom WebClient) for transactional emails
*   **Logistics**: Shiprocket REST Integration
*   **PDF Generation**: Microsoft Playwright (1.46.0) & Apache PDFBox (3.0.3)
*   **Media**: Cloudinary HTTP client (1.39.0)
*   **Configuration**: Spring Dotenv (4.0.0) & Spring Configuration Processor

---

## 📁 Repository Structure

```
cozy-backend-springboot/
│
├── .mvn/                     # Maven Wrapper configuration files
├── src/
│   ├── main/
│   │   ├── java/com/cozycreations/backend/
│   │   │   ├── BackendApplication.java     # Application Main Entry Point
│   │   │   │
│   │   │   ├── config/                     # Configuration classes
│   │   │   │   ├── CloudinaryConfig.java   # Cloudinary storage connector
│   │   │   │   ├── FirebaseConfig.java     # Firebase Admin initialization
│   │   │   │   ├── RazorpayConfig.java     # Razorpay API client registry
│   │   │   │   └── TwilioConfig.java       # Twilio SID & auth properties
│   │   │   │
│   │   │   ├── controllers/                # REST Controllers (API Endpoints)
│   │   │   │   ├── AdminController.java    # Store dashboard stats, settings & offers
│   │   │   │   ├── CatalogueController.java# Product catalogue generation endpoints
│   │   │   │   ├── ContactController.java  # Customer feedback & support pipeline
│   │   │   │   ├── EmailController.java    # Promotional & transaction email routes
│   │   │   │   ├── OfferController.java    # Customer discount/offer management
│   │   │   │   ├── OrderController.java    # Checkout, Razorpay & COD operations
│   │   │   │   ├── ProductController.java  # Product CRUD operations
│   │   │   │   ├── SettingsController.java # Store configurations (taxes, delivery)
│   │   │   │   ├── ShippingController.java # Pincode check & charge estimations
│   │   │   │   └── WebhookController.java  # Shiprocket status tracking webhook
│   │   │   │
│   │   │   ├── models/                     # Data transfer objects & models
│   │   │   │   ├── Address.java
│   │   │   │   ├── Order.java
│   │   │   │   └── OrderItem.java
│   │   │   │
│   │   │   └── services/                   # Business Logic Layer
│   │   │       ├── CatalogueService.java   # Headless Playwright PDF compiler
│   │   │       ├── EmailService.java       # Resend API email triggers
│   │   │       ├── PaymentService.java     # Razorpay order creator & Firestore writer
│   │   │       ├── ShippingService.java    # Shiprocket integration connector
│   │   │       └── WhatsappService.java    # Twilio WhatsApp messaging logic
│   │   │
│   │   └── resources/
│   │       ├── templates/                  # Thymeleaf HTML Templates
│   │       │   └── catalogue/
│   │       │       ├── template1.html      # Multi-page product layout template
│   │       │       └── welcome.html        # Cover-page layout for catalog
│   │       ├── application.yml             # Property binding definitions
│   │       └── firebase-admin-cred.json    # Firebase admin key placeholder
│   │
│   └── test/                               # JUnit & Integration Tests
│
├── .env                                # Local environment file
├── Dockerfile                          # Multi-stage optimized Docker deployment
├── mvnw                                # Maven wrapper script (Unix)
├── mvnw.cmd                            # Maven wrapper script (Windows)
├── pom.xml                             # Dependency management file
└── README.md                           # Project documentation
```

---

## ⚙️ Local Setup Guide

Follow these steps to run the application locally on your machine.

### Prerequisites
*   **Java**: JDK 21 installed.
*   **Maven**: 3.9+ (or use the packaged Maven Wrapper `mvnw`).
*   **Firebase Project**: An active Firebase Firestore database.

### 1. Configure Firebase Credentials
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Navigate to **Project Settings > Service Accounts**.
3.  Click **Generate New Private Key** to download the JSON credentials file.
4.  Rename the downloaded file to `firebase-admin-cred.json`.
5.  Place this file inside `src/main/resources/` (Ensure it is ignored by git to protect secrets).

### 2. Configure Environment Variables
Create a file named `.env` in the root of the project (adjacent to `pom.xml`) and populate it with the appropriate service keys:

```ini
# Administrator Emails
ADMIN_EMAIL=admin@cozycreations.in
EMAIL_FROM=info@cozycreations.in

# Razorpay Configuration
RAZORPAY_KEY_ID=rzp_test_...
RAZORPAY_KEY_SECRET=...

# Resend Email Integration
RESEND_API_KEY=re_...

# Cloudinary Integration
CLOUDINARY_CLOUD_NAME=...
CLOUDINARY_API_KEY=...
CLOUDINARY_API_SECRET=...

# Shiprocket Logistics Integration
SHIPROCKET_EMAIL=...
SHIPROCKET_PASSWORD=...
SHIPROCKET_PICKUP_LOCATION=HOME
SHIPROCKET_PICKUP_PINCODE=500055
ENABLE_SHIPPING_FEE=true
SHIPROCKET_WEBHOOK_SECRET=...

# Twilio WhatsApp Configuration
TWILIO_ACCOUNT_SID=AC...
TWILIO_AUTH_TOKEN=...
TWILIO_WHATSAPP_NUMBER=whatsapp:+14155238886
ADMIN_WHATSAPP_NUMBER=+91...
ENABLE_WHATSAPP_NOTIFICATIONS=true

# Firebase Configuration
FIREBASE_ADMIN_CRED_JSON=firebase-admin-cred.json
```

### 3. Run the Application
Start the Spring Boot dev server using Maven:

```bash
# On Unix/macOS
./mvnw spring-boot:run

# On Windows (Command Prompt/PowerShell)
.\mvnw.cmd spring-boot:run
```

Once successfully launched, the backend server will be live and listening on **`http://localhost:4000`**.

---

## 🐳 Docker Deployment

The project utilizes a highly efficient **multi-stage Docker build** process to ensure Playwright has the required Linux OS browser libraries inside the container without bloating the final image size.

*   **Stage 1**: Compiles the source code and packs the deployment `.jar` using a lightweight Eclipse Temurin Maven image.
*   **Stage 2**: Launches the `.jar` using Microsoft's official Playwright runtime container (`mcr.microsoft.com/playwright/java`), which has Chromium, WebKit, and Firefox pre-configured for instant PDF compilation.

### Build and Run with Docker
```bash
# Build the Docker image
docker build -t cozy-creations-backend .

# Run the container (binding port 4000 and passing the .env file)
docker run -p 4000:4000 --env-file .env cozy-creations-backend
```

---

## 📡 API Reference Summary

The service supports various REST endpoints across the following paths:

### 🛍️ Client & Checkout Endpoints

| Endpoint | Method | Authentication | Description |
| :--- | :---: | :---: | :--- |
| `/api/orders/create-payment` | `POST` | Public | Generates a new Razorpay payment order and creates a `pending` state in Firestore |
| `/api/orders/verify-payment` | `POST` | Optional (Bearer) | Validates the payment's HMAC signature, writes the complete order details, and triggers updates |
| `/api/orders/place-cod` | `POST` | Optional (Bearer) | Places a Cash on Delivery order and creates a record in Firestore |
| `/api/shipping/check-serviceability`| `POST`| Public | Queries Shiprocket to evaluate pin code serviceability and shipping costs |
| `/api/products` | `GET` | Public | Fetch list of all active catalog products |
| `/api/offers` | `GET` | Public | Fetch list of active coupons and store discount offers |

### 👑 Admin Control Endpoints

| Endpoint | Method | Description |
| :--- | :---: | :--- |
| `/api/admin/dashboard-stats` | `GET` | Aggregates revenue, active products, user signups, and recent order workflows |
| `/api/admin/generate-catalogue` | `GET` | Downloads a comprehensive PDF catalogue of all active retail products |
| `/api/admin/generate-bulk-catalogue`| `GET` | Downloads a PDF catalog restricted to corporate and bulk gifting inventory |
| `/api/admin/settings/{type}` | `PUT` | Dynamic storefront configuration updates (e.g. `delivery` or `payment` fees) |
| `/api/admin/offers` | `POST` | Adds a new promotional discount or coupon code to the storefront |
| `/api/admin/offers/{id}` | `PUT` | Updates details or active/inactive status of a promotional offer |
| `/api/admin/offers/{id}` | `DELETE`| Completely removes an offer from the database |

---

## ⚡ Integration Details

### 1. High-Fidelity Catalogues (Microsoft Playwright)
Unlike standard PDF-drawing libraries which are complex to maintain, Cozy Creations designs its catalogues using modern HTML & CSS in Thymeleaf templates. 
When the catalog API is hit:
1.  Spring fetches the live products from Google Firestore.
2.  Image URLs are optimized using Cloudinary's dynamic URL transformations (e.g., `w_300,h_300,c_fill`).
3.  Thymeleaf merges this data into `template1.html`.
4.  **Playwright** launches a headless browser, renders the HTML page with absolute styling precision, and prints it to a high-density PDF byte stream.

### 2. Automatic WhatsApp Alerts
Using Twilio's messaging service, the backend handles real-time customer and administrator updates:
-   **On Order Placement**: Sends an elegant receipt confirmation message directly to the customer's phone number.
-   **To Store Admin**: Fires a real-time order notification listing ordered products, customer details, and total revenue to alert the candle crafting team immediately.
-   **On Shipment Update**: When Shiprocket updates order statuses, a webhook triggers automated delivery notices directly to the customer.

---

## 📝 License
This backend service is private proprietary software for Cozy Creations. All rights reserved.
