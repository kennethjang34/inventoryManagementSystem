package ui.adminpanel;

import model.Admin;
import ui.AbstractLoginAccountPrompter;
import ui.InventoryManagementSystemApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

//NOTE: planning to make AdminPanel extend Admin directly
//A panel that lets the user deal with administrative jobs
public class AdminPanel extends JPanel {
    private Admin admin;
    private InventoryManagementSystemApplication application;
    private RegisterPrompter registerPanel;
    private String logedInID;



    //A small panel that will be displayed if the user presses register button to create a new account
    private class RegisterPrompter extends AbstractLoginAccountPrompter implements ActionListener {
        private JButton registerButton;
        private JPasswordField pwField = new JPasswordField(10);

        //EFFECTS: create a new register panel with empty text fields.
        private RegisterPrompter() {
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
            boolean successful = false;
            int personalCode = Integer.parseInt(this.codeField.getText());
            String birthdayText = birthdayField.getText();
            LocalDate birthDay = InventoryManagementSystemApplication.convertToLocalDate(birthdayText);
            //String id = idField.getText();
            String pw = String.valueOf(pwField.getPassword());
            //String name = nameField.getText();
            if (!admin.isEmpty()) {
                if (admin.isAdminMember(logedInID)) {
                    successful = admin.createLoginAccount(idField.getText(), pw, nameField.getText(),
                             birthDay, personalCode, false);
                } else {
                    displayPermissionDenied();
//                    JOptionPane.showMessageDialog(this, "you are not allowed to create "
//                            + "a new login account in this system");
                }
            } else {
                successful = admin.createLoginAccount(idField.getText(),
                        pw, nameField.getText(), birthDay, personalCode, true);
                logedInID = idField.getText();
            }
            if (successful) {
                application.setLoginStatus(true);
                JOptionPane.showMessageDialog(this, "a new account is successfully created");
                return;
            }
            displayFailedRegistration();
        }

        //EFFECTS: display an option pane that indicates the user doesn't
        //have a permission to create a new login account
        private void displayPermissionDenied() {
            JOptionPane.showMessageDialog(null, "you are not allowed to create a new login account");
        }

        //EFFECTS: display an option pane that indicates the login account wasn't created
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
    public void setLoginAccount(String id) {
        //assert admin.getLoginAccount(id) != null;
        logedInID = id;
    }

    //EFFECTS: create a new admin panel with the given admin and application
    public AdminPanel(Admin admin, InventoryManagementSystemApplication application) {
        this.admin = admin;
        this.application = application;
        registerPanel = new RegisterPrompter();
        add(registerPanel);
        setPreferredSize(new Dimension(400, 500));
    }


}
