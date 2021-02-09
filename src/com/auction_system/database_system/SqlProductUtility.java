package com.auction_system.database_system;

import com.auction_system.products.Furniture;
import com.auction_system.products.Jewelry;
import com.auction_system.products.Painting;
import com.auction_system.products.Product;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a SQL Utility class for a product.
 */
@UtilityClass
public class SqlProductUtility extends SqlUtility {

    /**
     * Adds a product to the database.
     * @param conn the connection used for the database
     * @param product the product to be added
     * @throws SQLException if there are any SQL errors
     */
    public void addProduct(Connection conn, Product product) throws SQLException {
        String query1 = "INSERT INTO products.product_table(id,name,minPrice,year) " +
                "VALUES(?,?,?,?);";
        try (PreparedStatement ps = conn.prepareStatement(query1)) {
            ps.setInt(1, product.getId());
            ps.setString(2, product.getName());
            ps.setDouble(3, product.getMinPrice());
            ps.setInt(4, product.getYear());

            ps.execute();
        }

        String table;
        if (product instanceof Furniture)
            table = "furniture_table(id,type,material)";
        else if (product instanceof Jewelry)
            table = "jewelry_table(id,material,preciousStone)";
        else
            table = "painting_table(id,artistName,color)";

        String query2 = "INSERT INTO products." + table +
                " VALUES(?,?,?);";

        try (PreparedStatement ps = conn.prepareStatement(query2)) {
            ps.setInt(1, product.getId());
            ps.setString(2, product.getCustom1());
            ps.setString(3, product.getCustom2());

            ps.execute();
        }
    }

    /**
     * Updates the sale price of a product.
     * @param productId the ID of the product
     * @param salePrice the new sale price
     * @throws SQLException if there are any SQL errors
     */
    public void updateProduct(int productId, double salePrice) throws SQLException {
        String query = "UPDATE products.product_table SET salePrice = " + salePrice +
                " WHERE id = " + productId + ";";
        try (PreparedStatement ps = defaultConn.prepareStatement(query)) {
            ps.execute();
        }
    }

    /**
     * Loads the products (with the sale price equal to null) from the database.
     * @param conn the connection used by the database
     * @return a list of products
     * @throws SQLException if there are any SQL errors
     */
    public List<Product> loadProducts(Connection conn) throws SQLException {
        List<Product> products = new ArrayList<>();

        String query = "SELECT * FROM products.product_table JOIN products.furniture_table on " +
                "product_table.id = furniture_table.id WHERE salePrice IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                products.add(new Furniture.FurnitureBuilder(rs.getInt(1), rs.getDouble(4))
                        .withName(rs.getString(2))
                        .withYear(rs.getInt(5))
                        .asFurnitureBuilder()
                        .withType(rs.getString(7))
                        .withMaterial(rs.getString(8))
                        .build());
        }

        query = "SELECT * FROM products.product_table JOIN products.jewelry_table on " +
                "product_table.id = jewelry_table.id WHERE salePrice IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                products.add(new Jewelry.JewelryBuilder(rs.getInt(1), rs.getDouble(4))
                        .withName(rs.getString(2))
                        .withYear(rs.getInt(5))
                        .asJewelryBuilder()
                        .withMaterial(rs.getString(7))
                        .withPreciousStone("yes".equals(rs.getString(8)))
                        .build());
        }

        query = "SELECT * FROM products.product_table JOIN products.painting_table on " +
                "product_table.id = painting_table.id WHERE salePrice IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                products.add(new Painting.PaintingBuilder(rs.getInt(1), rs.getDouble(4))
                        .withName(rs.getString(2))
                        .withYear(rs.getInt(5))
                        .asPaintingBuilder()
                        .withArtistName(rs.getString(7))
                        .withColor(Painting.Colors.valueOf(rs.getString(8)))
                        .build());
        }

        return products;
    }
}
