package ui.adminpanel.view;

import model.Account;
import model.Admin;
import ui.table.RowConverterViewerTableModel;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import java.awt.*;
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
        createButton = new JButton("Create a new account");
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600 , 850));
        registerPrompter = RegisterPrompter.getRegisterPrompter();
        retrievePrompter = RetrievePrompter.getRetrievePrompter();
        loginPanel = new LoginPanel();
        accountsTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (getColumnName(column).equals(Admin.ColumnNameEnum.ID.toString())) {
                    return false;
                }
                return true;
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        add(scrollPane, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth - 1;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(createButton, gbc);
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

    public JTable getAccountsTable() {
        return accountsTable;
    }


}
