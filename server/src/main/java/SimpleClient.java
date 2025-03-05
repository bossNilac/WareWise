import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SimpleClient {
    private static final String SERVER_ADDRESS = "localhost"; // Change if needed
    private static final int SERVER_PORT = 12345; // Change to match your server's port
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private String password="calin123";
    private String username="calin";

    public SimpleClient() {
        try {
            // Establish connection
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            System.out.println("Connected to the server.");

            // Start listening to responses from the server in a separate thread
            new Thread(this::listenToServer).start();

            // Run the authentication test
//            runAuthenticationTest();
            runUserManagementTest();
        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void runAuthenticationTest() {
        try {
            System.out.println("Sending HELLO command...");
            sendMessage("HELLO");

            Thread.sleep(1000); // Wait a bit to receive response

            System.out.print("Enter username for login: ");
            sendMessage("LOGIN~" + username+"~"+password);

            Thread.sleep(1000); // Wait a bit to receive response

            System.out.println("Testing invalid command...");
            sendMessage("INVALID_COMMAND");

            Thread.sleep(10000); // Wait a bit to receive response

            sendMessage("LOGOUT~" + username);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runUserManagementTest() {
        try {
            System.out.println("Sending HELLO command...");
            sendMessage("HELLO");

            Thread.sleep(1000); // Wait for server response

            System.out.println("Logging in...");
            sendMessage("LOGIN~calin~calin123");

            Thread.sleep(1000); // Wait for login response

            System.out.println("Testing ADD_USER...");
            sendMessage("ADD_USER~newUser1~ADMIN~newuser@example.com~password123");

            Thread.sleep(10000); // Wait for response

            System.out.println("Testing UPDATE_USER...");
            sendMessage("UPDATE_USER~newUser1~WORKER~updated2email@example.com~Newpassword");

            Thread.sleep(10000); // Wait for response


            Thread.sleep(1000); // Wait for response

            System.out.println("Testing LIST_USERS...");
            sendMessage("LIST_USERS");

            Thread.sleep(1000); // Wait for response

            System.out.println("Testing ADD_USER...");
            sendMessage("ADD_USER~newUser2~ADMIN~newuser2@example.com~password123");

            Thread.sleep(10000); // Wait for response

            System.out.println("Testing UPDATE_USER...");
            sendMessage("UPDATE_USER~newUser2~WORKER~updated3email@example.com~Newpassword");

            Thread.sleep(10000); // Wait for response

            System.out.println("Testing DELETE_USER...");
            sendMessage("DELETE_USER~newUser2");

            Thread.sleep(1000); // Wait for response

            System.out.println("Testing LIST_USERS...");
            sendMessage("LIST_USERS");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void sendMessage(String message) {
        System.out.println("Client: " + message);
        out.println(message);
    }

    private void listenToServer() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Server Response: " + response);
            }
        } catch (IOException e) {
            System.err.println("Connection lost: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new SimpleClient();
    }
}
