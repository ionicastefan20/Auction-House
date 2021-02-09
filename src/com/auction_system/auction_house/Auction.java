package com.auction_system.auction_house;

import com.auction_system.entities.employees.Broker;
import com.auction_system.products.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class Auction implements Runnable {

    @Getter(AccessLevel.PACKAGE)
    private final int productId;

    int currentParticipantsNum;

    // for testing purposes set the limit to 3 participants
    final int maxParticipantsNum = 1;
//    final int maxParticipantsNum = new Random().nextInt(5) + 2;

    private int currentStep = 0;
    private int maxStepNum = new Random().nextInt(11) + 5;

    private MutablePair<String, Double> maxBid;
    private final Set<Broker> brokerSet = new HashSet<>();
    private final Set<Broker> announceSet = new HashSet<>();

    private PrintStream ps;

    @SneakyThrows
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
        if (currentParticipantsNum == maxParticipantsNum)
            RealAuctionHouse.getInstance().startAuction(this);
    }

    @SneakyThrows
    @Override
    public void run() {
        setOut();
        announceSet.addAll(brokerSet);

        Set<Broker> toBeRemoved = new HashSet<>();
        RealAuctionHouse realAuctionHouse = RealAuctionHouse.getInstance();
        double minPrice = realAuctionHouse.getProduct(productId).getMinPrice();
        double startingBid = new Random().nextDouble() * minPrice;

        maxBid = new MutablePair<>("", startingBid);

        for (currentStep = 0; currentStep < maxStepNum; currentStep++) {
            CopyOnWriteArrayList<Pair<String, Double>> currentBids = new CopyOnWriteArrayList<>();

            brokerSet.parallelStream().forEach(broker -> {
                List<Pair<String, Double>> bids = broker.getBids(productId, maxBid.getValue());
                if (bids.isEmpty())
                    toBeRemoved.add(broker);
                else
                    currentBids.addAll(bids);
            });

            ps.println(productId + ": " + currentBids + " (" + (currentStep + 1) + "/" + maxStepNum + ")");

            if (currentBids.isEmpty() || (currentBids.size() == 1)) break;
            else {
                Pair<String, Double> biggestBid = realAuctionHouse.getMax(currentBids);

                maxBid.left = biggestBid.getLeft();
                maxBid.right = biggestBid.getRight();
            }

            toBeRemoved.parallelStream().forEach(brokerSet::remove);
        }

        double salePrice = maxBid.getRight();
        if (salePrice >= minPrice)
            realAuctionHouse.getProduct(productId).setSalePrice(salePrice);
        else
            maxBid.setLeft("");

        announceResults();
    }

    private void setOut() throws FileNotFoundException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd@HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();

        ps = new PrintStream(new FileOutputStream("res/logs/" + productId + "_" + dtf.format(now) + ".out"));
    }

    @SneakyThrows
    void announceResults() {
        Product product = RealAuctionHouse.getInstance().getProduct(productId);
        if (maxBid.getRight() < product.getMinPrice())
            currentParticipantsNum = 0;

        announceSet.parallelStream().forEach(b -> b.announceResults(maxBid, productId,
                new ImmutablePair<>(currentStep, maxStepNum)));
    }

    @Override
    public String toString() {
        return currentParticipantsNum + "/" + maxParticipantsNum;
    }
}
