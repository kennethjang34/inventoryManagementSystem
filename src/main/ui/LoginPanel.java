package ui;

import jdk.nashorn.internal.scripts.JO;
import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.time.LocalDate;


//A panel to prompt the user to log in/register a new login account/retrieve password
public class LoginPanel extends JPanel implements ActionListener {
    private int purpose;
    private boolean isAdmin = false;
    private final JTextField idField = new JTextField(10);
    private final JPasswordField pwField = new JPasswordField(10);
    private final JLabel idLabel = new JLabel("ID");
    private final JLabel pwLabel = new JLabel("PW");
    private final InventoryManagementSystemApplication application;
    public static final String LOGIN = "login";
    public static final String CREATE = "create";
    public static final String RETRIEVE = "retrieve";
    public static final int CANCEL = -1;
    public static final int SAVE = 0;
    public static final int LOAD = 1;
    public static final int ADMIN = 2;
    private Admin admin;
    private RegisterPanel registerPanel = new RegisterPanel();
    private RetrievePanel retrievePanel = new RetrievePanel();





    //A small panel that will be displayed if the user presses register button to create a new account
    private class RegisterPanel extends AbstractLoginAccountPanel implements ActionListener {
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
            if (isAdmin == true || (isAdmin == false && admin.isEmpty())) {
                int personalCode = Integer.parseInt(this.codeField.getText());
                String birthdayText = birthdayField.getText();
                LocalDate birthDay = InventoryManagementSystemApplication.convertToLocalDate(birthdayText);
                boolean successful = admin.createLoginAccount(idField.getText(),
                        String.valueOf(pwField.getPassword()),
                        nameField.getText(), birthDay, personalCode, !isAdmin);
                isAdmin = true;
                JOptionPane.showMessageDialog(null, "a new account is successfully created");
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(null, "you are not allowed to create "
                        + "a new login account in this system");
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: set the purpose of this login attempt
    public void setPurpose(int purpose) {
        if (purpose != SAVE && purpose != LOAD && purpose != ADMIN) {
            throw new IllegalArgumentException("The given purpose is not valid");
        }
        this.purpose = purpose;
    }



    //A small panel that will be displayed if the user presses retrieve button
    private class RetrievePanel extends AbstractLoginAccountPanel implements ActionListener {
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
            LocalDate birthday = InventoryManagementSystemApplication.convertToLocalDate(birthdayField.getText());
            String pw = admin.retrievePassword(idField.getText(), nameField.getText(), birthday, personalCode);
            if (pw != null) {
                JOptionPane.showMessageDialog(null, "The password is : " + pw);
            } else {
                JOptionPane.showMessageDialog(null, "Given info is not correct");
            }
            setVisible(false);
        }
    }



//    @Override
//    public void paintComponent(Graphics g) {
////        imagePath = "/data/seol.gif";
////        System.out.println(new File(imagePath).exists());
////        if (image == null) {
////            try {
////                image = ImageIO.read(new File(imagePath));
////                if (getWidth() != 0 && getHeight() != 0) {
////                    //image = image.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
////                }
//////                image = image.getScaledInstance((int)getPreferredSize().getWidth(),
//////                        (int)getPreferredSize().getHeight(), Image.SCALE_SMOOTH);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////            //image = new ImageIcon(Image.class.getResource(imagePath)).getImage();
////            if (image == null) {
////                throw new RuntimeException();
////            }
////        }
//
//
//        //ii = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
//        //ii.paintIcon(this, g, 0, 0);
//
//        super.paintComponent(g);
////        g.drawImage(image, 0, 0, this);
//    }
















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
        pwField.addActionListener(this);
        add(idLabel);
        add(idField);
        add(pwLabel);
        add(pwField);
        add(new JLabel("To retrieve password, "));
        JButton retrieveButton = new JButton("press here");
        retrieveButton.addActionListener(this);
        retrieveButton.setActionCommand(RETRIEVE);
        add(retrieveButton);
        add(new JLabel("To create a new login account, "));
        JButton createButton = new JButton("press here");
        createButton.addActionListener(this);
        createButton.setActionCommand(CREATE);
        add(createButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> application.dataChangeHandler(CANCEL));
        add(cancelButton);
        setPreferredSize(new Dimension(400, 500));

    }



    //MODIFIES: this
    //EFFECTS: If the user has tried to sign in, check if the input is valid.
    //If the input is valid, switch to the application panel.
    //Otherwise, display error message.
    //Else if the user pressed a button to create a new account, display a pane to create a new account
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if (actionCommand.equals(LOGIN)) {
            String id = idField.getText();
            char[] pw = pwField.getPassword();
            if (!admin.checkLoginAccount(id, String.valueOf(pw))) {
                displayLoginFail();
            } else {
                if (purpose != ADMIN || admin.isAdminMember(id)) {
                    application.setLoginStatus(true);
                    application.setLoginAccount(id);
                    application.dataChangeHandler(purpose);
                } else {
                    displayAdminLoginFail();
                }
            }
        } else if (actionCommand.equals(CREATE)) {
            displayRegisterPanel();
        } else if (actionCommand.equals(RETRIEVE)) {
            displayRetrievePanel();
        }
    }


    //MODIFIES: this
    //EFFECTS: display the retrieve panel of this
    private void displayRetrievePanel() {
        retrievePanel.setSize(600, 400);
        JDialog dialog = new JDialog();
        dialog.setLayout(new FlowLayout());
        dialog.setSize(600, 400);
        dialog.add(retrievePanel);
        dialog.setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: display the register panel of this
    private void displayRegisterPanel() {
        registerPanel.setSize(600, 400);
        JDialog dialog = new JDialog();
        dialog.setLayout(new FlowLayout());
        dialog.setSize(600, 400);
        dialog.add(registerPanel);
        dialog.setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: display a message that the current admin login attempt is not successful
    private void displayAdminLoginFail() {
        JOptionPane.showMessageDialog(null, "You are not registered in admin");
    }


    //MODIFIES: this
    //EFFECTS: display a message that the current login attempt is not successful.
    private void displayLoginFail() {
        JOptionPane.showMessageDialog(null, "Login failed");
    }

//    public static void main(String[] args) {
//        JFrame testFrame = new JFrame();
//        testFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        testFrame.add(new LoginPanel(new Admin()));
//        testFrame.setSize(400, 400);
//        testFrame.setVisible(true);
//    }




}
