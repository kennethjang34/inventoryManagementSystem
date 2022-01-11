package ui.adminpanel.view;


import ui.AbstractAdminInputPrompter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//A small panel that will be displayed if the user presses register button to create a new account
public class RegisterPrompter extends AbstractAdminInputPrompter {
    private JButton registerButton;
//    private JPasswordField pwField = new JPasswordField(10);
    private JCheckBox checkBox;
    private static final RegisterPrompter prompter = new RegisterPrompter();
    private static JDialog dialog;



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
        add(pwLabel);
        add(pwField);
        checkBox = new JCheckBox("is an admin member?");
        checkBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ((Component)e.getSource()).transferFocus();
                }
            }
        });
        checkBox.setBounds(new Rectangle(100, 100));
        add(checkBox);
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

    public JCheckBox getCheckBox() {
        return checkBox;
    }


    //EFFECTS: display an option pane that indicates the user doesn't
    //have a permission to create a new login account
    public static void displayPermissionDenied(Component parentComponent) {
        JOptionPane.showMessageDialog(parentComponent, "you are not allowed to create a new login account");
    }

    //EFFECTS: display an option pane that indicates the login account wasn't created
    public static void displayFailedRegistration() {
        JOptionPane.showMessageDialog(null, "Creating a new account failed");
    }


    public static void displayRegisterPrompter(Component parentComponent, Dialog.ModalityType modalityType) {
        prompter.setPreferredSize(new Dimension(600, 400));
        dialog = new JDialog();
        dialog.add(prompter);
        dialog.setModalityType(modalityType);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
    }

    public static void displayRegisterPrompterForAdmin(Component parentComponent, Dialog.ModalityType modalityType) {
        prompter.getCheckBox().setSelected(true);
        prompter.getCheckBox().setEnabled(false);
        prompter.setPreferredSize(new Dimension(600, 400));
        dialog = new JDialog();
        dialog.add(prompter);
        dialog.setModalityType(modalityType);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
        prompter.getCheckBox().setEnabled(true);
        prompter.getCheckBox().setSelected(false);
    }

    public void emptyPrompter() {
        idField.setText("");
        pwField.setText("");
        nameField.setText("");
        birthdayField.setText("");
        codeField.setText("");
    }

    public static JDialog getDialog() {
        return dialog;
    }
}