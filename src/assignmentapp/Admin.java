package assignmentapp;

public class Admin extends Account {

    public String role;

    //No-arguments constructor
    public Admin() {
        super(null, null, null, "admin"); 
        this.role = null; 
    }

    // Constructor for creating a new Admin
    public Admin(String username, String email, String password, String role) {
        // Pass "admin" as the account type
        super(username, email, password, "admin");
        this.role = role;
    }

    // Constructor for loading an Admin
    public Admin(String accountID, String username, String email, String password, String role) {
        // Pass "admin" as the account type
        super(accountID, username, email, password, "admin");
        this.role = role;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
         return "Admin{" +
               "accountID='" + getAccountID() + '\'' +
               ", username='" + getUsername() + '\'' +
               ", email='" + getEmail() + '\'' +
               ", accountType='" + getAccountType() + '\'' + // Include type
               ", role='" + role + '\'' +
               '}';
    }
}