package com.auction_system.database_system;

import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.MyException;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.WrongPasswordException;
import lombok.experimental.UtilityClass;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a SQL Utility class for a broker.
 */
@UtilityClass
public class SqlBrokerUtility extends SqlUtility {

    /**
     * Registers a new broker to the <code>AuctionHouse</code>.<br>
     * The broker's credentials and data are saved in the database.
     * @param broker the broker to be registered
     * @param hash the hash of the password of the broker
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    public void registerBroker(Broker broker, String hash) throws MyException, SQLException {
        String username = broker.getUsername();
        if (checkIfClientExists(username) || checkIfBrokerExists(username))
            throw new UserAlreadyExistsException(username);

        String query = "INSERT INTO entities.broker_table(username,password) " +
                "VALUES(?,?);";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, hash);

            ps.execute();
        }
    }

    /**
     * Logs in a broker the <code>AuctionHouse</code>.<br>
     * The broker's credentials and data are loaded from the database.
     * @param username the username of the broker
     * @param hash the hash of the password of the broker
     * @return the <code>Broker</code> object obtained by logging in
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    public Broker loginBroker(String username, String hash) throws MyException, SQLException {
        String query = "SELECT * FROM entities.broker_table WHERE username = '" + username + "' " +
                "AND password = '" + hash + "';";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) throw new WrongPasswordException();

            return new Broker(username);
        }
    }
}
