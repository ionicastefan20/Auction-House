package com.connection;

import com.connection.server_thread.ServerThread;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.System.*;

public class Server {

    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static boolean keepServerUp = true;
    private static ServerSocket serverSocket;

    private Server() {
    }

    @SneakyThrows
    public static void kill() {
        keepServerUp = false;
        serverSocket.close();
        System.exit(0);
    }

    private static void init() {
        try (ServerSocket serverSocket2 = new ServerSocket(8080)) {
            serverSocket = serverSocket2;
            Socket socket;

            out.println("Server has started...");
            while (keepServerUp) {
                socket = serverSocket.accept();

                ServerThread serverThread = new ServerThread(socket);
                pool.execute(serverThread);
                out.println("A connection has been made...");
            }
            out.println("Server killed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        init();
    }
}



