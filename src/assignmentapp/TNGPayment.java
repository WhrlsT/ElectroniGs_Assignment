package assignmentapp;

public class TNGPayment extends Payment {
    private String phoneNumber;

    // No-args constructor
    public TNGPayment() {
        super();
        this.phoneNumber = "";
    }

    public TNGPayment(String paymentId, double paymentAmount, String phoneNumber) {
        super(paymentId, "TNG", paymentAmount);
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean processPayment() {
        // Validate phone number
        if (!isValidPhoneNumber()) {
            setPaymentStatus("failed");
            return false;
        }

        // Simulate payment processing
        try {
            // In a real application, this would integrate with TNG's payment system
            Thread.sleep(1000); // Simulate processing time
            setPaymentStatus("paid");
            return true;
        } catch (InterruptedException e) {
            setPaymentStatus("failed");
            return false;
        }
    }

    private boolean isValidPhoneNumber() {
        // Basic validation: Malaysian phone number format
        return phoneNumber != null && phoneNumber.matches("^(\\+?6?01)[0-46-9]-*[0-9]{7,8}$");
    }

    // Getter
    public String getPhoneNumber() { return phoneNumber; }
} 