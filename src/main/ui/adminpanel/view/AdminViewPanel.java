package ui.adminpanel.view;

import model.Admin;
import ui.AbstractLoginAccountPrompter;
import ui.InventoryManagementSystemApplication;
import ui.table.RowConverterViewerTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

//NOTE: planning to make AdminPanel extend Admin directly
//A panel that lets the user deal with administrative jobs
public class AdminViewPanel extends JPanel {
    private Admin admin;
    private RegisterPrompter registerPanel;
    private JTable accountsTable;

//
//    //MODIFIES: this
//    //EFFECTS: set login account
//    public void setLoginAccount(String id) {
//        //assert admin.getLoginAccount(id) != null;
////        loggedInID = id;
//    }

    //EFFECTS: create a new admin panel with the given admin and application
    public AdminViewPanel(Admin admin) {
        this.admin = admin;
        registerPanel = RegisterPrompter.getRegisterPrompter();
        accountsTable = new JTable();
        accountsTable.setModel(new RowConverterViewerTableModel(admin.getAccounts()));
        add(new JScrollPane(accountsTable));
        accountsTable.setVisible(false);
    }

    public void setAccountsTableVisible(boolean visible) {
        accountsTable.setVisible(visible);
    }

    public RegisterPrompter getRegisterPrompter() {
        return registerPanel;
    }




}
