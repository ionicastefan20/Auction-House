package com.auction_system.database_system;

import com.auction_system.entities.clients.Client;
import com.auction_system.entities.clients.Individual;
import com.auction_system.entities.clients.LegalEntity;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.WrongPasswordException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.auction_system.database_system.SqlEntityUtility.*;

public class SqlClientUtility extends SqlUtility {

    private SqlClientUtility() {
    }

    private static int getClientId(String username) throws SQLException {
        String query = "SELECT id FROM entities.client_table WHERE username = '" + username + "';";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public static void registerClient(Client client, String hash) throws SQLException, UserAlreadyExistsException {
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
        query = "INSERT INTO entities." + table +
                " VALUES(" + values + ");";

        if (client instanceof Individual)
            try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
                ps.setInt(1, id);
                ps.setString(2, ((Individual) client).getBirthDate());
                ps.execute();
            }
        else
            try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
                ps.setInt(1, id);
                ps.setString(2, ((LegalEntity) client).getCompany().toString());
                ps.setDouble(3, ((LegalEntity) client).getShareCapital());
                ps.execute();
            }
    }

    public static Client loginClient(String username, String hash) throws SQLException, WrongPasswordException {
        String query = "SELECT * FROM entities.client_table JOIN entities.individual_table on " +
                "client_table.id = individual_table.id WHERE username = '" + username + "' AND " +
                "password = '" + hash + "';";

        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) throw new WrongPasswordException();

            if (rs.getMetaData().getColumnCount() == 9)
                return new Individual.IndividualBuilder(username)
                        .withName(rs.getString(3))
                        .withAddress(rs.getString(5))
                        .withAttendance(rs.getInt(6))
                        .withWonAuctions(rs.getInt(7))
                        .asIndividualBuilder()
                        .withBirthDate(rs.getString(9))
                        .build();
            else
                return new LegalEntity.LegalEntityBuilder(username)
                        .withName(rs.getString(3))
                        .withAddress(rs.getString(5))
                        .withAttendance(rs.getInt(6))
                        .withWonAuctions(rs.getInt(7))
                        .asLegalEntityBuilder()
                        .withCompany(LegalEntity.Company.valueOf(rs.getString(9)))
                        .withShareCapital(rs.getDouble(10))
                        .build();
        }
    }
}
