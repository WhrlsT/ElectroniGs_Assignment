package assignmentapp;

public class GraphicCard extends Product {

    public String modelName;
    public int vramSize; // in GB
    public double coreClockSpeed; // in GHz
    public int cores; 
    public String manufacturer;

    // No Args Constructor for creating a new GraphicCard
    public GraphicCard() {
        super(); // Call the no-args constructor of Product
        this.modelName = null;
        this.vramSize = 0;
        this.coreClockSpeed = 0.0;
        this.cores = 0;
        this.manufacturer = null;
        this.productType = "graphiccard"; 
    }

    // Constructor for creating a new GraphicCard
    public GraphicCard(String productName, double price, int stock,
                       String modelName, int vramSize, double coreClockSpeed, int cores, String manufacturer) {
        super(productName, price, stock, "graphiccard"); // Call Product constructor
        this.modelName = modelName;
        this.vramSize = vramSize;
        this.coreClockSpeed = coreClockSpeed;
        this.cores = cores;
        this.manufacturer = manufacturer;
    }

    // Getters for GraphicCard specific fields
    public String getModelName() {
        return modelName;
    }

    public int getVramSize() {
        return vramSize;
    }

    public double getCoreClockSpeed() {
        return coreClockSpeed;
    }

    public int getCores() {
        return cores;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return "GraphicCard{" +
               "productID='" + productID + '\'' +
               ", productName='" + productName + '\'' +
               ", price=" + price +
               ", stock=" + stock +
               ", modelName='" + modelName + '\'' +
               ", vramSize=" + vramSize + "GB" +
               ", coreClockSpeed=" + coreClockSpeed + "GHz" +
               ", cores=" + cores +
               ", manufacturer='" + manufacturer + '\'' +
               '}';
    }
}