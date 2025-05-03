package assignmentapp;

import java.util.List; 
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.Console;


public class MenuController {
    // Current user
    public static Account currentUser = null;
    // Scanner for user input
    private static final Scanner userInput = new Scanner(System.in);
    // List to store accounts
    private static List<Account> accounts = JsonHandler.loadAccounts(); // Load accounts once
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    // Password validation pattern: 8+ chars, >=1 letter, >=1 digit, >=1 special char
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");

    public static void start() {
        boolean running = true;
        while (running) {
            int choice = MainMenu();
            switch (choice) {
                case 1 -> LoginMenu();
                case 2 -> SignUpMenu();
                case 3 -> {
                    ClearScreen();
                    System.out.println("------------------------------------------------------------");
                    System.out.println("                        ABOUT US");
                    System.out.println("------------------------------------------------------------");
                    System.out.println("""
\nAbout Us
Welcome to ElectroniGs — your trusted destination for top-quality PC components!
At ElectroniGs, we specialize in bringing you the best in motherboards, processors (CPUs), and graphics cards (GPUs) from the world's leading brands. Whether you're building a high-performance gaming rig, upgrading your workstation, or assembling a powerful custom PC, we're here to provide the essential parts that power your tech dreams.

Our mission is simple:

Offer only genuine products at competitive prices

Provide expert advice and customer-first service

Deliver a smooth and secure shopping experience from browsing to checkout

With a passion for PC hardware and a commitment to quality, ElectroniGs has quickly grown into a go-to source for enthusiasts, gamers, and professionals alike. We stay up-to-date with the latest releases, ensuring you get cutting-edge technology and unbeatable deals.

Power up your build with ElectroniGs — where performance meets reliability.""");
                    System.out.println("\nPress Enter to return to the main menu...");
                    userInput.nextLine(); 
                    userInput.nextLine(); // Wait for user to press Enter
                }
                case 4 -> {
                    ClearScreen();
                    System.out.println("Exiting application. Goodbye!");
                    userInput.close(); // Close the scanner when exiting
                    running = false; // Set running to false to exit the loop
                }
                default -> {
                    System.out.println("Invalid choice. Please try again.");
                    Delay();
                }
            }
        }
        System.exit(0); // Exit the program after the loop ends
    }

    private static void Logo(){
        System.out.println("""
 ______ _           _             _   _ _              
|  ____| |         | |           | \\ | (_)             
| |__  | | ___  ___| |_ _ __ ___ |  \\| |_  __ _ ___   
|  __| | |/ _ \\/ __| __| '__/ _ \\| . ` | |/ _` / __|  
| |____| |  __/ (__| |_| | | (_) | |\\  | | (_| \\__ \\  
|______|_|\\___|\\___|\\__|_|  \\___/|_| \\_|_|\\__, |___/  
                                            __/ |       
                                            |___/        """);
    }

