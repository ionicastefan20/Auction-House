package com.gui;

import javax.swing.*;
import java.awt.*;

class LoginPanel extends JPanel {

    private JLabel userLabel;
    private JLabel passLabel;
    private JTextField userText;
    private JPasswordField passText;

    LoginPanel() {
//        this.setLayout(new GridLayout(2, 2, 10, 10));
        this.setLayout(null);
        userLabel = new JLabel("username");
        userLabel.setBounds(40, 40, 60, 20);

        userText = new JTextField();
        userText.setBounds(120, 40, 140, 20);

        passLabel = new JLabel("password");
        passLabel.setBounds(40, 80, 60, 20);

        passText = new JPasswordField();
        passText.setBounds(120, 80, 140, 20);

        this.add(userLabel);
        this.add(userText);
        this.add(passLabel);
        this.add(passText);
    }
}
