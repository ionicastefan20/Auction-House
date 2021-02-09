package com.auction_system.auction_house;

import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.InvalidOptionException;
import com.auction_system.exceptions.MyException;

import java.sql.SQLException;
import java.util.List;

/**
 * This class models a proxy of the <code>AuctionHouse</code> for the broker.
 */
public class BrokerAHProxy implements IBrokerAH {

    private final RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();

    /**
     * Returns a list of the products (as Strings), which have been sold (they have sale price), present
     * at the <code>AuctionHouse</code>. Only a broker can use this method through its proxy.
     * @return a list of products (as Strings)
     * @see List
     * @see String
     * @see Broker
     * @see BrokerAHProxy
     */
    @Override
    public List<String> getProducts() {
        return realAuctionHouse.getProductsBroker();
    }

    /**
     * {@inheritDoc}
     * @param broker the broker to be registered
     * @param hash the hash of the password of the broker
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public void registerBroker(Broker broker, String hash) throws MyException, SQLException {
        realAuctionHouse.registerBroker(broker, hash);
    }

    /**
     * {@inheritDoc}
     * @param username the username of the broker
     * @param hash the hash of the password of the broker
     * @return
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public Broker loginBroker(String username, String hash) throws MyException, SQLException {
        return realAuctionHouse.loginBroker(username, hash);
    }

    /**
     * {@inheritDoc}
     * @param username the username of the broker
     */
    @Override
    public void removeBroker(String username) {
        realAuctionHouse.removeBroker(username);
    }

    /**
     * {@inheritDoc}
     * @param productId the ID of the product
     * @throws SQLException
     */
    @Override
    public void removeProduct(int productId) throws SQLException, InvalidOptionException {
        realAuctionHouse.removeProduct(productId);
    }
}
