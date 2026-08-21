public class Loan {
    private int loanId;
    private double loanAmount;
    private double monthlyEmi;
    private double remainingAmount;
    private String status;

    public Loan(int loanId, double loanAmount, double monthlyEmi, double remainingAmount, String status) {
        this.loanId = loanId;
        this.loanAmount = loanAmount;
        this.monthlyEmi = monthlyEmi;
        this.remainingAmount = remainingAmount;
        this.status = status;
    }

    public int getLoanId() { return loanId; }
    public double getLoanAmount() { return loanAmount; }
    public double getMonthlyEmi() { return monthlyEmi; }
    public double getRemainingAmount() { return remainingAmount; }
    public String getStatus() { return status; }
}

