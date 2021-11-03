package ui;

import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryManagementSystemApplication extends JFrame implements ActionListener {

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
    private Manager manager;

    InventoryManagementSystemApplication(Manager manager) {
        this.manager = manager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cardLayout = new CardLayout();
        tabbedPane = new JTabbedPane();
        inventoryPanel = new InventoryPanel();
        ledgerPanel = new LedgerPanel();
        adminPanel = new AdminPanel();
        loginPanel = new LoginPanel(new Admin());
        tabbedPane.addTab("Inventory", tabbedPane);
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





    //MODIFIES: this
    //EFFECTS: create a panel that prompts the user to sign in. If the attempt is successful, load application panel
    private JPanel createLoginPanel() {
        loginPanel = new JPanel();
        JLabel descriptionLabel = new JLabel(description);
        JLabel idLabel = new JLabel("ID");
        JLabel pwLabel = new JLabel("PW");
        JTextField idField = new JTextField();
        JPasswordField passwordField = new JPasswordField();




        return null;

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
