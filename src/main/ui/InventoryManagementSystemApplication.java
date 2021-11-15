package ui;

import model.*;
import ui.inventorypanel.InventoryPanel;
import ui.ledgerpanel.LedgerPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;

public class InventoryManagementSystemApplication extends JFrame {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 850;
    //tabbed pane is only for application panels. Login panel won't have any tabs on it
    private CardLayout cardLayout;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel;
    private InventoryPanel inventoryPanel;
    private LedgerPanel ledgerPanel;
    private AdminPanel adminPanel;
    private JMenuBar menuBar;
    private final Admin admin;
    private final Ledger ledger;
    private final Inventory inventory;

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    InventoryManagementSystemApplication() {
        admin = new Admin();
        ledger = new Ledger();
        inventory = new Inventory();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        tabbedPane = new JTabbedPane();
        inventoryPanel = new InventoryPanel(inventory, this);
        ledgerPanel = new LedgerPanel(ledger);
        adminPanel = new AdminPanel(admin);
        loginPanel = new LoginPanel(admin, this);
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Ledger", ledgerPanel);
        tabbedPane.addTab("Admin", adminPanel);
//        tabbedPane.set
        menuBar = createMenuBar();
        cardLayout.addLayoutComponent(tabbedPane, "ControlPanel");
        cardLayout.addLayoutComponent(loginPanel, "LoginPanel");
        tabbedPane.setBounds(50, 50, 200, 200);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setLayout(cardLayout);
        panel.add(tabbedPane);
        panel.add(loginPanel);
        panel.setVisible(true);
        add(panel);
        setPreferredSize(new Dimension(1500, 2000));
        pack();
        setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: switch to the inventory panel.
    public void switchToControlPanel() {
        cardLayout.show(this, "ControlPanel");
    }

    //MODIFIES: this
    //EFFECTS: switch to the login panel.
    public void switchToLoginPanel() {
        cardLayout.show(this, "LoginPanel");
    }


    //EFFECTS: if there exists the id and password entered by the user, switch to the application panel.
    //Otherwise, display error
    public void loginActionPerformed(ActionEvent e) {

    }


    //MODIFIES: this
    //EFFECTS: creates a new menu bar that has 'load', 'save' menus
    private JMenuBar createMenuBar() {
        return null;
    }


    //REQUIRES: the date must be a string type in YYYYMMDD form without any space in between
    //EFFECTS: convert the date in string form into LocalDate type
    public static LocalDate convertToLocalDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        return LocalDate.of(year, month, day);
    }

    //EFFECTS: return inventory of this
    public Inventory getInventory() {
        return inventory;
    }

    //MODIFIES: this
    //EFFECTS: add a new account to the ledger
    public void addAccount(List<InventoryTag> stocks, String description, LocalDate date) {
        ledgerPanel.addAccount(stocks, description, date);
    }


//
//    //MODIFIES: this
//    //EFFECTS: add a new account to the ledger
//    public void addAccount(List<InventoryTag> tags, String description, LocalDate date) {
//        //
//    }


    //MODIFIES: this
    //EFFECTS: add a new account to the ledger
    public void addAccount(InventoryTag tag, String description, LocalDate date) {
        ledgerPanel.addAccount(tag, description, date);
    }

//    //MODIFIES: this
//    //EFFECTS: add a new account to the ledger
//    public void addAccount(List<Product> products, String description, LocalDate date) {
//
//    }

    public static void main(String[] args) {
        new InventoryManagementSystemApplication();
    }
}

