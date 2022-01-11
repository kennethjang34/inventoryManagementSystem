package ui.adminpanel.view;

import model.Account;
import model.Admin;
import ui.table.RowConverterViewerTableModel;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//NOTE: planning to make AdminPanel extend Admin directly
//A panel that lets the user deal with administrative jobs
public class AdminViewPanel extends JPanel {
    private Admin admin;
    private RegisterPrompter registerPrompter;
    private RetrievePrompter retrievePrompter;
    private LoginPanel loginPanel;
    private JTable accountsTable;
    private JButton createButton;


    private KeyListener buttonEnterListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                JButton button = (JButton) e.getSource();
                button.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };


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
        loginPanel.getLoginButton().addKeyListener(buttonEnterListener);
        loginPanel.getCancelButton().addKeyListener(buttonEnterListener);
        loginPanel.getRetrieveButton().addKeyListener(buttonEnterListener);
        retrievePrompter.getRetrieveButton().addKeyListener(buttonEnterListener);
        registerPrompter.getRegisterButton().addKeyListener(buttonEnterListener);
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
