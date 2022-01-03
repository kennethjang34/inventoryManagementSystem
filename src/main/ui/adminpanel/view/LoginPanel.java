package ui.adminpanel.view;

import model.Admin;
import ui.AbstractLoginAccountPrompter;
import ui.InventoryManagementSystemApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;


//A panel to prompt the user to log in/register a new login account/retrieve password
public class LoginPanel extends JPanel {
    private int purpose;
    private boolean isAdmin = false;
    private final JTextField idField = new JTextField(10);
    private final JPasswordField pwField = new JPasswordField(10);
    private final JLabel idLabel = new JLabel("ID");
    private final JLabel pwLabel = new JLabel("PW");
    private final InventoryManagementSystemApplication application;
    public static final String LOGIN = "login";
    public static final String RETRIEVE = "retrieve";
    public static final int CANCEL = -1;
    public static final int SAVE = 0;
    public static final int LOAD = 1;
    public static final int ADMIN = 2;
    private Admin admin;
    private RetrievePrompter retrievePanel = RetrievePrompter.getRetrievePrompter();
    private JButton loginButton;

    //MODIFIES: this
    //EFFECTS: set the purpose of this login attempt
    public void setPurpose(int purpose) {
        if (purpose != SAVE && purpose != LOAD && purpose != ADMIN) {
            throw new IllegalArgumentException("The given purpose is not valid");
        }
        this.purpose = purpose;
    }

    //EFFECTS: create a new panel that is used to process user login attempt/register a new account/retrieve password
    public LoginPanel(Admin admin, InventoryManagementSystemApplication application) {
//        JDialog dialog = new JDialog();
//        dialog.setLayout(new FlowLayout());
//        JLabel descriptionLabel = new JLabel();
//        descriptionLabel.setLayout(new BorderLayout());
//        descriptionLabel.add()
        this.application = application;
        this.admin = admin;
        pwField.setActionCommand(LOGIN);
        add(idLabel);
        add(idField);
        add(pwLabel);
        add(pwField);
        loginButton = new JButton("Login");
        add(loginButton);
        add(new JLabel("To retrieve password, "));
        JButton retrieveButton = new JButton("press here");
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayRetrievePanel();
            }
        });
        retrieveButton.setActionCommand(RETRIEVE);
        add(retrieveButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> application.dataChangeHandler(CANCEL));
        add(cancelButton);
        setPreferredSize(new Dimension(400, 500));
    }




    //MODIFIES: this
    //EFFECTS: display the retrieve panel of this
    private void displayRetrievePanel() {
        retrievePanel.setSize(600, 400);
        JDialog dialog = new JDialog(application, true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(600, 400);
        dialog.add(retrievePanel);
        dialog.setVisible(true);
    }



    //MODIFIES: this
    //EFFECTS: display a message that the current admin login attempt is not successful
    public void displayAdminLoginFail() {
        JOptionPane.showMessageDialog(null, "You are not registered in admin");
    }


    //MODIFIES: this
    //EFFECTS: display a message that the current login attempt is not successful.
    public void displayLoginFail() {
        JOptionPane.showMessageDialog(this, "Login failed");
    }

    public RetrievePrompter getRetrievePrompter() {
        return retrievePanel;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public Admin getAdmin() {
        return admin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public int getPurpose() {
        return purpose;
    }

    public String getIdFieldInput() {
        return idField.getText();
    }

    public char[] getPwFieldInput() {
        return pwField.getPassword();
    }


}
