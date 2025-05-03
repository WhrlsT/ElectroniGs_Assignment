package assignmentapp;

public class CartItem {
    private Product product;
    private int quantity;
    private double subtotal;

    // No-args constructor for JSON deserialization
    public CartItem() {
        this.product = null;
        this.quantity = 0;
        this.subtotal = 0.0;
    }

    // Constructor for creating new cart items
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    // Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
    public String getProductID() { return product.getProductID(); }

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