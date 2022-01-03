package ui.adminpanel.controller;

import model.Admin;
import ui.AbstractController;
import ui.InventoryManagementSystemApplication;
import ui.adminpanel.view.AdminViewPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;


//control admin + login status
public class AdminController extends AbstractController<Admin, AdminViewPanel> {
    private String loggedInID;

    public AdminController(Admin model, AdminViewPanel view) {
        super(model, view);
    }

    @Override
    public void setUpView() {
        AdminViewPanel.RegisterPrompter registerPrompter =  view.getRegisterPrompter();
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
