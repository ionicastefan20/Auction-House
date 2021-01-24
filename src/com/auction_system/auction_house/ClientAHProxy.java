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
    public void registerClient(Client client, String hash) throws UserAlreadyExistsException, SQLException {
        realAuctionHouse.registerClient(client, hash);
    }

    @Override
    public Client loginClient(String username, String hash) throws SQLException, WrongPasswordException {
        return realAuctionHouse.loginClient(username, hash);
    }

    @Override
    public void offerInit(String username, int productId, double maxPrice) throws NoBrokersException, ProductDoesNotExistException, InvalidPriceException {
        realAuctionHouse.offerInit(username, productId, maxPrice);
    }
}
