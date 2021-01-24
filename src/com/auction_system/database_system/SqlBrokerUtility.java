package com.auction_system.database_system;

import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.WrongPasswordException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.auction_system.database_system.SqlEntityUtility.*;

public class SqlBrokerUtility extends SqlUtility {

    private SqlBrokerUtility() {}

    public static void registerBroker(Broker broker, String hash) throws SQLException, UserAlreadyExistsException {
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

    public static Broker loginBroker(String username, String hash) throws SQLException, WrongPasswordException {

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
