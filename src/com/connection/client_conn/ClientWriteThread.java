package com.connection.client_conn;

import com.auction_system.auction_house.AuctionResult;
import com.auction_system.exceptions.MyException;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
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
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }
    }

    private void readInput() throws IOException {
        out.print("> ");
        message = conn.keyboard.readLine();
    }

    private void processMessage() throws InterruptedException, IOException, SQLException, MyException {
        command = message.trim().split(" ")[0];
        if ("check".equals(command))
            auctionChecks();
        else {
            if ("register".equals(command))
                registerFilter();

            conn.serverOut.println(message);
            Thread.sleep(100);
            processReply();
        }
    }

    private void registerFilter() {
        String[] data = message.trim().split(" ");
        data[3] = DigestUtils.sha3_512Hex(data[3]);
        message = Arrays.stream(data).reduce("", (s1, s2) -> (s1 + " " + s2)).trim();
    }

    private void auctionChecks() throws InterruptedException {
        if (blockingQueue.isEmpty()) out.println("Nothing there...");
        else {
            while (!blockingQueue.isEmpty()) {
                replyObject = blockingQueue.take();
                if (replyObject instanceof AuctionResult) {
                    out.print(replyObject);
//                replyObject = blockingQueue.take();
                } else {
                    break;
                }
            }
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
            case "register", "loadProducts", "addProduct", "removeProduct", "bid" -> defaultCase();
            case "getProducts" -> getProductsCase();
            case "getCommission" -> getCommissionCase();
            default -> out.println("No response!");
        }
    }

    private void connectCase() throws IOException, InterruptedException {
        reply = (String) replyObject;
        out.print(reply);

        conn.serverOut.println(DigestUtils.sha3_512Hex(conn.keyboard.readLine()));

        replyObject = blockingQueue.take();
        try {
            if (replyObject instanceof MyException)
                throw (MyException) replyObject;
            else if (replyObject instanceof SQLException)
                throw (SQLException) replyObject;
        } catch (Exception e) {
            out.println(e.getMessage());
            e.printStackTrace();
        }
        reply = (String) replyObject;
        out.println("Auction House: " + reply);
    }

    private void getProductsCase() {
        List<String> products = (List<String>) replyObject;
        products.forEach(out::println);
    }

    private void getCommissionCase() {
        out.println((double) replyObject);
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
