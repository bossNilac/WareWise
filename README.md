# WareWise

WareWise is a multi-interface Warehouse Management System built using Java, JavaFX, Jakarta EE, React Native, Vue.js, and PostgreSQL. It offers both text-based (CLI/TUI) and graphical (GUI) admin consoles, as well as dedicated client interfaces for managers and workers on desktop, mobile, and web. This robust architecture ensures efficient warehouse operations while centralizing all data and business logic within a Java (Jakarta EE) server connected to a PostgreSQL database.

## Key Features

### 1. Server-Client Architecture
- A standalone Java server using **Jakarta EE** hosts the PostgreSQL database and handles requests from admin and client applications across platforms.

### 2. Admin Console (CLI/TUI & GUI)
- Java-based Text-Based UI (**CLI/TUI**) for quick, command-driven operations, such as user management.
- **JavaFX-based GUI dashboard** for comprehensive logs and system monitoring.

### 3. Client Interface (Manager & Worker)
- **JavaFX desktop application** for daily tasks including inventory management, order processing, and basic reporting.
- **React Native mobile app** for managers and workers, supporting barcode scanning and mobile-specific workflows.
- Upcoming **Vue.js web application** for responsive, browser-based access, enhancing flexibility and accessibility.
- Role-based UIs ensure users access only authorized features based on their roles (Worker or Manager).

### 4. PostgreSQL Backend
- Secure storage of all data in **PostgreSQL**.
- Communication facilitated via **JDBC**, supporting optional AES-256 field-level encryption.

### 5. Security & Encryption
- **Role-Based Access Control (RBAC)**: Admin, Manager, Worker.
- Password hashing using **Argon2** for robust security.
- Optional AES-256 encryption for sensitive database fields.

### 6. Reporting & Analytics
- Real-time visibility into stock levels, order statuses, and sales trends.
- Visualization through intuitive **JavaFX charts** in desktop clients and planned **Vue.js web dashboards**.

## Strategic Technology Choices

- **Jakarta EE** ensures enterprise-grade reliability, scalability, and maintainability.
- **JavaFX** provides a native, performant desktop experience.
- **React Native** allows efficient cross-platform mobile app development.
- **Vue.js** offers rapid and flexible development for web interfaces, complementing desktop and mobile user experiences.
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
├──build.gradle
└── README.md              # This file
```

---

## **Getting Started**

### **Prerequisites**

1. **Java 17+** installed.
2. **Postgresql** database (embedded or file-based).
3. **JDBC Driver** for Postgresql (`postgresql-jdbc`).
4. **Maven or Gradle** for building the project.

### **Installation & Configuration**

**Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/warewise.git
   ```

## **Set Up Postgresql**

1. **Ensure Postgresql is installed** .
2. **Run the provided scripts** (if any) to create necessary tables (e.g., `inventory`, `orders`, `users`).



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

- **Argon2** for password hashing ensures **no plaintext passwords** in the database.
- **Role-Based Access Control (RBAC)** enforces **strict permissions** for each role.
- **Server-Side Authentication** validates **credentials** for every request from the **TUI** or **GUI** clients.

---

## **Contributing**

1. **Fork the Repository**
2. **Create a Feature Branch** (`feat/new-feature`)
3. **Commit Changes**

---

## **License**

This project is licensed under the **MIT License** — feel free to modify and use as needed.

---

## **About WareWise**

**WareWise** — Streamline your warehouse operations with a **secure, role-based, and easily extensible management solution**.
