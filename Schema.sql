CREATE DATABASE IF NOT EXISTS banking_db;
USE banking_db;

CREATE TABLE customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE accounts (
    account_number VARCHAR(15) PRIMARY KEY,
    customer_id INT UNIQUE,
    balance DOUBLE NOT NULL DEFAULT 0.0,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT,
    loan_amount DOUBLE NOT NULL,
    monthly_emi DOUBLE NOT NULL,
    remaining_amount DOUBLE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(15),
    type VARCHAR(20) NOT NULL, -- 'DEPOSIT', 'WITHDRAWAL', 'EMI_PAYMENT'
    amount DOUBLE NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES accounts(account_number)
);

