package com.auction_system.bidding_strategies;

/**
 * This class models a bidding context.
 */
public class BiddingContext {

    /**
     * The strategy used in this context.
     */
    private final IBiddingStrategy strategy;

    /**
     * Constructor for this class.
     * @param strategy the strategy used
     */
    public BiddingContext(IBiddingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Executes the strategy.
     * @param maxBid the maximum bid so far
     * @return the new bid
     */
    public double execute(double maxBid) {
        return strategy.getOffer(maxBid);
    }
}
