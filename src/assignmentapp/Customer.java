package assignmentapp;

public class Customer extends Account {

    // Add this public no-arguments constructor
    public Customer() {
        super(null, null, null, "customer");
    }

    // Constructor for creating a new Customer
    public Customer(String username, String email, String password) {
        // Pass "customer" as the account type
        super(username, email, password, "customer");
        // Initialize cart if needed
    }

    // Constructor for loading a Customer
    public Customer(String accountID, String username, String email, String password /*, cart data */) {
        // Pass "customer" as the account type
        super(accountID, username, email, password, "customer");
        // Load cart data if needed
    }

    @Override
    public String toString() {
        // Use super.toString() or customize
        return super.toString();
    }
}