package ui.adminpanel.controller;

import model.Admin;
import ui.AbstractController;
import ui.InventoryManagementSystemApplication;
import ui.adminpanel.view.AdminViewPanel;
import ui.adminpanel.view.LoginPanel;
import ui.adminpanel.view.RegisterPrompter;
import ui.adminpanel.view.RetrievePrompter;
import ui.table.RowConverterViewerTableModel;
import ui.table.ViewableTableEntryConvertibleModel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;


//controller for admin portion of the application.
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


    //Set up the register panel so that it can report the input to the controller and specify what to happen
    //MODIFIES: Register panel
    private void setUpRegisterPanel() {
        RegisterPrompter registerPrompter =  view.getRegisterPrompter();
        registerPrompter.getRegisterButton().addActionListener(e -> {
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
                        registerPrompter.displayPermissionDenied(view);
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

    //set up the retrieve panel so that it could report the user input and display the proper output.
    //MODIFIES: Retrieve panel
    private void setUpRetrievePanel() {
        RetrievePrompter retrievePrompter = view.getRetrievePrompter();
        retrievePrompter.getRetrieveButton().addActionListener(new ActionListener() {
            //EFFECTS: show the password of the account matching the given information.
            //If there isn't any, display error message.
            public void actionPerformed(ActionEvent e) {
                try {
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
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Given inputs are not in valid format");
                }
            }
        });
    }

    //set up admin panel so that the create button could properly report inputs.
    //if there is no account in the admin, prompt the user to register a new account first
    //MODIFIES: loginPanel
    public void setUpAdminPanel() {
        JButton createButton = view.getCreateButton();
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( model.isEmpty()) {
                    RegisterPrompter.displayRegisterPrompterForAdmin(InventoryManagementSystemApplication.getApplication(),
                            Dialog.ModalityType.APPLICATION_MODAL);
                } else if (model.isAdminLoggedIn()) {
                    RegisterPrompter.displayRegisterPrompter(InventoryManagementSystemApplication.getApplication(),
                            Dialog.ModalityType.APPLICATION_MODAL);
                } else {
                    RegisterPrompter.displayPermissionDenied(InventoryManagementSystemApplication.getApplication());
                }
            }
        });
        setUpAccountsTable(view.getAccountsTable());
    }


    //when the user changes data entries of a login account, update the login account model accordingly
    public void setUpAccountsTable(JTable accountsTable) {
        RowConverterViewerTableModel accountsTableModel = new RowConverterViewerTableModel(model, Admin.ACCOUNT) {
            //Modifies: entry object and row representing that object
            @Override
            public void setValueAt(Object value, int row, int column) {
                ViewableTableEntryConvertibleModel dataModel = tableEntries.get(row);
                String[] columnNames = dataModel.getColumnNames();
                try {
                    model.updateLoginAccount((Admin.LoginAccount) getRowEntryModel(row), columnNames[column], value);
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
            }
        };
        accountsTable.setModel(accountsTableModel);
    }

    //MODIFIES: loginPanel's login button
    //EFFECTS: the login button will get an action listener for when the button is pressed, which
    //makes a login attempt with the given field inputs in the login panel
    public void setUpLoginPanel() {
        LoginPanel loginPanel = view.getLoginPanel();
        JButton loginButton = loginPanel.getLoginButton();
        JButton cancelButton = loginPanel.getCancelButton();
        JButton retrieveButton = loginPanel.getRetrieveButton();
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
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginPanel.getLoginDialog().setVisible(false);
            }
        });
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RetrievePrompter.displayRetrievePrompter(view.getLoginPanel().getLoginDialog());
                RetrievePrompter.getRetrievePrompter().emptyPrompter();
            }
        });
    }


    private void displayAdminRegisterPanel() {
        RegisterPrompter.displayRegisterPrompterForAdmin(InventoryManagementSystemApplication.getApplication(),
                Dialog.ModalityType.APPLICATION_MODAL);
    }

    private void displayLoginDialog() {
        view.getLoginPanel().displayLoginDialog(Dialog.ModalityType.APPLICATION_MODAL);
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


    //It is only when the admin is empty
    public void promptAdminRegister() {
        displayAdminRegisterPanel();
        RegisterPrompter.getRegisterPrompter().emptyPrompter();
    }

    public void promptLogin() {
        displayLoginDialog();
        view.getLoginPanel().clearFields();
    }



}
