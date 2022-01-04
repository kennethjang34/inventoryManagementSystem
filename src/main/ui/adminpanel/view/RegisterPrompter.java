package ui.adminpanel.view;


import jdk.nashorn.internal.scripts.JD;
import ui.AbstractLoginAccountPrompter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//A small panel that will be displayed if the user presses register button to create a new account
public class RegisterPrompter extends AbstractLoginAccountPrompter {
    private JButton registerButton;
    private JPasswordField pwField = new JPasswordField(10);
    private static final RegisterPrompter prompter = new RegisterPrompter();

    //EFFECTS: create a new register panel with empty text fields.
    private RegisterPrompter() {
        registerButton = new JButton("Register");
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


    public static RegisterPrompter getRegisterPrompter() {
        return prompter;
    }

    public String getBirthDayInput() {
        return birthdayField.getText();
    }

    public String getCodeInput() {
        return codeField.getText();
    }

    public String getNameInput() {
        return nameField.getText();
    }

    public String getIDInput() {
        return idField.getText();
    }

    public char[] getPWInput() {
        return pwField.getPassword();
    }

    public JButton getRegisterButton() {
        return registerButton;
    }


    //EFFECTS: display an option pane that indicates the user doesn't
    //have a permission to create a new login account
    public void displayPermissionDenied() {
        JOptionPane.showMessageDialog(this, "you are not allowed to create a new login account");
    }

    //EFFECTS: display an option pane that indicates the login account wasn't created
    public void displayFailedRegistration() {
        JOptionPane.showMessageDialog(this, "Creating a new account failed");
    }


    public static void displayRegisterPrompter() {
        prompter.setPreferredSize(new Dimension(600, 400));
        JDialog dialog = new JDialog();
        dialog.add(prompter);
        dialog.pack();
        dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        dialog.setVisible(true);
    }


}