package assignmentapp;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Product implements Sortable<Product> {

    private static AtomicInteger idCounter = new AtomicInteger(1);
    // Make fields public for easier Gson serialization without custom adapters
    public String productID;
    public String productName;
    public double price;
    public int stock;
    public String productType; // Added field to store the type

    // No-args constructor
    protected Product() {
        this.productID = null;
        this.productName = null;
        this.price = 0.0;
        this.stock = 0;
        this.productType = null; // Default type
    }

    // Constructor for creating new products (used by subclasses)
    protected Product(String productName, double price, int stock, String productType) {
        this.productID = generateProductID();
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.productType = productType; // Set type from subclass
    }

    // Constructor for loading products (if manual ID setting is needed)
    protected Product(String productID, String productName, double price, int stock, String productType) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.stock = stock;
        this.productType = productType; // Set type
    }

    // Method to generate sequential product IDs using AtomicInteger
    private static String generateProductID() {
        // getAndIncrement provides atomic fetch-and-add operation
        int nextId = idCounter.getAndIncrement();
        return String.format("P%03d", nextId);
    }

    //Updates the ID counter based on the maximum ID found during loading.
    public static synchronized void updateIdCounter(int maxLoadedId) {
        // Set the counter to one greater than the max loaded ID.
        // Ensure it doesn't go below 1 if maxLoadedId is 0 (e.g., empty file).
        int nextId = Math.max(1, maxLoadedId + 1);
        idCounter.set(nextId);
        System.out.println("Product ID counter initialized to start at: P" + String.format("%03d", nextId)); // Optional: Log initialization
    }

    // Getters
    public String getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

     public String getProductType() {
        return productType;
    }

    public String getName() {
        return productName;
    }

    // Setters (optional, but useful for managing stock, etc.)
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        if (stock >= 0) {
            this.stock = stock;
        } else {
            System.err.println("Stock cannot be negative.");
            // Or throw an IllegalArgumentException
        }
    }
     @Override
    public String toString() {
        return "Product{" +
               "productID='" + productID + '\'' +
               ", productName='" + productName + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               ", productType='" + productType + '\'' + // Include type
               '}';
    }

    // Static sorting methods
    public static void sortProducts(List<Product> products, String sortBy, boolean ascending) {
        Comparator<Product> comparator = getSortComparator(sortBy);
        if (!ascending) {
            comparator = comparator.reversed();
        }
        products.sort(comparator);
    }

    public static Comparator<Product> getSortComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "alpha" -> Comparator.comparing(Product::getProductName, String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparingDouble(Product::getPrice);
            case "stock" -> Comparator.comparingInt(Product::getStock);
            case "id" -> Comparator.comparing(Product::getProductID);
            case "category" -> Comparator.comparing(p -> p.getClass().getSimpleName(), String.CASE_INSENSITIVE_ORDER);
            default -> throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        };
    }

    // Static method to get available sort options
    public static String[] getSortOptions() {
        return new String[]{"alpha", "price", "stock", "id", "category"};
    }

    // Category-specific sorting methods
    public static void sortProcessors(List<Processor> processors, String sortBy, boolean ascending) {
        Comparator<Processor> comparator;
        if (sortBy.equalsIgnoreCase("category")) {
            comparator = (p1, p2) -> p1.getClass().getSimpleName().compareToIgnoreCase(p2.getClass().getSimpleName());
        } else {
            comparator = getProcessorComparator(sortBy);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        processors.sort(comparator);
    }

    public static void sortMotherboards(List<Motherboard> motherboards, String sortBy, boolean ascending) {
        Comparator<Motherboard> comparator;
        if (sortBy.equalsIgnoreCase("category")) {
            comparator = (m1, m2) -> m1.getClass().getSimpleName().compareToIgnoreCase(m2.getClass().getSimpleName());
        } else {
            comparator = getMotherboardComparator(sortBy);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        motherboards.sort(comparator);
    }

    public static void sortGraphicCards(List<GraphicCard> graphicCards, String sortBy, boolean ascending) {
        Comparator<GraphicCard> comparator;
        if (sortBy.equalsIgnoreCase("category")) {
            comparator = (g1, g2) -> g1.getClass().getSimpleName().compareToIgnoreCase(g2.getClass().getSimpleName());
        } else {
            comparator = getGraphicCardComparator(sortBy);
        }
        if (!ascending) {
            comparator = comparator.reversed();
        }
        graphicCards.sort(comparator);
    }

    private static Comparator<Processor> getProcessorComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "manufacturer" -> Comparator.comparing(Processor::getManufacturer, String.CASE_INSENSITIVE_ORDER);
            case "cores" -> Comparator.comparingInt(Processor::getCoreCount);
            case "clockspeed" -> Comparator.comparingDouble(Processor::getBaseClockSpeed);
            case "socket" -> Comparator.comparing(Processor::getSocketType, String.CASE_INSENSITIVE_ORDER);
            default -> (p1, p2) -> getSortComparator(sortBy).compare(p1, p2);
        };
    }

    private static Comparator<Motherboard> getMotherboardComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "formfactor" -> Comparator.comparing(Motherboard::getFormFactor, String.CASE_INSENSITIVE_ORDER);
            case "chipset" -> Comparator.comparing(Motherboard::getChipset, String.CASE_INSENSITIVE_ORDER);
            case "socket" -> Comparator.comparing(Motherboard::getCpuSocketType, String.CASE_INSENSITIVE_ORDER);
            case "ramslots" -> Comparator.comparingInt(Motherboard::getRamSlots);
            case "network" -> Comparator.comparing(Motherboard::getNetwork, String.CASE_INSENSITIVE_ORDER);
            default -> (m1, m2) -> getSortComparator(sortBy).compare(m1, m2);
        };
    }

    private static Comparator<GraphicCard> getGraphicCardComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "model" -> Comparator.comparing(GraphicCard::getModelName, String.CASE_INSENSITIVE_ORDER);
            case "vram" -> Comparator.comparingInt(GraphicCard::getVramSize);
            case "clockspeed" -> Comparator.comparingDouble(GraphicCard::getCoreClockSpeed);
            case "cores" -> Comparator.comparingInt(GraphicCard::getCores);
            case "manufacturer" -> Comparator.comparing(GraphicCard::getManufacturer, String.CASE_INSENSITIVE_ORDER);
            default -> (g1, g2) -> getSortComparator(sortBy).compare(g1, g2);
        };
    }

    // Static methods to get category-specific sort options
    public static String[] getProcessorSortOptions() {
        return new String[]{"alpha", "price", "stock", "id", "manufacturer", "cores", "clockspeed", "socket"};
    }

    public static String[] getMotherboardSortOptions() {
        return new String[]{"alpha", "price", "stock", "id", "formfactor", "chipset", "socket", "ramslots", "network"};
    }

    public static String[] getGraphicCardSortOptions() {
        return new String[]{"alpha", "price", "stock", "id", "model", "vram", "clockspeed", "cores", "manufacturer"};
    }

    @Override
    public Comparator<Product> getComparator(String field) {
        return getSortComparator(field);
    }

    @Override
    public String[] getSortFields() {
        return getSortOptions();
    }

    public static List<Product> filterByCategory(List<Product> products, String category) {
        if (category == null || category.equalsIgnoreCase("all")) {
            return products;
        }
        return products.stream()
            .filter(p -> p.getClass().getSimpleName().equalsIgnoreCase(category))
            .collect(Collectors.toList());
    }
}