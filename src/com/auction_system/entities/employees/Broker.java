package com.auction_system.entities.employees;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.auction_house.BrokerAHProxy;
import com.auction_system.auction_house.IBrokerAH;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.clients.Individual;
import com.auction_system.exceptions.MyException;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@ToString
public class Broker implements IEmployee {

    @Getter
    private final String username;

    private double totalCommission;

    @Getter @ToStringExclude
    private final Map<String, BidData> bidsData = new HashMap<>();

    @Getter @ToStringExclude
    private final IBrokerAH auctionHouse;

    public Broker(String username) {
        this.username = username;
        auctionHouse = new BrokerAHProxy();
    }

    public static Broker login(String username, String hash) throws MyException, SQLException {
        return new BrokerAHProxy().loginBroker(username, hash);
    }

    public void addClient(Client client, int productId, double maxPrice) {
        String newId = productId + client.getUsername();

        if (!bidsData.containsKey(newId)) {
            double commission;
            int attendance = client.getAttendance();
            if (client instanceof Individual) {
                if (attendance < 5)
                    commission = 0.2;
                else
                    commission = 0.15;
            } else {
                if (attendance < 25)
                    commission = 0.25;
                else
                    commission = 0.10;
            }

            bidsData.put(newId, new BidData(client, productId, maxPrice, commission));
        }
    }

    public List<Pair<String, Double>> getBids(int productId, double maxBid) {
        List<Pair<String, Double>> bids = new LinkedList<>();

        // TODO announce pre-final losers

        Set<Client> clientSet = this.bidsData.values().stream()
                .filter(bidData -> bidData.productId == productId)
                .filter(bidData -> bidData.maxPrice > maxBid)
                .map(bidData -> bidData.client)
                .collect(Collectors.toSet());
        clientSet.forEach(client -> bids.add(
                new ImmutablePair<>(client.getUsername(), client.getOffer(productId, maxBid))));

        return bids;
    }

    public void processResults(Pair<String, Double> maxBid, int productId) {
        String id = productId + maxBid.getLeft();

        if (bidsData.containsKey(id))
            totalCommission += bidsData.get(id).commission * maxBid.getRight();

        bidsData.values().stream().filter(bidData -> bidData.productId == productId)
                .parallel().forEach(bidData -> bidData.client.sendResult(
                        new AuctionResult(maxBid.getLeft(), productId, maxBid.getRight())));

        bidsData.remove(id);
    }

    private static class BidData {
        final Client client;
        final int productId;
        final double maxPrice;
        final double commission;

        BidData(Client client, int productId, double maxPrice, double commission) {
            this.client = client;
            this.productId = productId;
            this.maxPrice = maxPrice;
            this.commission = commission;
        }
    }
}
