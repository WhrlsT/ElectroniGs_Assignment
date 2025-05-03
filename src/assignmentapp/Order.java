package assignmentapp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private String orderId;
    private String userId;
    private List<OrderItem> orderItems;
    private Payment payment;
    private String shippingStatus;
    private LocalDateTime orderDate;
    private double totalAmount;

    // No-args constructor
    public Order() {
        this.orderId = UUID.randomUUID().toString();
        this.orderItems = new ArrayList<>();
        this.shippingStatus = "pending";
        this.orderDate = LocalDateTime.now();
        this.totalAmount = 0.0;
    }

    public Order(String userId, List<OrderItem> items, Payment payment) {
        this.orderId = UUID.randomUUID().toString();
        this.userId = userId;
        this.orderItems = new ArrayList<>(items);
        this.payment = payment;
        this.shippingStatus = "pending";
        this.orderDate = LocalDateTime.now();
        this.totalAmount = calculateTotal();
    }

    private double calculateTotal() {
        return orderItems.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public Payment getPayment() { return payment; }
    public String getShippingStatus() { return shippingStatus; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }

    // Setters
    public void setShippingStatus(String status) { this.shippingStatus = status; }

    // Method to process the order
    public boolean processOrder() {
        // Process payment
        if (!payment.processPayment()) {
            return false;
        }

        // Update product stock
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            product.setStock(product.getStock() - item.getQuantity());
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(orderId).append("\n");
        sb.append("Date: ").append(orderDate).append("\n");
        sb.append("Items:\n");
        for (OrderItem item : orderItems) {
            sb.append("- ").append(item.toString()).append("\n");
        }
        sb.append("Total: RM").append(String.format("%.2f", totalAmount)).append("\n");
        sb.append("Payment Method: ").append(payment.getPaymentMethod()).append("\n");
        sb.append("Payment Status: ").append(payment.getPaymentStatus()).append("\n");
        sb.append("Shipping Status: ").append(shippingStatus);
        return sb.toString();
    }
} 