package com.auction_system.auction_house;

import com.auction_system.entities.clients.Client;
import com.auction_system.exceptions.*;

import java.sql.SQLException;
import java.util.List;

/**
 * This class models a proxy of the <code>AuctionHouse</code> for the client.
 */
public class ClientAHProxy implements IClientAH {

    private final RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();

    /**
     * Returns a list of the products (as Strings), which have not been sold (no sale price), present
     * at the <code>AuctionHouse</code>. Only a client can use this method through its proxy.
     * @return a list of products (as Strings)
     * @see List
     * @see String
     * @see Client
     * @see ClientAHProxy
     */
    @Override
    public List<String> getProducts() {
        return realAuctionHouse.getProductsClient();
    }

    /**
     * {@inheritDoc}
     * @param client the client to be registered
     * @param hash the hash of the password of the client
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public void registerClient(Client client, String hash) throws MyException, SQLException {
        realAuctionHouse.registerClient(client, hash);
    }

    /**
     * {@inheritDoc}
     * @param username the username of the client
     * @param hash the hash of the password of the client
     * @return
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public Client loginClient(String username, String hash) throws MyException, SQLException {
        return realAuctionHouse.loginClient(username, hash);
    }

    /**
     * {@inheritDoc}
     * @param username the username of the client
     * @param productId the ID of the product
     * @param maxPrice the maximum price to be offered while bidding
     * @param rate the growth rade
     * @throws MyException
     */
    @Override
    public void offerInit(String username, int productId, double maxPrice, int rate) throws MyException {
        realAuctionHouse.offerInit(username, productId, maxPrice, rate);
    }

    /**
     * {@inheritDoc}
     * @param username the username of the client
     */
    public void removeClient(String username) {
        realAuctionHouse.removeClient(username);
    }
}
