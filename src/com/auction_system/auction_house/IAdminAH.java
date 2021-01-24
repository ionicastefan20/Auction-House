package com.auction_system.auction_house;

import com.auction_system.products.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IAdminAH {

    List<String> getProducts();

    void addProduct(Connection conn, Product product) throws SQLException;

    void loadProducts(Connection conn) throws SQLException;

    // TODO remove from database too
    void removeProduct(Connection conn, int id) throws SQLException;

    void stopExecutor();
}
