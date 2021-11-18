package ui;

import javax.swing.*;
import java.awt.*;
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

    @Override
    public abstract void actionPerformed(ActionEvent e);
}
