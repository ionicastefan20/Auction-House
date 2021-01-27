package com.auction_system.auction_house;

import java.io.Serializable;

public class AuctionResult implements Serializable {

    private String username;
    private int productId;
    private double price;

    public AuctionResult(String username, int productId, double price) {
        this.username = username;
        this.productId = productId;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Auction no. " + productId + " winner: " + username + " with " + price;
    }
}
