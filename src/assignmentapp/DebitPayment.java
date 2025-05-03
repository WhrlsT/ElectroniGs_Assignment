package assignmentapp;

public class DebitPayment extends Payment {
    private String cardNumber;
    private String expiryDate;
    private String cvv;

    // No-args constructor
    public DebitPayment() {
        super();
        this.cardNumber = "";
        this.expiryDate = "";
        this.cvv = "";
    }

    public DebitPayment(String paymentId, double paymentAmount, String cardNumber, String expiryDate, String cvv) {
        super(paymentId, "Card", paymentAmount);
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    @Override
    public boolean processPayment() {
        // Validate card details
        if (!isValidCardNumber() || !isValidExpiryDate() || !isValidCVV()) {
            setPaymentStatus("failed");
            return false;
        }

        // Simulate payment processing
        try {
            // In a real application, this would integrate with a payment gateway
            Thread.sleep(1000); // Simulate processing time
            setPaymentStatus("paid");
            return true;
        } catch (InterruptedException e) {
            setPaymentStatus("failed");
            return false;
        }
    }

    private boolean isValidCardNumber() {
        // Basic validation: 16 digits
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }

    private boolean isValidExpiryDate() {
        // Basic validation: MM/YY format
        return expiryDate != null && expiryDate.matches("(0[1-9]|1[0-2])/([0-9]{2})");
    }

    private boolean isValidCVV() {
        // Basic validation: 3 digits
        return cvv != null && cvv.matches("\\d{3}");
    }

    // Getters
    public String getCardNumber() { return cardNumber; }
    public String getExpiryDate() { return expiryDate; }
    public String getCvv() { return cvv; }
} 