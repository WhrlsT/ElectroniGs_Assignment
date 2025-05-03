package assignmentapp;

public class Processor extends Product {

    public String manufacturer; // e.g., Intel, AMD
    public int coreCount;
    public double baseClockSpeed; // in GHz
    public String socketType; // Must match Motherboard socket type
    public boolean integratedGraphics;

    // Add this public no-args constructor
    public Processor() {
        super(); // Call the no-args constructor of Product
        this.manufacturer = null;
        this.coreCount = 0;
        this.baseClockSpeed = 0.0;
        this.socketType = null;
        this.integratedGraphics = false;
    }

    public Processor(String productName, double price, int stock,
                     String manufacturer, int coreCount, double baseClockSpeed, String socketType, boolean integratedGraphics) {
        super(productName, price, stock, "Processor"); // Call Product constructor
        this.manufacturer = manufacturer;
        this.coreCount = coreCount;
        this.baseClockSpeed = baseClockSpeed;
        this.socketType = socketType;
        this.integratedGraphics = integratedGraphics;
    }

    // Getters for Processor specific fields
    public String getManufacturer() {
        return manufacturer;
    }

    public int getCoreCount() {
        return coreCount;
    }

    public double getBaseClockSpeed() {
        return baseClockSpeed;
    }

    public String getSocketType() {
        return socketType;
    }

    public boolean hasIntegratedGraphics() {
        return integratedGraphics;
    }

    @Override
    public String toString() {
        return "Processor{" +
               "productID='" + productID + '\'' +
               ", productName='" + productName + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               ", manufacturer='" + manufacturer + '\'' +
               ", coreCount=" + coreCount +
               ", baseClockSpeed=" + baseClockSpeed + "GHz" +
               ", socketType='" + socketType + '\'' +
               ", integratedGraphics=" + integratedGraphics +
               '}';
    }
}