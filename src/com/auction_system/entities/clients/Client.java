package com.auction_system.entities.clients;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.auction_house.ClientAHProxy;
import com.auction_system.auction_house.IClientAH;
import com.auction_system.bidding_strategies.AutoBid;
import com.auction_system.bidding_strategies.BiddingContext;
import com.auction_system.database_system.SqlClientUtility;
import com.auction_system.entities.IEntity;
import com.auction_system.exceptions.MyException;
import com.connection.server_thread.ServerThread;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.*;

/**
 * This class models a client for the application.
 */
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract class Client implements IEntity {

    /**
     * The username of the client
     */
    @EqualsAndHashCode.Include
    final String username;

    /**
     * The name of the client
     */
    String name;

    /**
     * The address of the client
     */
    String address;

    /**
     * The attendance of the client
     */
    int attendance;

    /**
     * The number of won auctions by the client
     */
    int wonAuctionsNum;

    /**
     * The server thread associated with the client
     */
    @Getter(AccessLevel.NONE)
    @Setter
    ServerThread serverThread;

    /**
     * The proxy used to access the auction house
     */
    final IClientAH auctionHouse;

    /**
     * A map containing the bids of the client
     */
    @Getter(AccessLevel.NONE)
    final Map<Integer, BiddingContext> currentBids = new HashMap<>();

    protected Client(String username) {
        this.username = username;
        auctionHouse = new ClientAHProxy();
    }

    /**
     * Logs in the client to the auction house
     * @param username the username of the client
     * @param hash the password hash
     * @return a Client object
     * @throws MyException if there are any errors
     * @throws SQLException if there are any SQL errors
     */
    public static Client login(String username, String hash) throws MyException, SQLException {
        return new ClientAHProxy().loginClient(username, hash);
    }

    /**
     * Registers a client to the auction house
     * @param client the client object used by the registration
     * @param hash the password hash
     * @throws MyException if there are any errors
     * @throws SQLException if there are any SQL errors
     */
    public static void register(Client client, String hash) throws MyException, SQLException {
        new ClientAHProxy().registerClient(client, hash);
    }

    /**
     * Adds a new bid to an auction
     * @param productId the id of the product
     * @param maxPrice the maximum price that the client can bid
     * @param rate the bidding growth rate
     */
    public void addBid(int productId, double maxPrice, int rate) {
        if (!currentBids.containsKey(productId)) {
            currentBids.put(productId, new BiddingContext(new AutoBid(maxPrice, rate)));
        }
    }

    /**
     * Gets a new bid for the product
     * @param productId the id of the product
     * @param maxBid the maximum bid so far
     * @return the new offer
     */
    public synchronized double getOffer(int productId, double maxBid) {
        return currentBids.get(productId).execute(maxBid);
    }

    /**
     * Prints the result and sends it to the client app
     * @param result the auction result
     */
    @SneakyThrows
    public synchronized void announceResults(AuctionResult result) {
        out.println(username + ": " + result);
        attendance++;
        if (result.getUsername().equals(username)) wonAuctionsNum++;

        SqlClientUtility.updateClient(username, attendance, wonAuctionsNum);
        
        if (serverThread != null)
            serverThread.sendResult(result);
    }

    /**
     * Builder for the Client class
     */
    public abstract static class ClientBuilder {

        /**
         * The instance of the client
         */
        protected Client client;

        /**
         * Protected constructor
         */
        protected ClientBuilder() {
        }

        /**
         * Adds the name
         * @param name the name
         * @return the builder
         */
        public ClientBuilder withName(String name) {
            client.name = name;
            return this;
        }

        /**
         * Adds the address
         * @param address the address
         * @return the builder
         */
        public ClientBuilder withAddress(String address) {
            client.address = address;
            return this;
        }

        /**
         * Adds the attendance
         * @param attendance the attendance
         * @return the builder
         */
        public ClientBuilder withAttendance(int attendance) {
            client.attendance = attendance;
            return this;
        }

        /**
         * Adds the number of won auctions
         * @param wonAuctions the number of won auctions
         * @return the builder
         */
        public ClientBuilder withWonAuctions(int wonAuctions) {
            client.wonAuctionsNum = wonAuctions;
            return this;
        }

        /**
         * Returns the built client
         * @return the client
         */
        public Client build() {
            return client;
        }
    }
}


