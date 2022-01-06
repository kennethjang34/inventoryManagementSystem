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

    public AdminController(Admin admin, AdminViewPanel adminViewPanel) {
        super(admin, adminViewPanel);
    }

    public void setUpView() {
        setUpRegisterPanel();
        setUpRetrievePanel();
        setUpLoginPanel();
        setUpAdminPanel();
    }


    private void setUpRegisterPanel() {
        RegisterPrompter registerPrompter =  view.getRegisterPrompter();
        registerPrompter.getRegisterButton().addActionListener(e -> {
            System.out.println(registerPrompter.getRegisterButton().getActionListeners().length);
            int personalCode = Integer.parseInt(registerPrompter.getCodeInput());
            String birthdayText = registerPrompter.getBirthDayInput();
            LocalDate birthDay = InventoryManagementSystemApplication.convertToLocalDate(birthdayText);
            String pw = String.valueOf(registerPrompter.getPWInput());
            try {
                if (!model.isEmpty()) {
                    if (model.isAdminLoggedIn()) {
                        if (registerPrompter.getCheckBox().isSelected()) {
                            model.createLoginAccount(registerPrompter.getIDInput(), pw, registerPrompter.getNameInput(),
                                    birthDay, personalCode, true);
                        } else {
                            model.createLoginAccount(registerPrompter.getIDInput(), pw, registerPrompter.getNameInput(),
                                    birthDay, personalCode, false);
                        }
                    } else {
                        registerPrompter.displayPermissionDenied();
                    }
                } else {
                    model.setLoginAccount(model.createLoginAccount(registerPrompter.getIDInput(),
                            pw, registerPrompter.getNameInput(), birthDay, personalCode, true));
                }
                JDialog registerDialog = RegisterPrompter.getDialog();
                JOptionPane.showMessageDialog(registerDialog, "a new account is successfully created");
                registerDialog.setVisible(false);
            } catch (IllegalArgumentException illegalArgumentException) {
                displayExceptionMessage(illegalArgumentException);
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
                    JOptionPane.showMessageDialog(RetrievePrompter.getDialog(), "The password is : " + pw);
                    RetrievePrompter.getDialog().setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Given info is not correct");
                }
            }
        });
    }

    public void setUpAdminPanel() {
        JButton createButton = view.getCreateButton();
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( model.isEmpty()) {
                    RegisterPrompter.displayRegisterPrompterForAdmin();
                } else if (model.isAdminLoggedIn()) {
                    RegisterPrompter.displayRegisterPrompter();
                } else {
                    RegisterPrompter.displayPermissionDenied();
                }
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
                Admin.LoginAccount loginAccount = model.getAccount(id, String.valueOf(pw));
                if (loginAccount == null) {
                    view.getLoginPanel().displayLoginFail();
                } else {
                    model.setLoginAccount(loginAccount);
                    view.getLoginPanel().displayLoginSuccessful();
                    view.getLoginPanel().getLoginDialog().setVisible(false);
                    if (model.isAdminLoggedIn()) {
                        view.setAccountsTableVisible(true);
                    } else {
                        view.setAccountsTableVisible(false);
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
    private void displayRetrievePrompter() {
        RetrievePrompter.displayRetrievePrompter();
    }

    public void promptRetrievePrompter() {
        displayRetrievePrompter();
    }

    private void displayRegisterPrompter() {
        RegisterPrompter.displayRegisterPrompter();
    }

    public void promptAdminRegister() {
        displayAdminRegisterPanel();
    }

    private void displayAdminRegisterPanel() {
        RegisterPrompter.displayRegisterPrompterForAdmin();
    }

    public void promptRegisterPanel() {
        displayRegisterPrompter();
    }



    private void displayLoginDialog() {
        view.getLoginPanel().displayLoginDialog();
    }

    public void logout() {
        model.setLoginAccount(null);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }


    public void displayExceptionMessage(Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage());
    }


    public void setAdmin(Admin admin) {

    }


    public void promptLogin() {
        displayLoginDialog();
    }
}
