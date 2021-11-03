package ui;

import model.Admin;
import model.Inventory;
import model.Ledger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InventoryManagementSystemApplication extends JFrame {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 850;
    //tabbed pane is only for application panels. Login panel won't have any tabs on it
    private CardLayout cardLayout;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel;
    private JPanel inventoryPanel;
    private JPanel ledgerPanel;
    private JPanel adminPanel;
    private JMenuBar menuBar;
    private final Admin admin;
    private final Ledger ledger;
    private final Inventory inventory;


    InventoryManagementSystemApplication() {
        admin = new Admin();
        ledger = new Ledger();
        inventory = new Inventory();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cardLayout = new CardLayout();
        tabbedPane = new JTabbedPane();
        inventoryPanel = new InventoryPanel(inventory);
        ledgerPanel = new LedgerPanel(ledger);
        //adminPanel = new AdminPanel(admin);
        loginPanel = new LoginPanel(admin, this);
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Ledger", ledgerPanel);
        tabbedPane.addTab("Admin", adminPanel);
        menuBar = createMenuBar();
        cardLayout.addLayoutComponent(tabbedPane, "ControlPanel");
        cardLayout.addLayoutComponent(loginPanel, "LoginPanel");
        setLayout(cardLayout);
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

}

