package com.auction_system.bidding_strategies;

import java.util.Random;

/**
 * This class models an auto-bidding strategy.
 */
public class AutoBid implements IBiddingStrategy {

    /**
     * The maximum price that can be offered.
     */
    private final double maxPrice;

    /**
     * The growth rate for the bid.
     */
    private final int rate;

    /**
     * Constructor for this class.
     * @param maxPrice the maximum price
     * @param rate the growth rate
     */
    public AutoBid(double maxPrice, int rate) {
        this.maxPrice = maxPrice;
        this.rate = rate;
    }

    /**
     * {@inheritDoc}
     * @param maxBid the max bid so far
     * @return the new bid
     */
    @Override
    public double getOffer(double maxBid) {
        double bid = maxBid + (maxPrice - maxBid) / (new Random().nextInt(10) + 11 - rate);
        return (int) bid;
    }
}
