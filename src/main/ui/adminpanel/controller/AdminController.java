package ui.adminpanel.controller;

import model.Admin;
import ui.AbstractController;
import ui.InventoryManagementSystemApplication;
import ui.adminpanel.view.AdminViewPanel;
import ui.adminpanel.view.RegisterPrompter;
import ui.adminpanel.view.RetrievePrompter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;


//control admin + login status
public class AdminController extends AbstractController<Admin, AdminViewPanel> {
    private String loggedInID;
    private InventoryManagementSystemApplication application;

    public AdminController(Admin admin, AdminViewPanel adminViewPanel) {
        super(admin, adminViewPanel);
        this.model = admin;
        this.view = adminViewPanel;
    }

    public void setUpView() {
        setUpRegisterPanel();
        setUpRetrievePanel();
        setUpLoginPanel();
        setUpAdminPanel();
    }


    private void setUpRegisterPanel() {
        RegisterPrompter registerPrompter =  view.getRegisterPrompter();
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
                if (!model.isEmpty()) {
                    if (model.isAdminMember(loggedInID)) {
                        successful = model.createLoginAccount(registerPrompter.getIDInput(), pw, registerPrompter.getNameInput(),
                                birthDay, personalCode, false);
                    } else {
                        registerPrompter.displayPermissionDenied();
//                    JOptionPane.showMessageDialog(this, "you are not allowed to create "
//                            + "a new login account in this system");
                    }
                } else {
                    successful = model.createLoginAccount(registerPrompter.getIDInput(),
                            pw, registerPrompter.getNameInput(), birthDay, personalCode, true);
                    loggedInID = registerPrompter.getIDInput();
                }
                if (successful) {
                    JOptionPane.showMessageDialog(view, "a new account is successfully created");
                    return;
                }
                registerPrompter.displayPermissionDenied();
            }
        });
    }

    private void setUpRetrievePanel() {
        RetrievePrompter retrievePrompter = view.getRetrievePrompter();
        retrievePrompter.getRetrieveButton().addActionListener(new ActionListener() {
            //EFFECTS: show the password of the account matching the given information.
            //If there isn't any, display error message.
            public void actionPerformed(ActionEvent e) {
                int personalCode = Integer.parseInt(retrievePrompter.getPersonalCodeFieldInput());
                LocalDate birthday = InventoryManagementSystemApplication.convertToLocalDate(retrievePrompter.getBirthDayFieldInput());
                String pw = model.retrievePassword(retrievePrompter.getIdFieldInput(),
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
        JButton registerButton = view.getCreateButton();
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterPrompter.displayRegisterPrompter();
            }
        });
    }

    //MODIFIES: loginPanel's login button
    //EFFECTS: the login button will get an action listener for when the button is pressed, which
    //makes a login attempt with the given field inputs in the login panel
    public void setUpLoginPanel() {
        JButton loginButton = view.getLoginPanel().getLoginButton();
        loginButton.addActionListener(new ActionListener() {
            //MODIFIES: this
            //EFFECTS: If the user has tried to sign in, check if the input is valid.
            //If the input is valid, switch to the application panel.
            //Otherwise, display error message.
            //Else if the user pressed a loginButton to create a new account, display a pane to create a new account
            @Override
            public void actionPerformed(ActionEvent e) {
//                String actionCommand = e.getActionCommand();
                String id = view.getLoginPanel().getIdFieldInput();
                char[] pw = view.getLoginPanel().getPwFieldInput();
                Admin.LoginAccount loginAccount = model.getLoginAccount(id, String.valueOf(pw));
                if (loginAccount == null) {
                    view.getLoginPanel().displayLoginFail();
                } else {
                    model.setLoginAccount(loginAccount);
                    view.getLoginPanel().displayLoginSuccessful();
                    if (model.isAdminLoggedIn()) {
                        view.setAccountsTableVisible(true);
                    } else {
                        view.setVisible(false);
                    }
                }
            }
        });

        JButton retrieveButton = view.getLoginPanel().getRetrieveButton();
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RetrievePrompter.displayRetrievePrompter();
            }
        });
    }

    //MODIFIES: this
    //EFFECTS: display the retrieve panel of this
    public static void displayRetrievePrompter() {
        RetrievePrompter.displayRetrievePrompter();
    }

    public static void displayRegisterPrompter() {
        RegisterPrompter.displayRegisterPrompter();
    }


    public void displayLoginDialog() {
        view.getLoginPanel().displayLoginDialog();
    }

    public void logout() {
        model.setLoginAccount(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
