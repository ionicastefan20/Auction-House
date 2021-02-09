package com.auction_system.auction_house;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;

public class AuctionResult implements Serializable {

    @Getter
    private final String username;
    private final int productId;
    private final double price;
    private final Pair<Integer, Integer> step;

    public AuctionResult(String username, int productId, double price, Pair<Integer, Integer> step) {
        this.username = ("".equals(username)) ? "NO_WINNER" : username;
        this.productId = productId;
        this.price = price;
        this.step = step;
    }

    @Override
    public String toString() {
        return "\nAuction no. " + productId + " (" + step.getLeft() + "/" + step.getRight() + "): "
                + username + " with " + price + "\n";
    }
}
