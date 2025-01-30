# development.md

## 1. **Revised Requirements & Project Goals**

### 1.1 Project Scope
- **Warehouse Management App** with a **client-server architecture**:
    1. **Server Application**
        - Hosts the **Oracle SQL** database (locally or on-premise).
        - Runs a **Java server process** that handles requests from both Admin Console and Client GUI.
    2. **Admin Console**
        - **Text-Based UI (TUI)** and **GUI** options, both acting as clients.
        - Connects to the **server** to perform administrative commands (e.g., user management, backups).
        - Available only to users with **Admin** privileges.
    3. **Client Application (GUI)**
        - Used by **managers** and **workers**.
        - Connects to the **server** for daily warehouse tasks (inventory, order processing, basic reporting).

### 1.2 User Roles
1. **Admin** (System-Level Access)
    - Uses the **Admin Console** (TUI or GUI) to send commands to the **Server**.
    - Capabilities: database configuration, user roles/permissions, backups, and advanced settings.
2. **Manager** (Business-Level Access)
    - Uses the **Client GUI** (connected to the server).
    - Manages inventory, orders, and can access managerial-level reports.
3. **Worker** (Operational Access)
    - Uses the **Client GUI** (connected to the server).
    - Updates stock, processes orders, handles routine tasks.

### 1.3 Core Functionalities
- **Inventory Management** (create, update, delete products).
- **Order Processing** (initiate, track, fulfill, cancel orders).
- **Stock Alerts** (automated notifications for low levels).
- **Reporting & Analytics** (sales, inventory trends, order statistics).
- **User Management** (admin-only tasks, via the server).
- **Backup & Restore** (admin commands to the server, for database operations).
- **Security & Encryption** (user credential protection, optional data encryption).

---

## 2. **High-Level Architecture**

1. **Client-Server Model**
    - **Server**: A Java process that connects to Oracle SQL and exposes functionality to clients.
    - **Admin Console Clients**: TUI or GUI, connecting over a defined protocol (e.g., TCP sockets, RMI, or gRPC).
    - **Client GUI**: JavaFX desktop application for managers/workers.

2. **MVC Within Client UIs**
    - **Model**: Data representations and business logic (though main logic also resides server-side).
    - **View**: JavaFX (GUI) or Console I/O (TUI).
    - **Controller**: Handles user interactions, relays commands/queries to the **server**.

3. **Security & Access Control**
    - Role-based permissions enforced **both client-side** (UI restrictions) and **server-side** (actual authority checks).

---

## 3. **Development Stages**

### **Stage 1: Planning & Requirements Refinement**
1. Document **server-client** interactions:
    - What commands should the admin console be able to send?
    - How managers/workers interact with the server for daily tasks.
2. Update **UI flows** for TUI, Admin GUI, and Client GUI (Manager/Worker).
3. Define **database schema** and **communication protocol** (e.g., Java Sockets, RMI, or another RPC mechanism).

### **Stage 2: Architecture & Technology Choices**
1. Confirm **Java 17+** for all components.
2. **JavaFX** for the GUIs, console-based approach for the TUI.
3. **Server**: Java-based, possibly a simple custom socket server or a light framework for concurrency.
4. Oracle SQL for data storage.

### **Stage 3: Environment Setup**

1. **Install & configure the following dependencies:**
    - **Oracle SQL** (local server or on-prem VM).
    - **JDK + JavaFX** on development machines.
    - **JDBC driver** for Oracle.

2. **Create a multi-module or multi-project structure:**

```
warehouse-management-app/
├── server/             # Server-side application handling logic and database access
├── admin-console/      # TUI + optional GUI for administrative operations
├── client-gui/         # JavaFX-based GUI for Manager/Worker interactions
├── common-library/     # Shared models, utilities, and encryption modules
└── ...
```

### **Stage 4: Database Schema & Data Layer**
1. Define schema in Oracle SQL (`inventory`, `orders`, `users`, etc.).
2. Implement a **Data Access Layer** (DAL) or **DAO** classes within the **server** module.
3. Test queries with minimal unit tests.

### **Stage 5: Server-Side Development**
1. Create a **Java server** that listens for client requests:
- Admin commands (create user, backups, encryption settings).
- Manager/worker commands (inventory queries, order operations).
2. **Thread handling/concurrency**: handle multiple admin & client connections.
3. Incorporate **role-based logic** to restrict privileged operations.

### **Stage 6: Admin Console (TUI & GUI)**
1. **TUI**:
- Command-driven interface (e.g., `addUser`, `backupDB`, `setEncryptionKey`).
- Connects via sockets (or chosen protocol) to the **server**.
- Minimal local logic; it sends commands, receives results.
2. **Admin GUI**:
- JavaFX or Swing front-end for admins who prefer a graphical dashboard.
- Mirrors TUI functionality: sends requests to the same server endpoints.

### **Stage 7: Client GUI for Managers & Workers**
1. JavaFX application focusing on daily warehouse tasks:
- Inventory management UI (TableView for products, forms for updates).
- Order processing UI.
- Basic reports (charts, lists) for manager view.
2. Connect to the **server** over the same protocol, restricted by role.

### **Stage 8: Security & Encryption**
1. **Authentication** at the server:
- Store hashed/salted passwords (BCrypt).
- Validate user roles on each request.
2. **Encryption**:
- Optional field-level encryption (AES) for sensitive data in Oracle SQL.
- **TLS/SSL** for network connections (if needed for external or remote usage).

### **Stage 9: Reporting & Analytics**
1. **Server** compiles data from Oracle for analytics queries.
2. Client GUIs display results (charts, tables).
3. Admin console can retrieve system-wide or log-based reports.

### **Stage 10: Testing & Debugging**
1. **Unit tests** for server logic, TUI/GUI interactions.
2. **Integration tests** simulating multiple clients.
3. **Security tests** (role-based restrictions, penetration tests).

### **Stage 11: Deployment & Maintenance**
1. Package the **server** as an executable JAR or service:
- Runs on a dedicated machine with Oracle SQL.
2. Package **Admin Console** (TUI/GUI) and **Client GUI** as separate JARs for each user group.
3. Provide **user documentation**:
- Admin guide (commands, server config).
- Manager/Worker guide (daily operations).
4. **Logging & Monitoring**:
- Store logs on the server for queries and admin actions.
- Consider additional auditing in Oracle SQL.

### **Stage 12: Future Enhancements**
1. **Mobile or Web** front-end for managers/workers.
2. **Barcode scanning** integration to speed up stock updates.
3. **AI-based forecasting** for inventory predictions.
4. **Multi-warehouse** or multi-site support with advanced concurrency.

---