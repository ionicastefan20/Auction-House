package com.auction_system.auction_house;

import com.auction_system.database_system.*;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.*;
import com.auction_system.products.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

class RealAuctionHouse implements IAdminAH, IBrokerAH, IClientAH {

    // only with bidding clients
    final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    // loads all products at startup
    final Map<Integer, Product> productMap = new ConcurrentHashMap<>();
    private final List<Broker> brokerList = new CopyOnWriteArrayList<>();
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
    public synchronized void registerClient(Client client, String hash) throws UserAlreadyExistsException, SQLException {
        SqlClientUtility.registerClient(client, hash);
        clientMap.put(client.getUsername(), client);
    }

    @Override
    public synchronized Client loginClient(String username, String hash) throws SQLException, WrongPasswordException {
        Client client = SqlClientUtility.loginClient(username, hash);
        clientMap.put(client.getUsername(), client);

        return client;
    }

    @Override
    public synchronized void offerInit(String username, int productId, double maxPrice) throws NoBrokersException, ProductDoesNotExistException, InvalidPriceException {
        if (brokerList.isEmpty()) throw new NoBrokersException();
        if (!productMap.containsKey(productId)) throw new ProductDoesNotExistException(productId);

        Broker broker = brokerList.get(new Random().nextInt(brokerList.size()));

        Product product = productMap.get(productId);
        if (product.getMinPrice() > maxPrice) throw new InvalidPriceException();

        Auction auction = product.getAuction();

        broker.addClient(clientMap.get(username), productId, maxPrice);
        clientMap.get(username).addBid(productId, maxPrice);
        auction.addParticipant(broker);
    }

    @Override
    public synchronized void registerBroker(Broker broker, String hash) throws UserAlreadyExistsException, SQLException {
        SqlBrokerUtility.registerBroker(broker, hash);
        brokerList.add(broker);

    }

    @Override
    public synchronized Broker loginBroker(String username, String hash) throws UserDoesNotExistException, SQLException, WrongPasswordException {
        Broker broker = SqlBrokerUtility.loginBroker(username, hash);
        brokerList.add(broker);

        return broker;
    }

    void startAuction(Auction auction) {
        auctionExecutor.execute(auction);
    }

    @Override
    public void stopExecutor() {
        auctionExecutor.shutdown();
    }
}
