package assignmentapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class JsonHandler {

    // Shared Gson instance
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Account.class, new TypeAdapter<Account>() {
                @Override
                public void write(JsonWriter out, Account value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    // Write the actual class name as a type field
                    out.beginObject();
                    out.name("type").value(value.getClass().getName());
                    out.name("data");
                    gson.toJson(value, value.getClass(), out);
                    out.endObject();
                }

                @Override
                public Account read(JsonReader in) throws IOException {
                    if (in.peek() == null) {
                        in.nextNull();
                        return null;
                    }
                    in.beginObject();
                    String type = null;
                    Account result = null;
                    while (in.hasNext()) {
                        String name = in.nextName();
                        if (name.equals("type")) {
                            type = in.nextString();
                        } else if (name.equals("data")) {
                            try {
                                Class<?> clazz = Class.forName(type);
                                result = (Account) gson.fromJson(in, clazz);
                            } catch (ClassNotFoundException e) {
                                throw new IOException("Unknown class: " + type, e);
                            }
                        } else {
                            in.skipValue();
                        }
                    }
                    in.endObject();
                    return result;
                }
            })
            .registerTypeAdapter(Product.class, new ProductTypeAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new TypeAdapter<java.time.LocalDateTime>() {
                @Override
                public void write(JsonWriter out, java.time.LocalDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.value(value.toString());
                }

                @Override
                public java.time.LocalDateTime read(JsonReader in) throws IOException {
                    if (in.peek() == null) {
                        in.nextNull();
                        return null;
                    }
                    String dateStr = in.nextString();
                    return java.time.LocalDateTime.parse(dateStr);
                }
            })
            .registerTypeAdapter(Payment.class, new TypeAdapter<Payment>() {
                @Override
                public void write(JsonWriter out, Payment value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.beginObject();
                    out.name("type").value(value.getClass().getSimpleName());
                    out.name("paymentId").value(value.getPaymentId());
                    out.name("paymentMethod").value(value.getPaymentMethod());
                    out.name("paymentStatus").value(value.getPaymentStatus());
                    out.name("paymentAmount").value(value.getPaymentAmount());
                    out.name("paymentDate").value(value.getPaymentDate().toString());
                    
                    // Write specific fields based on payment type
                    if (value instanceof DebitPayment dp) {
                        out.name("cardNumber").value(dp.getCardNumber());
                        out.name("expiryDate").value(dp.getExpiryDate());
                        out.name("cvv").value(dp.getCvv());
                    } else if (value instanceof TNGPayment tng) {
                        out.name("phoneNumber").value(tng.getPhoneNumber());
                    }
                    out.endObject();
                }

                @Override
                public Payment read(JsonReader in) throws IOException {
                    if (in.peek() == null) {
                        in.nextNull();
                        return null;
                    }
                    in.beginObject();
                    String type = null;
                    String paymentId = null;
                    String paymentMethod = null;
                    String paymentStatus = null;
                    double paymentAmount = 0.0;
                    LocalDateTime paymentDate = null;
                    String cardNumber = null;
                    String expiryDate = null;
                    String cvv = null;
                    String phoneNumber = null;

                    while (in.hasNext()) {
                        String name = in.nextName();
                        switch (name) {
                            case "type" -> type = in.nextString();
                            case "paymentId" -> paymentId = in.nextString();
                            case "paymentMethod" -> paymentMethod = in.nextString();
                            case "paymentStatus" -> paymentStatus = in.nextString();
                            case "paymentAmount" -> paymentAmount = in.nextDouble();
                            case "paymentDate" -> paymentDate = LocalDateTime.parse(in.nextString());
                            case "cardNumber" -> cardNumber = in.nextString();
                            case "expiryDate" -> expiryDate = in.nextString();
                            case "cvv" -> cvv = in.nextString();
                            case "phoneNumber" -> phoneNumber = in.nextString();
                            default -> in.skipValue();
                        }
                    }
                    in.endObject();

                    // If type is null, infer it from paymentMethod
                    if (type == null && paymentMethod != null) {
                        if ("Card".equals(paymentMethod)) {
                            type = "DebitPayment";
                        } else if ("TNG".equals(paymentMethod)) {
                            type = "TNGPayment";
                        }
                    }

                    Payment payment;
                    if ("DebitPayment".equals(type)) {
                        payment = new DebitPayment(paymentId, paymentAmount, cardNumber, expiryDate, cvv);
                    } else if ("TNGPayment".equals(type)) {
                        payment = new TNGPayment(paymentId, paymentAmount, phoneNumber);
                    } else {
                        throw new JsonParseException("Unknown payment type: " + type + " (paymentMethod: " + paymentMethod + ")");
                    }

                    if (paymentStatus != null) {
                        payment.setPaymentStatus(paymentStatus);
                    }
                    return payment;
                }
            })
            .registerTypeAdapter(OrderItem.class, new TypeAdapter<OrderItem>() {
                @Override
                public void write(JsonWriter out, OrderItem value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.beginObject();
                    out.name("product");
                    gson.toJson(value.getProduct(), Product.class, out);
                    out.name("quantity").value(value.getQuantity());
                    out.name("subtotal").value(value.getSubtotal());
                    out.endObject();
                }

                @Override
                public OrderItem read(JsonReader in) throws IOException {
                    if (in.peek() == null) {
                        in.nextNull();
                        return null;
                    }
                    in.beginObject();
                    Product product = null;
                    int quantity = 0;
                    double subtotal = 0.0;

                    while (in.hasNext()) {
                        String name = in.nextName();
                        switch (name) {
                            case "product" -> product = gson.fromJson(in, Product.class);
                            case "quantity" -> quantity = in.nextInt();
                            case "subtotal" -> subtotal = in.nextDouble();
                            default -> in.skipValue();
                        }
                    }
                    in.endObject();

                    if (product == null) {
                        throw new JsonParseException("Product is required in OrderItem JSON");
                    }

                    OrderItem item = new OrderItem(product, quantity);
                    // Use reflection to set the subtotal since it's private
                    try {
                        java.lang.reflect.Field field = OrderItem.class.getDeclaredField("subtotal");
                        field.setAccessible(true);
                        field.set(item, subtotal);
                    } catch (Exception e) {
                        throw new JsonParseException("Failed to set subtotal", e);
                    }
                    return item;
                }
            })
            .registerTypeAdapter(Order.class, new TypeAdapter<Order>() {
                @Override
                public void write(JsonWriter out, Order value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.beginObject();
                    out.name("orderId").value(value.getOrderId());
                    out.name("userId").value(value.getUserId());
                    out.name("orderItems");
                    gson.toJson(value.getOrderItems(), new TypeToken<List<OrderItem>>(){}.getType(), out);
                    out.name("payment");
                    gson.toJson(value.getPayment(), Payment.class, out);
                    out.name("shippingStatus").value(value.getShippingStatus());
                    out.name("orderDate").value(value.getOrderDate().toString());
                    out.name("totalAmount").value(value.getTotalAmount());
                    out.endObject();
                }

                @Override
                public Order read(JsonReader in) throws IOException {
                    if (in.peek() == null) {
                        in.nextNull();
                        return null;
                    }
                    in.beginObject();
                    String orderId = null;
                    String userId = null;
                    List<OrderItem> orderItems = null;
                    Payment payment = null;
                    String shippingStatus = null;
                    LocalDateTime orderDate = null;
                    double totalAmount = 0.0;

                    while (in.hasNext()) {
                        String name = in.nextName();
                        switch (name) {
                            case "orderId" -> orderId = in.nextString();
                            case "userId" -> userId = in.nextString();
                            case "orderItems" -> orderItems = gson.fromJson(in, new TypeToken<List<OrderItem>>(){}.getType());
                            case "payment" -> payment = gson.fromJson(in, Payment.class);
                            case "shippingStatus" -> shippingStatus = in.nextString();
                            case "orderDate" -> orderDate = LocalDateTime.parse(in.nextString());
                            case "totalAmount" -> totalAmount = in.nextDouble();
                            default -> in.skipValue();
                        }
                    }
                    in.endObject();

                    if (userId == null || orderItems == null || payment == null) {
                        throw new JsonParseException("Required fields missing in Order JSON");
                    }

                    Order order = new Order(userId, orderItems, payment);
                    if (orderId != null) {
                        // Use reflection to set the orderId since it's private
                        try {
                            java.lang.reflect.Field field = Order.class.getDeclaredField("orderId");
                            field.setAccessible(true);
                            field.set(order, orderId);
                        } catch (Exception e) {
                            throw new JsonParseException("Failed to set orderId", e);
                        }
                    }
                    if (shippingStatus != null) {
                        order.setShippingStatus(shippingStatus);
                    }
                    return order;
                }
            })
            .create();

    private static final String DEFAULT_ACCOUNTS_FILE_PATH = "accounts.json";
    private static final String DEFAULT_PRODUCTS_FILE_PATH = "products.json";
    private static final String DEFAULT_CARTS_FILE_PATH = "carts.json";
    private static final String DEFAULT_ORDERS_FILE_PATH = "orders.json";

    private static class ProductTypeAdapter implements JsonSerializer<Product>, JsonDeserializer<Product> {
        @Override
        public JsonElement serialize(Product src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("productType", src.getClass().getSimpleName());
            jsonObject.addProperty("productID", src.getProductID());
            jsonObject.addProperty("productName", src.getProductName());
            jsonObject.addProperty("price", src.getPrice());
            jsonObject.addProperty("stock", src.getStock());

            // Add specific fields based on product type
            if (src instanceof Processor proc) {
                jsonObject.addProperty("manufacturer", proc.getManufacturer());
                jsonObject.addProperty("coreCount", proc.getCoreCount());
                jsonObject.addProperty("baseClockSpeed", proc.getBaseClockSpeed());
                jsonObject.addProperty("socketType", proc.getSocketType());
                jsonObject.addProperty("hasIntegratedGraphics", proc.hasIntegratedGraphics());
            } else if (src instanceof Motherboard mb) {
                jsonObject.addProperty("formFactor", mb.getFormFactor());
                jsonObject.addProperty("chipset", mb.getChipset());
                jsonObject.addProperty("cpuSocketType", mb.getCpuSocketType());
                jsonObject.addProperty("ramSlots", mb.getRamSlots());
                jsonObject.addProperty("network", mb.getNetwork());
            } else if (src instanceof GraphicCard gpu) {
                jsonObject.addProperty("manufacturer", gpu.getManufacturer());
                jsonObject.addProperty("modelName", gpu.getModelName());
                jsonObject.addProperty("vramSize", gpu.getVramSize());
                jsonObject.addProperty("coreClockSpeed", gpu.getCoreClockSpeed());
                jsonObject.addProperty("cores", gpu.getCores());
            }

            return jsonObject;
        }

        @Override
        public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String productType = jsonObject.get("productType").getAsString();
            String productName = jsonObject.get("productName").getAsString();
            double price = jsonObject.get("price").getAsDouble();
            int stock = jsonObject.get("stock").getAsInt();

            switch (productType) {
                case "Processor":
                    return new Processor(
                        productName,
                        price,
                        stock,
                        jsonObject.get("manufacturer").getAsString(),
                        jsonObject.get("coreCount").getAsInt(),
                        jsonObject.get("baseClockSpeed").getAsDouble(),
                        jsonObject.get("socketType").getAsString(),
                        jsonObject.get("hasIntegratedGraphics").getAsBoolean()
                    );
                case "Motherboard":
                    return new Motherboard(
                        productName,
                        price,
                        stock,
                        jsonObject.get("formFactor").getAsString(),
                        jsonObject.get("chipset").getAsString(),
                        jsonObject.get("cpuSocketType").getAsString(),
                        jsonObject.get("ramSlots").getAsInt(),
                        jsonObject.get("network").getAsString()
                    );
                case "GraphicCard":
                    return new GraphicCard(
                        productName,
                        price,
                        stock,
                        jsonObject.get("modelName").getAsString(),
                        jsonObject.get("vramSize").getAsInt(),
                        jsonObject.get("coreClockSpeed").getAsDouble(),
                        jsonObject.get("cores").getAsInt(),
                        jsonObject.get("manufacturer").getAsString()
                    );
                default:
                    throw new JsonParseException("Unknown product type: " + productType);
            }
        }
    }

    // --- Account Methods ---

    /**
     * Saves a list of accounts to the specified JSON file.
     * Assumes Account subclasses have necessary fields (like accountType) public or accessible via getters/setters.
     *
     * @param accounts The list of Account objects to save.
     * @param filePath The path to the file where accounts should be saved.
     */
    public static void saveAccounts(List<Account> accounts, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(accounts, writer);
            System.out.println("Accounts successfully saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving accounts to " + filePath + ": " + e.getMessage());
        } catch (Exception e) { // Catch other potential serialization errors
             System.err.println("Unexpected error saving accounts: " + e.getMessage());
             e.printStackTrace();
        }
    }

    /**
     * Saves a list of accounts to the default JSON file (accounts.json).
     *
     * @param accounts The list of Account objects to save.
     */
    public static void saveAccounts(List<Account> accounts) {
        saveAccounts(accounts, DEFAULT_ACCOUNTS_FILE_PATH);
    }

    /**
     * Loads a list of accounts, determining the type (Customer/Admin) based on the 'accountType' field.
     *
     * @param filePath The path to the file from which accounts should be loaded.
     * @return A List of Account objects, or an empty list if the file doesn't exist or an error occurs.
     */
    public static List<Account> loadAccounts(String filePath) {
        List<Account> loadedAccounts = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonArray()) {
                for (JsonElement accountElement : jsonElement.getAsJsonArray()) {
                    if (accountElement.isJsonObject()) {
                        JsonElement typeElement = accountElement.getAsJsonObject().get("accountType");
                        if (typeElement != null && typeElement.isJsonPrimitive()) {
                            String accountType = typeElement.getAsString();
                            Account account = null;
                            try {
                                if ("customer".equalsIgnoreCase(accountType)) { // Use equalsIgnoreCase for robustness
                                    account = gson.fromJson(accountElement, Customer.class);
                                } else if ("admin".equalsIgnoreCase(accountType)) {
                                    account = gson.fromJson(accountElement, Admin.class);
                                } else {
                                    System.err.println("Unknown account type found in JSON: " + accountType);
                                }

                                if (account != null) {
                                    loadedAccounts.add(account);
                                }
                            } catch (Exception e) { // Catch errors during individual object deserialization
                                 System.err.println("Error deserializing account object: " + accountElement.toString() + " - " + e.getMessage());
                            }
                        } else {
                             System.err.println("Account object in JSON missing or has invalid 'accountType' field: " + accountElement.toString());
                        }
                    } else {
                         System.err.println("Expected JSON object in accounts array, but found: " + accountElement.getClass().getSimpleName());
                    }
                }
                System.out.println("Accounts successfully loaded from " + filePath);
            } else {
                 System.err.println("Expected JSON array in " + filePath + " but found " + jsonElement.getClass().getSimpleName());
            }

        } catch (IOException e) {
            // File not found is common on first run, don't treat as critical error
            System.out.println("Info: Could not load accounts from " + filePath + ". File might not exist yet. " + e.getMessage());
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error parsing JSON from " + filePath + ": " + e.getMessage());
        } catch (Exception e) {
             System.err.println("An unexpected error occurred during account loading: " + e.getMessage());
             e.printStackTrace();
        }
        return loadedAccounts;
    }

    /**
     * Loads a list of accounts from the default JSON file (accounts.json).
     *
     * @return A List of Account objects, or an empty list if the file doesn't exist or an error occurs.
     */
    public static List<Account> loadAccounts() {
        return loadAccounts(DEFAULT_ACCOUNTS_FILE_PATH);
    }


    // --- Product Methods ---

    /**
     * Saves a list of products to the specified JSON file.
     * Assumes Product subclasses have necessary fields (like productType) public or accessible via getters/setters.
     *
     * @param products The list of Product objects to save.
     * @param filePath The path to the file where products should be saved.
     */
    public static void saveProducts(List<Product> products, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(products, writer);
            System.out.println("Products successfully saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving products to " + filePath + ": " + e.getMessage());
        } catch (Exception e) { // Catch other potential serialization errors
             System.err.println("Unexpected error saving products: " + e.getMessage());
             e.printStackTrace();
        }
    }

    /**
     * Saves a list of products to the default JSON file (products.json).
     *
     * @param products The list of Product objects to save.
     */
    public static void saveProducts(List<Product> products) {
        saveProducts(products, DEFAULT_PRODUCTS_FILE_PATH);
    }

     /**
     * Loads a list of products, determining the type based on the 'productType' field.
     *
     * @param filePath The path to the file from which products should be loaded.
     * @return A List of Product objects, or an empty list if the file doesn't exist or an error occurs.
     */
    public static List<Product> loadProducts(String filePath) {
        List<Product> loadedProducts = new ArrayList<>();
        int maxIdNum = 0; // Track the highest numeric part of the ID found
        // Regex to extract number from Product ID (e.g., P005 -> 5)
        Pattern idPattern = Pattern.compile("P(\\d{3,})"); // Assumes P followed by 3+ digits

        try (FileReader reader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonArray()) {
                for (JsonElement productElement : jsonElement.getAsJsonArray()) {
                    if (productElement.isJsonObject()) {
                        // Extract product ID to find max
                        JsonElement idElement = productElement.getAsJsonObject().get("productID");
                        if (idElement != null && idElement.isJsonPrimitive()) {
                            String productIdStr = idElement.getAsString();
                            Matcher matcher = idPattern.matcher(productIdStr);
                            if (matcher.matches()) {
                                try {
                                    int currentIdNum = Integer.parseInt(matcher.group(1));
                                    maxIdNum = Math.max(maxIdNum, currentIdNum); // Update max ID number
                                } catch (NumberFormatException nfe) {
                                     System.err.println("Could not parse number from Product ID: " + productIdStr);
                                }
                            }
                        }

                        // Deserialize based on type
                        JsonElement typeElement = productElement.getAsJsonObject().get("productType");
                        if (typeElement != null && typeElement.isJsonPrimitive()) {
                            String productType = typeElement.getAsString();
                            Product product = null;
                            try {
                                // Deserialize based on the type field
                                if ("motherboard".equalsIgnoreCase(productType)) {
                                    product = gson.fromJson(productElement, Motherboard.class);
                                } else if ("graphiccard".equalsIgnoreCase(productType)) {
                                    product = gson.fromJson(productElement, GraphicCard.class);
                                } else if ("processor".equalsIgnoreCase(productType)) {
                                    product = gson.fromJson(productElement, Processor.class);
                                } else {
                                    System.err.println("Unknown product type found in JSON: " + productType);
                                }

                                if (product != null) {
                                    loadedProducts.add(product);
                                }
                             } catch (Exception e) {
                                 System.err.println("Error deserializing product object: " + productElement.toString() + " - " + e.getMessage());
                             }
                        } else {
                             System.err.println("Product object in JSON missing or has invalid 'productType' field: " + productElement.toString());
                        }
                    } else {
                         System.err.println("Expected JSON object in products array, but found: " + productElement.getClass().getSimpleName());
                    }
                }
                System.out.println("Products successfully loaded from " + filePath);
            } else {
                 System.err.println("Expected JSON array in " + filePath + " but found " + jsonElement.getClass().getSimpleName());
            }

        } catch (IOException e) {
            System.out.println("Info: Could not load products from " + filePath + ". File might not exist yet. " + e.getMessage());
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error parsing JSON from " + filePath + ": " + e.getMessage());
        } catch (Exception e) {
             System.err.println("An unexpected error occurred during product loading: " + e.getMessage());
             e.printStackTrace();
        }

        // --- Initialize the Product ID counter ---
        Product.updateIdCounter(maxIdNum); // Call the static method in Product

        return loadedProducts;
    }

    /**
     * Loads a list of products from the default JSON file (products.json).
     *
     * @return A List of Product objects, or an empty list if the file doesn't exist or an error occurs.
     */
    public static List<Product> loadProducts() {
        return loadProducts(DEFAULT_PRODUCTS_FILE_PATH);
    }

    // --- Cart Methods ---

    /**
     * Saves a list of carts to the specified JSON file.
     *
     * @param carts The list of Cart objects to save.
     * @param filePath The path to the file where carts should be saved.
     */
    public static void saveCarts(List<Cart> carts, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(carts, writer);
            System.out.println("Carts successfully saved to " + filePath);
        } catch (IOException e) {
            System.err.println("Error saving carts to " + filePath + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error saving carts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves a list of carts to the default JSON file (carts.json).
     *
     * @param carts The list of Cart objects to save.
     */
    public static void saveCarts(List<Cart> carts) {
        saveCarts(carts, DEFAULT_CARTS_FILE_PATH);
    }

    /**
     * Loads a list of carts from the specified JSON file.
     *
     * @param filePath The path to the file from which carts should be loaded.
     * @return A List of Cart objects, or an empty list if the file doesn't exist or an error occurs.
     */
    public static List<Cart> loadCarts(String filePath) {
        List<Cart> loadedCarts = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonArray()) {
                for (JsonElement cartElement : jsonElement.getAsJsonArray()) {
                    if (cartElement.isJsonObject()) {
                        try {
                            Cart cart = gson.fromJson(cartElement, Cart.class);
                            if (cart != null) {
                                loadedCarts.add(cart);
                            }
                        } catch (Exception e) {
                            System.err.println("Error deserializing cart object: " + cartElement.toString() + " - " + e.getMessage());
                        }
                    }
                }
                System.out.println("Carts successfully loaded from " + filePath);
            } else {
                System.err.println("Expected JSON array in " + filePath + " but found " + jsonElement.getClass().getSimpleName());
            }
        } catch (IOException e) {
            System.out.println("Info: Could not load carts from " + filePath + ". File might not exist yet. " + e.getMessage());
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error parsing JSON from " + filePath + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during cart loading: " + e.getMessage());
            e.printStackTrace();
        }
        return loadedCarts;
    }

    /**
     * Loads a list of carts from the default JSON file (carts.json).
     *
     * @return A List of Cart objects, or an empty list if the file doesn't exist or an error occurs.
     */
    public static List<Cart> loadCarts() {
        return loadCarts(DEFAULT_CARTS_FILE_PATH);
    }

    public static void saveCart(String userId, Cart cart) {
        try {
            // Load existing carts
            List<Cart> carts = loadCarts();
            
            // Remove existing cart for this user if it exists
            carts.removeIf(c -> c.getUserID().equals(userId));
            
            // Add new cart if not null
            if (cart != null) {
                cart.setUserID(userId);
                carts.add(cart);
            }
            
            // Save updated carts list
            saveCarts(carts);
        } catch (Exception e) {
            System.err.println("Error saving cart: " + e.getMessage());
        }
    }

    // --- Order Methods ---
    public static void saveOrders(List<Order> orders) {
        try (FileWriter writer = new FileWriter(DEFAULT_ORDERS_FILE_PATH)) {
            gson.toJson(orders, writer);
            System.out.println("Orders successfully saved");
        } catch (IOException e) {
            System.err.println("Error saving orders: " + e.getMessage());
        }
    }

    public static List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(DEFAULT_ORDERS_FILE_PATH))) {
                return orders;
            }
            String json = Files.readString(Paths.get(DEFAULT_ORDERS_FILE_PATH));
            if (json.trim().isEmpty()) {
                return orders;
            }
            Type listType = new TypeToken<List<Order>>(){}.getType();
            orders = gson.fromJson(json, listType);
        } catch (IOException e) {
            System.err.println("Error loading orders: " + e.getMessage());
        }
        return orders;
    }

    public static void saveOrder(Order order) {
        List<Order> orders = loadOrders();
        orders.add(order);
        saveOrders(orders);
    }

    public static List<Order> getUserOrders(String userId) {
        return loadOrders().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

}