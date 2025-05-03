package assignmentapp;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Payment {
    private String paymentId;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String paymentStatus;
    private double paymentAmount;

    // No-args constructor
    public Payment() {
        this.paymentId = UUID.randomUUID().toString();
        this.paymentDate = LocalDateTime.now();
        this.paymentStatus = "pending";
        this.paymentAmount = 0.0;
    }

    public Payment(String paymentId, String paymentMethod, double paymentAmount) {
        this.paymentId = paymentId;
        this.paymentDate = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
        this.paymentStatus = "pending";
        this.paymentAmount = paymentAmount;
    }

    // Getters and setters
    public String getPaymentId() { return paymentId; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public double getPaymentAmount() { return paymentAmount; }

    public void setPaymentStatus(String status) { this.paymentStatus = status; }

    // Abstract method to process payment
    public abstract boolean processPayment();
} 