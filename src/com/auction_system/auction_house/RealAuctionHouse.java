package com.auction_system.auction_house;

import com.auction_system.database_system.*;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.*;
import com.auction_system.products.Product;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class RealAuctionHouse implements IAdminAH, IBrokerAH, IClientAH {

    // only with bidding clients
    final Map<String, Client> clientMap = new ConcurrentHashMap<>();
    private final Map<String, Broker> brokerMap = new ConcurrentHashMap<>();

    final Map<Integer, Product> productMap = new ConcurrentHashMap<>();
    final ExecutorService auctionExecutor = Executors.newFixedThreadPool(4);

    /**
     * Bill Pugh Singleton implementation.
     */
    private static class Helper {

        private static final RealAuctionHouse instance = new RealAuctionHouse();
    }

    private RealAuctionHouse() {
    }

    public static RealAuctionHouse getInstance() {
        return Helper.instance;
    }

    @Override
    public synchronized List<String> getProducts() {
        return productMap.values().stream()
                .map(Product::toString)
                .collect(Collectors.toList());
    }

    // Composite Design Pattern yoooo
    @Override
    public synchronized void addProduct(Connection conn, Product product) throws SQLException {
        SqlProductUtility.addProduct(conn, product);
        productMap.put(product.getId(), product);
    }

    @Override
    public synchronized void loadProducts(Connection conn) throws SQLException {
        SqlProductUtility.loadProducts(conn).forEach(product -> productMap.put(product.getId(), product));
    }

    @Override
    public synchronized void removeProduct(Connection conn, int id) throws SQLException {
        // TODO remove from database
        productMap.remove(id);
    }

    @Override
    public synchronized void registerClient(Client client, String hash) throws MyException, SQLException {
        SqlClientUtility.registerClient(client, hash);
        clientMap.put(client.getUsername(), client);
    }

    @Override
    public synchronized Client loginClient(String username, String hash) throws MyException, SQLException {
        Client client = SqlClientUtility.loginClient(username, hash);
        clientMap.put(client.getUsername(), client);

        return client;
    }

    @Override
    public synchronized void removeClient(String username) {
        clientMap.remove(username);
    }

    @Override
    public synchronized void offerInit(String username, int productId, double maxPrice, int rate)
            throws MyException {
        if (brokerMap.isEmpty())
            throw new NoBrokersException();
        if (!productMap.containsKey(productId))
            throw new ProductDoesNotExistException(productId);
        if (productMap.get(productId).getAuction().containsParticipant(username, productId))
            throw new BidOnTheSameProductException(productId);

        int index = new Random().nextInt(brokerMap.size()) - 1;
        Broker broker = brokerMap.values().toArray(new Broker[0])[index];

        Product product = productMap.get(productId);
        if (product.getMinPrice() > maxPrice) throw new InvalidPriceException();

        Auction auction = product.getAuction();

        broker.addClient(clientMap.get(username), productId, maxPrice);
        clientMap.get(username).addBid(productId, maxPrice, rate);
        auction.addParticipant(broker);
    }

    @Override
    public synchronized Broker loginBroker(String username, String hash) throws MyException, SQLException {
        Broker broker = SqlBrokerUtility.loginBroker(username, hash);
        brokerMap.put(broker.getUsername(), broker);

        return broker;
    }

    @Override
    public synchronized void registerBroker(Broker broker, String hash) throws MyException, SQLException {
        SqlBrokerUtility.registerBroker(broker, hash);
        brokerMap.put(broker.getUsername(), broker);
    }

    @Override
    public synchronized void removeBroker(String username) {
        brokerMap.remove(username);
    }

    void startAuction(Auction auction) {
        auctionExecutor.execute(auction);
    }

    Pair<String, Double> getMax(List<Pair<String, Double>> currentBids) {
        return Collections.max(currentBids,
                Comparator.comparing((Pair<String, Double> p) -> p.getRight())
                        .thenComparing((Pair<String, Double> p) -> clientMap.get(p.getLeft()).getWonAuctionsNum())
        );

    }

    @Override
    public void stopExecutor() {
        auctionExecutor.shutdown();
    }
}
