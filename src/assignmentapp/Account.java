package assignmentapp;

import java.util.UUID;

public abstract class Account {
    public String accountID;
    public String username;
    public String email; // verified with email
    public String password;
    public String accountType; // e.g., "customer", "admin"

    // Constructor for subclasses
    protected Account(String username, String email, String password, String accountType) {
        this.accountID = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType; // Set the type
    }

    // Constructor for loading from JSON
    protected Account(String accountID, String username, String email, String password, String accountType) {
        this.accountID = accountID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.accountType = accountType; // Set the type
    }

    // Getter for account type (needed for deserialization logic)
    public String getAccountType() {
        return accountType;
    }

    public String getAccountID() {
        return accountID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return accountID; // Return accountID as userID ( Userid == AccountID )
    }

    // Consider making getPassword protected or removing if not needed publicly
    public String getPassword() {
        return password;
    }

    // Setters (remain public, or change to protected if needed)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // toString can remain, or subclasses can override
    @Override
    public String toString() {
        return "Account{" +
               "accountID='" + accountID + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", accountType='" + accountType + '\'' +
               '}';
    }
}