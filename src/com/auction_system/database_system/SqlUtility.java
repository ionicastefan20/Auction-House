package com.auction_system.database_system;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class SqlUtility {
    protected static final String LOCALHOST = "jdbc:mysql://localhost:3306/";
    protected static Connection defaultConn;

    static {
        try {
            defaultConn = connect("default", "default");
        } catch (SQLException throwable) {
            System.exit(0);
        }
    }

    protected SqlUtility() {
    }

    public static Connection connect(String username, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(LOCALHOST, username, password);
    }
}
