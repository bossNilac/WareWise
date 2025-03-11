package com.warewise.common.handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_PATH = System.getProperty("user.home") + "/WareWise.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
//    private static final String DB_URL = "jdbc:sqlite:C:\\Users\\Calin\\IdeaProjects\\WareWise\\server\\WareWise.db";

    public static Connection getConnection() throws SQLException {
        System.out.println(DB_URL);
        return DriverManager.getConnection(DB_URL);
    }
}
