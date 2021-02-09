package com.connection.server_thread;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.entities.IEntity;
import com.auction_system.entities.clients.Client;
import com.auction_system.entities.clients.ClientFactory;
import com.auction_system.entities.employees.*;
import com.auction_system.exceptions.*;
import com.auction_system.products.ProductFactory;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.System.*;
import static com.auction_system.database_system.SqlUtility.*;

public class ServerThread implements Runnable {

    private static final String SEPARATOR = "\\u007c";

    private IEntity entity;
    private final Socket socket;
    private boolean threadUp = true;
    @Getter
    private BufferedReader clientIn;
    @Getter
    private ObjectOutputStream clientOut;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    private List<String> readMessage() {
        try {
            String message = clientIn.readLine();
            if (message != null) {
                message = message.trim();
                out.println(message);
                return Arrays.asList(message.split(" "));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public void run() {
        try {
            clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientOut = new ObjectOutputStream(socket.getOutputStream());

            while (threadUp) {
                List<String> words = readMessage();
                if (words.isEmpty()) {
                    break;
                }

                switch (words.get(0)) {
                    case "connect" -> connectCase(words);
                    case "loadProducts" -> loadProductsCase();

                    case "getProducts" -> getProductsCase();
                    case "addProduct" -> addProductCase(words);
                    case "bid" -> bidCase(words);
                    case "exit" -> exitCase();
                    case "removeProduct" -> removeProductCase(words);
                    case "register" -> registerCase(words);
                    case "getCommission" -> getCommissionCase();
//                    TODO killServer
//                    case "killServer" -> Server.kill();
                    default -> {
                        clientOut.writeObject(new InvalidCommandException());
                        clientOut.flush();
                    }
                }
            }
            out.println("The connection has been closed...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    private void exceptionHandle(Exception e) {
        e.printStackTrace();
        clientOut.writeObject(e);
        clientOut.flush();
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
            if (whichOne == 0) {
                entity = new Administrator(username, hash);
            } else if (whichOne == 1) {
                entity = Broker.login(username, hash);
            } else {
                entity = Client.login(username, hash);
                ((Client) entity).setServerThread(this);
            }
            clientOut.writeObject("Connection successful!");
            clientOut.flush();
        } catch (MyException | SQLException e) {
            exceptionHandle(e);
        }
    }

    @SneakyThrows
    private void registerCase(List<String> words) {
        try {
            String credString = words.get(2) + "|" + words.stream().skip(4)
                    .reduce("", (s1, s2) -> (s1 + " " + s2)).trim();
            String[] data = credString.split(SEPARATOR);

            if ("client".equals(words.get(1))) {
                Client client = ClientFactory.getClient(
                        (data.length == 4) ? "individual" : "legalEntity", data);
                Client.register(client, words.get(3));
                entity = client;
            } else if ("broker".equals(words.get(1))) {
                Broker broker = new Broker(words.get(2));
                Broker.register(broker, words.get(3));
                entity = broker;
            } else throw new InvalidOptionException();

            clientOut.writeObject("Registered successfully!");
            clientOut.flush();
        } catch (MyException e) {
            exceptionHandle(e);
        }
    }

    @SneakyThrows
    private void loadProductsCase() {
        try {
            if (entity == null) throw new NotConnectedException();
            if (entity instanceof Administrator) {
                Administrator admin = (Administrator) entity;
                admin.loadProducts();

                clientOut.writeObject("Products loaded successfully!");
                clientOut.flush();
            } else
                throw new PermissionDeniedException();
        } catch (MyException e) {
            exceptionHandle(e);
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
            exceptionHandle(e);
        }
    }

    @SneakyThrows
    private void addProductCase(List<String> words) {
        try {
            String productString = words.stream().skip(1)
                    .reduce("", (s1, s2) -> (s1 + " " + s2)).trim();
            String[] data = productString.split(SEPARATOR);

            String type = "";
            if (data[0].charAt(0) == '1') type = "painting";
            else if (data[0].charAt(0) == '2') type = "furniture";
            else type = "jewelry";

            if (!(entity instanceof Administrator)) throw new PermissionDeniedException();
            Administrator administrator = (Administrator) entity;

            administrator.getAuctionHouse().addProduct(administrator.getConn(),
                    ProductFactory.getProduct(type, data));

            clientOut.writeObject("Product added successfully!!");
            clientOut.flush();
        } catch (MyException e) {
            exceptionHandle(e);
        }
    }

    @SneakyThrows
    private void removeProductCase(List<String> words) {
        try {
            if (!(entity instanceof Broker)) throw new PermissionDeniedException();
            Broker broker = (Broker) entity;

            broker.getAuctionHouse().removeProduct(Integer.parseInt(words.get(1)));

            clientOut.writeObject("Product removed successfully!");
            clientOut.flush();
        } catch (MyException e) {
            exceptionHandle(e);
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
            exceptionHandle(e);
        }
    }

    @SneakyThrows
    private void getCommissionCase() {
        try {
            if (!(entity instanceof Broker)) throw new PermissionDeniedException();
            Broker broker = (Broker) entity;

            clientOut.writeObject(broker.getTotalCommission());
            clientOut.flush();
        } catch (MyException e) {
            exceptionHandle(e);
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
}
