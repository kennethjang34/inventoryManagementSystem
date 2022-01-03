package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//represents an abstract panel that has neccessary fields for jobs related to login accounts
public abstract class AbstractLoginAccountPrompter extends JPanel {
    protected JTextField nameField;
    protected JTextField birthdayField;
    protected JTextField codeField;
    protected JTextField idField;
    protected JLabel idLabel;
    protected JLabel birthdayLabel;
    protected JLabel codeLabel;
    protected JLabel nameLabel;

    //MODIFIES: initialize the fields that are used for prompting user to enter the input
    protected AbstractLoginAccountPrompter() {
        setLayout(new FlowLayout());
        nameField = new JTextField(10);
        birthdayField = new JTextField(10);
        codeField = new JTextField(10);
        idField = new JTextField(10);
        nameLabel = new JLabel("Name: ");
        idLabel = new JLabel("ID: ");
        birthdayLabel = new JLabel("Birthday: ");
        codeLabel = new JLabel("Personal Code: ");
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




//    @Override
//    public abstract void actionPerformed(ActionEvent e);
}
