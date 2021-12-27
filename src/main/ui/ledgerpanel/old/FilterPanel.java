package ui.ledgerpanel.old;

import model.Account;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

//represents a panel that provides options to filter stock change accounts based on the chosen category/item id
public class FilterPanel  extends JPanel implements ActionListener {
    private LedgerPanel ledgerPanel;
    private JComboBox dateBox;
    private JComboBox codeBox;
    private JTextField dateField;
    private JTextField codeField;
    private String selectedDate;
    private String selectedCode;
    private static final String ALL = "ALL";
    private static final String TYPE_MANUALLY = "TYPE_MANUALLY";

    //EFFECTS: initialize a new filter panel
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
        //add(new JLabel("Code"));
        add(codeBox);
        add(codeField);
    }

    //MODIFIES: this
    //EFFECTS: initialize the combo box for date selection
    public void initializeDateBox() {
        LocalDate[] dates = ledgerPanel.getDates();
        for (LocalDate date: dates) {
            dateBox.addItem(date.toString());
        }
        dateBox.addActionListener(this);
    }

    //MODIFIES: this
    //EFFECTS: initialize the combo box for code selection
    public void initializeCodeBox() {
        List<Account> accountsOnDisplay = ledgerPanel.getAccountsOnDisplay();

        for (Account account: accountsOnDisplay) {
            codeBox.addItem(account.getCode());
        }
        codeBox.addActionListener(this);
    }

    //MODIFIES: this
    //EFFECTS: update the combo box for code selection
    public void updateCodeBox() {
        DefaultComboBoxModel codeModel = new DefaultComboBoxModel();
        codeModel.addElement(ALL);
        codeModel.addElement(TYPE_MANUALLY);
        if (selectedDate.equals(ALL)) {
            List<String> codes = ledgerPanel.getCodes();
            for (String code: codes) {
                if (codeModel.getIndexOf(code) == -1) {
                    codeModel.addElement(code);
                }
            }
        } else {
            LocalDate date = LocalDate.parse(selectedDate);
            List<Account> accountList = ledgerPanel.getAccountsOn(date);
            for (Account account: accountList) {
                if (codeModel.getIndexOf(account.getCode()) == -1) {
                    codeModel.addElement(account.getCode());
                }
            }
        }
        codeModel.setSelectedItem(ALL);
        codeBox.setModel(codeModel);
    }


    //MODIFIES: this
    //EFFECTS: when date is chosen, update item box so that it only contains those that occur on the chosen date
    //When a code is chosen, display the account with the code
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dateBox) {
            selectedDate = (String)dateBox.getSelectedItem();
            if (selectedDate.equals(TYPE_MANUALLY)) {
                promptDateTyping();
                return;
            }
            dateField.setVisible(false);
            if (selectedDate.equals(ALL)) {
                ledgerPanel.displayAll();
                return;
            }
            ledgerPanel.displayAccountsOn(selectedDate);
        } else if (e.getSource() == codeBox) {
            selectedCode = (String)codeBox.getSelectedItem();
            if (selectedCode.equals(TYPE_MANUALLY)) {
                promptCodeTyping();
                return;
            }
            codeField.setVisible(false);
        }
        repaint();
    }

    //MODIFIES: this
    //EFFECTS: initialize the text fields for searching
    public void initializeTextFields() {
        initializeDateField();
        initializeCodeField();

    }

    //MODIFIES: this
    //EFFECTS: initialize code field
    private void initializeCodeField() {
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

    //MODIFIES: this
    //EFFECTS: initialize date field
    private void initializeDateField() {
        dateField = new JTextField("YYYY-MM-DD");
        dateField.addActionListener(e -> {
            String date = dateField.getText();
            if (date.length() == 8) {
                int year = Integer.parseInt(date.substring(0, 4));
                int month = Integer.parseInt(date.substring(4, 6));
                int day = Integer.parseInt(date.substring(6, 8));
                date = year + "-" + month + "-" + day;
            }
            DefaultComboBoxModel dateModel = (DefaultComboBoxModel)dateBox.getModel();
            int index = dateModel.getIndexOf(date);
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "No such date is recorded in ledger!");
                dateField.removeAll();
            } else {
                dateBox.setSelectedIndex(index);
                dateField.removeAll();
                dateField.setVisible(false);

            }
        });
    }

    //MODIFIES: this
    //EFFECTS: set visible the date text field so the user can search by typing the date
    private void promptDateTyping() {
        dateField.setVisible(true);
        repaint();
        revalidate();
        dateField.selectAll();
    }

    //MODIFIES: this
    //EFFECTS: set visible the code text field so the user can search by typing the code
    private void promptCodeTyping() {
        codeField.setVisible(true);
        repaint();
        revalidate();
        codeField.selectAll();
    }

    //MODIFIES: this
    //EFFECTS: add a new date to the date combo box
    public void addDate(String name) {
        DefaultComboBoxModel boxModel = (DefaultComboBoxModel)dateBox.getModel();
        if (boxModel.getIndexOf(name) == -1) {
            dateBox.addItem(name);
        }
    }

    //MODIFIES: this
    //EFFECTS: add a new account code to the code combo box if the currently selected category contains this new item
    //else do nothing
    public void addCode(String code) {
        if (ledgerPanel.getDate(code).equalsIgnoreCase(selectedDate)) {
            DefaultComboBoxModel boxModel = (DefaultComboBoxModel)codeBox.getModel();
            if (boxModel.getIndexOf(code) == -1) {
                dateBox.addItem(code);
            }
        }
    }

//    //EFFECTS: return a panel view of this
//    public JPanel getPanel() {
//        return panel;
//    }


//    @Override
//    public void notifyObservers() {
//
//    }
}
