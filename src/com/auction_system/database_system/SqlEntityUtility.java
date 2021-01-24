package com.auction_system.database_system;

import com.auction_system.entities.clients.Client;
import com.auction_system.entities.clients.Individual;
import com.auction_system.entities.clients.LegalEntity;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.WrongPasswordException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlEntityUtility extends SqlUtility {

    private SqlEntityUtility() {
    }

    public static boolean checkIfClientExists(String username) throws SQLException {

        String query = "SELECT EXISTS(SELECT 1 FROM entities.client_table WHERE username = '" +
                username + "')";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return (rs.getInt(1) != 0);
        }
    }

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
