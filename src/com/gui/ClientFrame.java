package com.gui;

import com.connection.ClientConn;

import javax.swing.*;
import java.awt.*;

public class ClientFrame extends JFrame {

    private ClientConn clientConn;

    public ClientFrame() throws HeadlessException {
        super("Best Auction House");

//        clientConn = new ClientConn();
//        clientConn.init();

//        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(new LoginPanel());
        this.setBounds(200, 200, 320, 200);
        this.setResizable(false);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new ClientFrame();
    }
}
