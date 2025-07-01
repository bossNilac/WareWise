package warewise.server.api;

import jakarta.ejb.Stateless;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import warewise.server.common.model.User;

import java.util.Properties;

/**
 * Service class responsible for creating notifications
 * and sending email alerts to system owners about new vulnerabilities.
 */
@Stateless
public class NotificationService {

    /**
     * Sends a welcome email to a new user with their account details.
     *
     * @param user the new user who will receive the email
     * @param password the generated password for the user
     */
    public static void notifyNewAccount(User user, String password) {
        String to = user.getEmail();
        String from = "eMagizDummyTest@gmail.com";
        String host = "smtp.gmail.com";
        final String username = "calin.baculescu@gmail.com";
        final String smtpPassword = "mlqv lelt brim pofo"; // Use app password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new jakarta.mail.PasswordAuthentication(username, smtpPassword);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("Welcome to eMagiz Security Platform");

            StringBuilder body = new StringBuilder();
            body.append("<html><body>");
            body.append("<p>Dear ").append(user.getUsername()).append(",</p>");
            body.append("<p>Welcome to the eMagiz Security Platform!</p>");
            body.append("<p>An account has been created for you. Below are your login details:</p>");
            body.append("<ul>");
            body.append("<li><strong>Username:</strong> ").append(user.getUsername()).append("</li>");
            body.append("<li><strong>Password:</strong> ").append(password).append("</li>");
            body.append("<li><strong>Role:</strong> ").append(user.getRole()).append("</li>");
            body.append("</ul>");
            body.append("<p>Please log in as soon as possible and change your password.</p>");
            body.append("<p>You can access the platform here: ")
                    .append("<a href=\"https://emagiz2.paas.hosted-by-previder.com/login\">Click here to log in</a>")
                    .append("</p>");
            body.append("<p>If you did not expect this email, please contact our support team immediately.</p>");
            body.append("<p>Kind regards,<br/>eMagiz Security Platform</p>");
            body.append("</body></html>");


            message.setContent(body.toString(), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("New account email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }



}
