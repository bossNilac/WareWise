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
            body.append("<p>An account has been created/updated for you. Below are your login details:</p>");
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

    /**
     * Sends a notification email to the user about a recent login activity
     * and provides a link to reset their password if the login was not them.
     *
     * @param user the user who logged in
     * @param resetLink the link to reset their password
     */
    public static void notifyLoginActivity(User user, String resetLink) {
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
            message.setSubject("Login Notification - eMagiz Security Platform");

            StringBuilder body = new StringBuilder();
            body.append("<html><body>");
            body.append("<p>Dear ").append(user.getUsername()).append(",</p>");
            body.append("<p>We noticed a login to your account. If this was you, no further action is needed.</p>");
            body.append("<p>If you did not log in, please reset your password immediately to secure your account.</p>");
            body.append("<p>You can reset your password here: ")
                    .append("<a href=\"").append(resetLink).append("\">Click here to reset your password</a>")
                    .append("</p>");
            body.append("<p>If you have any questions, please contact our support team.</p>");
            body.append("<p>Kind regards,<br/>eMagiz Security Platform</p>");
            body.append("</body></html>");

            message.setContent(body.toString(), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Login activity email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email to the user with a link to reset their password.
     *
     * @param user the user who requested a password reset
     * @param resetLink the secure link for resetting the password
     */
    public static void notifyForgotPassword(User user, String resetLink) {
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
            message.setSubject("Password Reset Request - eMagiz Security Platform");

            StringBuilder body = new StringBuilder();
            body.append("<html><body>");
            body.append("<p>Dear ").append(user.getUsername()).append(",</p>");
            body.append("<p>We received a request to reset your password.</p>");
            body.append("<p>If you did not request this, please ignore this email. Otherwise, you can reset your password using the link below:</p>");
            body.append("<p><a href=\"").append(resetLink).append("\">Click here to reset your password</a></p>");
            body.append("<p>This link will expire shortly for your security.</p>");
            body.append("<p>If you have any questions, please contact our support team.</p>");
            body.append("<p>Kind regards,<br/>eMagiz Security Platform</p>");
            body.append("</body></html>");

            message.setContent(body.toString(), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Password reset email sent successfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }




}
