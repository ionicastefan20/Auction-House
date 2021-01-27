package com.gui;

import javax.swing.*;
import java.awt.*;

class LoginPanel extends JPanel {

    private JPanel panel11, panel12, panel1, panel2;
    private JLabel userLabel;
    private JLabel passLabel;
    private JTextField userText;
    private JPasswordField passText;
    private JButton login;
    private static final int BORDER = 10;

    LoginPanel() {
        this.setLayout(new GridLayout(2, 1));
        this.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));

        panel11 = new JPanel(new GridLayout(2, 1));
        panel12 = new JPanel(new GridLayout(2, 1));
        panel1 = new JPanel(new FlowLayout());
        panel1.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));

        userLabel = new JLabel("username");
        userLabel.setSize(50,20);
        userLabel.setBorder(BorderFactory.createMatteBorder(BORDER, BORDER, BORDER, BORDER, Color.lightGray));
        panel11.add(userLabel);

        passLabel = new JLabel("password");
        passLabel.setSize(50,20);
        passLabel.setBorder(BorderFactory.createMatteBorder(BORDER, BORDER, BORDER, BORDER, Color.lightGray));
        panel11.add(passLabel);

        userText = new JTextField(25);
        userText.setBorder(BorderFactory.createMatteBorder(BORDER, BORDER, BORDER, BORDER, Color.lightGray));
        panel12.add(userText);

        passText = new JPasswordField(25);
        passText.setBorder(BorderFactory.createMatteBorder(BORDER, BORDER, BORDER, BORDER, Color.lightGray));
        panel12.add(passText);

        panel1.add(panel11);
        panel1.add(panel12);

        panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));

        login = new JButton("LOGIN");
        login.setBorder(BorderFactory.createMatteBorder(BORDER, BORDER, BORDER, BORDER, Color.lightGray));
        login.setSize(50, 30);
        panel2.add(login);

        this.add(panel1);
        this.add(panel2);
    }
}
