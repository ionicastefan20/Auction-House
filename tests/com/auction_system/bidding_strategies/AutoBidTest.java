package com.auction_system.bidding_strategies;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AutoBidTest {

    @Test
    public void getOfferTest() {

        double maxBid = 8000;
        double maxPrice = 10000;
        double bid = new AutoBid(maxPrice, 8).getOffer(maxBid);

        assertTrue(bid > maxBid);
    }

    @Test
    public void getOfferLimitTest() {

        double maxBid = 8000;
        double maxPrice = 8000;
        double bid = new AutoBid(maxPrice, 8).getOffer(maxBid);
        System.out.println(bid);
        assertTrue(bid == maxBid);
    }
}
