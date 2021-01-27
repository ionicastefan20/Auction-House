package com.auction_system.entities.clients;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.auction_house.ClientAHProxy;
import com.auction_system.auction_house.IClientAH;
import com.auction_system.bidding_strategies.AutoBid;
import com.auction_system.bidding_strategies.BiddingContext;
import com.auction_system.entities.IEntity;
import com.auction_system.exceptions.MyException;
import com.connection.ServerThread;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Client implements IEntity {

    @Getter @EqualsAndHashCode.Include
    final String username;

    @Getter
    String name;

    @Getter
    String address;

    @Getter
    int attendance;

    @Getter
    int wonAuctionsNum;

    @Setter
    ServerThread serverThread;

    @Getter
    final IClientAH auctionHouse;

    final Map<Integer, Pair<Double, BiddingContext>> currentBids = new HashMap<>();

    protected Client(String username) {
        this.username = username;
        auctionHouse = new ClientAHProxy();
    }

    public static Client login(String username, String hash) throws MyException, SQLException {
        return new ClientAHProxy().loginClient(username, hash);
    }

    public static void register(Client client, String hash) throws MyException, SQLException {
        new ClientAHProxy().registerClient(client, hash);
    }

    public void addBid(int productId, double maxPrice, int rate) {
        if (!currentBids.containsKey(productId)) {
            currentBids.put(productId, new ImmutablePair<>(maxPrice, new BiddingContext(
                    new AutoBid(maxPrice, rate))));
        }
    }

    public double getOffer(int productId, double maxBid) {
        return currentBids.get(productId).getRight().execute(maxBid);
    }

    public void sendResult(AuctionResult result) {
        serverThread.sendResult(result);
    }

    public static class ClientBuilder {

        protected Client client;

        protected ClientBuilder() {
        }

        public ClientBuilder withName(String name) {
            client.name = name;
            return this;
        }

        public ClientBuilder withAddress(String address) {
            client.address = address;
            return this;
        }

        public ClientBuilder withAttendance(int attendance) {
            client.attendance = attendance;
            return this;
        }

        public ClientBuilder withWonAuctions(int wonAuctions) {
            client.wonAuctionsNum = wonAuctions;
            return this;
        }

        public LegalEntity.LegalEntityBuilder asLegalEntityBuilder() {
            return (LegalEntity.LegalEntityBuilder) this;
        }

        public Individual.IndividualBuilder asIndividualBuilder() {
            return (Individual.IndividualBuilder) this;
        }

        public Client build() {
            return client;
        }
    }
}


