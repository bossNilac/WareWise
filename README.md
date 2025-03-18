# WareWise

**WareWise** is a **multi-interface Warehouse Management System** built on Java, JavaFX, and SQLite. It offers both **text-based (TUI) and graphical (GUI) admin consoles**, as well as a **dedicated client GUI** for managers and workers. This architecture ensures efficient warehouse operations while centralizing all data and business logic in a **Java server** connected to an **SQLite** database.

---

## **Key Features**

1. **Server-Client Architecture**
    - A standalone **Java server** hosts the SQLite database and handles requests from admin and client applications.

2. **Admin Console (TUI & GUI)**
    - **Text-Based UI** for quick, command-driven operations (user management, backups, etc.).
    - **Admin GUI** (JavaFX) with a graphical dashboard for system configuration, logs, and monitoring.

3. **Client GUI (Manager & Worker)**
    - **JavaFX** application for daily tasks: inventory management, order processing, and basic reporting.
    - **Role-based UI** restricts or enables features depending on whether the user is a Worker or Manager.

4. **SQLite Backend**
    - All data is stored securely in **SQLite**.
    - Uses **JDBC** for communication and supports optional **field-level encryption** (AES).

5. **Security & Encryption**
    - **Role-Based Access Control (RBAC)**: Admin, Manager, Worker.
    - **Password Hashing**:Argon2.
    - **Optional Data Encryption**: AES-256 for sensitive fields.

6. **Backup & Restore**
    - Admin commands to back up or restore the SQLite database.
    - Securely stored on the server side to prevent unauthorized access.

7. **Reporting & Analytics**
    - Real-time stock levels, order statuses, and sales trends.
    - JavaFX charts in the Client and Admin GUI for easy visualization.

---

## **Project Structure**

```
warewise/
├── server/
│   ├── src/
│   │   └── ...            # Java server code, SQLite connectivity, business logic
├── admin-console/
│   ├── tui/
│   │   └── ...            # Text-based UI (commands, console interactions)
│   └── gui/
│       └── ...            # Optional JavaFX or Swing GUI for the admin console
├── client-gui/
│   └── src/
│       └── ...            # JavaFX app for managers/workers
├── common-library/
│   └── ...                # Shared models, utilities, encryption libraries
├── pom.xml (or build.gradle)
└── README.md              # This file
```

---

## **Getting Started**

### **Prerequisites**

1. **Java 17+** installed.
2. **SQLite** database (embedded or file-based).
3. **JDBC Driver** for SQLite (`sqlite-jdbc`).
4. **Maven or Gradle** for building the project.

### **Installation & Configuration**

**Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/warewise.git
   ```

## **Set Up SQLite**

1. **Ensure SQLite is installed** (or use an embedded version).
2. **Run the provided scripts** (if any) to create necessary tables (e.g., `inventory`, `orders`, `users`).

## **Configure Database Connection**

In the `application.properties` or a similar config file, update the database connection settings:

```properties
db.url=jdbc:sqlite:warewise.db
db.username=
db.password=
```

## **Build the Project**

Using **Maven**:
```bash
mvn clean install
```

Using **Gradle**:
```bash
gradle build
```

## **Running the Applications**

### **Server**

Launch the **server JAR** or run from your IDE:

```bash
java -jar server/target/warewise-server.jar
```

Ensures it can connect to **SQLite** on startup.

### **Admin Console (TUI)**

Start the **TUI client**:

```bash
java -jar admin-console/tui/target/warewise-admin-tui.jar
```

Type `help` (or similar command) to see available admin commands.

### **Admin Console (GUI) (Optional)**

Run the **Admin GUI**:

```bash
java -jar admin-console/gui/target/warewise-admin-gui.jar
```

### **Client GUI**

For **managers/workers**, launch the **Client GUI**:

```bash
java -jar client-gui/target/warewise-client.jar
```

---

## **Usage Overview**

### **Admin**
- Log in using **admin credentials**.
- Manage **users, backups, and encryption settings**.
- Access **advanced logs** and system monitoring.

### **Manager**
- Track and manage **inventory, orders**, and generate **warehouse reports**.
- Accessible via the **Client GUI** with **manager-level privileges**.

### **Worker**
- Perform daily warehouse tasks: **scanning items, updating stock, fulfilling orders**.
- Uses a **simplified Client GUI** interface.

---

## **Security Highlights**

- **BCrypt** for password hashing ensures **no plaintext passwords** in the database.
- **AES-256 encryption** (optional) secures **highly sensitive fields**.
- **Role-Based Access Control (RBAC)** enforces **strict permissions** for each role.
- **Server-Side Authentication** validates **credentials** for every request from the **TUI** or **GUI** clients.

---

## **Contributing**

1. **Fork the Repository**
2. **Create a Feature Branch** (`feat/new-feature`)
3. **Commit Changes**
4. **Open a Pull Request** against the `develop` or `main` branch.

---

## **License**

This project is licensed under the **MIT License** — feel free to modify and use as needed.

---

## **About WareWise**

**WareWise** — Streamline your warehouse operations with a **secure, role-based, and easily extensible management solution**.
