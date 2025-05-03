package assignmentapp;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.awt.Desktop;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PDFCreation {
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 14, Font.BOLD);
    private static final Font SUBHEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL);
    
    private static void ensureReportDirectoryExists() {
        File reportsDir = new File("reports");
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }
    
    private static void openPDF(String filename) {
        try {
            File file = new File(filename);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            System.out.println("Error opening PDF: " + e.getMessage());
        }
    }
    
    public static void generateOverallReport() {
        try {
            ensureReportDirectoryExists();
            Document document = new Document(PageSize.A4);
            String filename = "reports/overall_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            // Title
            Paragraph title = new Paragraph("Overall System Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Date
            Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Load data
            List<Account> accounts = JsonHandler.loadAccounts();
            List<Order> orders = JsonHandler.loadOrders();
            List<Product> products = JsonHandler.loadProducts();
            
            // User Statistics
            addSection(document, "User Statistics", HEADER_FONT);
            PdfPTable userTable = new PdfPTable(2);
            userTable.setWidthPercentage(100);
            userTable.addCell(createCell("Total Users", true));
            userTable.addCell(createCell(String.valueOf(accounts.size()), false));
            userTable.addCell(createCell("Total Customers", true));
            userTable.addCell(createCell(String.valueOf(accounts.stream().filter(a -> "customer".equals(a.getAccountType())).count()), false));
            userTable.addCell(createCell("Total Admins", true));
            userTable.addCell(createCell(String.valueOf(accounts.stream().filter(a -> "admin".equals(a.getAccountType())).count()), false));
            document.add(userTable);
            
            // Order Statistics
            addSection(document, "Order Statistics", HEADER_FONT);
            PdfPTable orderTable = new PdfPTable(2);
            orderTable.setWidthPercentage(100);
            orderTable.addCell(createCell("Total Orders", true));
            orderTable.addCell(createCell(String.valueOf(orders.size()), false));
            orderTable.addCell(createCell("Total Revenue", true));
            orderTable.addCell(createCell(String.format("RM %.2f", orders.stream().mapToDouble(Order::getTotalAmount).sum()), false));
            document.add(orderTable);
            
            // Product Statistics
            addSection(document, "Product Statistics", HEADER_FONT);
            PdfPTable productTable = new PdfPTable(2);
            productTable.setWidthPercentage(100);
            productTable.addCell(createCell("Total Products", true));
            productTable.addCell(createCell(String.valueOf(products.size()), false));
            productTable.addCell(createCell("Total Stock Value", true));
            productTable.addCell(createCell(String.format("RM %.2f", 
                products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum()), false));
            document.add(productTable);
            
            // Best Selling Products
            addSection(document, "Best Selling Products", HEADER_FONT);
            Map<String, Long> productSales = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                    item -> item.getProduct().getName(),
                    Collectors.summingLong(OrderItem::getQuantity)
                ));
            
            PdfPTable bestSellingTable = new PdfPTable(2);
            bestSellingTable.setWidthPercentage(100);
            bestSellingTable.addCell(createCell("Product", true));
            bestSellingTable.addCell(createCell("Units Sold", true));
            
            productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    bestSellingTable.addCell(createCell(entry.getKey(), false));
                    bestSellingTable.addCell(createCell(String.valueOf(entry.getValue()), false));
                });
            document.add(bestSellingTable);
            
            document.close();
            System.out.println("Overall report generated: " + filename);
            openPDF(filename);
            
        } catch (Exception e) {
            System.out.println("Error generating overall report: " + e.getMessage());
        }
    }
    
    public static void generateUsersReport() {
        try {
            ensureReportDirectoryExists();
            Document document = new Document(PageSize.A4);
            String filename = "reports/users_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            // Title
            Paragraph title = new Paragraph("Users Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Date
            Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Load data
            List<Account> accounts = JsonHandler.loadAccounts();
            
            // User Statistics
            addSection(document, "User Statistics", HEADER_FONT);
            PdfPTable statsTable = new PdfPTable(2);
            statsTable.setWidthPercentage(100);
            statsTable.addCell(createCell("Total Users", true));
            statsTable.addCell(createCell(String.valueOf(accounts.size()), false));
            statsTable.addCell(createCell("Total Customers", true));
            statsTable.addCell(createCell(String.valueOf(accounts.stream().filter(a -> "customer".equals(a.getAccountType())).count()), false));
            statsTable.addCell(createCell("Total Admins", true));
            statsTable.addCell(createCell(String.valueOf(accounts.stream().filter(a -> "admin".equals(a.getAccountType())).count()), false));
            document.add(statsTable);
            
            // User Details
            addSection(document, "User Details", HEADER_FONT);
            PdfPTable userTable = new PdfPTable(4);
            userTable.setWidthPercentage(100);
            userTable.addCell(createCell("Username", true));
            userTable.addCell(createCell("Email", true));
            userTable.addCell(createCell("Account Type", true));
            userTable.addCell(createCell("Role", true));
            
            for (Account account : accounts) {
                userTable.addCell(createCell(account.getUsername(), false));
                userTable.addCell(createCell(account.getEmail(), false));
                userTable.addCell(createCell(account.getAccountType(), false));
                userTable.addCell(createCell(account instanceof Admin ? ((Admin) account).getRole() : "N/A", false));
            }
            document.add(userTable);
            
            document.close();
            System.out.println("Users report generated: " + filename);
            openPDF(filename);
            
        } catch (Exception e) {
            System.out.println("Error generating users report: " + e.getMessage());
        }
    }
    
    public static void generateOrdersReport() {
        try {
            ensureReportDirectoryExists();
            Document document = new Document(PageSize.A4);
            String filename = "reports/orders_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            // Title
            Paragraph title = new Paragraph("Orders Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Date
            Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Load data
            List<Order> orders = JsonHandler.loadOrders();
            
            // Order Statistics
            addSection(document, "Order Statistics", HEADER_FONT);
            PdfPTable statsTable = new PdfPTable(2);
            statsTable.setWidthPercentage(100);
            statsTable.addCell(createCell("Total Orders", true));
            statsTable.addCell(createCell(String.valueOf(orders.size()), false));
            statsTable.addCell(createCell("Total Revenue", true));
            statsTable.addCell(createCell(String.format("RM %.2f", orders.stream().mapToDouble(Order::getTotalAmount).sum()), false));
            document.add(statsTable);
            
            // Order Details
            addSection(document, "Order Details", HEADER_FONT);
            for (Order order : orders) {
                Paragraph orderHeader = new Paragraph("Order #" + order.getOrderId(), SUBHEADER_FONT);
                orderHeader.setSpacingBefore(10);
                orderHeader.setSpacingAfter(5);
                document.add(orderHeader);
                
                PdfPTable orderTable = new PdfPTable(5);
                orderTable.setWidthPercentage(100);
                orderTable.addCell(createCell("Product", true));
                orderTable.addCell(createCell("Quantity", true));
                orderTable.addCell(createCell("Price", true));
                orderTable.addCell(createCell("Subtotal", true));
                orderTable.addCell(createCell("Status", true));
                
                for (OrderItem item : order.getOrderItems()) {
                    orderTable.addCell(createCell(item.getProduct().getName(), false));
                    orderTable.addCell(createCell(String.valueOf(item.getQuantity()), false));
                    orderTable.addCell(createCell(String.format("RM %.2f", item.getProduct().getPrice()), false));
                    orderTable.addCell(createCell(String.format("RM %.2f", item.getSubtotal()), false));
                    orderTable.addCell(createCell(order.getShippingStatus(), false));
                }
                document.add(orderTable);
                
                Paragraph orderTotal = new Paragraph("Total Amount: RM " + String.format("%.2f", order.getTotalAmount()), NORMAL_FONT);
                orderTotal.setAlignment(Element.ALIGN_RIGHT);
                orderTotal.setSpacingAfter(10);
                document.add(orderTotal);
            }
            
            document.close();
            System.out.println("Orders report generated: " + filename);
            openPDF(filename);
            
        } catch (Exception e) {
            System.out.println("Error generating orders report: " + e.getMessage());
        }
    }
    
    public static void generateProductsReport() {
        try {
            ensureReportDirectoryExists();
            Document document = new Document(PageSize.A4);
            String filename = "reports/products_report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            // Title
            Paragraph title = new Paragraph("Products Report", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            // Date
            Paragraph date = new Paragraph("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), NORMAL_FONT);
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setSpacingAfter(20);
            document.add(date);
            
            // Load data
            List<Product> products = JsonHandler.loadProducts();
            List<Order> orders = JsonHandler.loadOrders();
            
            // Product Statistics
            addSection(document, "Product Statistics", HEADER_FONT);
            PdfPTable statsTable = new PdfPTable(2);
            statsTable.setWidthPercentage(100);
            statsTable.addCell(createCell("Total Products", true));
            statsTable.addCell(createCell(String.valueOf(products.size()), false));
            statsTable.addCell(createCell("Total Stock Value", true));
            statsTable.addCell(createCell(String.format("RM %.2f", 
                products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum()), false));
            document.add(statsTable);
            
            // Product Details
            addSection(document, "Product Details", HEADER_FONT);
            PdfPTable productTable = new PdfPTable(5);
            productTable.setWidthPercentage(100);
            productTable.addCell(createCell("ID", true));
            productTable.addCell(createCell("Name", true));
            productTable.addCell(createCell("Category", true));
            productTable.addCell(createCell("Price", true));
            productTable.addCell(createCell("Stock", true));
            
            for (Product product : products) {
                productTable.addCell(createCell(String.valueOf(product.getProductID()), false));
                productTable.addCell(createCell(product.getName(), false));
                productTable.addCell(createCell(product.getProductType(), false));
                productTable.addCell(createCell(String.format("RM %.2f", product.getPrice()), false));
                productTable.addCell(createCell(String.valueOf(product.getStock()), false));
            }
            document.add(productTable);
            
            // Best Selling Products
            addSection(document, "Best Selling Products", HEADER_FONT);
            Map<String, Long> productSales = orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                    item -> item.getProduct().getName(),
                    Collectors.summingLong(OrderItem::getQuantity)
                ));
            
            PdfPTable bestSellingTable = new PdfPTable(2);
            bestSellingTable.setWidthPercentage(100);
            bestSellingTable.addCell(createCell("Product", true));
            bestSellingTable.addCell(createCell("Units Sold", true));
            
            productSales.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(entry -> {
                    bestSellingTable.addCell(createCell(entry.getKey(), false));
                    bestSellingTable.addCell(createCell(String.valueOf(entry.getValue()), false));
                });
            document.add(bestSellingTable);
            
            document.close();
            System.out.println("Products report generated: " + filename);
            openPDF(filename);
            
        } catch (Exception e) {
            System.out.println("Error generating products report: " + e.getMessage());
        }
    }
    
    private static void addSection(Document document, String title, Font font) throws DocumentException {
        Paragraph section = new Paragraph(title, font);
        section.setSpacingBefore(15);
        section.setSpacingAfter(10);
        document.add(section);
    }
    
    private static PdfPCell createCell(String text, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isHeader ? HEADER_FONT : NORMAL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }
} 