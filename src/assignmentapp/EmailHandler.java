package assignmentapp;

import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EmailHandler {
    private static final String EMAIL_FROM = "rotanrontan@gmail.com"; //Email
    private static final String EMAIL_PASSWORD = "mwwa owks ztzi pkrj"; // App Password
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    static {
        // Set system properties for JavaMail
        System.setProperty("mail.mime.address.strict", "false");
        System.setProperty("mail.mime.encodeparameters", "false");
        System.setProperty("mail.mime.encodefilename", "false");
        // Suppress the specific resource warning
        System.setProperty("mail.mime.address.map", "false");
        // Disable JavaMail debug output
        System.setProperty("mail.debug", "false");
    }

    public static String generateOTP() {
        // Generate a 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.format("%06d", otp);
    }

    public static boolean sendOTPEmail(String recipientEmail, String otp) {
        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.debug", "false");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        // Add these properties to suppress the resource warning
        props.put("mail.mime.address.map", "false");
        props.put("mail.mime.address.strict", "false");

        // Create session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Your OTP for Registration");
            
            // Create email body
            String emailBody = String.format("""
                Hello,
                
                Your OTP for registration is: %s
                
                This OTP is valid for 5 minutes.
                
                If you did not request this OTP, please ignore this email.
                
                Best regards,
                Your Application Team""", otp);
            
            message.setText(emailBody);

            // Send message
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
            return false;
        }
    }

    public static boolean verifyOTP(String inputOTP, String generatedOTP) {
        return inputOTP.equals(generatedOTP);
    }
} 