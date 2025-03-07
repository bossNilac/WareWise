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
            runAuthenticationTest();
//            runUserManagementTest();
//            runCategoryManagementTest();
//            runItemManagementTest();
//            runInventoryManagementTest();
//            runOrderManagementTest();
            runSupplierManagementTest();
            Thread.sleep(5000); // Wait a bit to receive response
            runStockAlertManagementTest();



            sendMessage("LOGOUT~" + username);
            socket.close();
            System.exit(0);
        } catch (IOException | InterruptedException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void runItemManagementTest() {
        try {
            System.out.println("Sending ADD_ITEM command...");
            sendMessage("ADD_ITEM~1~101~5~299.99~2001~10");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending UPDATE_ITEM command...");
            sendMessage("UPDATE_ITEM~1~101~10~279.99~2001~10");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_ITEMS command...");
            sendMessage("LIST_ITEMS");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending DELETE_ITEM command...");
            sendMessage("DELETE_ITEM~1");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_ITEMS command after deletion...");
            sendMessage("LIST_ITEMS");

            Thread.sleep(1000); // Wait for response

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runInventoryManagementTest() {
        try {
            System.out.println("Sending ADD_INVENTORY command...");
            sendMessage("ADD_INVENTORY~Warehouse A~General warehouse for products~500~2024-03-01 10:00:00");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending UPDATE_INVENTORY command...");
            sendMessage("UPDATE_INVENTORY~0~Warehouse B~Updated storage facility~600~2024-03-02 12:00:00");

            Thread.sleep(10000); // Wait for response

            System.out.println("Sending LIST_INVENTORY command...");
            sendMessage("LIST_INVENTORY");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending DELETE_INVENTORY command...");
            sendMessage("DELETE_INVENTORY~0");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_INVENTORY command after deletion...");
            sendMessage("LIST_INVENTORY");

            Thread.sleep(1000); // Wait for response

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runOrderManagementTest() {
        try {
            System.out.println("Sending CREATE_ORDER command...");
            sendMessage("CREATE_ORDER~John Doe~johndoe@example.com~PENDING~2024-03-01 10:00:00~2024-03-01 10:00:00");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending UPDATE_ORDER command...");
            sendMessage("UPDATE_ORDER~1~John Smith~johnsmith@example.com~FULFILLED~2024-03-02 12:00:00");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_ORDERS command...");
            sendMessage("LIST_ORDERS");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending DELETE_ORDER command...");
            sendMessage("DELETE_ORDER~1");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_ORDERS command after deletion...");
            sendMessage("LIST_ORDERS");

            Thread.sleep(1000); // Wait for response

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runSupplierManagementTest() {
        try {
            System.out.println("Sending ADD_SUPPLIER command...");
            sendMessage("ADD_SUPPLIER~Acme Corp~contact@acme.com~123456789~123 Main St");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending UPDATE_SUPPLIER command...");
            sendMessage("UPDATE_SUPPLIER~1~Acme Corp Ltd~newcontact@acme.com~987654321~456 Elm St");

            Thread.sleep(10000); // Wait for response

            System.out.println("Sending LIST_SUPPLIERS command...");
            sendMessage("LIST_SUPPLIERS");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending DELETE_SUPPLIER command...");
            sendMessage("DELETE_SUPPLIER~1");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_SUPPLIERS command after deletion...");
            sendMessage("LIST_SUPPLIERS");

            Thread.sleep(1000); // Wait for response

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runStockAlertManagementTest() {
        try {
            System.out.println("Sending STOCK_ALERT command...");
            sendMessage("STOCK_ALERT~5001~ACTIVE~2024-03-01 10:00:00");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending UPDATE_STOCK_ALERT command...");
            sendMessage("UPDATE_STOCK_ALERT~1~RESOLVED~2024-03-02 12:00:00");

            Thread.sleep(10000); // Wait for response

            System.out.println("Sending LIST_STOCK_ALERTS command...");
            sendMessage("LIST_STOCK_ALERTS");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending DELETE_STOCK_ALERT command...");
            sendMessage("DELETE_STOCK_ALERT~1");

            Thread.sleep(1000); // Wait for response

            System.out.println("Sending LIST_STOCK_ALERTS command after deletion...");
            sendMessage("LIST_STOCK_ALERTS");

            Thread.sleep(1000); // Wait for response

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private void runCategoryManagementTest() {
        try {
            System.out.println("Testing ADD_CATEGORY...");
            sendMessage("ADD_CATEGORY~Electronics~Devices and gadgets");

            Thread.sleep(1000); // Wait for response

            System.out.println("Testing UPDATE_CATEGORY...");
            sendMessage("UPDATE_CATEGORY~1~Electronics~Updated gadgets section");

            Thread.sleep(10000); // Wait for response

            System.out.println("Testing DELETE_CATEGORY...");
            sendMessage("DELETE_CATEGORY~1");

            Thread.sleep(1000); // Wait for response

            System.out.println("Testing LIST_CATEGORIES...");
            sendMessage("LIST_CATEGORIES");

            Thread.sleep(1000); // Wait for response

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void runUserManagementTest() {
        try {
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
