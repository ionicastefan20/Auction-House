package com.auction_system.auction_house;

import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.MyException;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.UserDoesNotExistException;
import com.auction_system.exceptions.WrongPasswordException;

import java.sql.SQLException;
import java.util.List;

public class BrokerAHProxy implements IBrokerAH {

    private final RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();

    @Override
    public List<String> getProducts() {
        return realAuctionHouse.getProducts();
    }

    // TODO
    @Override
    public void registerBroker(Broker broker, String hash) throws MyException, SQLException {
        realAuctionHouse.registerBroker(broker, hash);
    }

    @Override
    public Broker loginBroker(String username, String hash) throws MyException, SQLException {
        return realAuctionHouse.loginBroker(username, hash);
    }

    // TODO
//    @Override
//    public void markAsSold(Connection conn, int id) throws SQLException {
//        removeProduct(conn, id);
//    }

    @Override
    public void removeBroker(String username) {
        realAuctionHouse.removeBroker(username);
    }
}
