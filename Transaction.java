import java.sql.Timestamp;

public class Transaction {
    private int transactionId;
    private String type;
    private double amount;
    private Timestamp timestamp;

    public Transaction(int transactionId, String type, double amount, Timestamp timestamp) {
        this.transactionId = transactionId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Type: %-12s | Amount: ₹%.2f", 
                timestamp.toString(), transactionId, type, amount);
    }
}

