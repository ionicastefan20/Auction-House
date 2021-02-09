package com.auction_system.database_system;

import com.auction_system.entities.clients.Client;
import com.auction_system.entities.clients.Individual;
import com.auction_system.entities.clients.LegalEntity;
import com.auction_system.exceptions.MyException;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.WrongPasswordException;
import lombok.experimental.UtilityClass;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class represents a SQL Utility class for a client.
 */
@UtilityClass
public class SqlClientUtility extends SqlUtility {

    /**
     * Returns the ID of the client with the specified username.
     * @param username the username of the client
     * @return the ID
     * @throws SQLException if there are any SQL errors
     */
    private int getClientId(String username) throws SQLException {
        String query = "SELECT id FROM entities.client_table WHERE username = '" + username + "';";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public void updateClient(String username, int attendance, int wonAuctionsNum) throws SQLException {
        String query = "UPDATE entities.client_table SET attendance = " + attendance + " AND " +
                "wonAuctions = " + wonAuctionsNum + " WHERE username = '" + username + "';";
        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ps.execute();
        }
    }

    /**
     * Registers a new client to the <code>AuctionHouse</code>.<br>
     * The client's credentials and data are saved in the database.
     * @param client the client to be registered
     * @param hash the hash of the password of the client
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    public void registerClient(Client client, String hash) throws MyException, SQLException {
        String username = client.getUsername();
        if (checkIfClientExists(username) || checkIfBrokerExists(username))
            throw new UserAlreadyExistsException(username);

        String query = "INSERT INTO entities.client_table(username,name,password,address) " +
                "VALUES(?,?,?,?);";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, client.getName());
            ps.setString(3, hash);
            ps.setString(4, client.getAddress());

            ps.execute();
        }

        int id = getClientId(username);
        String table;
        String values;
        if (client instanceof Individual) {
            table = "individual_table(id,birthDate)";
            values = "?,?";
        } else {
            table = "legal_entity_table(id,company,shareCapital)";
            values = "?,?,?";
        }
        query = "INSERT INTO entities." + table + " VALUES(" + values + ");";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (client instanceof Individual) {
                ps.setString(2, ((Individual) client).getBirthDate());
            } else {
                ps.setString(2, ((LegalEntity) client).getCompany().toString());
                ps.setDouble(3, ((LegalEntity) client).getShareCapital());
            }
            ps.execute();
        }
    }

    /**
     * Logs in a client the <code>AuctionHouse</code>.<br>
     * The client's credentials and data are loaded from the database.
     * @param username the username of the client
     * @param hash the hash of the password of the client
     * @return the <code>Client</code> object obtained by logging in
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    public Client loginClient(String username, String hash) throws
            MyException, SQLException {
        String queryInd = "SELECT * FROM entities.client_table JOIN entities.individual_table on " +
                "client_table.id = individual_table.id WHERE username = '" + username + "' AND " +
                "password = '" + hash + "';";

        String queryLegal = "SELECT * FROM entities.client_table JOIN entities.legal_entity_table on " +
                "client_table.id = legal_entity_table.id WHERE username = '" + username + "' AND " +
                "password = '" + hash + "';";

        boolean tryLegal = false;
        ResultSet rs;
        Client.ClientBuilder builder = null;
        String name = null;
        String address = null;
        int attendance = 0;
        int wonAuctions = 0;
        try (PreparedStatement ps = defaultConn.prepareStatement(queryInd)) {
            rs = ps.executeQuery();
            if (!rs.next()) tryLegal = true;
            else {
                builder = new Individual.IndividualBuilder(username)
                        .withBirthDate(rs.getString(9));
                name = rs.getString(3);
                address = rs.getString(5);
                attendance = rs.getInt(6);
                wonAuctions = rs.getInt(7);
            }
        }
        if (tryLegal) {
            try (PreparedStatement ps = defaultConn.prepareStatement(queryLegal)) {
                rs = ps.executeQuery();
                if (!rs.next()) throw new WrongPasswordException();
                else {
                    builder = new LegalEntity.LegalEntityBuilder(username)
                            .withCompany(LegalEntity.Company.valueOf(rs.getString(9)))
                            .withShareCapital(rs.getDouble(10));
                    name = rs.getString(3);
                    address = rs.getString(5);
                    attendance = rs.getInt(6);
                    wonAuctions = rs.getInt(7);
                }
            }
        }

        return builder.withName(name)
                .withAddress(address)
                .withAttendance(attendance)
                .withWonAuctions(wonAuctions)
                .build();
    }
}
