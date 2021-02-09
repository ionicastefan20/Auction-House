package com.auction_system.auction_house;

import com.auction_system.entities.clients.Client;
import com.auction_system.exceptions.*;

import java.sql.SQLException;

public interface IClientAH extends IEntityAH {

    /**
     * Registers a new client to the <code>AuctionHouse</code>.
     * @param client the client to be registered
     * @param hash the hash of the password of the client
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    void registerClient(Client client, String hash) throws MyException, SQLException;

    /**
     * Logs in a client the <code>AuctionHouse</code>.
     * @param username the username of the client
     * @param hash the hash of the password of the client
     * @return the <code>Client</code> object obtained by logging in
     * @throws MyException if there is any error
     * @throws SQLException if there are any SQL errors
     */
    Client loginClient(String username, String hash) throws MyException, SQLException;

    /**
     * Offers a bid from the client with the specified username, for the specified product, with a
     * maximum bid and a rate of growth for the bids (1 - slow, 10 - fast).
     * @param username the username of the client
     * @param productId the ID of the product
     * @param maxPrice the maximum price to be offered while bidding
     * @param rate the growth rade
     * @throws MyException if there is any error
     */
    void offerInit(String username, int productId, double maxPrice, int rate) throws MyException;

    /**
     * Removes a client from the <code>AuctionHouse</code>
     * @param username the username of the client
     */
    void removeClient(String username);
}
