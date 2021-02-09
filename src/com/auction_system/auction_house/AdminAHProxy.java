package com.auction_system.auction_house;

import com.auction_system.entities.employees.Administrator;
import com.auction_system.products.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * This class models a proxy of the <code>AuctionHouse</code> for the administrator.
 */
public class AdminAHProxy implements IAdminAH {

    private final RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();

    /**
     * Returns a list of all products (as Strings) present at the <code>AuctionHouse</code>. Only
     * the administrator can use this method through its proxy.
     * @return a list of all products (as Strings)
     * @see List
     * @see String
     * @see Administrator
     * @see AdminAHProxy
     */
    @Override
    public List<String> getProducts() {
        return realAuctionHouse.getProducts();
    }

    /**
     * {@inheritDoc}
     * @param conn the SQL connection used to add the product to the house
     * @param product the product to be added
     * @throws SQLException
     */
    @Override
    public void addProduct(Connection conn, Product product) throws SQLException {
        realAuctionHouse.addProduct(conn, product);
    }

    /**
     * {@inheritDoc}
     * @param conn the SQL connection used to load the products to the house
     * @throws SQLException
     */
    @Override
    public void loadProducts(Connection conn) throws SQLException {
        realAuctionHouse.loadProducts(conn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopExecutor() {
        realAuctionHouse.stopExecutor();
    }
}
