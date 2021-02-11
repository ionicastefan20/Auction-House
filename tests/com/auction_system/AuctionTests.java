package com.auction_system;

import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.Administrator;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.MyException;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuctionTests {

    static final int MAX_PART = 3;
    static List<Pair<String, String>> brokerCreds;
    private static List<Pair<String, String>> clientCreds;

    static Administrator admin;
    static Broker[] brokers;
    static Client[] clients;

    @SneakyThrows
    private static void loadUsers() {
        try (Stream<String> stream = Files.lines(Paths.get("res/tests/brokers"))) {
            brokerCreds = stream.map(s -> s.split(" "))
                    .map(arr -> new ImmutablePair<>(arr[0], DigestUtils.sha3_512Hex(arr[1])))
                    .collect(Collectors.toList());
        }
        try (Stream<String> stream = Files.lines(Paths.get("res/tests/clients"))) {
            clientCreds = stream.map(s -> s.split(" "))
                    .map(arr -> new ImmutablePair<>(arr[0], DigestUtils.sha3_512Hex(arr[1])))
                    .collect(Collectors.toList());
        }
    }

    private static void loadData(int brokerNum, int clientsNum) throws SQLException, MyException {
        loadUsers();
        admin = new Administrator("admin", DigestUtils.sha3_512Hex("admin"));
        brokers = new Broker[brokerNum];
        clients = new Client[clientsNum];

        for (int i = 0; i < brokers.length; i++) {
            int index = new Random().nextInt(brokerCreds.size());
            Pair<String, String> pair = brokerCreds.get(index);
            brokerCreds.remove(index);

            brokers[i] = Broker.login(pair.getLeft(), pair.getRight());
        }

        for (int i = 0; i < clientsNum; i++) {
            int index = new Random().nextInt(clientCreds.size());
            Pair<String, String> pair = clientCreds.get(index);
            clientCreds.remove(index);

            clients[i] = Client.login(pair.getLeft(), pair.getRight());
        }

        admin.loadProducts();
    }

    private static Set<Pair<Integer, Integer>> generateBids(int[] products) {
        Map<Integer, Integer> clientsCount = new HashMap<>();
        Map<Integer, Integer> productsCount = new HashMap<>();
        Set<Pair<Integer, Integer>> bids = new HashSet<>();

        for (int i = 0; i < MAX_PART * products.length; i++) {
            while (true) {
                int client = new Random().nextInt(clients.length);
                int product = new Random().nextInt(products.length);

                if (bids.contains(new ImmutablePair<>(client, product))) continue;

                if (!clientsCount.containsKey(client)) {
                    clientsCount.put(client, 0);
                }
                if (!productsCount.containsKey(product)) {
                    productsCount.put(product, 0);
                }

                if ((clientsCount.get(client) < products.length) && (productsCount.get(product) < MAX_PART)) {
                    clientsCount.replace(client, clientsCount.get(client) + 1);
                    productsCount.replace(product, productsCount.get(product) + 1);
                    bids.add(new ImmutablePair<>(client, product));
                    break;
                }
            }
        }

        return bids;
    }

    @SneakyThrows
    public static void test(int brokerNum, int clientsNum, int[] products, double[] prices) {
        loadData(brokerNum, clientsNum);

        Set<Pair<Integer, Integer>> bids = generateBids(products);

        bids.parallelStream().forEach(p -> {
            int cIndex = p.getLeft();
            int pIndex = p.getRight();
            try {
                double price = prices[pIndex] + (new Random().nextInt(20) * 100);
                out.println(products[pIndex] + ": " + clients[cIndex].getUsername() + " -> " + price);
                clients[cIndex].getAuctionHouse().offerInit(clients[cIndex].getUsername(),
                        products[pIndex], price, new Random().nextInt(10) + 1);
            } catch (MyException e) {
                e.printStackTrace();
            }
        });

        admin.getAuctionHouse().stopExecutor();
    }

    public static void main(String[] args) {
        int which = 4;
        switch (which) {
            case 1 -> test(2, 6, new int[]{100, 101, 103, 105}, new double[]{8500, 7500, 10000, 9000});
            case 2 -> test(2, 6, new int[]{205, 301, 106, 207}, new double[]{3250, 11000, 2750, 8000});
            case 3 -> test(2, 6, new int[]{206, 105, 200, 107}, new double[]{500, 9200, 300, 12000});
            case 4 -> test(2, 6, new int[]{202, 303, 106, 204}, new double[]{5000, 15000, 2250, 4000});
            case 5 -> test(2, 6, new int[]{205, 104, 303, 301}, new double[]{3000, 9000, 14800, 10500});
            case 6 -> test(2, 6, new int[]{105, 300, 106, 206}, new double[]{10000, 7500, 2300, 750});
            case 7 -> test(2, 6, new int[]{205, 204, 303, 203}, new double[]{3250, 4100, 15200, 6000});
            case 8 -> test(2, 6, new int[]{207, 106, 301, 205}, new double[]{8500, 2600, 11500, 3500});
            case 9 -> test(2, 6, new int[]{203, 106, 201, 204}, new double[]{6200, 2650, 2000, 3850});
            case 10 -> test(2, 6, new int[]{303, 300, 107, 105}, new double[]{14000, 11000, 13000, 9750});
            default -> {}
        }
    }
}