package com.auction_system.bidding_strategies;

public class BiddingContext {
    private final IBiddingStrategy strategy;

    public BiddingContext(IBiddingStrategy strategy) {
        this.strategy = strategy;
    }

    public double execute(double maxBid) {
        return strategy.getOffer(maxBid);
    }
}
