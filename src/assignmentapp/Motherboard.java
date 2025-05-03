package assignmentapp;

public class Motherboard extends Product {

    public String formFactor; // e.g., ATX, Micro-ATX, ITX
    public String chipset;
    public String cpuSocketType;
    public int ramSlots;
    public String network; // e.g., "Gigabit Ethernet", "2.5Gb Ethernet", "WiFi 6"

    // Add this public no-args constructor
    public Motherboard() {
        super(); // Call the no-args constructor of Product
        this.formFactor = null;
        this.chipset = null;
        this.cpuSocketType = null;
        this.ramSlots = 0;
        this.network = null;
    }

    public Motherboard(String productName, double price, int stock,
                       String formFactor, String chipset, String cpuSocketType, int ramSlots, String network) {
        super(productName, price, stock, "Motherboard"); // Call Product constructor
        this.formFactor = formFactor;
        this.chipset = chipset;
        this.cpuSocketType = cpuSocketType;
        this.ramSlots = ramSlots;
        this.network = network;
    }

    // Getters for Motherboard specific fields
    public String getFormFactor() {
        return formFactor;
    }

    public String getChipset() {
        return chipset;
    }

    public String getCpuSocketType() {
        return cpuSocketType;
    }

    public int getRamSlots() {
        return ramSlots;
    }

    public String getNetwork() {
        return network;
    }

    // Optional: Setters for specific fields

    @Override
    public String toString() {
        return "Motherboard{" +
               "productID='" + productID + '\'' +
               ", productName='" + productName + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               ", formFactor='" + formFactor + '\'' +
               ", chipset='" + chipset + '\'' +
               ", cpuSocketType='" + cpuSocketType + '\'' +
               ", ramSlots=" + ramSlots +
               ", network='" + network + '\'' +
               '}';
    }
}