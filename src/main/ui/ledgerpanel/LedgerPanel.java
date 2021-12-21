package ui.ledgerpanel;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

//represents a panel that contains information of ledger
public class LedgerPanel extends JPanel implements ActionListener {
    private final Ledger ledger;
    private AccountTable accountTable;
    private SelectedAccountTable selectedAccountTable;
    private FilterPanel filterPanel;

    //create a new panel that has info about the given ledger
    public LedgerPanel(Ledger ledger) {
        this.ledger = ledger;
        accountTable = new AccountTable(ledger, this);
        selectedAccountTable = new SelectedAccountTable(this);
        filterPanel = new FilterPanel(this);
        setLayout(new BorderLayout());
        add(accountTable, BorderLayout.SOUTH);
        add(selectedAccountTable, BorderLayout.NORTH);
//        add(filterPanel, BorderLayout.CENTER);
        setSize(new Dimension(600, 900));
    }

    //ToBeDetermined
    public void actionPerformed(ActionEvent e) {
    }

    //EFFECTS: return a list of accounts that were written on the given date
    public List<Account> getAccountsOn(LocalDate date) {
        return ledger.getAccounts(date);
    }



    //MODIFIES: this
    //EFFECTS: add a list of new accounts to this with the given list of stock change information, description,
    //and date
    public void addAccount(List<InventoryTag> stocks, String description, LocalDate date) {
        for (InventoryTag tag: stocks) {
            ledger.addAccount(tag, description, date);
        }
        accountTable.update(ApplicationConstantValue.ACCOUNT);
        accountTable.repaint();

    }

    //MODIFIES: this
    //EFFECTS: add a new account to this with a piece of  stock change information, description,
    //and date
    public void addAccount(InventoryTag tag, String description, LocalDate date) {
        Account account = ledger.addAccount(tag, description, date);
        filterPanel.addDate(date.toString());
        accountTable.update(ApplicationConstantValue.ACCOUNT);
        accountTable.repaint();
    }

    //MODIFIES: this
    //EFFECTS: make selected account table to add a new list of accounts of the given date to the table
    public void addToSelected(LocalDate date) {
        selectedAccountTable.addToList(date);
        //selectedAccountTable.repaint();
    }

    //EFFECTS: return a list of dates existing in the ledger
    public LocalDate[] getDates() {
        String[] dateInfo = ledger.getDates();
        LocalDate[] dates = new LocalDate[dateInfo.length];
        for (int i = 0; i < dates.length; i++) {
            dates[i] = LocalDate.parse(dateInfo[i]);
        }
        return dates;
    }

    //EFFECTS: return a list of account codes inside this
    public List<String> getCodes() {
        return ledger.getCodes();
    }


    //EFFECTS: return the date of the account with the given code
    public String getDate(String code) {
        Account account = ledger.getAccount(code);
        if (account != null) {
            return account.getDate().toString();
        }
        return null;
    }

    //MODIFIES: this
    //EFFECTS: display accounts written on the selected date
    public void displayAccountsOn(String selectedDate) {
        accountTable.display(selectedDate);
    }

    //EFFECTS: return a list of accounts that are added to the selected account table
    public List<Account> getAccountsOnDisplay() {
        return selectedAccountTable.getAccountsOndisplay();
    }

    //MODIFIES: this
    //EFFECTS: display all accounts
    public void displayAll() {
        accountTable.displayAll();
    }

}
