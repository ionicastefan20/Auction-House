package com.auction_system.auction_house;

import com.auction_system.entities.clients.Client;
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
    private Set<Broker> brokerSet = new HashSet<>();

    public Auction(int productId) {
        this.productId = productId;
    }

    public synchronized void addParticipant(Broker broker) {
        brokerSet.add(broker);
        currentParticipantsNum++;
        if (currentParticipantsNum == participantsNum)
            RealAuctionHouse.getInstance().startAuction(this);
    }

    // TODO the max bid is calculated in the AuctionHouse
    @Override
    public void run() {
        RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();
        MutablePair<String, Double> maxBid = new MutablePair<>("", realAuctionHouse.productMap
                .get(productId).getMinPrice());
        Map<String, Client> clientMap = realAuctionHouse.clientMap;

        System.out.println(maxStepNum);
        while (maxStepNum > 0) {
            List<Pair<String, Double>> currentBids = new LinkedList<>();

            Iterator<Broker> it = brokerSet.iterator();
            while (it.hasNext()) {
                // TODO Thread this shit up
                List<Pair<String, Double>> bids = it.next().getBids(productId, maxBid.getValue());
                if (bids.isEmpty())
                    it.remove();
                else
                    currentBids.addAll(bids);
            }

            if (!currentBids.isEmpty()) {
                currentBids.forEach(pair -> System.out.println(pair.getLeft() + ": " + pair.getRight()));
                Pair<String, Double> biggestBid = Collections.max(currentBids,
                        Comparator.comparing((Pair<String, Double> p) -> p.getRight())
                                .thenComparing((Pair<String, Double> p) -> clientMap.get(p.getLeft()).getWonAuctionsNum())
                );

                maxBid.left = biggestBid.getLeft();
                maxBid.right = biggestBid.getRight();
                System.out.println(biggestBid.getRight() + "\n");
            } else
                break;

            if (currentBids.size() == 1)
                break;

            maxStepNum--;
        }

        // TODO announce winner
        System.out.println(maxBid.left + ": " + String.format("%,.02f", maxBid.right));
    }

    @Override
    public String toString() {
        return currentParticipantsNum + "/" + participantsNum;
    }
}
