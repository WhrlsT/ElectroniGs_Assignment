/**
 * AdminMenu class handles all administrative functionality of the application.
 * This includes managing users, orders, products, and generating reports.
 * The class provides different access levels based on admin roles (Admin, Manager, Staff).
 */
package assignmentapp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 * @author Whrl
 */
public class AdminMenu {

    private static final Scanner userInput = new Scanner(System.in);
    private static List<Product> products = new ArrayList<>(); 

    public static void AdminMenu() {

        // Check if current user has permission to access admin menu
        Admin currentAdmin = (Admin) MenuController.currentUser;
        if (currentAdmin == null || !currentAdmin.getAccountType().equals("admin")) {
            System.out.println("Access denied. Admin privileges required.");
            MenuController.Delay();
            return;
        }

        // The actual admin menu
        while (true){
            MenuController.ClearScreen();
            System.out.println("""
------------------------------------------------------------
                     ADMIN MENU
------------------------------------------------------------""");
            
            // Show menu items based on role
            if (currentAdmin.getRole().equals("Staff")) {
                System.out.println("""
                (1) Manage Orders
                (2) Manage Products
                (3) Log Out                                 """);
            } else if (currentAdmin.getRole().equals("Manager")) {
                System.out.println("""
                (1) View Report
                (2) Manage Orders
                (3) Manage Products
                (4) Log Out                                 """);
            } else { // Admin role
                System.out.println("""
                (1) View Report
                (2) Manage Users
                (3) Manage Orders
                (4) Manage Products
                (5) Log Out                                 """);
            }

            System.out.print("Please select an option: ");
            try {
                int choice = userInput.nextInt();
                userInput.nextLine(); 
                
                if (currentAdmin.getRole().equals("Staff")) {
                    switch(choice) {
                        case 1 -> ManageOrders();
                        case 2 -> ManageProducts();
                        case 3 -> {
                            MenuController.currentUser = null;
                            return; // Simply return to let MenuController handle the flow
                        }
                        default -> {
                            System.out.println("Invalid input. Please enter a number between 1 and 3.");
                            MenuController.Delay();
                        }
                    }
                } else if (currentAdmin.getRole().equals("Manager")) {
                    switch(choice) {
                        case 1 -> ViewReport();
                        case 2 -> ManageOrders();
                        case 3 -> ManageProducts();
                        case 4 -> {
                            MenuController.currentUser = null;
                            return; // Simply return to let MenuController handle the flow
                        }
                        default -> {
                            System.out.println("Invalid input. Please enter a number between 1 and 4.");
                            MenuController.Delay();
                        }
                    }
                } else { // Admin role
                    switch(choice) {
                        case 1 -> ViewReport();
                        case 2 -> ManageUsers();
                        case 3 -> ManageOrders();
                        case 4 -> ManageProducts();
                        case 5 -> {
                            MenuController.currentUser = null;
                            return; // Simply return to let MenuController handle the flow
                        }
                        default -> {
                            System.out.println("Invalid input. Please enter a number between 1 and 5.");
                            MenuController.Delay();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine(); // Clear the invalid input
                MenuController.Delay(); 
            }
        }
    }

    //Admin View Report Menu
    private static void ViewReport() {
        while (true) {
            MenuController.ClearScreen();
            System.out.println("""
------------------------------------------------------------
                        VIEW REPORT
------------------------------------------------------------
                (1) View Overall Report
                (2) View Users Report
                (3) View Orders Report
                (4) View Products Report
                (5) Generate PDF Reports
                (0) Back""");

            int choice = GetUserChoice(0, 5);
            switch(choice) {
                case 0 -> {
                    return; // Exit ViewReport
                }
                case 1 -> {
                    // View Overall Report
                    MenuController.ClearScreen();
                    System.out.println("""
------------------------------------------------------------
                        OVERALL REPORT
------------------------------------------------------------""");
                    
                    // Load all data
                    List<Account> accounts = JsonHandler.loadAccounts();
                    List<Order> orders = JsonHandler.loadOrders();
                    List<Product> products = JsonHandler.loadProducts();

                    // Calculate statistics
                    long totalUsers = accounts.size();
                    long totalCustomers = accounts.stream().filter(a -> "customer".equals(a.getAccountType())).count();
                    long totalAdmins = accounts.stream().filter(a -> "admin".equals(a.getAccountType())).count();
                    long totalOrders = orders.size();
                    double totalRevenue = orders.stream().mapToDouble(Order::getTotalAmount).sum();
                    long totalProducts = products.size();
                    double totalStockValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();

                    // Display statistics
                    System.out.println("\nUser Statistics:");
                    System.out.println("Total Users: " + totalUsers);
                    System.out.println("Total Customers: " + totalCustomers);
                    System.out.println("Total Admins: " + totalAdmins);

                    System.out.println("\nOrder Statistics:");
                    System.out.println("Total Orders: " + totalOrders);
                    System.out.printf("Total Revenue: RM %.2f\n", totalRevenue);

                    System.out.println("\nProduct Statistics:");
                    System.out.println("Total Products: " + totalProducts);
                    System.out.printf("Total Stock Value: RM %.2f\n", totalStockValue);

                    // Best Selling Products
                    System.out.println("\nBest Selling Products:");
                    Map<String, Long> productSales = orders.stream()
                        .flatMap(order -> order.getOrderItems().stream())
                        .collect(Collectors.groupingBy(
                            item -> item.getProduct().getProductName(),
                            Collectors.summingLong(OrderItem::getQuantity)
                        ));

                    productSales.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .forEach(entry -> {
                            System.out.printf("%s: %d units%n", entry.getKey(), entry.getValue());
                        });

                    // Wait for user
                    System.out.println("\nPress Enter to go back...");
                    userInput.nextLine();
                }
                case 2 -> {
                    // View Users Report
                    MenuController.ClearScreen();
                    System.out.println("""
------------------------------------------------------------
                        USERS REPORT
------------------------------------------------------------""");
                    
                    List<Account> accounts = JsonHandler.loadAccounts();
                    
                    System.out.println("\nUser Statistics:");
                    System.out.println("Total Users: " + accounts.size());
                    System.out.println("Total Customers: " + accounts.stream().filter(a -> "customer".equals(a.getAccountType())).count());
                    System.out.println("Total Admins: " + accounts.stream().filter(a -> "admin".equals(a.getAccountType())).count());
                    
                    System.out.println("\nUser Details:");
                    System.out.println("Username\t\tEmail\t\t\tAccount Type\tRole");
                    System.out.println("----------------------------------------------------------------");
                    for (Account account : accounts) {
                        String role = account instanceof Admin ? ((Admin) account).getRole() : "N/A";
                        System.out.printf("%-16s\t%-24s\t%-12s\t%s%n",
                            account.getUsername(),
                            account.getEmail(),
                            account.getAccountType(),
                            role);
                    }
                    // Wait for user
                    System.out.println("\nPress Enter to go back...");
                    userInput.nextLine();
                }
                case 3 -> {
                    // View Orders Report
                    MenuController.ClearScreen();
                    System.out.println("""
------------------------------------------------------------
                        ORDERS REPORT
------------------------------------------------------------""");
                    
                    List<Order> orders = JsonHandler.loadOrders();
                    
                    if (orders.isEmpty()) {
                        System.out.println("\nNo orders found.");
                    } else {
                        System.out.println("\nOrder Details:");
                        for (Order order : orders) {
                            System.out.println("\nOrder #" + order.getOrderId());
                            System.out.println("Date: " + order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                            System.out.println("Status: " + order.getShippingStatus());
                            System.out.println("Payment Method: " + order.getPayment().getPaymentMethod());
                            System.out.println("Payment Status: " + order.getPayment().getPaymentStatus());

                            System.out.println("\nItems:");
                            System.out.println("--------------------------------------------------------------------------------------------------------");
                            System.out.printf("| %-5s | %-55s | %-10s | %-8s | %-10s |\n", "ID", "Name", "Price", "Qty", "Total");
                            System.out.println("--------------------------------------------------------------------------------------------------------");
                            for (OrderItem item : order.getOrderItems()) {
                                System.out.printf("| %-5s | %-55s | RM%-8.2f | %-8d | RM%-8.2f |\n",
                                    item.getProduct().getProductID(),
                                    item.getProduct().getProductName(),
                                    item.getProduct().getPrice(),
                                    item.getQuantity(),
                                    item.getSubtotal());
                            }
                            System.out.println("--------------------------------------------------------------------------------------------------------");
                            System.out.printf("Order Total: RM%.2f\n", order.getTotalAmount());
                        }
                    }
                    // Wait for user
                    System.out.println("\nPress Enter to go back...");
                    userInput.nextLine();
                }
                case 4 -> {
                    // View Products Report
                    MenuController.ClearScreen();
                    System.out.println("""
------------------------------------------------------------
                        PRODUCTS REPORT
------------------------------------------------------------""");
                    
                    List<Product> products = JsonHandler.loadProducts();
                    List<Order> orders = JsonHandler.loadOrders();
                    
                    System.out.println("\nProduct Details:");
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    System.out.printf("| %-5s | %-55s | %-15s | %-10s | %-8s |\n", "ID", "Name", "Category", "Price", "Stock");
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    for (Product product : products) {
                        System.out.printf("| %-5s | %-55s | %-15s | RM%-8.2f | %-8d |\n",
                            product.getProductID(),
                            product.getProductName(),
                            product.getClass().getSimpleName(),
                            product.getPrice(),
                            product.getStock());
                    }
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    
                    // Best Selling Products
                    System.out.println("\nBest Selling Products:");
                    Map<String, Long> productSales = orders.stream()
                        .flatMap(order -> order.getOrderItems().stream())
                        .collect(Collectors.groupingBy(
                            item -> item.getProduct().getProductName(),
                            Collectors.summingLong(OrderItem::getQuantity)
                        ));

                    productSales.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .forEach(entry -> {
                            System.out.printf("%s: %d units%n", entry.getKey(), entry.getValue());
                        });
                    
                    // Wait for user
                    System.out.println("\nPress Enter to go back...");
                    userInput.nextLine();
                }
                case 5 -> {
                    // Generate PDF Reports
                    MenuController.ClearScreen();
                    System.out.println("""
------------------------------------------------------------
                    GENERATE PDF REPORTS
------------------------------------------------------------
                (1) Overall Report
                (2) Users Report
                (3) Orders Report
                (4) Products Report
                (5) All Reports
                (0) Back""");

                    int pdfChoice = GetUserChoice(0, 5);
                    switch(pdfChoice) {
                        case 0 -> {
                            // Back to report menu
                        }
                        case 1 -> {
                            PDFCreation.generateOverallReport();
                            MenuController.Delay();
                        }
                        case 2 -> {
                            PDFCreation.generateUsersReport();
                            MenuController.Delay();
                        }
                        case 3 -> {
                            PDFCreation.generateOrdersReport();
                            MenuController.Delay();
                        }
                        case 4 -> {
                            PDFCreation.generateProductsReport();
                            MenuController.Delay();
                        }
                        case 5 -> {
                            PDFCreation.generateOverallReport();
                            PDFCreation.generateUsersReport();
                            PDFCreation.generateOrdersReport();
                            PDFCreation.generateProductsReport();
                            MenuController.Delay();
                        }
                    }
                }
            }
        }
    }

    private static void ManageUsers() {
        // Check if current user has Admin role
        Admin currentAdmin = (Admin) MenuController.currentUser;
        if (!currentAdmin.getRole().equals("Admin")) {
            System.out.println("Access denied. Admin role required.");
            MenuController.Delay();
            return;
        }

        // Admin Manage Users Menu
        while (true) {
            MenuController.ClearScreen();
            System.out.println("""
------------------------------------------------------------
                     MANAGE USERS
------------------------------------------------------------""");

            List<Account> accounts = JsonHandler.loadAccounts();
            
            // Display users in a table format
            System.out.println("\nUser List:");
            System.out.println("------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-15s | %-25s | %-10s | %-10s |\n", 
                "Count", "Username", "Email", "Type", "Role");
            System.out.println("------------------------------------------------------------------------------------");

            for (int i = 0; i < accounts.size(); i++) {
                Account acc = accounts.get(i);
                System.out.printf("| %-5d | %-15s | %-25s | %-10s | %-10s |\n",
                    i + 1,
                    acc.getUsername(),
                    acc.getEmail(),
                    acc.getAccountType(),
                    acc instanceof Admin ? ((Admin)acc).getRole() : "N/A");
            }
            System.out.println("------------------------------------------------------------------------------------");

            System.out.println("\nOptions:");
            System.out.println("(1) View User Details");
            System.out.println("(2) Add User");
            System.out.println("(3) Delete User");
            System.out.println("(0) Return to Admin Menu");

            System.out.print("\nEnter your choice: ");
            try {
                int choice = userInput.nextInt();
                userInput.nextLine(); 

                switch (choice) {
                    case 0 -> { return; }
                    case 1 -> {
                        System.out.print("Enter user count to view details: ");
                        int userIndex = userInput.nextInt();
                        userInput.nextLine(); 
                        if (userIndex > 0 && userIndex <= accounts.size()) {
                            ViewUserDetails(accounts.get(userIndex - 1));
                        } else {
                            System.out.println("Invalid user count.");
                            MenuController.Delay();
                        }
                    }
                    case 2 -> AddUser();
                    case 3 -> {
                        System.out.print("Enter user count to delete: ");
                        int userIndex = userInput.nextInt();
                        userInput.nextLine(); 
                        if (userIndex > 0 && userIndex <= accounts.size()) {
                            DeleteUser(accounts.get(userIndex - 1));
                            accounts = JsonHandler.loadAccounts(); // Reload accounts after deletion
                        } else {
                            System.out.println("Invalid user count.");
                            MenuController.Delay();
                        }
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        MenuController.Delay();
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine(); // Clear the invalid input
                MenuController.Delay();
            }
        }
    }

    private static void ViewUserDetails(Account account) {
        MenuController.ClearScreen();
        System.out.println("""
------------------------------------------------------------
                     USER DETAILS
------------------------------------------------------------""");

        System.out.println("Username: " + account.getUsername());
        System.out.println("Email: " + account.getEmail());
        System.out.println("Account Type: " + account.getAccountType());
        if (account instanceof Admin admin) {
            System.out.println("Role: " + admin.getRole());
        }

        // Display order history for customers
        if (account instanceof Customer) {
            List<Order> userOrders = JsonHandler.getUserOrders(account.getUserID());
            System.out.println("\nOrder History:");
            if (userOrders.isEmpty()) {
                System.out.println("No orders found.");
            } else {
                System.out.println("------------------------------------------------------------------------------------");
                System.out.printf("| %-10s | %-12s | %-10s | %-12s |\n", 
                    "Order ID", "Date", "Items", "Total");
                System.out.println("------------------------------------------------------------------------------------");
                for (Order order : userOrders) {
                    System.out.printf("| %-10s | %-12s | %-10d | RM%-10.2f |\n",
                        order.getOrderId(),
                        order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        order.getOrderItems().size(),
                        order.getTotalAmount());
                }
                System.out.println("------------------------------------------------------------------------------------");
            }
        }

        System.out.println("\nPress Enter to return...");
        userInput.nextLine();
    }

    private static void DeleteUser(Account account) {
        if (account.getUserID().equals(MenuController.currentUser.getUserID())) {
            System.out.println("Cannot delete your own account!");
            MenuController.Delay();
            return;
        }

        System.out.println("\nAre you sure you want to delete user: " + account.getUsername() + "?");
        System.out.println("(1) Yes");
        System.out.println("(2) No");
        System.out.print("Enter your choice: ");

        try {
            int choice = userInput.nextInt();
            userInput.nextLine(); 

            if (choice == 1) {
                List<Account> accounts = JsonHandler.loadAccounts();
                accounts.removeIf(a -> a.getUserID().equals(account.getUserID()));
                JsonHandler.saveAccounts(accounts);
                System.out.println("User deleted successfully.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");
            userInput.nextLine(); // Clear the invalid input
        }
        MenuController.Delay();
    }

    private static void ManageOrders() {
        while (true) {
            MenuController.ClearScreen();
            System.out.println("""
------------------------------------------------------------
                     MANAGE ORDERS
------------------------------------------------------------""");

            List<Order> orders = JsonHandler.loadOrders();
            List<Account> accounts = JsonHandler.loadAccounts();

            // Display orders in a table format
            System.out.println("\nOrder List:");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-40s | %-12s | %-15s | %-10s | %-12s |\n", 
                "Count", "Order ID", "Date", "Customer", "Status", "Total");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                String customerName = accounts.stream()
                    .filter(a -> a.getUserID().equals(order.getUserId()))
                    .findFirst()
                    .map(Account::getUsername)
                    .orElse("Unknown");
                System.out.printf("| %-5d | %-40s | %-12s | %-15s | %-10s | RM%-10.2f |\n",
                    i + 1,
                    order.getOrderId(),
                    order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    customerName,
                    order.getShippingStatus(),
                    order.getTotalAmount());
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");

            System.out.println("\nCommands: [Count] details | [Count] update | back");
            System.out.print("Enter command: ");
            String command = userInput.nextLine().trim();

            if (command.equalsIgnoreCase("back") || command.equals("0")) {
                return;
            }

            String[] parts = command.split("\\s+", 2);
            String action = parts[0].toLowerCase();

            try {
                if (action.matches("\\d+")) {
                    int count = Integer.parseInt(action);
                    if (count > 0 && count <= orders.size()) {
                        final Order foundOrder = orders.get(count - 1);
                        if (parts.length > 1 && parts[1].equalsIgnoreCase("update")) {
                            // Show status update menu
                            MenuController.ClearScreen();
                            System.out.println("""
------------------------------------------------------------
                  UPDATE ORDER STATUS
------------------------------------------------------------""");
                            System.out.println("Order ID: " + foundOrder.getOrderId());
                            System.out.println("Current Status: " + foundOrder.getShippingStatus());
                            System.out.println("\nAvailable Statuses:");
                            System.out.println("[1] Pending");
                            System.out.println("[2] Processing");
                            System.out.println("[3] Shipped");
                            System.out.println("[4] Delivered");
                            System.out.println("[5] Cancelled");
                            System.out.println("[0] Cancel Update");
                            
                            System.out.print("\nEnter new status number: ");
                            try {
                                int statusChoice = userInput.nextInt();
                                userInput.nextLine(); 
                                
                                String newStatus = switch (statusChoice) {
                                    case 1 -> "Pending";
                                    case 2 -> "Processing";
                                    case 3 -> "Shipped";
                                    case 4 -> "Delivered";
                                    case 5 -> "Cancelled";
                                    default -> null;
                                };
                                
                                if (newStatus != null) {
                                    foundOrder.setShippingStatus(newStatus);
                                    List<Order> allOrders = JsonHandler.loadOrders();
                                    allOrders.replaceAll(o -> o.getOrderId().equals(foundOrder.getOrderId()) ? foundOrder : o);
                                    JsonHandler.saveOrders(allOrders);
                                    System.out.println("Order status updated successfully.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input. Please enter a number.");
                                userInput.nextLine(); // Clear the invalid input
                            }
                            MenuController.Delay();
                        } else {
                            ViewOrderDetails(foundOrder);
                        }
                    } else {
                        System.out.println("Invalid order count. Please enter a number between 1 and " + orders.size());
                        MenuController.Delay();
                    }
                } else {
                    System.out.println("Invalid command. Use format: [Count] details | [Count] update | back");
                    MenuController.Delay();
                }
            } catch (Exception e) {
                System.out.println("An error occurred processing the command: " + e.getMessage());
                MenuController.Delay();
            }
        }
    }

    private static void ViewOrderDetails(Order order) {
        MenuController.ClearScreen();
        System.out.println("""
------------------------------------------------------------
                     ORDER DETAILS
------------------------------------------------------------""");

        System.out.println("Order ID: " + order.getOrderId());
        System.out.println("Date: " + order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("Status: " + order.getShippingStatus());
        System.out.println("Payment Method: " + order.getPayment().getPaymentMethod());
        System.out.println("Payment Status: " + order.getPayment().getPaymentStatus());

        System.out.println("\nItems:");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-55s | %-10s | %-8s | %-10s |\n", "ID", "Name", "Price", "Qty", "Total");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (OrderItem item : order.getOrderItems()) {
            System.out.printf("| %-5s | %-55s | RM%-8.2f | %-8d | RM%-8.2f |\n",
                item.getProduct().getProductID(),
                item.getProduct().getProductName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getSubtotal());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.printf("Total: RM%.2f\n", order.getTotalAmount());

        System.out.println("\nPress Enter to return...");
        userInput.nextLine();
    }

    private static void ManageProducts() {
        while (true) {
            products = JsonHandler.loadProducts();

            MenuController.ClearScreen(); // Use MenuController's ClearScreen
            System.out.println("""
------------------------------------------------------------
                    PRODUCT MANAGEMENT
------------------------------------------------------------ """);

            // Calculate counts dynamically
            long totalProducts = products.size();
            long graphicCardsCount = products.stream().filter(p -> p instanceof GraphicCard).count();
            long motherboardsCount = products.stream().filter(p -> p instanceof Motherboard).count(); 
            long processorsCount = products.stream().filter(p -> p instanceof Processor).count();

            System.out.println("Total Number of Products: " + totalProducts);
            System.out.println("Total Graphic Cards: " + graphicCardsCount);
            System.out.println("Total Motherboards: " + motherboardsCount);
            System.out.println("Total Processors: " + processorsCount);


            System.out.println("""

                (1) Add Product
                (2) View Products
                (0) Return to Admin Menu""");

            System.out.print("Please select an option: ");
            try {
                int choice = userInput.nextInt();
                userInput.nextLine(); 

                if (choice >= 0 && choice <= 2) {
                    switch (choice){
                    case 0 -> {return;}
                    case 1 -> {AddProduct(); }
                    case 2 -> {ViewProduct();}
                    }
                } else {
                    System.out.println("Please enter a number between 0 and 2.");
                    MenuController.Delay();
                }
            } catch (InputMismatchException e) { 
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine(); 
                MenuController.Delay();
            } catch (Exception e) { 
                 System.out.println("An unexpected error occurred: " + e.getMessage());
                 userInput.nextLine(); 
                 MenuController.Delay();
            }
        }
    }

    private static void AddProduct() {
        MenuController.ClearScreen();
        System.out.println("""
------------------------------------------------------------
                       ADD PRODUCT
------------------------------------------------------------""");

        int categoryChoice = -1;
        while (categoryChoice < 1 || categoryChoice > 3) {
            System.out.println("Select Category:");
            System.out.println("  (1) Motherboard");
            System.out.println("  (2) Graphic Card");
            System.out.println("  (3) Processor");
            System.out.print("Enter choice (1-3): ");
            try {
                categoryChoice = userInput.nextInt();
                userInput.nextLine(); 
                if (categoryChoice < 1 || categoryChoice > 3) {
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    MenuController.Delay();
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine();
                MenuController.Delay();
                categoryChoice = -1; 
            }
        }

        // --- Common Fields ---
        String productName = "";
        while (productName.trim().isEmpty()) {
            System.out.print("Product Name: ");
            productName = userInput.nextLine();
            if (productName.trim().isEmpty()) {
                System.out.println("Product name cannot be empty.");
            }
        }

        double price = -1.0;
        while (price < 0) {
            System.out.print("Price: ");
            try {
                price = userInput.nextDouble();
                userInput.nextLine(); 
                if (price < 0) {
                    System.out.println("Price cannot be negative.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number for price.");
                userInput.nextLine(); 
                price = -1.0; 
            }
        }

        int stock = -1;
        while (stock < 0) {
            System.out.print("Stock Quantity: ");
            try {
                stock = userInput.nextInt();
                userInput.nextLine(); 
                if (stock < 0) {
                    System.out.println("Stock cannot be negative.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid whole number for stock.");
                userInput.nextLine(); 
                stock = -1; 
            }
        }

// --- Category Specific Fields ---
Product newProduct = null;
try {
    switch (categoryChoice) {
        case 1: // Motherboard
            String mbFormFactor, mbSocket, mbChipset, mbNetwork;
            int mbRamSlots;

            do { System.out.print("Form Factor (e.g., ATX, Micro-ATX): "); mbFormFactor = userInput.nextLine().trim(); if(mbFormFactor.isEmpty()) System.out.println("Form Factor cannot be empty."); } while (mbFormFactor.isEmpty());
            do { System.out.print("Socket Type: "); mbSocket = userInput.nextLine().trim(); if(mbSocket.isEmpty()) System.out.println("Socket Type cannot be empty."); } while (mbSocket.isEmpty());

            mbRamSlots = -1;
            while (mbRamSlots <= 0) {
                System.out.print("RAM Slots: ");
                try {
                    mbRamSlots = userInput.nextInt();
                    userInput.nextLine(); 
                    if (mbRamSlots <= 0) {
                        System.out.println("RAM Slots must be a positive number.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid whole number for RAM Slots.");
                    userInput.nextLine(); // Consume invalid input
                    mbRamSlots = -1; // Reset to loop again
                }
            }

            do { System.out.print("Chipset: "); mbChipset = userInput.nextLine().trim(); if(mbChipset.isEmpty()) System.out.println("Chipset cannot be empty."); } while (mbChipset.isEmpty());
            do { System.out.print("Network: "); mbNetwork = userInput.nextLine().trim(); if(mbNetwork.isEmpty()) System.out.println("Network cannot be empty."); } while (mbNetwork.isEmpty());

            newProduct = new Motherboard(productName, price, stock, mbFormFactor, mbChipset, mbSocket, mbRamSlots, mbNetwork);
            break;

        case 2: // Graphic Card
            String gpuManufacturer, gpuModel;
            int gpuVRam, gpuCores;
            double gpuCoreClock;

            do { System.out.print("Manufacturer: "); gpuManufacturer = userInput.nextLine().trim(); if(gpuManufacturer.isEmpty()) System.out.println("Manufacturer cannot be empty."); } while (gpuManufacturer.isEmpty());
            do { System.out.print("Model Name (e.g., GeForce RTX 4090): "); gpuModel = userInput.nextLine().trim(); if(gpuModel.isEmpty()) System.out.println("Model Name cannot be empty."); } while (gpuModel.isEmpty());

            gpuVRam = -1;
            while (gpuVRam <= 0) {
                System.out.print("Virtual Memory Size (GB): ");
                try {
                    gpuVRam = userInput.nextInt();
                    userInput.nextLine(); 
                    if (gpuVRam <= 0) {
                        System.out.println("VRAM size must be a positive number.");
                    }
                } catch (InputMismatchException e) { System.out.println("Invalid input. Please enter a valid whole number for VRAM."); userInput.nextLine(); gpuVRam = -1; }
            }

            gpuCores = -1;
            while (gpuCores <= 0) {
                System.out.print("Cores: ");
                try {
                    gpuCores = userInput.nextInt();
                    userInput.nextLine(); 
                    if (gpuCores <= 0) {
                        System.out.println("Core count must be a positive number.");
                    }
                } catch (InputMismatchException e) { System.out.println("Invalid input. Please enter a valid whole number for Cores."); userInput.nextLine(); gpuCores = -1; }
            }

            gpuCoreClock = -1.0;
            while (gpuCoreClock <= 0) {
                System.out.print("Core Clock Speed (GHz): ");
                 try {
                    gpuCoreClock = userInput.nextDouble();
                    userInput.nextLine(); 
                    if (gpuCoreClock <= 0) {
                        System.out.println("Core Clock Speed must be a positive number.");
                    }
                } catch (InputMismatchException e) { System.out.println("Invalid input. Please enter a valid number for Core Clock."); userInput.nextLine(); gpuCoreClock = -1.0; }
            }

            newProduct = new GraphicCard(productName, price, stock, gpuModel, gpuVRam, gpuCoreClock, gpuCores, gpuManufacturer);
            break;

        case 3: // Processor
            String cpuManufacturer, cpuSocket;
            int cpuCores;
            double cpuClock;
            boolean cpuIntegratedGraphics = false; // Initialize
            boolean validBooleanInput = false;

            do { System.out.print("Manufacturer (e.g., Intel, AMD): "); cpuManufacturer = userInput.nextLine().trim(); if(cpuManufacturer.isEmpty()) System.out.println("Manufacturer cannot be empty."); } while (cpuManufacturer.isEmpty());

            cpuCores = -1;
             while (cpuCores <= 0) {
                System.out.print("Core Count: ");
                try {
                    cpuCores = userInput.nextInt();
                    userInput.nextLine(); 
                    if (cpuCores <= 0) {
                        System.out.println("Core count must be a positive number.");
                    }
                } catch (InputMismatchException e) { System.out.println("Invalid input. Please enter a valid whole number for Core Count."); userInput.nextLine(); cpuCores = -1; }
            }

            cpuClock = -1.0;
            while (cpuClock <= 0) {
                System.out.print("Base Clock Speed (GHz): ");
                try {
                    cpuClock = userInput.nextDouble();
                    userInput.nextLine(); 
                    if (cpuClock <= 0) {
                        System.out.println("Base Clock Speed must be a positive number.");
                    }
                } catch (InputMismatchException e) { System.out.println("Invalid input. Please enter a valid number for Base Clock Speed."); userInput.nextLine(); cpuClock = -1.0; }
            }

            do { System.out.print("Socket Type: "); cpuSocket = userInput.nextLine().trim(); if(cpuSocket.isEmpty()) System.out.println("Socket Type cannot be empty."); } while (cpuSocket.isEmpty());

            while (!validBooleanInput) {
                System.out.print("Has Integrated Graphics (true/false): ");
                try {
                    cpuIntegratedGraphics = userInput.nextBoolean();
                    userInput.nextLine(); 
                    validBooleanInput = true; // Input was valid boolean
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter 'true' or 'false'.");
                    userInput.nextLine(); // Consume the invalid input
                }
            }

            newProduct = new Processor(productName, price, stock, cpuManufacturer, cpuCores, cpuClock, cpuSocket, cpuIntegratedGraphics);
            break;
    }


            if (newProduct != null) {
                products.add(newProduct);
                JsonHandler.saveProducts(products);
                System.out.println("\nProduct '" + newProduct.getProductName() + "' added successfully with ID: " + newProduct.getProductID());
            } else {
                 System.out.println("\nFailed to create product. Please ensure necessary classes (Motherboard, GraphicCard) exist.");
            }

        } catch (InputMismatchException e) {
             System.out.println("\nError: Invalid input type provided for one of the fields.");
        } catch (Exception e) { // Catch potential errors during object creation
             System.out.println("\nAn error occurred while creating the product: " + e.getMessage());
             e.printStackTrace(); // For debugging
        }

        System.out.println("\nPress Enter to return to Product Management...");
        userInput.nextLine();
    }

    private static void ViewProduct() {
        List<Product> products = JsonHandler.loadProducts();
        String currentSort = "ID";
        boolean ascending = true;
        String categoryFilter = null;

        while (true) {
            MenuController.ClearScreen();
            System.out.println("""
------------------------------------------------------------
                       VIEW PRODUCTS
------------------------------------------------------------""");

            // Filter by category
            List<Product> filteredProducts = Product.filterByCategory(products, categoryFilter);

            // Sort filtered products
            final boolean asc = ascending;
            if (currentSort.equalsIgnoreCase("category")) {
                filteredProducts.sort((p1, p2) -> {
                    int cmp = p1.getClass().getSimpleName().compareToIgnoreCase(p2.getClass().getSimpleName());
                    return asc ? cmp : -cmp;
                });
            } else {
                Comparator<Product> comparator = Product.getSortComparator(currentSort);
                if (!ascending) comparator = comparator.reversed();
                filteredProducts.sort(comparator);
            }

            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-55s | %-15s | %-10s | %-8s |\n", "ID", "Name", "Category", "Price", "Stock");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            if (filteredProducts.isEmpty()) {
                System.out.println("| No products found.                                                                                                |");
            } else {
                for (Product p : filteredProducts) {
                    String category = p.getClass().getSimpleName();
                    System.out.printf("| %-5s | %-55s | %-15s | RM%-8.2f | %-8d |\n",
                            p.getProductID(),
                            p.getProductName(),
                            category,
                            p.getPrice(),
                            p.getStock());
                }
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");

            System.out.println("Current Sort: " + currentSort + " (" + (ascending ? "Asc" : "Desc") + ")");
            System.out.println("\nCommands: [ProductID] | sort Alpha | sort Price | sort Stock | sort Category | sort Processor | sort Motherboard | sort GraphicCard | sort All | back");
            System.out.print("Enter command: ");
            String command = userInput.nextLine().trim();

            if (command.equalsIgnoreCase("back") || command.equals("0")) {
                return;
            }

            String[] parts = command.split("\\s+", 2);
            String action = parts[0].toLowerCase();

            try {
                if (action.matches("p\\d{3}")) {
                    Product foundProduct = null;
                    for (Product p : filteredProducts) {
                        if (p.getProductID().equalsIgnoreCase(action)) {
                            foundProduct = p;
                            break;
                        }
                    }
                    if (foundProduct != null) {
                        ShowProductDetails(foundProduct);
                        products = JsonHandler.loadProducts();
                    } else {
                        System.out.println("Product ID '" + action.toUpperCase() + "' not found.");
                        MenuController.Delay();
                    }
                } else if (action.equals("sort") && parts.length == 2) {
                    String sortType = parts[1].toLowerCase();
                    if (sortType.equals("processor") || sortType.equals("motherboard") || sortType.equals("graphiccard")) {
                        categoryFilter = sortType.substring(0, 1).toUpperCase() + sortType.substring(1).toLowerCase();
                    } else if (sortType.equals("all")) {
                        categoryFilter = null;
                    } else if (sortType.equals("alpha") || sortType.equals("price") || sortType.equals("stock") || sortType.equals("category")) {
                        if (currentSort.equalsIgnoreCase(sortType)) {
                            ascending = !ascending;
                        } else {
                            currentSort = sortType;
                            ascending = true;
                        }
                    } else {
                        System.out.println("Invalid sort/filter type.");
                        MenuController.Delay();
                    }
                } else {
                    System.out.println("Invalid command. Use format: [ProductID], sort [Type], back");
                    MenuController.Delay();
                }
            } catch (Exception e) {
                System.out.println("An error occurred processing the command: " + e.getMessage());
                MenuController.Delay();
            }
        }
    }

    // --- New Method for Product Details ---
    private static void ShowProductDetails(Product product) {
        while (true) {
            MenuController.ClearScreen();
            System.out.println("------------------------------------------------------------");
            System.out.println("                  PRODUCT DETAILS (" + product.getProductID() + ")");
            System.out.println("------------------------------------------------------------");

            // Display Common Fields
            System.out.println("Product Name : " + product.getProductName());
            System.out.println("Category     : " + product.getClass().getSimpleName());
            System.out.printf("Price        : $%.2f\n", product.getPrice());
            System.out.println("Stock        : " + product.getStock());

            // Display Specific Fields
            System.out.println("\n--- Specifications ---");
            if (product instanceof Processor proc) {
                System.out.println("Manufacturer : " + proc.getManufacturer());
                System.out.println("Core Count   : " + proc.getCoreCount());
                System.out.printf("Clock Speed  : %.2f GHz\n", proc.getBaseClockSpeed());
                System.out.println("Socket Type  : " + proc.getSocketType());
                System.out.println("Integrated GFX: " + (proc.hasIntegratedGraphics() ? "Yes" : "No"));
            } else if (product instanceof Motherboard mb) {
                System.out.println("Form Factor  : " + mb.getFormFactor());
                System.out.println("Chipset      : " + mb.getChipset());
                System.out.println("CPU Socket   : " + mb.getCpuSocketType());
                System.out.println("RAM Slots    : " + mb.getRamSlots());
                System.out.println("Network      : " + mb.getNetwork());
            } else if (product instanceof GraphicCard gpu) {
                System.out.println("Manufacturer : " + gpu.getManufacturer());
                System.out.println("Model Name   : " + gpu.getModelName());
                System.out.println("VRAM Size    : " + gpu.getVramSize() + " GB");
                System.out.printf("Core Clock   : %.2f GHz\n", gpu.getCoreClockSpeed());
                System.out.println("Cores        : " + gpu.getCores());
            } else {
                System.out.println("No specific details available for this product type.");
            }

            System.out.println("\n------------------------------------------------------------");
            System.out.println("[1] Edit Product");
            System.out.println("[2] Delete Product");
            System.out.println("[3] Return to Product List");
            System.out.print("Select an option: ");

            try {
                int choice = userInput.nextInt();
                userInput.nextLine(); 

                switch (choice) {
                    case 1:
                        EditProduct(product); // Call edit function
                        MenuController.Delay();
                        break;
                    case 2:
                        boolean deleted = DeleteProduct(product); // Call delete function
                        if (deleted) {
                        return; // Exit details view if deleted
                        }
                        MenuController.Delay();
                        // If not deleted, loop continues
                        break;
                    case 3:
                        return; // Exit the details view
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                        MenuController.Delay();
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine(); // Consume invalid input
                MenuController.Delay();
            } catch (Exception e) {
                 System.out.println("An unexpected error occurred: " + e.getMessage());
                 userInput.nextLine(); // Consume potentially leftover input
                 MenuController.Delay();
            }
        }
    }

    private static void EditProduct(Product product) {
        boolean editing = true;
        while (editing) {
            MenuController.ClearScreen();
            System.out.println("------------------------------------------------------------");
            System.out.println("             EDITING PRODUCT (" + product.getProductID() + ")");
            System.out.println("------------------------------------------------------------");
            System.out.println("Current Name : " + product.getProductName());
            System.out.printf("Current Price: $%.2f\n", product.getPrice());
            System.out.println("Current Stock: " + product.getStock());
            System.out.println("\n--- Specifics ---");
             // Display Specific Fields for context
            if (product instanceof Processor proc) {
                System.out.println("Manufacturer : " + proc.getManufacturer());
                System.out.println("Core Count   : " + proc.getCoreCount());
                System.out.printf("Clock Speed  : %.2f GHz\n", proc.getBaseClockSpeed());
                System.out.println("Socket Type  : " + proc.getSocketType());
                System.out.println("Integrated GFX: " + (proc.hasIntegratedGraphics() ? "Yes" : "No"));
            } else if (product instanceof Motherboard mb) {
                System.out.println("Form Factor  : " + mb.getFormFactor());
                System.out.println("Chipset      : " + mb.getChipset());
                System.out.println("CPU Socket   : " + mb.getCpuSocketType());
                System.out.println("RAM Slots    : " + mb.getRamSlots());
                System.out.println("Network      : " + mb.getNetwork());
            } else if (product instanceof GraphicCard gpu) {
                System.out.println("Manufacturer : " + gpu.getManufacturer());
                System.out.println("Model Name   : " + gpu.getModelName());
                System.out.println("VRAM Size    : " + gpu.getVramSize() + " GB");
                System.out.printf("Core Clock   : %.2f GHz\n", gpu.getCoreClockSpeed());
                System.out.println("Cores        : " + gpu.getCores());
            }

            System.out.println("\n--- What to edit? ---");
            System.out.println("1. Product Name");
            System.out.println("2. Price");
            System.out.println("3. Stock");
            // Add options for specific fields based on type
            int specificOptionStart = 4;
            if (product instanceof Processor) {
                System.out.println("4. Manufacturer");
                System.out.println("5. Core Count");
                System.out.println("6. Clock Speed");
                System.out.println("7. Socket Type");
                System.out.println("8. Integrated Graphics");
                specificOptionStart = 9;
            } else if (product instanceof Motherboard) {
                System.out.println("4. Form Factor");
                System.out.println("5. Chipset");
                System.out.println("6. CPU Socket");
                System.out.println("7. RAM Slots");
                System.out.println("8. Network");
                specificOptionStart = 9;
            } else if (product instanceof GraphicCard) {
                System.out.println("4. Manufacturer");
                System.out.println("5. Model Name");
                System.out.println("6. VRAM Size");
                System.out.println("7. Core Clock");
                System.out.println("8. Cores");
                specificOptionStart = 9;
            }
            System.out.println(specificOptionStart + ". Finish Editing");
            System.out.print("Select field to edit: ");

            int choice = -1;
            try {
                choice = userInput.nextInt();
                userInput.nextLine(); 

                boolean changed = false; // Flag to check if save is needed

                // --- Handle Common Fields ---
                if (choice == 1) {
                    System.out.print("Enter new Product Name: ");
                    String newName = userInput.nextLine();
                    if (!newName.trim().isEmpty()) {
                        product.setProductName(newName);
                        changed = true;
                    } else {
                        System.out.println("Product name cannot be empty."); MenuController.Delay();
                    }
                } else if (choice == 2) {
                    System.out.print("Enter new Price: ");
                    try {
                        double newPrice = userInput.nextDouble();
                        userInput.nextLine();
                        if (newPrice >= 0) {
                            product.setPrice(newPrice);
                            changed = true;
                        } else {
                            System.out.println("Price cannot be negative."); MenuController.Delay();
                        }
                    } catch (InputMismatchException e) { System.out.println("Invalid price format."); userInput.nextLine(); MenuController.Delay();}
                } else if (choice == 3) {
                     System.out.print("Enter new Stock quantity: ");
                    try {
                        int newStock = userInput.nextInt();
                        userInput.nextLine();
                        if (newStock >= 0) {
                            product.setStock(newStock);
                            changed = true;
                        } else {
                            System.out.println("Stock cannot be negative."); MenuController.Delay();
                        }
                    } catch (InputMismatchException e) { System.out.println("Invalid stock format."); userInput.nextLine(); MenuController.Delay();}
                }
                // --- Handle Specific Fields ---
                else if (choice >= 4 && choice < specificOptionStart) {
                    if (product instanceof Processor proc) {
                        switch (choice) {
                            case 4: System.out.print("New Manufacturer: "); proc.manufacturer = userInput.nextLine(); changed = true; break;
                            case 5: System.out.print("New Core Count: "); try { proc.coreCount = userInput.nextInt(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid number."); userInput.nextLine(); MenuController.Delay();} break;
                            case 6: System.out.print("New Clock Speed (GHz): "); try { proc.baseClockSpeed = userInput.nextDouble(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid number."); userInput.nextLine(); MenuController.Delay();} break;
                            case 7: System.out.print("New Socket Type: "); proc.socketType = userInput.nextLine(); changed = true; break;
                            case 8: System.out.print("Has Integrated Graphics (true/false): "); try { proc.integratedGraphics = userInput.nextBoolean(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid input (true/false)."); userInput.nextLine(); MenuController.Delay();} break;
                        }
                    } else if (product instanceof Motherboard mb) {
                         switch (choice) {
                            case 4: System.out.print("New Form Factor: "); mb.formFactor = userInput.nextLine(); changed = true; break;
                            case 5: System.out.print("New Chipset: "); mb.chipset = userInput.nextLine(); changed = true; break;
                            case 6: System.out.print("New CPU Socket: "); mb.cpuSocketType = userInput.nextLine(); changed = true; break;
                            case 7: System.out.print("New RAM Slots: "); try { mb.ramSlots = userInput.nextInt(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid number."); userInput.nextLine(); MenuController.Delay();} break;
                            case 8: System.out.print("New Network: "); mb.network = userInput.nextLine(); changed = true; break;
                        }
                    } else if (product instanceof GraphicCard gpu) {
                         switch (choice) {
                            case 4: System.out.print("New Manufacturer: "); gpu.manufacturer = userInput.nextLine(); changed = true; break;
                            case 5: System.out.print("New Model Name: "); gpu.modelName = userInput.nextLine(); changed = true; break;
                            case 6: System.out.print("New VRAM Size (GB): "); try { gpu.vramSize = userInput.nextInt(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid number."); userInput.nextLine(); MenuController.Delay();} break;
                            case 7: System.out.print("New Core Clock (GHz): "); try { gpu.coreClockSpeed = userInput.nextDouble(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid number."); userInput.nextLine(); MenuController.Delay();} break;
                            case 8: System.out.print("New Cores: "); try { gpu.cores = userInput.nextInt(); userInput.nextLine(); changed = true; } catch (InputMismatchException e) { System.out.println("Invalid number."); userInput.nextLine(); MenuController.Delay();} break;
                        }
                    }
                }
                // --- Finish Editing ---
                else if (choice == specificOptionStart) {
                    editing = false; // Exit the editing loop
                } else {
                    System.out.println("Invalid choice.");
                    MenuController.Delay();
                }

                // Save changes if any were made in this iteration
                if (changed) {
                    JsonHandler.saveProducts(products);
                    System.out.println("Changes saved.");
                    MenuController.Delay();
                    // The loop will continue, showing the updated product
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                userInput.nextLine(); // Consume invalid input
                MenuController.Delay();
            } catch (Exception e) {
                 System.out.println("An error occurred during editing: " + e.getMessage());
                 e.printStackTrace();
                 userInput.nextLine(); // Consume potentially leftover input
                 MenuController.Delay();
            }
        }
        System.out.println("Finished editing " + product.getProductName());
        MenuController.Delay();
    }


    // --- New Method to Delete Product ---
    private static boolean DeleteProduct(Product productToDelete) {
        System.out.print("Are you sure you want to delete '" + productToDelete.getProductName() + "' (ID: " + productToDelete.getProductID() + ")? (y/n): ");
        String confirmation = userInput.nextLine().trim().toLowerCase();

        if (confirmation.equals("y")) {
            boolean removed = products.removeIf(p -> p.getProductID().equals(productToDelete.getProductID()));
            if (removed) {
                JsonHandler.saveProducts(products); // Save the updated list
                return true; // Indicate successful deletion
            } else {
                System.out.println("Error: Product could not be found in the list for deletion.");
                MenuController.Delay();
                return false; // Indicate failure
            }
        } else {
            System.out.println("Deletion cancelled.");
            MenuController.Delay();
            return false; // Indicate cancellation
        }
    }

    private static void AddUser() {
        MenuController.ClearScreen();
        System.out.println("""
------------------------------------------------------------
                     ADD USER
------------------------------------------------------------""");

        // Get username
        String username;
        while (true) {
            System.out.print("Username: ");
            username = userInput.nextLine();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
                continue;
            }
            if (MenuController.usernameExists(username)) {
                System.out.println("Username already exists.");
                continue;
            }
            break;
        }

        // Get email
        String email;
        while (true) {
            System.out.print("Email: ");
            email = userInput.nextLine();
            if (!MenuController.isValidEmail(email)) {
                System.out.println("Invalid email format.");
                continue;
            }
            if (MenuController.emailExists(email)) {
                System.out.println("Email already exists.");
                continue;
            }
            break;
        }

        // Get password
        String password;
        while (true) {
            System.out.print("Password (min 8 chars, incl. letter, number, special char [@$!%*?&]): ");
            password = userInput.nextLine();
            if (!MenuController.isValidPassword(password)) {
                System.out.println("Password does not meet complexity requirements.");
                continue;
            }
            break;
        }

        // Get user type
        String userType;
        while (true) {
            System.out.print("User Type [customer/admin]: ");
            userType = userInput.nextLine().toLowerCase();
            if (!userType.equals("customer") && !userType.equals("admin")) {
                System.out.println("Invalid user type. Please enter 'customer' or 'admin'.");
                continue;
            }
            break;
        }

        // Get role if admin
        String role = null;
        if (userType.equals("admin")) {
            while (true) {
                System.out.print("Role [Admin/Staff/Manager]: ");
                role = userInput.nextLine();
                if (!role.equals("Admin") && !role.equals("Staff") && !role.equals("Manager")) {
                    System.out.println("Invalid role. Please enter 'Admin', 'Staff', or 'Manager'.");
                    continue;
                }
                break;
            }
        }

        // Create and save the new user
        Account newAccount;
        if (userType.equals("admin")) {
            newAccount = new Admin(username, email, password, role);
        } else {
            newAccount = new Customer(username, email, password);
        }

        List<Account> accounts = JsonHandler.loadAccounts();
        accounts.add(newAccount);
        JsonHandler.saveAccounts(accounts);

        System.out.println("\nUser created successfully!");
        MenuController.Delay();
    }

    private static int GetUserChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("\nEnter your choice: ");
                String input = userInput.nextLine().trim();
                if (input.isEmpty()) {
                    System.out.println("Please enter a number.");
                    continue;
                }
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Invalid choice. Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}