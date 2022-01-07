package ui;

import model.*;
import org.json.JSONObject;
import persistence.JsonConvertible;
import persistence.Reader;
import persistence.Writer;
import ui.adminpanel.controller.AdminController;
import ui.adminpanel.view.AdminViewPanel;
import ui.inventorypanel.controller.InventoryController;
import ui.inventorypanel.view.InventoryViewPanel;
import ui.ledgerpanel.controller.LedgerController;
import ui.ledgerpanel.view.LedgerViewPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

//An application that manages an inventory/warehouse
public class InventoryManagementSystemApplication extends JFrame implements JsonConvertible, ActionListener, PropertyChangeListener {
    private Image image;
    private String imagePath = "./data/seol.gif";
    private static final String fileLocation = "./data/inventory_management_system.json";
    private String description = "WELCOMEEEEEE!";
    public static final int WIDTH = 1100;
    public static final int HEIGHT = 850;
    private boolean login;
    //tabbed pane is only for application panels. Login panel won't have any tabs on it
    private JTabbedPane tabbedPane;
//    private LoginPanel loginPanel;
//    private LedgerPanel ledgerPanel;
//    private AdminViewPanel adminPanel;
    private AdminController adminController;
    private InventoryController inventoryController;
    private LedgerController ledgerController;
    private JMenuBar menuBar;
    private Admin admin;
    private Ledger ledger;
    private Inventory inventory;
    private JPanel mainPanel;


    public enum MenuItemList {
        SAVE, LOAD, QUIT, LOGIN, LOGOUT
    }

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
        admin.addPropertyChangeListener(Admin.LOGIN, this);
        ledger = new Ledger();
        inventory = new Inventory();
        inventory.addDataChangeListener(ledger);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMainPanel();
        add(mainPanel);
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
//        setPreferredSize(new Dimension(1500, 2000));
        pack();
        setVisible(true);
        displayWelcomingDialog();
    }


    //EFFECTS: create a tabbed pane for the main panel
    public void createTabbedPane() {
        tabbedPane = new JTabbedPane();
        if (inventoryController != null) {
            inventoryController.setInventory(inventory);
        } else {
            inventoryController = new InventoryController(inventory, new InventoryViewPanel(inventory));
        }
        if (ledgerController != null) {
            ledgerController.setLedger(ledger);
        } else {
            ledgerController = new LedgerController(ledger, new LedgerViewPanel(ledger));
        }
        if (adminController != null) {
//            adminController.setAdmin(admin);
        } else {
            adminController = new AdminController(admin, new AdminViewPanel(admin));
        }
        tabbedPane.addTab("Inventory", inventoryController.getView());
        tabbedPane.addTab("Ledger", ledgerController.getView());
        tabbedPane.addTab("Admin", adminController.getView());
        if (admin.isAdminLoggedIn()) {
            tabbedPane.setEnabledAt(2, true);
            tabbedPane.setToolTipTextAt(2, null);
        } else if (!admin.isEmpty()) {
            tabbedPane.setToolTipTextAt(2, "Only admin members can access admin panel");
            tabbedPane.setEnabledAt(2, false);
        }
    }


    //MODIFIES: this
    //EFFECTS: set up the main panel(control panel) for the application and return it
    public void createMainPanel() {
        mainPanel = new JPanel();
        createTabbedPane();
        mainPanel.add(tabbedPane);
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
            inventory.addDataChangeListener(ledger);
            for (Item item: inventory.getItemList()) {
                item.addDataChangeListener(ledger);
            }
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
        JMenuItem login = new JMenuItem("Log-in");
        login.setActionCommand(MenuItemList.LOGIN.toString());
        JMenuItem save = new JMenuItem("Save");
        save.setActionCommand(MenuItemList.SAVE.toString());
        JMenuItem load = new JMenuItem("Load");
        load.setActionCommand(MenuItemList.LOAD.toString());
        JMenuItem quit = new JMenuItem("Quit");
        quit.setActionCommand(MenuItemList.QUIT.toString());
        JMenuItem logOut = new JMenuItem("Log-out");
        logOut.setActionCommand(MenuItemList.LOGOUT.toString());
        save.addActionListener(this);
        load.addActionListener(this);
        quit.addActionListener(this);
        login.addActionListener(this);
        logOut.addActionListener(this);
        file.add(login);
        file.add(logOut);
        file.add(save);
        file.add(load);
        file.add(quit);
        menuBar.add(file);
        if (admin.isLoggedIn()) {
            file.getItem(0).setVisible(false);
        } else {
            file.getItem(1).setVisible(false);
        }
        return menuBar;
    }

    //MODIFIES: this
    //EFFECTS: according to the menu selected, perform the requested behaviour
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (MenuItemList.valueOf(command)) {
            case QUIT:
                System.exit(0);
            case SAVE:
                if (admin.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please create a login account first");
                    adminController.promptAdminRegister();
                } else if (!admin.isLoggedIn()) {
//                loginPanel.setPurpose(LoginPanel.SAVE);
                    adminController.promptLogin();
//                switchToLoginPanel();
                }

                if (admin.isLoggedIn()) {
                    save();
                }

                break;
            case LOAD:
            case LOGIN:
                if (admin.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please create a login account first");
                    adminController.promptAdminRegister();
                } else if (!admin.isLoggedIn()) {
                    adminController.promptLogin();
                }
                if (admin.isLoggedIn()) {
                    load();
                }
                break;
            case LOGOUT:
                adminController.logout();
                break;
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




    public void reset() {
        getContentPane().removeAll();
        setVisible(false);
        repaint();
        inventory = new Inventory();
        ledger = new Ledger();
        inventory.addDataChangeListener(ledger);
        for (Item item: inventory.getItemList()) {
            item.addDataChangeListener(ledger);
        }
        createMainPanel();
        add(mainPanel);
        menuBar = createMenuBar();
        setJMenuBar(menuBar);
        pack();
        repaint();
        setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == admin) {
            //log-in event
            if (evt.getPropertyName().equals(Admin.LOGIN) && (Boolean)evt.getNewValue() == true) {
                if (admin.isAdminLoggedIn()) {
                    tabbedPane.setEnabledAt(2, true);
                    tabbedPane.setToolTipTextAt(2, null);
                }
                // file → login invisible
                menuBar.getMenu(0).getItem(0).setVisible(false);
                menuBar.getMenu(0).getItem(1).setVisible(true);
            }

            //log-out event
            else {
                //file → login visible
                menuBar.getMenu(0).getItem(0).setVisible(true);
                //file → logout invisible
                menuBar.getMenu(0).getItem(1).setVisible(false);
                if (tabbedPane.getSelectedIndex() == 2) {
                    tabbedPane.setSelectedIndex(0);
                }
                tabbedPane.setEnabledAt(2, false);
                tabbedPane.setToolTipTextAt(2, "Only admin members can access admin panel");
                reset();
            }
        }
    }
}

