package com.auction_system.auction_house;

import com.auction_system.database_system.*;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.Administrator;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.*;
import com.auction_system.products.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class models an Auction House.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class RealAuctionHouse implements IAdminAH, IBrokerAH, IClientAH {

    /**
     * This {@link Map} holds the clients who are connected to the application.<br>
     * It is instantiated using the {@link ConcurrentHashMap} implementation for thread safety.
     *
     * @see Client
     */
    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    /**
     * This {@link Map} holds the brokers who are connected to the application.<br>
     * It is instantiated using the {@link ConcurrentHashMap} implementation for thread safety.
     *
     * @see Broker
     */
    private final Map<String, Broker> brokerMap = new ConcurrentHashMap<>();

    /**
     * This {@link Map} holds the products that are going to be sold by the <code>AuctionHouse</code>.<br>
     * It is instantiated using the {@link ConcurrentHashMap} implementation for thread safety.
     *
     * @see Product
     */
    private final Map<Integer, Product> productMap = new ConcurrentHashMap<>();

    /**
     * This {@link ExecutorService} holds each auction thread which will be created.
     *
     * @see Auction
     */
    private final ExecutorService auctionExecutor = Executors.newCachedThreadPool();

    /**
     * Bill Pugh's Singleton implementation.
     */
    private static class Helper {
        private static final RealAuctionHouse instance = new RealAuctionHouse();
    }

    /**
     * Returns the instance of the <code>AuctionHouse</code>. Thread safety is guaranteed by Bill Pugh's
     * Singleton implementation.
     *
     * @return the instance of the <code>AuctionHouse</code>
     */
    public static RealAuctionHouse getInstance() {
        return Helper.instance;
    }

    /**
     * Returns the product with the ID given as parameter or <code>null</code> if there is
     * no product with that ID.
     *
     * @param productId the ID of the product
     * @return the {@link Product} with
     */
    Product getProduct(int productId) {
        return productMap.get(productId);
    }

    /**
     * Returns a list of all products (as Strings) present at the <code>AuctionHouse</code>. Only
     * the administrator can use this method through its proxy.
     *
     * @return a list of all products (as Strings)
     * @see List
     * @see String
     * @see Administrator
     * @see AdminAHProxy
     */
    @Override
    public List<String> getProducts() {
        synchronized (productMap) {
            return productMap.values().stream()
                    .map(Product::toString)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns a list of the products (as Strings), which have not been sold (no sale price), present
     * at the <code>AuctionHouse</code>. Only a client can use this method through its proxy.
     *
     * @return a list of products (as Strings)
     * @see List
     * @see String
     * @see Client
     * @see ClientAHProxy
     */
    public List<String> getProductsClient() {
        synchronized (productMap) {
            return productMap.values().stream()
                    .filter(p -> p.getSalePrice() == 0)
                    .map(Product::toString)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns a list of the products (as Strings), which have been sold (they have sale price), present
     * at the <code>AuctionHouse</code>. Only a broker can use this method through its proxy.
     *
     * @return a list of products (as Strings)
     * @see List
     * @see String
     * @see Broker
     * @see BrokerAHProxy
     */
    public List<String> getProductsBroker() {
        synchronized (productMap) {
            return productMap.values().stream()
                    .filter(p -> p.getSalePrice() != 0)
                    .map(Product::toString)
                    .collect(Collectors.toList());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param conn    the SQL connection used to add the product to the house
     * @param product the product to be added
     * @throws SQLException
     */
    @Override
    public synchronized void addProduct(Connection conn, Product product) throws SQLException {
        SqlProductUtility.addProduct(conn, product);
        productMap.put(product.getId(), product);
    }

    /**
     * {@inheritDoc}
     *
     * @param conn the SQL connection used to load the products to the house
     * @throws SQLException
     */
    @Override
    public synchronized void loadProducts(Connection conn) throws SQLException {
        SqlProductUtility.loadProducts(conn).forEach(product -> productMap.put(product.getId(), product));
    }

    /**
     * {@inheritDoc}
     *
     * @param productId the ID of the product
     * @throws SQLException
     */
    @Override
    public synchronized void removeProduct(int productId) throws SQLException, InvalidOptionException {
        if (productMap.get(productId).getSalePrice() < productMap.get(productId).getMinPrice())
            throw new InvalidOptionException();
        SqlProductUtility.updateProduct(productId, productMap.get(productId).getSalePrice());
        productMap.remove(productId);
    }

    /**
     * {@inheritDoc}
     *
     * @param client the client to be registered
     * @param hash   the hash of the password of the client
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public synchronized void registerClient(Client client, String hash) throws MyException, SQLException {
        SqlClientUtility.registerClient(client, hash);
        clientMap.put(client.getUsername(), client);
    }

    /**
     * {@inheritDoc}
     *
     * @param username the username of the client
     * @param hash     the hash of the password of the client
     * @return
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public synchronized Client loginClient(String username, String hash) throws MyException, SQLException {
        Client client = SqlClientUtility.loginClient(username, hash);

        if (clientMap.containsKey(username)) throw new UserAlreadyLoggedInException(username);
        clientMap.put(username, client);

        return client;
    }

    /**
     * {@inheritDoc}
     *
     * @param username the username of the client
     */
    @Override
    public synchronized void removeClient(String username) {
        clientMap.remove(username);
    }

    /**
     * {@inheritDoc}
     *
     * @param username  the username of the client
     * @param productId the ID of the product
     * @param maxPrice  the maximum price to be offered while bidding
     * @param rate      the growth rade
     * @throws MyException
     */
    @Override
    public synchronized void offerInit(String username, int productId, double maxPrice, int rate)
            throws MyException {
        if (brokerMap.isEmpty())
            throw new NoBrokersException();
        if (!productMap.containsKey(productId))
            throw new ProductDoesNotExistException(productId);

        Auction auction = productMap.get(productId).getAuction();

        if (auction.containsParticipant(username, productId))
            throw new BidOnTheSameProductException(productId);
        if (auction.currentParticipantsNum == Auction.MAX_PARTICIPANTS_NUM)
            throw new MaxBidsNumberException(productId);

        // Select a random broker
        int index = new Random().nextInt(brokerMap.size());
        Broker broker = brokerMap.values().toArray(new Broker[0])[index];

        Product product = productMap.get(productId);
        if (product.getMinPrice() > maxPrice) throw new InvalidPriceException();

        broker.addClient(clientMap.get(username), productId, maxPrice);
        clientMap.get(username).addBid(productId, maxPrice, rate);
        auction.addParticipant(broker);
    }

    /**
     * {@inheritDoc}
     *
     * @param broker the broker to be registered
     * @param hash   the hash of the password of the broker
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public synchronized void registerBroker(Broker broker, String hash) throws MyException, SQLException {
        SqlBrokerUtility.registerBroker(broker, hash);
        brokerMap.put(broker.getUsername(), broker);
    }

    /**
     * {@inheritDoc}
     *
     * @param username the username of the broker
     * @param hash     the hash of the password of the broker
     * @return
     * @throws MyException
     * @throws SQLException
     */
    @Override
    public synchronized Broker loginBroker(String username, String hash) throws MyException, SQLException {
        Broker broker = SqlBrokerUtility.loginBroker(username, hash);
        brokerMap.put(broker.getUsername(), broker);

        return broker;
    }

    /**
     * {@inheritDoc}
     *
     * @param username the username of the broker
     */
    @Override
    public synchronized void removeBroker(String username) {
        brokerMap.remove(username);
    }

    /**
     * Starts the auction.
     *
     * @param auction the auction to be started
     */
    void startAuction(Auction auction) {
        auctionExecutor.execute(auction);
    }

    /**
     * Gets the maximum bid.
     *
     * @param currentBids the bids from which the the maximum one will be extracted
     * @return the maximum bid
     */
    Pair<String, Double> getMax(List<Pair<String, Double>> currentBids) {
        return Collections.max(currentBids,
                // SonarLint is not happy with this line and I cannot do anything about it
                Comparator.comparing((Function<Pair<String, Double>, Double>) Pair::getRight)
                        .thenComparing((Pair<String, Double> p) -> clientMap.get(p.getLeft()).getWonAuctionsNum())
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopExecutor() {
        auctionExecutor.shutdown();
    }
}
