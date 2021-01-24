package com.auction_system.auction_house;

import com.auction_system.products.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AdminAHProxy implements IAdminAH {

    private final RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();

    @Override
    public List<String> getProducts() {
        return realAuctionHouse.getProducts();
    }

    @Override
    public void addProduct(Connection conn, Product product) throws SQLException {
        realAuctionHouse.addProduct(conn, product);
    }

    @Override
    public void loadProducts(Connection conn) throws SQLException {
        realAuctionHouse.loadProducts(conn);
    }

    @Override
    public void removeProduct(Connection conn, int id)  throws SQLException{
        realAuctionHouse.removeProduct(conn, id);
    }

    @Override
    public void stopExecutor() {
        realAuctionHouse.stopExecutor();
    }
}
