package com.auction_system.bidding_strategies;

import java.util.Random;

public class AutoBid implements IBiddingStrategy {

    private final double maxPrice;
    private final int rate;

    public AutoBid(double maxPrice, int rate) {
        this.maxPrice = maxPrice;
        this.rate = rate;
    }

    @Override
    public double getOffer(double maxBid) {
        double newBid = -1;

        if (maxBid < maxPrice)
            newBid = maxBid + (maxPrice - maxBid) / (new Random().nextInt(10) + 11 - rate);
        else if (maxBid == maxPrice)
            newBid = maxPrice;

        return newBid;
    }
}