    static void ClearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
        Logo();
    }

    public static void Delay() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    public static int MainMenu() {
        while (true) {
            ClearScreen();
            System.out.println("""
------------------------------------------------------------
                        MAIN MENU
------------------------------------------------------------

                (1) Log In
                (2) Sign Up
                (3) About Us
                (4) Exit                                 \n""");
            
            System.out.print("Please select an option: ");
            try {
                int choice = userInput.nextInt();
                if (choice >= 1 && choice <= 4) {
                    return choice;
                } else {
                    System.out.println("Please enter a number between 1 and 4.");
                    Thread.sleep(1500); // Pause to show error message
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine(); // Clear the invalid input
                Delay();
            }
        }
    }

    private static void LoginMenu() {
        userInput.nextLine();
        while (true) {
            ClearScreen();
            System.out.println("""
------------------------------------------------------------
                        LOG IN
------------------------------------------------------------
Please enter your account details (Enter 0 to return to main menu) """);

            System.out.print("Username or Email: ");
            String identifier = userInput.nextLine();
            if (identifier.equals("0")) {
                return;
            }

            System.out.print("Password: ");
            String password = userInput.nextLine();
            if (password.equals("0")) { 
                return;
            }

            Account loggedInAccount = null;
            for (Account acc : accounts) {
                if ((acc.getUsername().equalsIgnoreCase(identifier) || acc.getEmail().equalsIgnoreCase(identifier))
                        && acc.getPassword().equals(password)) {
                    loggedInAccount = acc;
                    break;
                }
            }

            if (loggedInAccount != null) {
                currentUser = loggedInAccount;
                ClearScreen();
                System.out.println("Login successful! Welcome, " + currentUser.getUsername() + "!");
                System.out.println("Account Type: " + currentUser.getAccountType());
                if (currentUser instanceof Customer) { CustomerMenu.UserMenu(); }
                if (currentUser instanceof Admin) { AdminMenu.AdminMenu(); }
                return; 
            } else {
                currentUser = null;
                System.out.println("Invalid username/email or password. Please try again.");
                Delay();
            }
        }
    }

    private static void SignUpMenu() {
        while (true) {
            ClearScreen();
            System.out.println("""
------------------------------------------------------------
                        SIGN UP
------------------------------------------------------------
Please enter your details (Enter 0 at any point to return) """);

            if (userInput.hasNextLine()) {
                userInput.nextLine();
            }

            // --- Username Input & Validation ---
            String username;
            while (true) {
                System.out.print("Username: ");
                username = userInput.nextLine();
                if (username.equals("0")) return;
                if (username.trim().isEmpty()) {
                    System.out.println("Username cannot be empty.");
                } else if (usernameExists(username)) {
                    System.out.println("Username already taken. Please choose another.");
                } else {
                    break;
                }
            }

            // --- Email Input & Validation ---
            String email;
            while (true) {
                System.out.print("Email: ");
                email = userInput.nextLine();
                if (email.equals("0")) return;
                if (!isValidEmail(email)) {
                    System.out.println("Invalid email format. Please enter a valid email (e.g., user@example.com).");
                } else if (emailExists(email)) {
                    System.out.println("Email already registered. Please use a different email or log in.");
                } else {
                    break;
                }
            }

            // Generate and send OTP
            String otp = EmailHandler.generateOTP();
            System.out.println("\nSending OTP to your email...");
            if (!EmailHandler.sendOTPEmail(email, otp)) {
                System.out.println("Failed to send OTP. Please try again later.");
                Delay();
                return;
            }

            // Verify OTP
            String inputOTP;
            while (true) {
                System.out.print("\nEnter the 6-digit OTP sent to your email: ");
                inputOTP = userInput.nextLine();
                if (inputOTP.equals("0")) return;
                if (inputOTP.length() != 6 || !inputOTP.matches("\\d{6}")) {
                    System.out.println("Invalid OTP format. Please enter a 6-digit number.");
                } else if (!EmailHandler.verifyOTP(inputOTP, otp)) {
                    System.out.println("Invalid OTP. Please try again.");
                } else {
                    break;
                }
            }

            // --- Password Input & Validation ---
            String password;
            while (true) {
                System.out.print("Password (min 8 chars, incl. letter, number, special char [@$!%*?&]): ");
                password = userInput.nextLine();
                if (password.equals("0")) return;
                if (!isValidPassword(password)) {
                    System.out.println("Password does not meet complexity requirements.");
                    System.out.println("Requirements: Minimum 8 characters, at least one letter, one number, and one special character (@$!%*?&).");
                } else {
                    break;
                }
            }

            // --- Confirm Password ---
            String confirmPassword;
            while (true) {
                System.out.print("Confirm Password: ");
                confirmPassword = userInput.nextLine();
                if (confirmPassword.equals("0")) return;
                if (!password.equals(confirmPassword)) {
                    System.out.println("Passwords do not match. Please try again.");
                } else {
                    break;
                }
            }

            // --- Account Creation & Saving ---
            try {
                Customer newCustomer = new Customer(username, email, password);
                accounts.add(newCustomer);
                JsonHandler.saveAccounts(accounts);
                System.out.println("\nSign up successful! Account created for " + username + ".");
                System.out.println("You can now log in.");
                Delay();
                return;
            } catch (Exception e) {
                System.err.println("An error occurred during sign up: " + e.getMessage());
                System.out.println("Sign up failed. Please try again later.");
                Delay();
                return;
            }
        }
    }

    // Helper method to validate email format
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    // Helper method to validate password complexity
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        return matcher.matches();
    }

    // Helper method to check if username exists
    public static boolean usernameExists(String username) {
        return accounts.stream().anyMatch(acc -> acc.getUsername().equalsIgnoreCase(username));
    }

    // Helper method to check if email exists
    public static boolean emailExists(String email) {
        return accounts.stream().anyMatch(acc -> acc.getEmail().equalsIgnoreCase(email));
    }
}
