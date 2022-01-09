package ui.adminpanel.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


//A panel to prompt the user to log in/register a new login account/retrieve password
public class LoginPanel extends JPanel {
    private final JTextField idField = new JTextField(10);
    private final JPasswordField pwField = new JPasswordField(10);
    private final JLabel idLabel = new JLabel("ID");
    private final JLabel pwLabel = new JLabel("PW");
    public static final String LOGIN = "login";
    private RetrievePrompter retrievePanel = RetrievePrompter.getRetrievePrompter();
    private JButton loginButton;
    private JButton retrieveButton;
    private JDialog dialog;


    private KeyListener buttonEnterListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                JButton button = (JButton) e.getSource();
                button.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };


    //EFFECTS: create a new panel that is used to process user login attempt/register a new account/retrieve password
    public LoginPanel() {
        pwField.setActionCommand(LOGIN);
        add(idLabel);
        add(idField);
        add(pwLabel);
        add(pwField);
        loginButton = new JButton("Login");
        add(loginButton);
        add(new JLabel("To retrieve password, "));
        retrieveButton = new JButton("press here");
//        retrieveButton.setActionCommand(RETRIEVE);
        add(retrieveButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
                clearFields();
            }
        });
        loginButton.addKeyListener(buttonEnterListener);
        cancelButton.addKeyListener(buttonEnterListener);
//        cancelButton.addActionListener(e -> application.dataChangeHandler(CANCEL));
        add(cancelButton);
        setPreferredSize(new Dimension(400, 500));
        idField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JComponent)(e.getSource())).transferFocus();
            }
        });
        pwField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });
    }

    public void clearFields() {
        idField.setText("");
        pwField.setText("");
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

    public JButton getRetrieveButton() {
        return retrieveButton;
    }




    public String getIdFieldInput() {
        return idField.getText();
    }

    public char[] getPwFieldInput() {
        return pwField.getPassword();
    }

    public JDialog getLoginDialog() {
        return dialog;
    }


    public void displayLoginDialog() {
        dialog = new JDialog();
        dialog.add(this);
        dialog.setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        dialog.pack();
        dialog.setVisible(true);
        clearFields();
    }

    public void displayLoginSuccessful() {
        JOptionPane.showMessageDialog(this, "Login successful");
    }
}
