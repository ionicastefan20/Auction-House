package com.connection;

import lombok.AccessLevel;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import static java.lang.System.*;

// TODO separate thread for auction details
public class ClientConn {

    @Setter(AccessLevel.PRIVATE)
    private boolean clientUp = true;
    private ObjectInputStream serverIn;
    private BufferedReader keyboard;
    private PrintWriter serverOut;

    private String message;
    private String command;
    private String reply;
    private Object replyObject;

    private ClientConn() {
    }

    public void init() {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), 8080)) {
            serverIn = new ObjectInputStream(socket.getInputStream());
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            serverOut = new PrintWriter(socket.getOutputStream(), true);

            while (clientUp) {
                readInput();
                try {
                    processMessage();
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            out.println(e.getMessage());
        }
    }

    void readInput() throws IOException {
        out.print("> ");
        message = keyboard.readLine();
    }

    void processMessage() throws Exception {
        command = message.trim().split(" ")[0];
        serverOut.println(message);
        processReply();
    }

    void processReply() throws Exception {
        replyObject = serverIn.readObject();
        if (replyObject instanceof Exception)
            throw (Exception) replyObject;

        command = message.split(" ")[0];
        switch (command) {
            case "connect" -> connectCase();
            case "register" -> registerCase();
            case "loadProducts", "bid" -> defaultCase();
            case "getProducts" -> getProductsCase();
        }
    }

    private void connectCase() throws IOException, ClassNotFoundException {
        reply = (String) replyObject;
        out.print(reply);
//        EraserTask et = new EraserTask();
//        Thread mask = new Thread(et);
//        mask.start();

        serverOut.println(DigestUtils.sha3_512Hex(keyboard.readLine())); // give the password
//            et.stopMasking();
//            reply = serverIn.readLine(); // get final message
        reply = (String) serverIn.readObject(); // get final message
        out.println("Auction House: " + reply);
    }

    // TODO register
    private void registerCase() throws IOException {
        reply = (String) replyObject;
        out.print(reply);
        serverOut.println(keyboard.readLine()); // [individual/legalEntity]

        out.println("Auction House: " + reply);
    }

    private void getProductsCase() throws IOException, ClassNotFoundException {
        List<String> products = (List<String>) replyObject;
        products.forEach(out::println);
    }

    private void defaultCase() throws IOException, ClassNotFoundException {
        reply = (String) replyObject;
        out.println(reply);
    }

    public static void main(String[] args) {
        ClientConn clientConn = new ClientConn();
        clientConn.init();
    }

}
