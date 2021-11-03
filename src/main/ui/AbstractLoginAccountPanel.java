package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AbstractLoginAccountPanel extends JPanel implements ActionListener {
    protected JTextField nameField;
    protected JTextField birthdayField;
    protected JTextField codeField;
    protected JTextField idField;
    protected JLabel idLabel;
    protected JLabel birthdayLabel;
    protected JLabel codeLabel;
    protected JLabel nameLabel;

    protected AbstractLoginAccountPanel() {
        nameField = new JTextField();
        birthdayField = new JTextField();
        codeField = new JTextField();
        idField = new JTextField();
        nameLabel = new JLabel("Name: ");
        idLabel = new JLabel("ID: ");
        birthdayLabel = new JLabel("Birthday: ");
        codeLabel = new JLabel("Personal Code: ");
    }

    @Override
    public abstract void actionPerformed(ActionEvent e);
}
