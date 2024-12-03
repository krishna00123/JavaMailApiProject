package org.backend.vrv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Random;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/sendEmail")
public class SendEmailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vrv?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "admin";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve form data
        String recipient = request.getParameter("recipient");
        String subject = request.getParameter("subject");
        //String messageBody = request.getParameter("message");

        // Validate email in database
        if (!isEmailExists(recipient)) {
            response.getWriter().write("The email does not exist in the database.");
            return;
        }

        // Generate a unique reset code
        String resetCode = generateResetCode();

        // SMTP Configuration
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "your_gmail.com";  // Your email address
        String password = "your_gmail_password";             // Your Gmail App password

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Session creation with email and password authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create and send email with reset code
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            
            // Create the email body with the reset code
            String emailBody = "Hello,\n\n" +
                               "To reset your password, please use the following reset code:\n" +
                               resetCode + "\n\n" +
                               "If you did not request this, please ignore this email.";
            

            message.setText(emailBody);

            // Send the email
            Transport.send(message);

            // Respond to client
            response.getWriter().write("Reset code sent successfully!");

        } catch (MessagingException | ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().write("Failed to send email: " + e.getMessage());
        }
    }

    // Helper method to check if an email exists in the database
    private boolean isEmailExists(String email) {
        boolean exists = false;

        try {
            // Explicitly load the MySQL JDBC driver class
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection to the database
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT 1 FROM users WHERE email = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, email);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        exists = resultSet.next();  // If any result is found, the email exists
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }

    // Helper method to generate a reset code
    private String generateResetCode() {
        // Generate a random 6-digit code
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // Generates a 6-digit number
        return String.valueOf(code);
    }
}
