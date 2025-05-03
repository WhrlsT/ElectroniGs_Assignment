/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignmentapp;
import java.util.ArrayList; 
import java.util.Comparator; 
import java.util.InputMismatchException; 
import java.util.List; 
import java.util.Scanner;
import java.util.stream.Collectors; 
import java.util.UUID;


public class CustomerMenu {
    private static final Scanner userInput = new Scanner(System.in);
    private static List<Product> products = new ArrayList<>(); // Initialize empty list for now
    private static Cart currentCart; // Stores the current user's shopping cart


    //Main customer menu interface that handles user navigation and operations
    public static void UserMenu() {
        // Initialize cart for current user
        currentCart = loadOrCreateCart();
        
        while (true) {
            ClearScreen();
            System.out.println("""
------------------------------------------------------------
                     CUSTOMER MENU
------------------------------------------------------------
                (1) View Products
                (2) View Cart
                (3) Order History
                (4) Log Out                                 """);

            System.out.print("\nPlease select an option: ");
            try {
                int choice = Integer.parseInt(userInput.nextLine());
                if (choice >= 1 && choice <= 4) {
                    switch(choice) {
                        case 1 -> ViewProducts();
                        case 2 -> ViewCart();
                        case 3 -> ViewOrderHistory();
                        case 4 -> {
                            MenuController.currentUser = null;
                            return; // Simply return to let MenuController handle the flow
                        }
                    }
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    Delay();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                Delay();
            }
        }
    }

    //Loads existing cart for current user or creates a new one if none exists
    private static Cart loadOrCreateCart() {
        List<Cart> carts = JsonHandler.loadCarts();
        String currentUserID = MenuController.currentUser.getUserID();
        
        // Find existing cart for current user
        for (Cart cart : carts) {
            if (cart.getUserID().equals(currentUserID)) {
                return cart;
            }
        }
        
        // Create new cart if none exists
        Cart newCart = new Cart(currentUserID);
        carts.add(newCart);
        JsonHandler.saveCarts(carts);
        return newCart;
    }

    //Displays available products with sorting and filtering options
    private static void ViewProducts() {
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

            // Filter by category and stock
            List<Product> filteredProducts = Product.filterByCategory(products, categoryFilter)
                .stream()
                .filter(p -> p.getStock() > 0) // Only show products with stock > 0
                .collect(Collectors.toList());

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
                        while (true) {
                            MenuController.ClearScreen();
                            System.out.println("------------------------------------------------------------");
                            System.out.println("                  PRODUCT DETAILS (" + foundProduct.getProductID() + ")");
                            System.out.println("------------------------------------------------------------");
                    
                            // Display Common Fields
                            System.out.println("Product Name : " + foundProduct.getProductName());
                            System.out.println("Category     : " + foundProduct.getClass().getSimpleName());
                            System.out.printf("Price        : $%.2f\n", foundProduct.getPrice());
                            System.out.println("Stock        : " + foundProduct.getStock());
                    
                            // Display Specific Fields
                            System.out.println("\n--- Specifications ---");
                            if (foundProduct instanceof Processor proc) {
                                System.out.println("Manufacturer : " + proc.getManufacturer());
                                System.out.println("Core Count   : " + proc.getCoreCount());
                                System.out.printf("Clock Speed  : %.2f GHz\n", proc.getBaseClockSpeed());
                                System.out.println("Socket Type  : " + proc.getSocketType());
                                System.out.println("Integrated GFX: " + (proc.hasIntegratedGraphics() ? "Yes" : "No"));
                            } else if (foundProduct instanceof Motherboard mb) {
                                System.out.println("Form Factor  : " + mb.getFormFactor());
                                System.out.println("Chipset      : " + mb.getChipset());
                                System.out.println("CPU Socket   : " + mb.getCpuSocketType());
                                System.out.println("RAM Slots    : " + mb.getRamSlots());
                                System.out.println("Network      : " + mb.getNetwork());
                            } else if (foundProduct instanceof GraphicCard gpu) {
                                System.out.println("Manufacturer : " + gpu.getManufacturer());
                                System.out.println("Model Name   : " + gpu.getModelName());
                                System.out.println("VRAM Size    : " + gpu.getVramSize() + " GB");
                                System.out.printf("Core Clock   : %.2f GHz\n", gpu.getCoreClockSpeed());
                                System.out.println("Cores        : " + gpu.getCores());
                            }
                    
                            System.out.println("\n------------------------------------------------------------");
                            System.out.println("[1] Add to Cart");
                            System.out.println("[2] Return to Product List");
                            System.out.print("Select an option: ");
                    
                            try {
                                int choice = userInput.nextInt();
                                userInput.nextLine(); 
                    
                                switch (choice) {
                                    case 1:
                                        AddToCart(foundProduct);
                                        ViewProducts();
                                        return;
                                    case 2:
                                        ViewProducts();
                                        return;
                                    default:
                                        System.out.println("Invalid choice. Please enter 1 or 2.");
                                        MenuController.Delay();
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input. Please enter a number.");
                                userInput.nextLine();
                                MenuController.Delay();
                            }
                             }
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

    //Adds selected product to the shopping cart with quantity validation
    private static void AddToCart(Product product) {
        if (product.getStock() <= 0) {
            System.out.println("Sorry, this product is out of stock.");
            MenuController.Delay();
            return;
        }

        // Check current quantity in cart
        int currentCartQuantity = 0;
        for (CartItem item : currentCart.getItems()) {
            if (item.getProductID().equals(product.getProductID())) {
                currentCartQuantity = item.getQuantity();
                break;
            }
        }

        System.out.print("Enter quantity to add: ");
        try {
            int quantity = userInput.nextInt();
            userInput.nextLine();

            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0.");
            } else if (currentCartQuantity + quantity > product.getStock()) {
                System.out.println("Sorry, we only have " + product.getStock() + " in stock.");
                if (currentCartQuantity > 0) {
                    System.out.println("You already have " + currentCartQuantity + " in your cart.");
                }
            } else {
                currentCart.addItem(product, quantity);
                JsonHandler.saveCarts(List.of(currentCart));
                System.out.println("Added " + quantity + " " + product.getProductName() + " to cart.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            userInput.nextLine();
        }
        MenuController.Delay();
    }

    //Displays current cart contents and handles cart management operations
    private static void ViewCart() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            System.out.println("Your cart is empty!");
            Delay();
            return;
        }

        while (true) {
            ClearScreen();
            System.out.println("""
------------------------------------------------------------
                        VIEW CART
------------------------------------------------------------""");
            
            // Display cart contents
            System.out.println("\nYour Cart:");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-55s | %-10s | %-8s | %-10s |\n", "ID", "Name", "Price", "Qty", "Total");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < currentCart.getItems().size(); i++) {
                CartItem item = currentCart.getItems().get(i);
                System.out.printf("| %-5s | %-55s | RM%-8.2f | %-8d | RM%-8.2f |\n",
                    item.getProduct().getProductID(),
                    item.getProduct().getProductName(),
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("\nTotal Amount: RM%.2f\n", currentCart.getTotal());

            System.out.println("\nCommands: [ProductID] | update [ProductID] [Quantity] | remove [ProductID] | checkout | back");
            System.out.print("Enter command: ");
            String command = userInput.nextLine().trim();

            if (command.equalsIgnoreCase("back") || command.equals("0")) {
                return;
            }

            String[] parts = command.split("\\s+");
            String action = parts[0].toLowerCase();

            try {
                if (action.matches("p\\d{3}")) {
                    // View product details
                    ViewCartItemDetails(action);
                    // Refresh cart view after returning from product details
                    continue;
                } else if (action.equals("update") && parts.length == 3) {
                    String productId = parts[1];
                    int newQuantity = Integer.parseInt(parts[2]);
                    UpdateCartQuantity(productId, newQuantity);
                    // Refresh cart view after updating quantity
                    continue;
                } else if (action.equals("remove") && parts.length == 2) {
                    String productId = parts[1];
                    RemoveFromCart(productId);
                    // Refresh cart view after removing item
                    continue;
                } else if (action.equals("checkout")) {
                    CheckOut();
                    return;
                } else {
                    System.out.println("Invalid command. Use format: [ProductID], update [ProductID] [Quantity], remove [ProductID], checkout, back");
                    Delay();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Please enter a number.");
                Delay();
            } catch (Exception e) {
                System.out.println("An error occurred processing the command: " + e.getMessage());
                Delay();
            }
        }
    }

    //Shows detailed information about a specific cart item
    private static void ViewCartItemDetails(String productId) {
        // Find the item in cart
        CartItem targetItem = null;
        for (CartItem item : currentCart.getItems()) {
            if (item.getProduct().getProductID().equalsIgnoreCase(productId)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            System.out.println("Product not found in cart!");
            Delay();
            return;
        }

        Product product = targetItem.getProduct();
        while (true) {
            ClearScreen();
            System.out.println("------------------------------------------------------------");
            System.out.println("                  PRODUCT DETAILS (" + product.getProductID() + ")");
            System.out.println("------------------------------------------------------------");
    
            // Display Common Fields
            System.out.println("Product Name : " + product.getProductName());
            System.out.println("Category     : " + product.getClass().getSimpleName());
            System.out.printf("Price        : RM%.2f\n", product.getPrice());
            System.out.println("Stock        : " + product.getStock());
            System.out.println("In Cart      : " + targetItem.getQuantity());
    
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
            }
    
            System.out.println("\n------------------------------------------------------------");
            System.out.println("[1] Update Quantity");
            System.out.println("[2] Remove from Cart");
            System.out.println("[3] Return to Cart");
            System.out.print("Select an option: ");
    
            try {
                int choice = Integer.parseInt(userInput.nextLine());
                switch (choice) {
                    case 1:
                        System.out.print("Enter new quantity: ");
                        int newQuantity = Integer.parseInt(userInput.nextLine());
                        UpdateCartQuantity(productId, newQuantity);
                        return;
                    case 2:
                        RemoveFromCart(productId);
                        return;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                        Delay();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                Delay();
            }
        }
    }

    //Updates quantity of an item in the cart
    private static void UpdateCartQuantity(String productId, int newQuantity) {
        // Find the item in cart
        CartItem targetItem = null;
        for (CartItem item : currentCart.getItems()) {
            if (item.getProduct().getProductID().equalsIgnoreCase(productId)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            System.out.println("Product not found in cart!");
            Delay();
            return;
        }

        if (newQuantity <= 0) {
            currentCart.removeItem(productId);
            System.out.println("Item removed from cart.");
        } else if (newQuantity > targetItem.getProduct().getStock()) {
            System.out.println("Not enough stock available!");
        } else {
            currentCart.updateItemQuantity(productId, newQuantity);
            System.out.println("Quantity updated successfully!");
        }
        Delay();
    }

    //Removes an item from the cart
    private static void RemoveFromCart(String productId) {
        // Find the item in cart
        CartItem targetItem = null;
        for (CartItem item : currentCart.getItems()) {
            if (item.getProduct().getProductID().equalsIgnoreCase(productId)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem != null) {
            currentCart.removeItem(productId);
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("Product not found in cart!");
        }
        Delay();
    }


    //Validates credit/debit card number format
    private static boolean validateCardNumber(String cardNumber) {
        // Basic validation: 16 digits
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }

    //Validates card expiry date format and checks if card is expired
    private static boolean validateExpiryDate(String expiryDate) {
        // Basic validation: MM/YY format
        if (!expiryDate.matches("(0[1-9]|1[0-2])/([0-9]{2})")) {
            return false;
        }
        
        // Check if card is expired
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);
        
        java.time.YearMonth current = java.time.YearMonth.now();
        java.time.YearMonth expiry = java.time.YearMonth.of(2000 + year, month);
        
        return !expiry.isBefore(current);
    }


    //Validates CVV format
    private static boolean validateCVV(String cvv) {
        // Basic validation: 3 digits
        return cvv != null && cvv.matches("\\d{3}");
    }


    //Validates Malaysian phone number format
    private static boolean validatePhoneNumber(String phoneNumber) {
        // Malaysian phone number format
        return phoneNumber != null && phoneNumber.matches("^(\\+?6?01)[0-46-9]-*[0-9]{7,8}$");
    }


    //Processes the checkout operation including payment and order creation
    public static void CheckOut() {
        if (currentCart == null || currentCart.getItems().isEmpty()) {
            System.out.println("Your cart is empty!");
            Delay();
            return;
        }

        ClearScreen();
        System.out.println("""
------------------------------------------------------------
                        CHECKOUT
------------------------------------------------------------""");
        
        // Display cart summary in table format
        System.out.println("\nOrder Summary:");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-55s | %-10s | %-8s | %-10s |\n", "ID", "Name", "Price", "Qty", "Total");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (CartItem item : currentCart.getItems()) {
            System.out.printf("| %-5s | %-55s | RM%-8.2f | %-8d | RM%-8.2f |\n",
                    item.getProduct().getProductID(),
                    item.getProduct().getName(),
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.printf("Total: RM%.2f\n", currentCart.getTotal());

        // Payment method selection
        System.out.println("\nSelect payment method:");
        System.out.println("1. Debit Card");
        System.out.println("2. Touch 'n Go");
        System.out.println("0. Cancel");

        int choice = GetUserChoice(0, 2);
        if (choice == 0) return;

        Payment payment = null;
        if (choice == 1) {
            // Debit Card payment
            String cardNumber;
            do {
                System.out.print("\nEnter card number (16 digits): ");
                cardNumber = userInput.nextLine();
                if (!validateCardNumber(cardNumber)) {
                    System.out.println("Invalid card number. Please enter 16 digits.");
                }
            } while (!validateCardNumber(cardNumber));

            String expiryDate;
            do {
                System.out.print("Enter expiry date (MM/YY): ");
                expiryDate = userInput.nextLine();
                if (!validateExpiryDate(expiryDate)) {
                    System.out.println("Invalid expiry date. Please use MM/YY format and ensure card is not expired.");
                }
            } while (!validateExpiryDate(expiryDate));

            String cvv;
            do {
                System.out.print("Enter CVV (3 digits): ");
                cvv = userInput.nextLine();
                if (!validateCVV(cvv)) {
                    System.out.println("Invalid CVV. Please enter 3 digits.");
                }
            } while (!validateCVV(cvv));

            payment = new DebitPayment(
                UUID.randomUUID().toString(),
                currentCart.getTotal(),
                cardNumber,
                expiryDate,
                cvv
            );
        } else {
            // TNG payment
            String phoneNumber;
            do {
                System.out.print("\nEnter TNG phone number: ");
                phoneNumber = userInput.nextLine();
                if (!validatePhoneNumber(phoneNumber)) {
                    System.out.println("Invalid phone number. Please enter a valid Malaysian phone number.");
                }
            } while (!validatePhoneNumber(phoneNumber));

            payment = new TNGPayment(
                UUID.randomUUID().toString(),
                currentCart.getTotal(),
                phoneNumber
            );
        }

        // Create order items from cart items
        List<OrderItem> orderItems = currentCart.getItems().stream()
            .map(item -> new OrderItem(item.getProduct(), item.getQuantity()))
            .collect(Collectors.toList());

        // Create and process order
        Order order = new Order(
            MenuController.currentUser.getUserID(),
            orderItems,
            payment
        );

        System.out.println("\nProcessing payment...");
        if (order.processOrder()) {
            // Update product stock
            List<Product> products = JsonHandler.loadProducts();
            for (OrderItem orderItem : order.getOrderItems()) {
                for (Product product : products) {
                    if (product.getProductID().equals(orderItem.getProduct().getProductID())) {
                        product.setStock(product.getStock() - orderItem.getQuantity());
                        break;
                    }
                }
            }
            JsonHandler.saveProducts(products);

            System.out.println("\nPayment successful!");
            System.out.println("\nOrder Details:");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-5s | %-55s | %-10s | %-8s | %-10s |\n", "ID", "Name", "Price", "Qty", "Total");
        System.out.println("--------------------------------------------------------------------------------------------------------");

        for (CartItem item : currentCart.getItems()) {
            System.out.printf("| %-5s | %-55s | RM%-8.2f | %-8d | RM%-8.2f |\n",
                    item.getProduct().getProductID(),
                    item.getProduct().getName(),
                    item.getProduct().getPrice(),
                    item.getQuantity(),
                    item.getSubtotal());
        }
            System.out.println("------------------------------------------------------------------------------------");
            System.out.printf("Total: RM%.2f\n", order.getTotalAmount());
            System.out.println("Payment Method: " + order.getPayment().getPaymentMethod());
            System.out.println("Payment Status: " + order.getPayment().getPaymentStatus());
            System.out.println("Shipping Status: " + order.getShippingStatus());
            
            // Save the order
            JsonHandler.saveOrder(order);
            
            // Generate and send receipt
            if (MenuController.currentUser instanceof Customer) {
                Customer customer = (Customer) MenuController.currentUser;
                if (ReceiptHandler.generateAndSendReceipt(order, customer)) {
                    System.out.println("\nReceipt has been sent to your email.");
                } else {
                    System.out.println("\nWarning: Could not send receipt to your email.");
                }
            }
            
            // Clear the cart after successful order
            currentCart = null;
            JsonHandler.saveCart(MenuController.currentUser.getUserID(), null);
        } else {
            System.out.println("\nPayment failed. Please try again.");
        }

        System.out.println("\nPress Enter to continue...");
        userInput.nextLine();
    }


    //Displays user's order history with detailed view options
    private static void ViewOrderHistory() {
        while (true) {
            MenuController.ClearScreen();
            System.out.println("""
------------------------------------------------------------
                     ORDER HISTORY
------------------------------------------------------------""");

            List<Order> orders = JsonHandler.getUserOrders(MenuController.currentUser.getUserID());
            if (orders.isEmpty()) {
                System.out.println("No orders found.");
                System.out.println("\nPress Enter to return to Customer Menu...");
                userInput.nextLine();
                return;
            }

            // Display orders in a table format
            System.out.println("\nOrder List:");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            System.out.printf("| %-5s | %-40s | %-12s | %-10s | %-10s | %-12s |\n", 
                "Count", "Order ID", "Date", "Items", "Status", "Total");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                System.out.printf("| %-5d | %-40s | %-12s | %-10d | %-10s | RM%-10.2f |\n",
                    i + 1,
                    order.getOrderId(),
                    order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    order.getOrderItems().size(),
                    order.getShippingStatus(),
                    order.getTotalAmount());
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");

            System.out.println("\nEnter order count to view details (0 to return): ");
            String input = userInput.nextLine().trim();
            
            if (input.equals("0")) {
                return;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice > 0 && choice <= orders.size()) {
                    Order selectedOrder = orders.get(choice - 1);
                    MenuController.ClearScreen();
                    System.out.println("""
------------------------------------------------------------
                     ORDER DETAILS
------------------------------------------------------------""");

                    System.out.println("Order ID: " + selectedOrder.getOrderId());
                    System.out.println("Date: " + selectedOrder.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                    System.out.println("Status: " + selectedOrder.getShippingStatus());
                    System.out.println("Payment Method: " + selectedOrder.getPayment().getPaymentMethod());
                    System.out.println("Payment Status: " + selectedOrder.getPayment().getPaymentStatus());

                    System.out.println("\nItems:");
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    System.out.printf("| %-5s | %-55s | %-10s | %-8s | %-10s |\n", 
                        "ID", "Name", "Price", "Qty", "Total");
                    System.out.println("--------------------------------------------------------------------------------------------------------");

                    for (OrderItem item : selectedOrder.getOrderItems()) {
                        System.out.printf("| %-5s | %-55s | RM%-8.2f | %-8d | RM%-8.2f |\n",
                            item.getProduct().getProductID(),
                            item.getProduct().getProductName(),
                            item.getProduct().getPrice(),
                            item.getQuantity(),
                            item.getSubtotal());
                    }
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    System.out.printf("Total: RM%.2f\n", selectedOrder.getTotalAmount());

                    System.out.println("\nPress Enter to return to Order History...");
                    userInput.nextLine();
                } else {
                    System.out.println("Invalid order count.");
                    MenuController.Delay();
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                MenuController.Delay();
            }
        }
    }


    //Utility method to clear the console screen
    private static void ClearScreen() {
        MenuController.ClearScreen();
    }


    //Utility method to add a delay in the program
    private static void Delay() {
        MenuController.Delay();
    }


    ///Handles user input validation for menu choices
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
