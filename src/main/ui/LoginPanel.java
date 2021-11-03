package ui;

import model.Admin;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;


//A panel to prompt the user to log in/register a new login account/retrieve password
public class LoginPanel extends JPanel implements ActionListener {
    private String description;
    private final JTextField idField = new JTextField(10);
    private final JPasswordField pwField = new JPasswordField(10);
    private final JLabel idLabel = new JLabel("ID");
    private final JLabel pwLabel = new JLabel("PW");
    private final InventoryManagementSystemApplication application;
    private static final String login = "login";
    private static final String create = "create";
    private static final String retrieve = "retrieve";
    private Admin admin;
    private RegisterPanel registerPanel = new RegisterPanel();
    private RetrievePanel retrievePanel = new RetrievePanel();







    //A small panel that will be displayed if the user presses register button to create a new account
    private class RegisterPanel extends AbstractLoginAccountPanel {
        private JButton registerButton;
        private JPasswordField pwField = new JPasswordField(10);


        //EFFECTS: create a new register panel with empty text fields.
        private RegisterPanel() {
            registerButton = new JButton("Register");
            registerButton.addActionListener(this);
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

        //REQUIRES: all fields must be in valid form and cannot remain empty
        //MODIFIES: this
        //EFFECTS: create a new login account and register it in the system.
        @Override
        public void actionPerformed(ActionEvent e) {
            int personalCode = Integer.parseInt(this.codeField.getText());
            String birthdayText = birthdayField.getText();
            LocalDate birthDay = convertToLocalDate(birthdayText);
            admin.createLoginAccount(idField.getText(), String.valueOf(pwField.getPassword()),
                    nameField.getText(), birthDay, personalCode);
            JOptionPane.showMessageDialog(this, "a new account is successfully created");
            this.setVisible(false);

        }
    }


    //A small panel that will be displayed if the user presses retrieve button
    private class RetrievePanel extends AbstractLoginAccountPanel {
        private JButton retrieveButton;

        //EFFECTS: create a new retrieve panel with empty text fields.
        private RetrievePanel() {
            retrieveButton = new JButton("Retrieve PW");
            retrieveButton.addActionListener(this);
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

        //EFFECTS: show the password of the account matching the given information.
        //If there isn't any, display error message.
        public void actionPerformed(ActionEvent e) {
            int personalCode = Integer.parseInt(codeField.getText());
            LocalDate birthday = convertToLocalDate(birthdayField.getText());
            String pw = admin.retrievePassword(idField.getText(), nameField.getText(), birthday, personalCode);
            if (pw != null) {
                JOptionPane.showMessageDialog(null, "The password is : " + pw);
            } else {
                JOptionPane.showMessageDialog(null, "Given info is not correct");
            }
            setVisible(false);
        }
    }























    //REQUIRES: the date must be a string type in YYYYMMDD form without any space in between
    //EFFECTS: convert the date in string form into LocalDate type
    public static LocalDate convertToLocalDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        return LocalDate.of(year, month, day);
    }




    //EFFECTS: create a new panel that is used to process user login attempt/register a new account/retrieve password
    public LoginPanel(Admin admin, InventoryManagementSystemApplication application) {
        this.admin = admin;
        this.application = application;
        description = "Welcome to Inventory Management System application. " + "\n this application helps you "
                + "control/check quantities in stocks of different products in your warehouse.\n"
                + "To start the application, please login.\n"
                + "if you create a new login account, a new empty inventory will be created";
        pwField.setActionCommand(login);
        pwField.addActionListener(this);
        add(new JLabel(description));
        add(idLabel);
        add(idField);
        add(pwLabel);
        add(pwField);
        add(new JLabel("To retrieve password, "));
        JButton retrieveButton = new JButton("press here");
        retrieveButton.addActionListener(this);
        retrieveButton.setActionCommand(retrieve);
        add(retrieveButton);
        add(new JLabel("To create a new login account, "));
        JButton createButton = new JButton("press here");
        createButton.addActionListener(this);
        createButton.setActionCommand(create);
        add(createButton);
    }



    //MODIFIES: this
    //EFFECTS: If the user has tried to sign in, check if the input is valid.
    //If the input is valid, switch to the application panel.
    //Otherwise, display error message.
    //Else if the user pressed a button to create a new account, display a pane to create a new account
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(login)) {
            String id = idField.getText();
            char[] pw = pwField.getPassword();
            if (!admin.checkLoginAccount(id, String.valueOf(pw))) {
                displayLoginFail();
            } else {
                application.switchToControlPanel();
            }
        } else if (actionCommand.equals(create)) {
            registerPanel.setSize(600, 400);
            registerPanel.setVisible(true);
            //JOptionPane.showMessageDialog(this, registerPanel);
        } else if (actionCommand.equals(retrieve)) {
            retrievePanel.setSize(600, 400);
            retrievePanel.setVisible(true);
            //JOptionPane.showMessageDialog(this, retrievePanel);
        }
    }



    //MODIFIES: this
    //EFFECTS: display a message that the current login attempt is not successful.
    private void displayLoginFail() {
        JOptionPane.showMessageDialog(null,"Login failed");
    }

//    public static void main(String[] args) {
//        JFrame testFrame = new JFrame();
//        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        testFrame.add(new LoginPanel(new Admin()));
//        testFrame.setSize(400, 400);
//        testFrame.setVisible(true);
//    }




}
