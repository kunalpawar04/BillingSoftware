# Billing Software

A full-stack **Billing Software Application** built with **Spring Boot (Java)** on the backend and **React** on the frontend.  
The system allows customers to create accounts, purchase items across categories, and pay using an integrated payment gateway or cash on delivery.  

It also provides secure authentication using JWT and role-based access for **Admin** users.

---

## 📑 Table of Contents

- [Introduction](#-introduction)
- [Features](#-features)
  - [Customer](#customer)
  - [Admin](#admin)
  - [Security](#security)
- [Installation](#️-installation)
  - [Prerequisites](#prerequisites)
  - [Backend Setup (Spring Boot)](#backend-setup-spring-boot)
  - [Frontend Setup (React + Vite)](#frontend-setup-react--vite)
- [Usage](#️-usage)
- [Technologies Used](#-technologies-used)
  - [Backend](#backend)
  - [Frontend](#frontend)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🚀 Introduction

This project is designed to simulate a real-world billing platform where customers can browse categories, add items to their cart, and make payments.  
Admins can manage users, categories, and items, as well as track revenue and order summaries.  

The application implements both **customer functionality** and **admin functionality**, ensuring secure and smooth billing workflows.

---

## ✨ Features

### Customer
- Create an account and get approval from Admin.
- Browse categories and explore items.
- Purchase items with:
  - Online Payment (Stripe payment gateway integration).
  - Cash on Delivery (COD).
- View past orders and order details.
- Access user features only after authentication (JWT-based security).

### Admin
- Approve or reject customer account requests.
- Create, update, and delete:
  - Users
  - Categories
  - Items
- View daily revenue, order count, and past order history.

### Security
- JWT Authentication for secure access.
- Role-based authorization for Admin and User.

---

## ⚙️ Installation

### Prerequisites
- [Java 21](https://www.oracle.com/java/technologies/downloads/)  
- [Maven](https://maven.apache.org/install.html)  
- [MySQL](https://dev.mysql.com/downloads/)  
- [Node.js](https://nodejs.org/) (with npm or yarn)

### Backend Setup (Spring Boot)
```bash
# Navigate to backend folder
cd backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Frontend Setup (React + Vite)
```bash
# Navigate to frontend folder
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

---

## ▶️ Usage

1. Start the **Spring Boot backend** (port usually `8080`).
2. Start the **React frontend** (`npm run dev` → usually `5173`).
3. Access the application in your browser:
   
   ```
   http://localhost:5173
   ```
5. Register as a new customer (requires Admin approval).
6. Admin can log in and approve/reject accounts.
7. Explore categories, place orders, and complete payments.

---

## 🛠 Technologies Used

### Backend
- **Java 21**
- **Spring Boot** (Web, Security, Data JPA)
- **JWT** for authentication
- **Hibernate** (JPA implementation)
- **MySQL** (database)
- **Stripe API** (payment gateway integration)
- **Lombok** (boilerplate reduction)

### Frontend
- **React 19**
- **Vite** (build tool)
- **React Router DOM** (routing)
- **Axios** (API calls)
- **Bootstrap 5 & Bootstrap Icons**
- **React Hot Toast** (notifications)
- **Stripe.js** (payment handling)

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the project, please follow these steps:

1. **Fork** the repository.
2. **Create** a new branch (`git checkout -b feature-branch`).
3. **Commit** your changes (`git commit -m "Add new feature"`).
4. **Push** to your branch (`git push origin feature-branch`).
5. **Submit a Pull Request**.

---

## 📜 License

This project is for educational purposes and does not hold any production license. Feel free to explore and adapt it to your learning needs.
