package com.auction_system.auction_house;

import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.UserDoesNotExistException;
import com.auction_system.exceptions.WrongPasswordException;

import java.sql.SQLException;
import java.util.List;

public interface IBrokerAH {

    List<String> getProducts();

    void registerBroker(Broker broker, String hash) throws UserAlreadyExistsException, SQLException;

    Broker loginBroker(String username, String hash) throws UserDoesNotExistException, SQLException, WrongPasswordException;

//    void removeProduct(Connection conn, int id)  throws SQLException;
}
