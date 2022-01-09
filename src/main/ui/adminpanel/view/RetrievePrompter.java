package ui.adminpanel.view;

import model.Admin;
import ui.AbstractAdminInputPrompter;

import javax.swing.*;
import java.awt.*;

//A small panel that will be displayed if the user presses retrieve button
public class RetrievePrompter extends AbstractAdminInputPrompter {
    private JButton retrieveButton;
    private static JDialog dialog;

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

    public static void displayRetrievePrompter(Component parentComponent) {
        prompter.setPreferredSize(new Dimension(600, 400));
        dialog = new JDialog();
        dialog.add(prompter);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);
        prompter.emptyPrompter();
    }

    public void emptyPrompter() {
        idField.setText("");
        nameField.setText("");
        birthdayField.setText("");
        codeField.setText("");
    }

    public static JDialog getDialog() {
        return dialog;
    }
}


