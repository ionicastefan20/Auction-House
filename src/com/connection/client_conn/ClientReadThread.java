package com.connection.client_conn;

import java.util.concurrent.BlockingQueue;

import static java.lang.System.*;

public class ClientReadThread extends Thread {

    private final ClientConn conn;

    private final BlockingQueue<Object> blockingQueue;

    public ClientReadThread(BlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
        conn = ClientConn.getInstance();
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                blockingQueue.put(conn.serverIn.readObject());
            } catch (Exception e) {
                out.println(e.getMessage());
            }
        }
    }
}
