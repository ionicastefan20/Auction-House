package com.connection.client_conn;

import lombok.AccessLevel;
import lombok.Setter;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.*;

public class ClientConn {
    @Setter(AccessLevel.PRIVATE)
    boolean clientUp = true;
    ObjectInputStream serverIn;
    BufferedReader keyboard;
    PrintWriter serverOut;

    final ExecutorService executorService = Executors.newFixedThreadPool(2);
    final BlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(1024);
    ClientWriteThread cwt;
    ClientReadThread crt;

    private ClientConn() {
    }

    private void init() {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), 8080)) {
            serverIn = new ObjectInputStream(socket.getInputStream());
            keyboard = new BufferedReader(new InputStreamReader(System.in));
            serverOut = new PrintWriter(socket.getOutputStream(), true);

            cwt = new ClientWriteThread(blockingQueue);
            crt = new ClientReadThread(blockingQueue);

            cwt.start();
            crt.start();

            while (!(cwt.isInterrupted() && crt.isInterrupted())) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            out.println(e.getMessage());
        }
    }

    private static class Helper {
        private static final ClientConn instance = new ClientConn();

        private Helper() {}
    }

    static ClientConn getInstance() {
        return Helper.instance;
    }

    public static void main(String[] args) {
        ClientConn conn = getInstance();
        conn.init();
    }

}
