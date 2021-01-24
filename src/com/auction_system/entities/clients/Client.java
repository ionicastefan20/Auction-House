package com.auction_system.entities.clients;

import com.auction_system.auction_house.ClientAHProxy;
import com.auction_system.auction_house.IClientAH;
import com.auction_system.entities.IEntity;
import com.auction_system.exceptions.UserAlreadyExistsException;
import com.auction_system.exceptions.UserDoesNotExistException;
import com.auction_system.exceptions.WrongPasswordException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Client implements IEntity {

    @EqualsAndHashCode.Include
    protected final String username;
    protected String name;
    protected String address;
    protected int attendance;
    protected int wonAuctionsNum;
    private final Map<Integer, Double> currentBids = new HashMap<>();

    protected final IClientAH auctionHouse;

    protected Client(String username) {
        this.username = username;
        auctionHouse = new ClientAHProxy();
    }

    public static Client login(String username, String hash) throws UserDoesNotExistException, SQLException, WrongPasswordException {
        return new ClientAHProxy().loginClient(username, hash);
    }

    public static void register(Client client, String hash) throws SQLException, UserAlreadyExistsException {
        new ClientAHProxy().registerClient(client, hash);
    }

    public void addBid(int productId, double maxPrice) {
        if (!currentBids.containsKey(productId))
            currentBids.put(productId, maxPrice);
    }

    // TODO strategy pattern here for auto/manual bid
    public double getOffer(int productId, double maxBid) {
        double newBid = -1;
        double maxPrice = currentBids.get(productId);

        // TODO get offer from client

        if (maxBid < maxPrice) {
            // TODO auto cu rata de zgarcire de la 1 la 10
            newBid = maxBid + (maxPrice -  maxBid) / (new Random().nextInt(10) + 10);
        } else if (maxBid == maxPrice)
            newBid = maxPrice;

        return newBid;
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


