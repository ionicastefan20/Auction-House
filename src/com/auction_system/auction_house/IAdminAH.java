package com.auction_system.auction_house;

import com.auction_system.products.Product;

import java.sql.Connection;
import java.sql.SQLException;

public interface IAdminAH extends IEntityAH {

    /**
     * Adds a products to the <code>AuctionHouse</code>. Only the administrator can use this method
     * through its proxy.
     * @param conn the SQL connection used to add the product to the house
     * @param product the product to be added
     * @throws SQLException if there is an SQL error
     */
    void addProduct(Connection conn, Product product) throws SQLException;

    /**
     * Loads the products from the database into the <code>AuctionHouse</code>.
     * @param conn the SQL connection used to load the products to the house
     * @throws SQLException if there is an SQL error
     */
    void loadProducts(Connection conn) throws SQLException;

    /**
     * Stops the all auctions from the <code>ExecutorService</code>.
     */
    void stopExecutor();
}
