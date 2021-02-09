package com.connection;

import com.connection.server_thread.ServerThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.*;

public class Server {

    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static boolean keepServerUp = true;

    private Server() {
    }

    public static void kill() {
        out.println("Server killed...");
        keepServerUp = false;
        exit(0);
    }

    private static void init() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Socket socket;

            out.println("Server has started...");
            while (keepServerUp) {
                socket = serverSocket.accept();

                ServerThread serverThread = new ServerThread(socket);
                pool.execute(serverThread);
                out.println("A connection has been made...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
    }
}



