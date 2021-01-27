package com.auction_system.auction_house;

import com.auction_system.entities.clients.Client;
import com.auction_system.exceptions.*;

import java.sql.SQLException;
import java.util.List;

public interface IClientAH {

    List<String> getProducts();

    void registerClient(Client client, String hash) throws MyException, SQLException;

    Client loginClient(String username, String hash) throws MyException, SQLException;

    void offerInit(String username, int productId, double maxPrice, int rate) throws MyException;

    void removeClient(String username);
}
