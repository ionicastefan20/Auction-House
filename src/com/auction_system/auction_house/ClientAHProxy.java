package com.auction_system.auction_house;

import com.auction_system.entities.clients.Client;
import com.auction_system.exceptions.*;

import java.sql.SQLException;
import java.util.List;

public class ClientAHProxy implements IClientAH {

    private final RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();

    @Override
    public List<String> getProducts() {
        return realAuctionHouse.getProducts();
    }

    @Override
    public void registerClient(Client client, String hash) throws MyException, SQLException {
        realAuctionHouse.registerClient(client, hash);
    }

    @Override
    public Client loginClient(String username, String hash) throws MyException, SQLException {
        return realAuctionHouse.loginClient(username, hash);
    }

    @Override
    public void offerInit(String username, int productId, double maxPrice, int rate) throws MyException {
        realAuctionHouse.offerInit(username, productId, maxPrice, rate);
    }

    public void removeClient(String username) {
        realAuctionHouse.removeClient(username);
    }
}
