package ui.ledgerpanel;

import model.Account;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilterPanel extends JPanel implements ActionListener {
    private LedgerPanel ledgerPanel;
    private JComboBox dateBox;
    private JComboBox codeBox;
    private JTextField dateField;
    private JTextField codeField;
    private String selectedDate;
    private String selectedCode;
    private static final String ALL = "ALL";
    private static final String TYPE_MANUALLY = "TYPE_MANUALLY";


    public FilterPanel(LedgerPanel panel) {
        dateField = new JTextField("YYYY-MM-DD");
        codeField = new JTextField();
        this.ledgerPanel = panel;
        dateBox = new JComboBox();
        codeBox = new JComboBox();
        dateBox.addItem(ALL);
        dateBox.addItem(TYPE_MANUALLY);
        codeBox.addItem(ALL);
        codeBox.addItem(TYPE_MANUALLY);
        initializeCodeBox();
        initializeDateBox();
        initializeTextFields();;
        add(new JLabel("Date"));
        codeField.setVisible(false);
        dateField.setVisible(false);
        /////
        codeBox.setVisible(false);
        add(dateBox);
        add(dateField);
        add(new JLabel("Code"));
        add(codeBox);
        add(codeField);
    }

    public void initializeDateBox() {
        LocalDate[] dates = ledgerPanel.getDates();
        for (LocalDate date: dates) {
            dateBox.addItem(date);
        }
        dateBox.addActionListener(this);
    }


    public void initializeCodeBox() {
        List<Account> accountsOnDisplay = ledgerPanel.getAccountsOnDisplay();

        for (Account account: accountsOnDisplay) {
            codeBox.addItem(account.getCode());
        }
        codeBox.addActionListener(this);
    }

    public void updateCodeBox() {
        DefaultComboBoxModel codeModel = new DefaultComboBoxModel();
        codeModel.addElement(ALL);
        codeModel.addElement(TYPE_MANUALLY);
        if (selectedDate.equals(ALL)) {
            List<String> codes = ledgerPanel.getCodes();
            for (String code: codes) {
                codeModel.addElement(code);
            }
        } else {
            LocalDate date = LocalDate.parse(selectedDate);
            List<Account> accountList = ledgerPanel.getAccountsOn(date);
            for (Account account: accountList) {
                codeModel.addElement(account.getCode());
            }
        }
        codeModel.setSelectedItem(ALL);
        codeBox.setModel(codeModel);
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dateBox) {
            selectedDate = (String)dateBox.getSelectedItem();
            if (selectedDate.equals(TYPE_MANUALLY)) {
                promptDateTyping();
                return;
            }
            ledgerPanel.displayAccountsOn(selectedDate);
        } else if (e.getSource() == codeBox) {
            selectedCode = (String)codeBox.getSelectedItem();
            if (selectedCode.equals(TYPE_MANUALLY)) {
                promptCodeTyping();
            }
        }
    }


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public void initializeTextFields() {
        dateField = new JTextField("YYYY-MM-DD");
        dateField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                DefaultComboBoxModel dateModel = (DefaultComboBoxModel)dateBox.getModel();
                int index = dateModel.getIndexOf(date);
                if (index == -1) {
                    JOptionPane.showMessageDialog(null, "No such date is recorded in ledger!");
                    dateField.removeAll();
                } else {
                    dateBox.setSelectedIndex(index);
                    dateField.setVisible(false);
                }
            }
        });


        codeField = new JTextField("ACCOUNT CODE");
        codeField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeField.getText();
                DefaultComboBoxModel model = (DefaultComboBoxModel) codeBox.getModel();
                int index = model.getIndexOf(code);
                if (index == -1) {
                    JOptionPane.showMessageDialog(null,
                            "There is no such account with the given code!");
                    codeField.removeAll();
                } else {
                    codeBox.setSelectedIndex(index);
                    codeField.setVisible(false);
                }
            }
        });
    }


    private void promptDateTyping() {
        dateField.setVisible(true);
        revalidate();
    }

    private void promptCodeTyping() {
        codeField.setVisible(true);
        revalidate();
    }

    //MODIFIES: this
    //EFFECTS: add a new date to the date combo box
    public void addDate(String name) {
        dateBox.addItem(name);
    }

    //MODIFIES: this
    //EFFECTS: add a new account code to the code combo box if the currently selected category contains this new item
    //else do nothing
    public void addCode(String code) {
        if (ledgerPanel.getDate(code).equalsIgnoreCase(selectedDate)) {
            codeBox.addItem(code);
        }
    }


}
