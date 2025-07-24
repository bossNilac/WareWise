package com.warewise.client.util.model;


import javafx.scene.layout.StackPane;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Log {
    private int ID;
    private String username;
    private String action;
    private String description;
    private String createdAt;

    public Log(int ID, String username, String action, String description, String createdAt) {
        this.ID = ID;
        this.username = username;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }
    public Log(String username, String action, String description, String createdAt) {
        this.username = username;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^" +
                    "(\\S+)\\s+" +                                           // username
                    "(GET|POST|PATCH|DELETE)\\s+" +                         // HTTP method
                    "(\\S+)\\s+" +                                          // path
                    "(" +
                    "(?:\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+)" +  // ISO with fraction
                    "|" +
                    "(?:\\d{2}-\\d{2}-\\d{4}\\s\\d{2}:\\d{2}:\\d{2})" +       // dd-MM-yyyy HH:mm:ss
                    ")" +
                    "$"
    );


    // Irregular plural forms
        private static final Map<String,String> PLURALS = Map.of(
                "inventory",    "inventories",
                "category",     "categories",
                "item",         "items",
                "order",        "orders",
                "stock_alert",  "stock_alerts",
                "user",         "users",
                "log",          "logs"
        );

        /**
         * Parses a raw line like:
         *
         *   manager1 GET /api/users/get_users 2025-07-18T16:40:57.522907800
         *
         * and returns:
         *
         *   manager1 saw users at 2025-07-18T16:40:57.522907800
         */
        public static String parseLine(String raw) {
            Matcher m = LOG_PATTERN.matcher(raw.trim());
            if (!m.find()) {
                throw new IllegalArgumentException("Cannot parse log line: " + raw);
            }
            String user     = m.group(1);
            String method   = m.group(2);
            String fullPath = m.group(3);
            String ts       = m.group(4);

            // Extract resource key (first segment after "/api/")
            String key = "resource";
            int idx = fullPath.indexOf("/api/");
            if (idx >= 0) {
                String after = fullPath.substring(idx + 5);
                String[] parts = after.split("/");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    key = parts[0];
                }
            }

            // Pluralize if needed
            String noun = PLURALS.getOrDefault(key, key);

            // Map HTTP verb to your custom action words
            String action;
            switch (method) {
                case "GET":    action = "saw";     break;
                case "POST":   action = "added";   break;
                case "PATCH":  action = "updated"; break;
                case "DELETE": action = "deleted"; break;
                default:       action = method.toLowerCase();
            }

            // Build the final sentence
            return String.format("%s %s %s at %s", user, action, noun, ts);
        }

    @Override
    public String toString() {
        return username+" "+action+" "+description+" "+createdAt;
    }

}
