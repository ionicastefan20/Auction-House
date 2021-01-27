package com.auction_system.auction_house;

import com.auction_system.entities.employees.Broker;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Auction implements Runnable {

    @Getter(AccessLevel.PACKAGE)
    private int productId;

    private int currentParticipantsNum;
    private final int participantsNum = 4;
//    private int participantsNum = new Random().nextInt(5) + 2;

    private int maxStepNum = new Random().nextInt(11) + 5;

    private MutablePair<String, Double> maxBid;
    private final Set<Broker> brokerSet = new HashSet<>();
    private final Set<Broker> announceSet = new HashSet<>();

    public Auction(int productId) {
        this.productId = productId;
    }

    boolean containsParticipant(String username, int productId) {
        String newId = productId + username;
        for (Broker broker : brokerSet) {
            if (broker.getBidsData().containsKey(newId)) return true;
        }
        return false;
    }

    public void addParticipant(Broker broker) {
        brokerSet.add(broker);
        currentParticipantsNum++;
        if (currentParticipantsNum == participantsNum)
            RealAuctionHouse.getInstance().startAuction(this);
    }

    // TODO
    void announceResults() {
        announceSet.parallelStream().forEach(b -> b.processResults(maxBid, productId));

        Iterator<Broker> it = brokerSet.iterator();
        while (it.hasNext()) {
            // TODO Thread this shit up

        }
    }

    @Override
    public void run() {
        announceSet.addAll(brokerSet);
        Set<Broker> toBeRemoved = new HashSet<>();
        RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();
        double startingBid = new Random().nextDouble() * realAuctionHouse.productMap
                .get(productId).getMinPrice();
        maxBid = new MutablePair<>("", startingBid);

        while (maxStepNum > 0) {
            List<Pair<String, Double>> currentBids = new LinkedList<>();
            new Random().nextInt(5);
            /*
            Iterator<Broker> it = brokerSet.iterator();
            while (it.hasNext()) {
                List<Pair<String, Double>> bids = it.next().getBids(productId, maxBid.getValue());
                if (bids.isEmpty())
                    it.remove();
                else
                    currentBids.addAll(bids);
            }*/
            brokerSet.parallelStream().forEach(broker -> {
                List<Pair<String, Double>> bids = broker.getBids(productId, maxBid.getValue());
                if (bids.isEmpty())
                    toBeRemoved.add(broker);
                else
                    currentBids.addAll(bids);
            });

            if (currentBids.isEmpty() || (currentBids.size() == 1)) {
                break;
            } else {
                Pair<String, Double> biggestBid = realAuctionHouse.getMax(currentBids);

                maxBid.left = biggestBid.getLeft();
                maxBid.right = biggestBid.getRight();
            }

            toBeRemoved.parallelStream().forEach(brokerSet::remove);
            maxStepNum--;
        }

        // TODO announce winner
        announceResults();

        // TODO add finished auctions to db
    }

    @Override
    public String toString() {
        return currentParticipantsNum + "/" + participantsNum;
    }
}
