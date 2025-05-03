package assignmentapp;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private String userID; // ID of the user who owns this cart
    private List<CartItem> items;
    private double total;

    // No-args constructor for JSON deserialization
    public Cart() {
        this.userID = null;
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    // Constructor for creating new cart
    public Cart(String userID) {
        this.userID = userID;
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    // Setters for Gson
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        updateTotal();
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Add item to cart
    public void addItem(Product product, int quantity) {
        // Check if product already exists in cart
        for (CartItem item : items) {
            if (item.getProduct().getProductID().equals(product.getProductID())) {
                item.updateQuantity(item.getQuantity() + quantity);
                updateTotal();
                return;
            }
        }
        // If product not in cart, add new item
        items.add(new CartItem(product, quantity));
        updateTotal();
    }

    // Remove item from cart
    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getProductID().equals(productId));
        updateTotal();
    }

    // Update item quantity
    public void updateItemQuantity(String productId, int newQuantity) {
        for (CartItem item : items) {
            if (item.getProduct().getProductID().equals(productId)) {
                item.updateQuantity(newQuantity);
                break;
            }
        }
        updateTotal();
    }

    // Clear cart
    public void clearCart() {
        items.clear();
        total = 0.0;
    }

    // Update total
    private void updateTotal() {
        total = items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    // Check if cart is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cart Contents:\n");
        for (CartItem item : items) {
            sb.append("- ").append(item.toString()).append("\n");
        }
        sb.append("Total: RM").append(String.format("%.2f", total));
        return sb.toString();
    }
} 