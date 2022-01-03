package ui.adminpanel.controller;

import model.Admin;
import ui.AbstractController;
import ui.InventoryManagementSystemApplication;
import ui.adminpanel.view.AdminViewPanel;
import ui.adminpanel.view.LoginPanel;
import ui.adminpanel.view.RegisterPrompter;
import ui.adminpanel.view.RetrievePrompter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;

import static ui.adminpanel.view.LoginPanel.RETRIEVE;


//control admin + login status
public class AdminController {
    private Admin admin;
    private AdminViewPanel adminViewPanel;
    private LoginPanel loginPanel;
    private String loggedInID;
    private InventoryManagementSystemApplication application;


    public AdminController(Admin admin, AdminViewPanel adminViewPanel, LoginPanel loginPanel) {
        this.admin = admin;
        this.adminViewPanel = adminViewPanel;
        this.loginPanel = loginPanel;
        setUpView();
    }

    public void setUpView() {
        setUpRegisterPanel();
        setUpRetrievePanel();
    }


    private void setUpRegisterPanel() {
        RegisterPrompter registerPrompter =  adminViewPanel.getRegisterPrompter();
        registerPrompter.getRegisterButton().addActionListener(new ActionListener() {
            //REQUIRES: all fields must be in valid form and cannot remain empty
            //MODIFIES: this
            //EFFECTS: create a new login account and register it in the system.
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean successful = false;
                int personalCode = Integer.parseInt(registerPrompter.getCodeInput());
                String birthdayText = registerPrompter.getBirthDayInput();
                LocalDate birthDay = InventoryManagementSystemApplication.convertToLocalDate(birthdayText);
                //String id = idField.getText();
                String pw = String.valueOf(registerPrompter.getPWInput());
                //String name = nameField.getText();
                if (!admin.isEmpty()) {
                    if (admin.isAdminMember(loggedInID)) {
                        successful = admin.createLoginAccount(registerPrompter.getIDInput(), pw, registerPrompter.getNameInput(),
                                birthDay, personalCode, false);
                    } else {
                        registerPrompter.displayPermissionDenied();
//                    JOptionPane.showMessageDialog(this, "you are not allowed to create "
//                            + "a new login account in this system");
                    }
                } else {
                    successful = admin.createLoginAccount(registerPrompter.getIDInput(),
                            pw, registerPrompter.getNameInput(), birthDay, personalCode, true);
                    loggedInID = registerPrompter.getIDInput();
                }
                if (successful) {
                    JOptionPane.showMessageDialog(adminViewPanel, "a new account is successfully created");
                    return;
                }
                registerPrompter.displayPermissionDenied();
            }
        });
    }

    private void setUpRetrievePanel() {
        RetrievePrompter retrievePrompter = loginPanel.getRetrievePrompter();
        retrievePrompter.getRetrieveButton().addActionListener(new ActionListener() {
            //EFFECTS: show the password of the account matching the given information.
            //If there isn't any, display error message.
            public void actionPerformed(ActionEvent e) {
                int personalCode = Integer.parseInt(retrievePrompter.getPersonalCodeFieldInput());
                LocalDate birthday = InventoryManagementSystemApplication.convertToLocalDate(retrievePrompter.getBirthDayFieldInput());
                String pw = admin.retrievePassword(retrievePrompter.getIdFieldInput(),
                        retrievePrompter.getNameFieldInput(), birthday, personalCode);
                if (pw != null) {
                    JOptionPane.showMessageDialog(null, "The password is : " + pw);
                } else {
                    JOptionPane.showMessageDialog(null, "Given info is not correct");
                }
                retrievePrompter.setVisible(false);
            }
        });
    }

    public void setUpAdminPanel() {

    }

    //MODIFIES: loginPanel's login button
    //EFFECTS: the login button will get an action listener for when the button is pressed, which
    //makes a login attempt with the given field inputs in the login panel
    public void setUpLoginPanel() {
        JButton button = loginPanel.getLoginButton();
        button.addActionListener(new ActionListener() {
            //MODIFIES: this
            //EFFECTS: If the user has tried to sign in, check if the input is valid.
            //If the input is valid, switch to the application panel.
            //Otherwise, display error message.
            //Else if the user pressed a button to create a new account, display a pane to create a new account
            @Override
            public void actionPerformed(ActionEvent e) {
                String actionCommand = e.getActionCommand();
                String id = loginPanel.getIdFieldInput();
                char[] pw = loginPanel.getPwFieldInput();
                Admin.LoginAccount loginAccount = admin.getLoginAccount(id, String.valueOf(pw));
                if (loginAccount == null) {
                    loginPanel.displayLoginFail();
                } else {
                    admin.setLoginAccount(loginAccount);
                    if (loginPanel.getPurpose() != LoginPanel.ADMIN || admin.isAdminMember(id)) {
                        application.setLoginStatus(true);
                        application.setLoginAccount(id);
                        application.dataChangeHandler(loginPanel.getPurpose());
                    } else {
                        loginPanel.displayAdminLoginFail();
                    }
                }
            }
        });
    }


}
