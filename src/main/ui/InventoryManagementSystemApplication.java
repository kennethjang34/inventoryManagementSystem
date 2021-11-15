package ui;

import model.*;
import org.json.JSONObject;
import persistence.JsonConvertible;
import persistence.Reader;
import persistence.Writer;
import ui.inventorypanel.InventoryPanel;
import ui.ledgerpanel.LedgerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryManagementSystemApplication extends JFrame implements JsonConvertible {
    private Image image;
    private ImageIcon ii;
    private String imagePath = "./data/seol.gif";
    private static final String fileLocation = "./data/inventory_management_system.json";
    private String description = "WELCOMEEEEEE!";
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 850;
//    private List<JPanel> panels;
    //tabbed pane is only for application panels. Login panel won't have any tabs on it
    private CardLayout cardLayout;
    private JTabbedPane tabbedPane;
    private JPanel loginPanel;
    private InventoryPanel inventoryPanel;
    private LedgerPanel ledgerPanel;
    private AdminPanel adminPanel;
    private JMenuBar menuBar;
    private Admin admin;
    private Ledger ledger;
    private Inventory inventory;
    private JPanel mainPanel;


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    InventoryManagementSystemApplication() {
        try {
            Reader reader = new Reader(fileLocation);
            JSONObject jsonObject = reader.read();
            admin = new Admin(jsonObject.getJSONObject("admin"));
        } catch (IOException e) {
            admin = new Admin();
        }

        try {
            image = ImageIO.read(new File(imagePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
        ii = new ImageIcon(image);
        image = ii.getImage();
        JDialog dialog = new JDialog();
        dialog.setLayout(new BorderLayout());
        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setLayout(new FlowLayout());
        descriptionLabel.setIcon(ii);
        for (int i = 0; i < 100; i++) {
            JLabel label = new JLabel(description);
            label.setForeground(Color.CYAN);
            descriptionLabel.add(label);
        }
        dialog.add(descriptionLabel, BorderLayout.CENTER);
        dialog.setSize(500, 600);


        ledger = new Ledger();
        inventory = new Inventory();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        tabbedPane = new JTabbedPane();
        inventoryPanel = new InventoryPanel(inventory, this);
        ledgerPanel = new LedgerPanel(ledger);
        adminPanel = new AdminPanel(admin);
        loginPanel = new LoginPanel(admin, this);
//        panels.add(ledgerPanel);
//        panels.add(adminPanel);
//        panels.add(ledgerPanel);
//        panels.add(loginPanel);
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Ledger", ledgerPanel);
        tabbedPane.addTab("Admin", adminPanel);
//        tabbedPane.set
        menuBar = createMenuBar();
        cardLayout.addLayoutComponent(tabbedPane, "ControlPanel");
        cardLayout.addLayoutComponent(loginPanel, "LoginPanel");
        tabbedPane.setBounds(50, 50, 200, 200);
        setSize(100, 200);
        mainPanel = new JPanel();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(tabbedPane, "ControlPanel");
        mainPanel.add(loginPanel, "LoginPanel");
        cardLayout.show(mainPanel, "LoginPanel");
        add(mainPanel);
        setJMenuBar(menuBar);
        setPreferredSize(new Dimension(1500, 2000));
        pack();
        setVisible(true);
        dialog.setVisible(true);
    }

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    InventoryManagementSystemApplication(JSONObject jsonObject) {
        try {
            image = ImageIO.read(new File(imagePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
        ii = new ImageIcon(image);
        image = ii.getImage();
        JDialog dialog = new JDialog();
        dialog.setLayout(new FlowLayout());
        JLabel descriptionLabel = new JLabel();
        descriptionLabel.setLayout(new BorderLayout());
        descriptionLabel.setIcon(ii);
        descriptionLabel.add(new JLabel(description), BorderLayout.CENTER);
        dialog.add(descriptionLabel);
        dialog.setSize(500, 600);

        admin = new Admin(jsonObject.getJSONObject("admin"));
        ledger = new Ledger(jsonObject.getJSONObject("ledger"));
        inventory = new Inventory(jsonObject.getJSONObject("inventory"));
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
        mainPanel = new JPanel();
        mainPanel.setLayout(cardLayout);
        mainPanel.add(tabbedPane);
        mainPanel.add(loginPanel);
        cardLayout.show(mainPanel, "LoginPanel");
        add(mainPanel);
        pack();
        setVisible(true);
        dialog.setVisible(true);
    }




    //MODIFIES: this
    //EFFECTS: switch to the inventory panel.
    public void switchToControlPanel() {
        cardLayout.show(mainPanel, "ControlPanel");
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

    //EFFECTS: save the status of the program
    public void save() {
        try {
            Writer writer = new Writer(fileLocation);
            writer.write(this);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("The current file cannot be found");
        }
    }

    //EFFECTS: load the status of the program
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public void load() {
        try {
            Reader reader = new Reader(fileLocation);
            JSONObject jsonObject = reader.read();
            setVisible(false);
            getContentPane().removeAll();
            repaint();
            revalidate();
            admin = new Admin(jsonObject.getJSONObject("admin"));
            assert admin.size() == 1;
            ledger = new Ledger(jsonObject.getJSONObject("ledger"));
            inventory = new Inventory(jsonObject.getJSONObject("inventory"));
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
            menuBar = createMenuBar();
            cardLayout.addLayoutComponent(tabbedPane, "ControlPanel");
            cardLayout.addLayoutComponent(loginPanel, "LoginPanel");
            tabbedPane.setBounds(50, 50, 200, 200);
            mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setLayout(cardLayout);
            mainPanel.add(tabbedPane);
            mainPanel.add(loginPanel);
            mainPanel.setVisible(true);
            add(mainPanel);
            setPreferredSize(new Dimension(1500, 2000));
            pack();
            repaint();
            setVisible(true);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "No existing data can be found. please create a new inventory manager");
            System.out.println("Please create a login account first");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                    "Data for manager is in wrong format. please create a new inventory manager");
        }
    }

    //MODIFIES: this
    //EFFECTS: creates a new menu bar that has 'load', 'save' menus
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem quit = new JMenuItem("Quit");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        file.add(save);
        file.add(load);
        file.add(quit);
        menuBar.add(file);
        return menuBar;
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

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("admin", admin.toJson());
        json.put("inventory", inventory.toJson());
        json.put("ledger", ledger.toJson());
        return json;
    }

}

