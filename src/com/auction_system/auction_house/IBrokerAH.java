package com.auction_system.auction_house;

import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.InvalidOptionException;
import com.auction_system.exceptions.MyException;

import java.sql.SQLException;

public interface IBrokerAH extends IEntityAH {

    /**
     * Registers a new broker to the <code>AuctionHouse</code>.
     * @param broker the broker to be registered
     * @param hash the hash of the password of the broker
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    void registerBroker(Broker broker, String hash) throws MyException, SQLException;

    /**
     * Logs in a broker the <code>AuctionHouse</code>.
     * @param username the username of the broker
     * @param hash the hash of the password of the broker
     * @return the <code>Broker</code> object obtained by logging in
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    Broker loginBroker(String username, String hash) throws MyException, SQLException;

    /**
     * Removes a broker from the <code>AuctionHouse</code>
     * @param username the username of the broker
     */
    void removeBroker(String username);

    /**
     * Removes a sold product from the <code>AuctionHouse</code>
     * @param productId the ID of the product
     * @throws SQLException if there are any SQL errors
     */
    void removeProduct(int productId) throws SQLException, InvalidOptionException;
}
