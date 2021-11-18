package ui.adminpanel;

import model.Admin;
import ui.AbstractLoginAccountPanel;
import ui.InventoryManagementSystemApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class AdminPanel extends JPanel {
    private Admin admin;
    private InventoryManagementSystemApplication application;
    private RegisterPanel registerPanel;
    private Admin.LoginAccount currentAccount;

    public Admin.LoginAccount getLoginAccount() {
        return currentAccount;
    }

    //A small panel that will be displayed if the user presses register button to create a new account
    private class RegisterPanel extends AbstractLoginAccountPanel {
        private JButton registerButton;
        private JPasswordField pwField = new JPasswordField(10);

        //EFFECTS: create a new register panel with empty text fields.
        private RegisterPanel() {
            registerButton = new JButton("Register");
            registerButton.addActionListener(this);
            add(nameLabel);
            add(nameField);
            add(birthdayLabel);
            add(birthdayField);
            add(codeLabel);
            add(codeField);
            add(idLabel);
            add(super.idField);
            add(new JLabel("PW:"));
            add(pwField);
            add(registerButton);
        }


        //REQUIRES: all fields must be in valid form and cannot remain empty
        //MODIFIES: this
        //EFFECTS: create a new login account and register it in the system.
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!admin.isEmpty()) {
                if (admin.isAdminMember(currentAccount)) {
                    int personalCode = Integer.parseInt(this.codeField.getText());
                    String birthdayText = birthdayField.getText();
                    LocalDate birthDay = InventoryManagementSystemApplication.convertToLocalDate(birthdayText);
                    Admin.LoginAccount newAccount = admin.createLoginAccount(idField.getText(),
                            String.valueOf(pwField.getPassword()),
                            nameField.getText(), birthDay, personalCode, Admin.INVENTORY_ACCESS);
                    if (newAccount == null) {
                        displayFailedRegistration();
                        return;
                    }
                    if (currentAccount == null) {
                        currentAccount = newAccount;
                    }
                    application.save();
                    application.setLoginStatus(true);
                    JOptionPane.showMessageDialog(this, "a new account is successfully created");
                } else {
                    JOptionPane.showMessageDialog(this, "you are not allowed to create "
                            + "a new login account in this system");
                }
            } else {
                int personalCode = Integer.parseInt(this.codeField.getText());
                String birthdayText = birthdayField.getText();
                LocalDate birthDay = InventoryManagementSystemApplication.convertToLocalDate(birthdayText);
                Admin.LoginAccount newAccount = admin.createLoginAccount(idField.getText(),
                        String.valueOf(pwField.getPassword()),
                        nameField.getText(), birthDay, personalCode, Admin.ADMIN_ACCESS);
                if (newAccount == null) {
                    displayFailedRegistration();
                    return;
                }
                if (currentAccount == null) {
                    currentAccount = newAccount;
                }
                application.save();
                application.setLoginStatus(true);
                JOptionPane.showMessageDialog(this, "a new account is successfully created");
            }
        }

        private void displayFailedRegistration() {
            JOptionPane.showMessageDialog(null, "Creating a new account failed");
        }
    }

//    //REQUIRES:
//    //MODIFIES: this
//    //EFFECTS: set the purpose of this login attempt
//    public void setPurpose(int purpose) {
//        if (purpose != SAVE && purpose != LOAD) {
//            throw new IllegalArgumentException("The given purpose is not valid");
//        }
//        this.purpose = purpose;
//    }

    //MODIFIES: this
    //EFFECTS: set login account
    public void setLoginAccount(Admin.LoginAccount account) {
        currentAccount = account;
    }

    //MODIFIES: this
    //EFFECTS: set login account
    public void setLoginAccount(String id) {
        assert admin.getLoginAccount(id) != null;
        currentAccount = admin.getLoginAccount(id);
        assert currentAccount != null : id;
    }

    public AdminPanel(Admin admin, InventoryManagementSystemApplication application) {
        this.admin = admin;
        this.application = application;
        registerPanel = new RegisterPanel();
        add(registerPanel);
        setPreferredSize(new Dimension(400, 500));
    }


}
