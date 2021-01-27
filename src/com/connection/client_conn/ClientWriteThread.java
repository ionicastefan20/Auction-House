package com.connection.client_conn;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.exceptions.MyException;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static java.lang.System.*;

public class ClientWriteThread extends Thread {

    private final ClientConn conn;

    private String message;
    private String command;
    private String reply;
    private Object replyObject;

    private final BlockingQueue<Object> blockingQueue;

    public ClientWriteThread(BlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
        conn = ClientConn.getInstance();
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!this.isInterrupted()) {
            readInput();
            try {
                processMessage();
                Thread.sleep(100);
                processReply();
            } catch (Exception e) {
                out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void readInput() throws IOException {
        out.print("> ");
        message = conn.keyboard.readLine();
    }

    private void processMessage() {
        command = message.trim().split(" ")[0];
        conn.serverOut.println(message);
    }

    private void auctionChecks() throws InterruptedException {
        replyObject = blockingQueue.take();
        while (replyObject instanceof AuctionResult) {
            out.print(replyObject);
            replyObject = blockingQueue.take();
        }
    }

    private void processReply() throws MyException, SQLException, IOException, InterruptedException {
        if ("exit".equals(command)) {
            exitCase();
            return;
        }
        auctionChecks();

        if (replyObject instanceof MyException)
            throw (MyException) replyObject;
        else if (replyObject instanceof SQLException)
            throw (SQLException) replyObject;

        switch (command) {
            case "connect" -> connectCase();
            case "register" -> registerCase();
            case "loadProducts", "addProduct", "bid" -> defaultCase();
            case "getProducts" -> getProductsCase();
            default -> out.println("No response!");
        }
    }

    private void connectCase() throws IOException, InterruptedException {
        reply = (String) replyObject;
        out.print(reply);

        conn.serverOut.println(DigestUtils.sha3_512Hex(conn.keyboard.readLine())); // give the password

        auctionChecks();
        reply = (String) replyObject;
        out.println("Auction House: " + reply);
    }

    // TODO register
    private void registerCase() throws IOException {
        reply = (String) replyObject;
        out.print(reply);
        conn.serverOut.println(conn.keyboard.readLine()); // [individual/legalEntity]

        out.println("Auction House: " + reply);
    }

    private void getProductsCase() {
        List<String> products = (List<String>) replyObject;
        products.forEach(out::println);
    }

    private void exitCase() {
        conn.clientUp = false;
        conn.crt.interrupt();
        this.interrupt();
        blockingQueue.clear();
    }

    private void defaultCase() {
        reply = (String) replyObject;
        out.println(reply);
    }
}
