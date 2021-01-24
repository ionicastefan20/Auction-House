package com.connection;

import com.auction_system.entities.IEntity;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.Administrator;
import com.auction_system.entities.employees.Broker;
import com.auction_system.exceptions.*;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.auction_system.database_system.SqlEntityUtility.*;

public class ServerThread implements Runnable {

    private IEntity entity;
    private final Socket socket;
    private String message;
    private boolean threadUp = true;
    @Getter
    private BufferedReader clientIn;
    @Getter
    private ObjectOutputStream clientOut;

    ServerThread(Socket socket) {
        this.socket = socket;
    }

    private List<String> readMessage() throws IOException {
        message = clientIn.readLine().trim();
        if (message == null) return Collections.emptyList();
        return Arrays.asList(message.split(" "));
    }

    @Override
    public void run() {
        try {
            clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientOut = new ObjectOutputStream(socket.getOutputStream());

            while (threadUp) {
                List<String> words = readMessage();
                if (words.isEmpty()) words.add("a");

                switch (words.get(0)) {
                    case "connect" -> connectCase(words);
                    case "loadProducts" -> loadProductsCase();
                    case "getProducts" -> getProductsCase();
                    case "bid" -> bidCase(words);
//                    TODO addProduct
//                    case "addProduct" -> addProductCase(words);
//                    TODO register
//                    case "register" -> registerCase();
//                    TODO killServer
//                    case "killServer" -> Server.kill();
                    default -> {
                        clientOut.writeObject(new InvalidCommandException());
                        clientOut.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private void connectCase(List<String> words) {
        try {
            if (words.size() != 2) throw new WrongNumberOfArgumentsException();

            String username = words.get(1);
            int whichOne;

            if ("admin".equals(words.get(1))) whichOne = 0;
            else if (checkIfBrokerExists(username)) whichOne = 1;
            else if (checkIfClientExists(username)) whichOne = 2;
            else throw new UserDoesNotExistException(username);

            clientOut.writeObject("Type your password: ");
            clientOut.flush();

            String hash = clientIn.readLine();
            if (whichOne == 0)
                entity = new Administrator(username, hash);
            else if (whichOne == 1)
                entity = Broker.login(username, hash);
            else
                entity = Client.login(username, hash);


            clientOut.writeObject("Connection successful!");
            clientOut.flush();
        } catch (Exception e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }

    // TODO register
    @SneakyThrows
    private void loadProductsCase() {
        try {
            if (entity == null) throw new NotConnectedException();
            if (entity instanceof Administrator) {
                Administrator admin = (Administrator) entity;
                admin.getAuctionHouse().loadProducts(admin.getConn());
                clientOut.writeObject("Products added successfully!");
                clientOut.flush();
            } else
                throw new PermissionDeniedException();
        } catch (Exception e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }
    @SneakyThrows
    private void getProductsCase() {
        try {
            if (entity == null) throw new NotConnectedException();
            List<String> products;
            if (entity instanceof Administrator)
                products = ((Administrator) entity).getAuctionHouse().getProducts();
            else if (entity instanceof Broker)
                products = ((Broker) entity).getAuctionHouse().getProducts();
            else
                products = ((Client) entity).getAuctionHouse().getProducts();

            clientOut.writeObject(products);
            clientOut.flush();
        } catch (Exception e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }

    @SneakyThrows
    private void bidCase(List<String> words) {
        try {
            if (words.size() != 3) throw new WrongNumberOfArgumentsException();
            if (entity == null) throw new NotConnectedException();
            if (entity instanceof Client) {
                Client client = (Client) entity;
                client.getAuctionHouse().offerInit(client.getUsername(),
                        Integer.parseInt(words.get(1)), Double.parseDouble(words.get(2)));
                clientOut.writeObject("Products added successfully!");
                clientOut.flush();
            } else
                throw new PermissionDeniedException();

        } catch (Exception e) {
            e.printStackTrace();
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }
    /*
        private void registerCase() {
            String option;
            String username;
            String name;
            String address;
            String hash;

            try {
                // Username
                clientOut.println("Username: ");
                username = clientIn.readLine();
                if (checkIfBrokerExists(username) || checkIfClientExists(username))
                    throw new UserDoesntExistException(username);

                // Name
                clientOut.println(": ");
                clientOut.println("Register as [individual/legalEntity]: ");
                clientOut.println("Register as [individual/legalEntity]: ");
                clientOut.println("Register as [individual/legalEntity]: ");
                option = clientIn.readLine();
                if ("individual".equalsIgnoreCase(option)) {

                } else if ("legalEntity".equalsIgnoreCase(option)) {
                    // TODO
                } else
                    throw new InvalidOptionException();
            } catch (IOException | InvalidOptionException | SQLException | UserDoesntExistException e) {
                clientOut.println(e.getMessage());
            }
        }
    */

/*
    private void addProductCase(List<String> words) {
        try {
            String productString = words.stream().skip(2)
                    .reduce("", (s1, s2) -> s1 + " " + s2).trim();

            String[] productData = productString.split("@")

            Product.ProductBuilder builder;

            if ("furniture".equals(words.get(1)))
                builder = new Furniture.FurnitureBuilder();

            entity.getProxy().addProduct(

                    .wi
            );
        } catch (IllegalArgumentException e) {
            clientOut.println(e.getMessage());
        }
    }*/
}
