package com.gui;

import com.connection.client_conn.ClientConn;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {

    private ClientConn clientConn;

    public ClientFrame() throws HeadlessException {
        super("Best Auction House");

//        clientConn = new ClientConn();
//        clientConn.init();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(new LoginPanel());
        this.setSize(700,400);
//        this.pack();
        this.setVisible(true);

    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
