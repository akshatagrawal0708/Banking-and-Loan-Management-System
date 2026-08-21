import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankingDAO {

    // 1. Register Customer & Create Default Account
    public boolean registerCustomer(String name, String email, String phone, String password, String accNo) {
        String custSql = "INSERT INTO customers (name, email, phone, password) VALUES (?, ?, ?, ?)";
        String accSql = "INSERT INTO accounts (account_number, customer_id, balance) VALUES (?, ?, 0.0)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction Control

            try (PreparedStatement p1 = conn.prepareStatement(custSql, Statement.RETURN_GENERATED_KEYS)) {
                p1.setString(1, name);
                p1.setString(2, email);
                p1.setString(3, phone);
                p1.setString(4, password);
                p1.executeUpdate();

                ResultSet rs = p1.getGeneratedKeys();
                if (rs.next()) {
                    int custId = rs.getInt(1);
                    try (PreparedStatement p2 = conn.prepareStatement(accSql)) {
                        p2.setString(1, accNo);
                        p2.setInt(2, custId);
                        p2.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Registration Failed: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    // 2. Customer Login
    public Customer authenticate(String email, String password) {
        String sql = "SELECT * FROM customers WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.out.println("Auth Error: " + e.getMessage());
        }
        return null;
    }

    // Fetch Account Number by Customer ID
    public String getAccountNumber(int customerId) {
        String sql = "SELECT account_number FROM accounts WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("account_number");
        } catch (SQLException e) {
            System.out.println("Account Fetch Error: " + e.getMessage());
        }
        return null;
    }

    // 3. Deposit Money
    public boolean deposit(String accNo, double amount) {
        String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'DEPOSIT', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(updateSql);
                 PreparedStatement p2 = conn.prepareStatement(txnSql)) {
                
                p1.setDouble(1, amount);
                p1.setString(2, accNo);
                p1.executeUpdate();

                p2.setString(1, accNo);
                p2.setDouble(2, amount);
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // 4. Withdraw Money
    public boolean withdraw(String accNo, double amount) {
        double currentBal = getBalance(accNo);
        if (currentBal < amount) return false;

        String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String txnSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'WITHDRAWAL', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(updateSql);
                 PreparedStatement p2 = conn.prepareStatement(txnSql)) {
                
                p1.setDouble(1, amount);
                p1.setString(2, accNo);
                p1.executeUpdate();

                p2.setString(1, accNo);
                p2.setDouble(2, amount);
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // Check Balance
    public double getBalance(String accNo) {
        String sql = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            System.out.println("Balance Error: " + e.getMessage());
        }
        return 0.0;
    }

    // 5. Apply New Loan
    public boolean applyLoan(int customerId, double loanAmount, int tenureMonths) {
        double emi = (loanAmount + (loanAmount * 0.10)) / tenureMonths; // Simple 10% Interest Rate
        String sql = "INSERT INTO loans (customer_id, loan_amount, monthly_emi, remaining_amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setDouble(2, loanAmount);
            pstmt.setDouble(3, emi);
            pstmt.setDouble(4, loanAmount + (loanAmount * 0.10));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Loan Application Error: " + e.getMessage());
            return false;
        }
    }

    // Get Active Loan
    public Loan getLoanDetails(int customerId) {
        String sql = "SELECT * FROM loans WHERE customer_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Loan(
                    rs.getInt("loan_id"),
                    rs.getDouble("loan_amount"),
                    rs.getDouble("monthly_emi"),
                    rs.getDouble("remaining_amount"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            System.out.println("Loan Fetch Error: " + e.getMessage());
        }
        return null;
    }

    // 6. Pay EMI
    public boolean payEmi(int customerId, String accNo) {
        Loan loan = getLoanDetails(customerId);
        if (loan == null) return false;

        double emiAmount = loan.getMonthlyEmi();
        double currentBal = getBalance(accNo);

        if (currentBal < emiAmount) return false;

        double newRemaining = loan.getRemainingAmount() - emiAmount;
        String status = newRemaining <= 0 ? "CLOSED" : "ACTIVE";

        String updateAcc = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String updateLoan = "UPDATE loans SET remaining_amount = ?, status = ? WHERE loan_id = ?";
        String txnSql = "INSERT INTO transactions (account_number, type, amount) VALUES (?, 'EMI_PAYMENT', ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement p1 = conn.prepareStatement(updateAcc);
                 PreparedStatement p2 = conn.prepareStatement(updateLoan);
                 PreparedStatement p3 = conn.prepareStatement(txnSql)) {

                p1.setDouble(1, emiAmount);
                p1.setString(2, accNo);
                p1.executeUpdate();

                p2.setDouble(1, Math.max(0, newRemaining));
                p2.setString(2, status);
                p2.setInt(3, loan.getLoanId());
                p2.executeUpdate();

                p3.setString(1, accNo);
                p3.setDouble(2, emiAmount);
                p3.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    // 7. Get Transaction History using Collection Framework (ArrayList)
    public List<Transaction> getTransactionStatement(String accNo) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accNo);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getString("type"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Statement Error: " + e.getMessage());
        }
        return list;
    }
}

