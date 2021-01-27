package com.connection;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.entities.IEntity;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.employees.*;
import com.auction_system.exceptions.*;
import com.auction_system.products.Product;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import static com.auction_system.products.Furniture.FurnitureBuilder;
import static com.auction_system.products.Jewelry.JewelryBuilder;
import static com.auction_system.products.Painting.PaintingBuilder;
import static com.auction_system.products.Painting.Colors;

import static com.auction_system.database_system.SqlEntityUtility.*;

public class ServerThread implements Runnable {

    private IEntity entity;
    private final Socket socket;
    private boolean threadUp = true;
    @Getter
    private BufferedReader clientIn;
    @Getter
    private ObjectOutputStream clientOut;

    ServerThread(Socket socket) {
        this.socket = socket;
    }

    private List<String> readMessage() throws IOException {
        String message = clientIn.readLine().trim();
//        if (message == null) return Collections.emptyList();
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
                    case "addProduct" -> addProductCase(words);
                    case "bid" -> bidCase(words);
                    case "exit" -> exitCase();
//                    case "removeProduct -> removeProductCase(words);
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
//            hash = DigestUtils.sha3_512Hex(hash);
            if (whichOne == 0) {
                entity = new Administrator(username, hash);
            } else if (whichOne == 1) {
                entity = Broker.login(username, hash);
            } else {
                entity = Client.login(username, hash);

                // TODO add serverThread to constructor
                ((Client) entity).setServerThread(this);
            }
            clientOut.writeObject("Connection successful!");
            clientOut.flush();
        } catch (MyException e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }

    @SneakyThrows
    private void loadProductsCase() {
        try {
            if (entity == null) throw new NotConnectedException();
            if (entity instanceof Administrator) {
                Administrator admin = (Administrator) entity;
                admin.getAuctionHouse().loadProducts(admin.getConn());
                clientOut.writeObject("Products loaded successfully!");
                clientOut.flush();
            } else
                throw new PermissionDeniedException();
        } catch (MyException e) {
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
        } catch (MyException e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }

    @SneakyThrows
    private void addProductCase(List<String> words) {
        try {
            String productString = words.stream().skip(2)
                    .reduce("", (s1, s2) -> (s1 + " " + s2));
            String[] data = productString.substring(1).split("\\u007c");

            if (!(entity instanceof Administrator)) throw new PermissionDeniedException();
            Administrator administrator = (Administrator) entity;
            Product.ProductBuilder builder = null;

            int productId = Integer.parseInt(data[0]);
            double minPrice = Double.parseDouble(data[2]);
            int year = Integer.parseInt(data[3]);

            if ("furniture".equalsIgnoreCase(words.get(1)))
                builder = new FurnitureBuilder(productId, minPrice)
                        .withType(data[4])
                        .withMaterial(data[5]);
            else if ("jewelry".equalsIgnoreCase(words.get(1)))
                builder = new JewelryBuilder(productId, minPrice)
                        .withMaterial(data[4])
                        .withPreciousStone("yes".equals(data[5]));
            else if ("painting".equalsIgnoreCase(words.get(1)))
                builder = new PaintingBuilder(productId, minPrice)
                        .withArtistName(data[4])
                        .withColor(Colors.valueOf(data[5]));
            else throw new InvalidOptionException();

            administrator.getAuctionHouse().addProduct(administrator.getConn(), builder.withName(data[1])
                    .withYear(year)
                    .build());

            clientOut.writeObject("Product added successfully!");
            clientOut.flush();
        } catch (MyException e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }

    @SneakyThrows
    private void bidCase(List<String> words) {
        try {
            if (words.size() != 4)
                throw new WrongNumberOfArgumentsException();
            if (entity == null) throw new NotConnectedException();

            if (entity instanceof Client) {
                Client client = (Client) entity;
                client.getAuctionHouse().offerInit(client.getUsername(), Integer.parseInt(words.get(1)),
                        Double.parseDouble(words.get(2)), Integer.parseInt(words.get(3)));
            } else
                throw new PermissionDeniedException();

            clientOut.writeObject("Bid added successfully!");
            clientOut.flush();
        } catch (MyException e) {
            clientOut.writeObject(e);
            clientOut.flush();
        }
    }

    @SneakyThrows
    private void exitCase() {
        if (entity == null) return;

        if (entity instanceof Administrator)
            ((Administrator) entity).closeConn();
        else if (entity instanceof Broker)
            ((Broker) entity).getAuctionHouse().removeBroker(((Broker) entity).getUsername());
        else
            ((Client) entity).getAuctionHouse().removeClient(((Client) entity).getUsername());
        entity = null;
    }

    public void sendResult(AuctionResult result) {
        try {
            clientOut.writeObject(result);
            clientOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
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
                } else
                    throw new InvalidOptionException();
            } catch (IOException | InvalidOptionException | SQLException | UserDoesntExistException e) {
                clientOut.println(e.getMessage());
            }
        }
    */

}
