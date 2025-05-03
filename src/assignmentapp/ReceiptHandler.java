package assignmentapp;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.File;
import java.io.FileOutputStream;

public class ReceiptHandler {
    private static final String EMAIL_FROM = "rotanrontan@gmail.com"; // Same as EmailHandler
    private static final String EMAIL_PASSWORD = "mwwa owks ztzi pkrj"; // Same as EmailHandler
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    public static boolean generateAndSendReceipt(Order order, Customer customer) {
        try {
            // Generate PDF
            byte[] pdfBytes = generateReceiptPDF(order, customer);
            
            // Send email with PDF attachment
            return sendReceiptEmail(customer.getEmail(), pdfBytes, order);
        } catch (Exception e) {
            System.err.println("Error generating/sending receipt: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] generateReceiptPDF(Order order, Customer customer) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Add company logo/header
        Font headerFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph header = new Paragraph("Purchase Receipt", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(20);
        document.add(header);

        // Add order details
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
        Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);

        // Order information
        document.add(new Paragraph("Order ID: " + order.getOrderId(), normalFont));
        document.add(new Paragraph("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), normalFont));
        document.add(new Paragraph("Customer: " + customer.getUsername(), normalFont));
        document.add(new Paragraph("Email: " + customer.getEmail(), normalFont));
        document.add(new Paragraph("\n"));

        // Create table for items
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        
        // Add table headers
        table.addCell(new PdfPCell(new Phrase("Product", boldFont)));
        table.addCell(new PdfPCell(new Phrase("Quantity", boldFont)));
        table.addCell(new PdfPCell(new Phrase("Price", boldFont)));
        table.addCell(new PdfPCell(new Phrase("Total", boldFont)));

        // Add items
        for (OrderItem item : order.getOrderItems()) {
            table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(), normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("RM%.2f", item.getProduct().getPrice()), normalFont)));
            table.addCell(new PdfPCell(new Phrase(String.format("RM%.2f", item.getSubtotal()), normalFont)));
        }

        document.add(table);
        document.add(new Paragraph("\n"));

        // Add total
        Paragraph total = new Paragraph(String.format("Total Amount: RM%.2f", order.getTotalAmount()), boldFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        // Add footer
        Paragraph footer = new Paragraph("\nThank you for your purchase!", normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }

    private static boolean sendReceiptEmail(String recipientEmail, byte[] pdfBytes, Order order) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            // Create temporary file for PDF
            File tempFile = File.createTempFile("receipt_" + order.getOrderId(), ".pdf");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pdfBytes);
            }

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your Purchase Receipt - Order #" + order.getOrderId());

            // Create the message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Thank you for your purchase! Please find your receipt attached.");

            // Create the attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(tempFile);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("receipt_" + order.getOrderId() + ".pdf");

            // Create the multipart message
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);

            // Clean up temporary file
            tempFile.delete();
            return true;
        } catch (Exception e) {
            System.err.println("Error sending receipt email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 