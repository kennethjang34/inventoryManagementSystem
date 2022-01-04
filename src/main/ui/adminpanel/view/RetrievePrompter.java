package ui.adminpanel.view;

import model.Admin;
import ui.AbstractLoginAccountPrompter;
import ui.InventoryManagementSystemApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

//A small panel that will be displayed if the user presses retrieve button
public class RetrievePrompter extends AbstractLoginAccountPrompter {
    private JButton retrieveButton;
    private Admin admin = Admin.getAdmin();

    //for singleton pattern
    private static final RetrievePrompter prompter = new RetrievePrompter();

    //EFFECTS: create a new retrieve panel with empty text fields.
    private RetrievePrompter() {
        retrieveButton = new JButton("Retrieve PW");
        add(idLabel);
        add(idField);
        add(nameLabel);
        add(nameField);
        add(birthdayLabel);
        add(birthdayField);
        add(codeLabel);
        add(codeField);
        add(retrieveButton);
    }

    //Singleton pattern applied
    public static RetrievePrompter getRetrievePrompter() {
        return prompter;
    }

    public JButton getRetrieveButton() {
        return retrieveButton;
    }

    public String getPersonalCodeFieldInput() {
        return codeField.getText();
    }

    public String getIdFieldInput() {
        return idField.getText();
    }

    public String getNameFieldInput() {
        return nameField.getText();
    }

    public String getBirthDayFieldInput() {
        return birthdayField.getText();
    }

    public static void displayRetrievePrompter() {
        prompter.setPreferredSize(new Dimension(600, 400));
        JDialog dialog = new JDialog();
        dialog.add(prompter);
        dialog.pack();
        dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        dialog.setVisible(true);
    }

}


