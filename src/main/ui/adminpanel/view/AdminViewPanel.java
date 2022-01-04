package ui.adminpanel.view;

import model.Account;
import model.Admin;
import ui.table.RowConverterViewerTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//NOTE: planning to make AdminPanel extend Admin directly
//A panel that lets the user deal with administrative jobs
public class AdminViewPanel extends JPanel {
    private Admin admin;
    private RegisterPrompter registerPrompter;
    private RetrievePrompter retrievePrompter;
    private LoginPanel loginPanel;
    private JTable accountsTable;
    private JButton createButton;




    //EFFECTS: create a new admin panel with the given admin and application
    public AdminViewPanel(Admin admin) {
        this.admin = admin;
        createButton = new JButton("Sign-up");
        add(createButton);
        registerPrompter = RegisterPrompter.getRegisterPrompter();
        retrievePrompter = RetrievePrompter.getRetrievePrompter();
        loginPanel = new LoginPanel();
        accountsTable = new JTable();
        accountsTable.setModel(new RowConverterViewerTableModel(admin.getAccounts(), Account.getDataListNames()));
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        add(scrollPane);
        scrollPane.setVisible(false);
//        accountsTable.setVisible(false);
    }

    public void setAccountsTableVisible(boolean visible) {
        accountsTable.setVisible(visible);
    }

    public RegisterPrompter getRegisterPrompter() {
        return registerPrompter;
    }

    public RetrievePrompter getRetrievePrompter() {
        return retrievePrompter;
    }


    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    public JButton getCreateButton() {
        return createButton;
    }





}
