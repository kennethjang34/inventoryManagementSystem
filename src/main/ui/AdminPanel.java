package ui;

import model.Admin;

import javax.swing.*;

public class AdminPanel extends JPanel {
    private Admin admin;

    public AdminPanel(Admin admin) {
        this.admin = admin;
        add(new JLabel("TESTING"));
    }
}
