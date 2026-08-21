
# 🏦 Banking & Loan Management System

A comprehensive Console-based Banking and Loan Application developed in **Java** using **JDBC** and **MySQL**. This application handles core banking operations along with loan approvals, EMI calculations, and transaction tracking.

---

## 🚀 Features
- **Account Management:** Create new bank accounts and manage customer details.
- **Core Banking:** Deposit, withdraw, and transfer funds with real-time balance updates.
- **Loan & EMI Processing:** Apply for loans, calculate EMIs, and track loan status.
- **Transaction History:** Record every transaction in MySQL database.

---

## 🛠️ Tech Stack & Concepts Used
- **Language:** Java 
- **Database:** MySQL
- **Connectivity:** JDBC (Java Database Connectivity)
- **Concepts:** Object-Oriented Programming (OOPs)

---

## 📁 Project Structure
- `Customer.java` - Customer profile model.
- `Loan.java` - Loan calculation and approval logic.
- `Transaction.java` - Debit and credit transaction logs.
- `DBConnection.java` - Database connection setup.
- `BankingDAO.java` - SQL queries and database CRUD operations.
- `Main.java` - Main execution menu interface.
- `schema.sql` - Database tables structure.

---

## ⚙️ How to Run
1. Clone this repository or download source files.
2. Import `schema.sql` into MySQL Workbench.
3. Update database credentials in `DBConnection.java`.
4. Compile and run `Main.java`.
