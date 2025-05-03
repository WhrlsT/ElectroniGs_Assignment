package assignmentapp;

public class OrderItem {
    private Product product;
    private int quantity;
    private double subtotal;

    // No-args constructor
    public OrderItem() {
        this.quantity = 0;
        this.subtotal = 0.0;
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }

    // Method to update quantity and recalculate subtotal
    public void updateQuantity(int newQuantity) {
        this.quantity = newQuantity;
        this.subtotal = product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s x%d - RM%.2f", 
            product.getName(), quantity, subtotal);
    }
} 