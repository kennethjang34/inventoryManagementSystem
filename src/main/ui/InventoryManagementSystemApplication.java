package ui;

import model.*;
import model.Event;
import org.json.JSONObject;
import persistence.JsonConvertible;
import persistence.Reader;
import persistence.Writer;
import ui.adminpanel.AdminPanel;
import ui.inventorypanel.InventoryPanel;
import ui.ledgerpanel.LedgerPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

//An application that manages an inventory/warehouse
public class InventoryManagementSystemApplication extends JFrame implements JsonConvertible, ActionListener {
    private Image image;
    private String imagePath = "./data/seol.gif";
    private static final String fileLocation = "./data/inventory_management_system.json";
    private String description = "WELCOMEEEEEE!";
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 850;
    private boolean login;
    //tabbed pane is only for application panels. Login panel won't have any tabs on it
    private CardLayout cardLayout;
    private JTabbedPane tabbedPane;
    private LoginPanel loginPanel;
    private InventoryPanel inventoryPanel;
    private LedgerPanel ledgerPanel;
    private AdminPanel adminPanel;
    private JMenuBar menuBar;
    private Admin admin;
    private Ledger ledger;
    private Inventory inventory;
    private JPanel mainPanel;

    //EFFECTS: create a new application program
    InventoryManagementSystemApplication() {
        login = false;
        try {
            Reader reader = new Reader(fileLocation);
            JSONObject jsonObject = reader.read();
            admin = new Admin(jsonObject.getJSONObject("admin"));
        } catch (IOException e) {
            admin = new Admin();
        }
//        try {
//            image = ImageIO.read(new File(imagePath));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ii = new ImageIcon(image);
//        image = ii.getImage();
//        JDialog dialog = new JDialog();
//        dialog.setLayout(new BorderLayout());
//        JLabel descriptionLabel = new JLabel();
//        descriptionLabel.setLayout(new FlowLayout());
//        descriptionLabel.setIcon(ii);
//        for (int i = 0; i < 100; i++) {
//            JLabel label = new JLabel(description);
//            label.setForeground(Color.CYAN);
//            descriptionLabel.add(label);
//        }
//        dialog.add(descriptionLabel, BorderLayout.CENTER);
//        dialog.setSize(500, 600);

        ledger = new Ledger();
        inventory = new Inventory();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        cardLayout = new CardLayout();
//        tabbedPane = new JTabbedPane();
//        inventoryPanel = new InventoryPanel(inventory, this);
//        ledgerPanel = new LedgerPanel(ledger);
//        adminPanel = new AdminPanel(admin);
//        loginPanel = new LoginPanel(admin, this);
//        tabbedPane.addTab("Inventory", inventoryPanel);
//        tabbedPane.addTab("Ledger", ledgerPanel);
//        tabbedPane.addTab("Admin", adminPanel);
//        menuBar = createMenuBar();
//        cardLayout.addLayoutComponent(tabbedPane, "ControlPanel");
//        cardLayout.addLayoutComponent(loginPanel, "LoginPanel");
//        tabbedPane.setBounds(50, 50, 200, 200);
//        setSize(100, 200);
        createMainPanel();
//        mainPanel.setLayout(cardLayout);
//        mainPanel.add(tabbedPane, "ControlPanel");
//        mainPanel.add(loginPanel, "LoginPanel");
//        cardLayout.show(mainPanel, "LoginPanel");
        add(mainPanel);
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
        setPreferredSize(new Dimension(1500, 2000));
        pack();
        setVisible(true);
        displayWelcomingDialog();
    }


