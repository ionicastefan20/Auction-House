package com.auction_system.bidding_strategies;

/**
 * Interface for a bidding strategy (Strategy Design Pattern).
 */
public interface IBiddingStrategy {

    /**
     * Returns a bid offer.
     * @param maxBid the max bid so far
     * @return the new bid
     */
    double getOffer(double maxBid);
}
