package com.auction_system.database_system;

import java.sql.*;

/**
 * This class represent a SQL Utility class.
 */
public abstract class SqlUtility {

    /**
     * The connection string.
     */
    protected static final String LOCALHOST = "jdbc:mysql://localhost:3306/";

    /**
     * The default connection used for basic operations
     */
    protected static Connection defaultConn;

    static {
        try {
            defaultConn = connect("default", "default");
        } catch (SQLException throwable) {
            System.exit(0);
        }
    }

    /**
     * Empty constructor for this class.
     */
    protected SqlUtility() {
    }

    /**
     * Connects an user to the database (administrator or the default connection).
     * @param username the username
     * @param password the password
     * @return a connection to the database
     * @throws SQLException if there are any SQL errors
     */
    public static Connection connect(String username, String password) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(LOCALHOST, username, password);
    }

    /**
     * Checks if the client exists in the database.
     * @param username the username of the client
     * @return true if the client exists, false otherwise
     * @throws SQLException if there are any SQL errors
     */
    public static boolean checkIfClientExists(String username) throws SQLException {
        String query = "SELECT EXISTS(SELECT 1 FROM entities.client_table WHERE username = '" +
                username + "')";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return (rs.getInt(1) != 0);
        }
    }

    /**
     * Checks if the broker exists in the database.
     * @param username the username of the broker
     * @return true if the broker exists, false otherwise
     * @throws SQLException if there are any SQL errors
     */
    public static boolean checkIfBrokerExists(String username) throws SQLException {
        String query = "SELECT EXISTS(SELECT 1 FROM entities.broker_table WHERE username = '" +
                username + "')";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return (rs.getInt(1) != 0);
        }
    }
}