    //EFFECTS: create a tabbed pane for the main panel
    public void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Inventory", inventoryPanel);
        tabbedPane.addTab("Ledger", ledgerPanel);
        tabbedPane.addTab("Admin", adminPanel);
        tabbedPane.addChangeListener(e -> {
            JTabbedPane pane = (JTabbedPane) e.getSource();
            if (pane.getSelectedIndex() == 2) {
                if (login == false && !admin.isEmpty()) {
                    loginPanel.setPurpose(loginPanel.ADMIN);
                    switchToLoginPanel();
                }
            }
        });
    }

    //MODIFIES: this
    //EFFECTS: set up the main panel(control panel) for the application and return it
    public void createMainPanel() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        //tabbedPane = new JTabbedPane();
        inventoryPanel = new InventoryPanel(inventory, this);

        //inventoryPanel = new InventoryPanel(inventory, this);
        ledgerPanel = new LedgerPanel(ledger);
        adminPanel = new AdminPanel(admin, this);
        if (loginPanel == null) {
            loginPanel = new LoginPanel(admin, this);
        }
        createTabbedPane();
        cardLayout.addLayoutComponent(tabbedPane, "ControlPanel");
        cardLayout.addLayoutComponent(loginPanel, "LoginPanel");
        //tabbedPane.setPreferredSize(new Dimension(400, 500));
        tabbedPane.setBounds(50, 50, 200, 200);
        mainPanel.setLayout(cardLayout);
        mainPanel.add(tabbedPane, "ControlPanel");
        mainPanel.add(loginPanel, "LoginPanel");
        cardLayout.show(mainPanel, "ControlPanel");
    }


    //MODIFIES: this
    //EFFECTS: create a new dialog that welcomes the user with my dog's photo
    //and display it
    public void displayWelcomingDialog() {
        try {
            image = ImageIO.read(new File(imagePath));

        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageIcon ii = new ImageIcon(image);
        //image = ii.getImage();
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
        dialog.setVisible(true);
    }



    //MODIFIES: this
    //EFFECTS: set login true
    public void switchToControlPanel() {
        cardLayout.show(mainPanel, "ControlPanel");
    }



    //MODIFIES: this
    //EFFECTS: switch to the login panel.
    public void switchToLoginPanel() {
//        login = false;
//        updateDisplay();
        cardLayout.show(mainPanel, "LoginPanel");
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
    public void load() {
        try {
//            Admin.LoginAccount account = adminPanel.getLoginAccount();
            Reader reader = new Reader(fileLocation);
            JSONObject jsonObject = reader.read();
            getContentPane().removeAll();
            setVisible(false);
            repaint();

            inventory = new Inventory(jsonObject.getJSONObject("inventory"));
            ledger = new Ledger(jsonObject.getJSONObject("ledger"));
            createMainPanel();

            add(mainPanel);
            menuBar = createMenuBar();
            setJMenuBar(menuBar);
            pack();
            repaint();
            setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "No existing data can be found. please create a new inventory manager");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                    "Data for manager is in wrong format. please create a new inventory manager");
        }
    }


    //MODIFIES: this
    //EFFECTS: turn into the empty program after log-out
    public void openNewFile() {

        getContentPane().removeAll();
        setVisible(false);
        repaint();
        inventory = new Inventory();
        ledger = new Ledger();
        createMainPanel();
        add(mainPanel);
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
        pack();
        repaint();
        setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: creates a new menu bar that has 'load', 'save' menus
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem save = new JMenuItem("Save");
        save.setActionCommand("Save");
        JMenuItem load = new JMenuItem("Load");
        load.setActionCommand("Load");
        JMenuItem quit = new JMenuItem("Quit");
        quit.setActionCommand("Quit");
        JMenuItem logOut = new JMenuItem("Log out");
        logOut.setActionCommand("LogOut");
        save.addActionListener(this);
        load.addActionListener(this);
        quit.addActionListener(this);
        logOut.addActionListener(this);
        file.add(save);
        file.add(load);
        file.add(logOut);
        file.add(quit);
        menuBar.add(file);
        return menuBar;
    }

    //MODIFIES: this
    //EFFECTS: according to the menu selected, perform the requested behaviour
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Quit")) {
            printLog(EventLog.getInstance());
            System.exit(0);
        }

        if (command.equals("Save")) {
            if (!login) {
                loginPanel.setPurpose(LoginPanel.SAVE);
                switchToLoginPanel();
            } else {
                save();
            }
        } else if (command.equals("Load")) {
            if (!login) {
                loginPanel.setPurpose(LoginPanel.LOAD);
                switchToLoginPanel();
            } else {
                load();
            }
        } else if (command.equals("LogOut")) {
            setLoginStatus(false);
        }
    }


    //REQUIRES: the date must be a string type in YYYYMMDD form without any space in between
    //EFFECTS: convert the date in string form into LocalDate type
    public static LocalDate convertToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(4, 6));
            int day = Integer.parseInt(date.substring(6, 8));
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of the date is illegal");
        }
    }

    //EFFECTS: return inventory of this
    public Inventory getInventory() {
        return inventory;
    }

    //MODIFIES: this
    //EFFECTS: add a new account to the ledger
    public void addAccount(List<InventoryTag> stocks, String description, LocalDate date) {
        for (InventoryTag tag: stocks) {
            ledger.addAccount(tag, description, date);
        }
    }

    //MODIFIES: this
    //EFFECTS: add a new account to the ledger
    public void addAccount(InventoryTag tag, String description, LocalDate date) {
        ledgerPanel.addAccount(tag, description, date);
    }

//    //MODIFIES: this
//    //EFFECTS: after succeeding login, set the current to the accont
//    public void setLoginAccount(Admin.LoginAccount account) {
//        adminPanel.setLoginAccount(account);
//    }

    //MODIFIES: this
    //EFFECTS: after succeeding login, set the current to the account
    public void setLoginAccount(String id) {
        adminPanel.setLoginAccount(id);
    }



    //MODIFIES: this
    //EFFECTS: set login status true and save the current state
    public void setLoginStatus(boolean value) {
        if (login == true && value == false) {
            adminPanel.setLoginAccount(null);
            login = false;
            openNewFile();
            return;
        }
        login = value;
    }

    public static void main(String[] args) {
        new InventoryManagementSystemApplication();
    }


    //EFFECTS: convert data of this to JSONObject and return it
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("admin", admin.toJson());
        json.put("inventory", inventory.toJson());
        json.put("ledger", ledger.toJson());
        return json;
    }

    //MODIFIES: this
    //EFFECTS: based on whether the login attempt succeeds or not, or whether the user has cancelled the requiest,
    //save/load data or go back to the control panel
    public void dataChangeHandler(int result) {
        if (result == LoginPanel.CANCEL) {
            switchToControlPanel();
        } else {
            if (result == LoginPanel.LOAD) {
                load();
                switchToControlPanel();
            } else if (result == LoginPanel.SAVE) {
                save();
                JOptionPane.showMessageDialog(this, "System saved");
                switchToControlPanel();
            } else if (result == LoginPanel.ADMIN) {
                switchToControlPanel();
            }
        }
    }

    //EFFECTS: print logs on the console
    public void printLog(EventLog log) {
        for (Event event: log) {
            System.out.println(event);
        }
        log.clear();
    }
}

