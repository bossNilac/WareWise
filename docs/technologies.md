# technologies.md

Below is an updated overview of the **technologies** and **infrastructure** for the **Warehouse Management App** featuring a **client-server architecture** with Oracle SQL.

---

## 1. **Server Application**

- **Java 17+**
    - Core language for the server; ensures long-term support and compatibility with libraries.

- **Communication Protocol**
    - **Custom TCP Sockets**, **Java RMI**, or **gRPC** (developer’s choice).
    - Handles incoming requests from both the Admin Console and Client GUI.

- **Oracle SQL Database**
    - Hosted on the **same machine** or a local network server for a “standalone” deployment.
    - The server process interacts with Oracle SQL via **JDBC**.

- **Threading / Concurrency**
    - The server must handle multiple admin and client connections simultaneously.
    - **Java concurrency** tools (Executors, ThreadPools).

---

## 2. **Admin Console (TUI & GUI)**

1. **TUI**
    - **Standalone Java application** that connects to the server.
    - Provides a command-based interface for admin operations (user mgmt, backups, encryption settings).

2. **Admin GUI** (Optional)
    - **JavaFX** (or Swing) for a more visual approach to admin tasks.
    - Connects to the same server endpoints as the TUI.
    - Useful for monitoring, advanced logs, or configuration screens.

3. **Core Features**
    - Role & User Management (create/update/delete users)
    - Backup/Restore (trigger DB backups)
    - Encryption & Security Config (manage keys, encryption policies)
    - System Logs & Audits

---

## 3. **Client GUI (Manager & Worker)**

- **JavaFX**
    - Rich UI for daily warehouse tasks (inventory, orders, reports).
- **Stand-alone Application**
    - Each manager/worker runs their own local JavaFX client.
    - Connects to the server using the same communication protocol (TCP, RMI, gRPC, etc.).
- **Role-based UI**
    - **Worker**: Operational tasks (scan inventory, update stock).
    - **Manager**: Includes worker privileges plus advanced reporting.

---

## 4. **Database & Data Layer**

- **Oracle SQL**
    - Robust, enterprise-grade DB for reliable storage.
    - Locally installed (on the same machine as the server) or on a dedicated on-prem server.
- **Oracle JDBC Driver**
    - Provides connectivity to the database from the server’s data-access layer (DAOs or repository classes).

---

## 5. **Security & Encryption**

1. **Password Hashing**
    - **BCrypt** (or Argon2) for storing user credentials securely in the DB.
2. **Data Encryption** (Optional)
    - **AES-256** for fields containing sensitive data.
    - Performed on the **server side** before writing to Oracle SQL.
3. **Network Encryption**
    - **TLS/SSL** can be used if the server and clients communicate over untrusted networks.

---

## 6. **Build & Dependency Management**

- **Maven or Gradle**
    - Each module (server, admin console, client GUI) can be managed as sub-projects.
    - Dependencies: JavaFX SDK, Oracle JDBC driver, any encryption libraries (e.g., Bouncy Castle).

---

## 7. **Logging & Monitoring**

- **Server-Side Logging**
    - Use **Log4j** or **Java Util Logging** to capture server events, admin commands, and errors.
    - Store logs locally or in the DB for auditing.
- **Database Auditing** (Optional)
    - Oracle’s built-in auditing features for deeper compliance needs.

---

## 8. **Deployment Overview**

- **Server**:
    - Packaged as a runnable JAR or installed as a Windows/Linux service.
    - Runs continuously, listening for client and admin console connections.
    - Local or on-prem hosting for a “standalone” environment.

- **Admin Console (TUI/GUI)**:
    - Packaged separately. The admin runs this application, which connects to the server for commands.

- **Client GUI**:
    - Distributed to managers/workers.
    - Each instance connects to the server using provided credentials.

---

## 9. **Summary of the Tech Stack**

| Layer                  | Technology                         |
|------------------------|------------------------------------|
| **Language**           | Java 17+                           |
| **Server Framework**   | Custom Socket/RMI/gRPC (developer’s choice) |
| **Client UI**          | JavaFX (GUI); Console (TUI) for Admin |
| **Database**           | Oracle SQL                         |
| **DB Connectivity**    | Oracle JDBC Driver (ojdbc8/11)     |
| **Password Security**  | BCrypt (password hashing)          |
| **Data Encryption**    | AES-256 (optional, via `javax.crypto` or Bouncy Castle) |
| **Build**              | Maven or Gradle                    |
| **Testing**            | JUnit / TestNG                     |
| **Logging**            | Log4j / Java Util Logging          |

With this **client-server** architecture, your Warehouse Management App remains **standalone** (under your control), while providing a **centralized server** for all admin and client operations. The **Admin Console** (TUI/GUI) and **Client GUI** connect to the **server**, which handles the **Oracle SQL** database and enforces **security** and **role-based rules**.
