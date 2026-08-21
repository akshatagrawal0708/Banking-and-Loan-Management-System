import java.util.List;
import java.util.Scanner;

public class Main {
    private static BankingDAO dao = new BankingDAO();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n========== BANKING & LOAN MANAGEMENT SYSTEM ==========");
            System.out.println("1. Register New Account");
            System.out.println("2. Customer Login");
            System.out.println("3. Exit");
            System.out.print("Select Option (1-3): ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    handleRegister();
                    break;
                case 2:
                    handleLogin();
                    break;
                case 3:
                    System.out.println("Thank you for using Bank System!");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    private static void handleRegister() {
        System.out.print("Enter Full Name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Phone: ");
        String phone = sc.next();
        System.out.print("Set Password: ");
        String pass = sc.next();
        System.out.print("Assign 10-Digit Account Number: ");
        String accNo = sc.next();

        if (dao.registerCustomer(name, email, phone, pass, accNo)) {
            System.out.println("Registration Successful! Account Created.");
        } else {
            System.out.println("Registration Failed!");
        }
    }

    private static void handleLogin() {
        System.out.print("Enter Email: ");
        String email = sc.next();
        System.out.print("Enter Password: ");
        String pass = sc.next();

        Customer customer = dao.authenticate(email, pass);
        if (customer != null) {
            System.out.println("\nWelcome, " + customer.getName() + "!");
            String accNo = dao.getAccountNumber(customer.getCustomerId());
            customerMenu(customer, accNo);
        } else {
            System.out.println("Invalid Credentials!");
        }
    }

    private static void customerMenu(Customer cust, String accNo) {
        while (true) {
            System.out.println("\n----- CUSTOMER DASHBOARD (Acc No: " + accNo + ") -----");
            System.out.println("1. Check Account Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Apply for Loan");
            System.out.println("5. Check Loan & Due EMI Status");
            System.out.println("6. Pay EMI");
            System.out.println("7. Download Transaction Statement");
            System.out.println("8. Logout");
            System.out.print("Select Choice (1-8): ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Current Balance: ₹" + dao.getBalance(accNo));
                    break;

                case 2:
                    System.out.print("Enter Amount to Deposit: ₹");
                    double dep = sc.nextDouble();
                    if (dao.deposit(accNo, dep)) {
                        System.out.println("Deposited Successfully!");
                        System.out.println("New Balance: ₹" + dao.getBalance(accNo));
                    }
                    break;

                case 3:
                    System.out.print("Enter Amount to Withdraw: ₹");
                    double wth = sc.nextDouble();
                    if (dao.withdraw(accNo, wth)) {
                        System.out.println("Withdrawal Successful!");
                        System.out.println("Remaining Balance: ₹" + dao.getBalance(accNo));
                    } else {
                        System.out.println("Transaction Failed (Insufficient Funds)!");
                    }
                    break;

                case 4:
                    System.out.print("Enter Required Loan Amount: ₹");
                    double lAmt = sc.nextDouble();
                    System.out.print("Enter Tenure (Months): ");
                    int tenure = sc.nextInt();
                    if (dao.applyLoan(cust.getCustomerId(), lAmt, tenure)) {
                        System.out.println("Loan Approved & Disbursed!");
                    }
                    break;

                case 5:
                    Loan loan = dao.getLoanDetails(cust.getCustomerId());
                    if (loan != null) {
                        System.out.println("\n--- ACTIVE LOAN DETAILS ---");
                        System.out.println("Total Sanctioned Loan : ₹" + loan.getLoanAmount());
                        System.out.println("Monthly EMI Amount     : ₹" + loan.getMonthlyEmi());
                        System.out.println("Total Due Balance      : ₹" + loan.getRemainingAmount());
                        System.out.println("Status                 : " + loan.getStatus());
                    } else {
                        System.out.println("No Active Loan Found.");
                    }
                    break;

                case 6:
                    if (dao.payEmi(cust.getCustomerId(), accNo)) {
                        System.out.println("EMI Payment Successful!");
                        System.out.println("Updated Account Balance: ₹" + dao.getBalance(accNo));
                    } else {
                        System.out.println("EMI Payment Failed (Check Balance or Loan Status)!");
                    }
                    break;

                case 7:
                    List<Transaction> statement = dao.getTransactionStatement(accNo);
                    System.out.println("\n========== TRANSACTION STATEMENT ==========");
                    if (statement.isEmpty()) {
                        System.out.println("No transactions found.");
                    } else {
                        for (Transaction t : statement) {
                            System.out.println(t);
                        }
                    }
                    break;

                case 8:
                    System.out.println("Logged out successfully.");
                    return;

                default:
                    System.out.println("Invalid Option!");
            }
        }
    }
}

