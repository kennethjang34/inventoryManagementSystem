package ui.ledgerpanel;

import model.Account;
import model.InventoryTag;
import model.Ledger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class LedgerPanel extends JPanel implements ActionListener {
    private final Ledger ledger;
    private AccountTable accountTable;
    private SelectedAccountTable selectedAccountTable;
    private FilterPanel filterPanel;

    public LedgerPanel(Ledger ledger) {
        this.ledger = ledger;
        accountTable = new AccountTable(ledger, this);
        selectedAccountTable = new SelectedAccountTable(ledger, this);
        filterPanel = new FilterPanel(this);
        setLayout(new BorderLayout());
        add(accountTable, BorderLayout.SOUTH);
        add(selectedAccountTable, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.CENTER);
        setSize(new Dimension(600, 900));
    }

    public void actionPerformed(ActionEvent e) {
    }


    public List<Account> getAccountsOn(LocalDate date) {
        return ledger.getAccounts(date);
    }



    public void addAccount(List<InventoryTag> stocks, String description, LocalDate date) {
        for (InventoryTag tag: stocks) {
            ledger.addAccount(tag, description, date);
        }
        accountTable.update();
        accountTable.repaint();

    }

    public void addAccount(InventoryTag tag, String description, LocalDate date) {
        Account account = ledger.addAccount(tag, description, date);
//        assert account != null;
//        assert ledger.getSize() == 1;
        filterPanel.addDate(date.toString());
        accountTable.update();
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

    public static void main(String[] args) {
        Ledger ledger = new Ledger();
        LedgerPanel panel = new LedgerPanel(ledger);
        InventoryTag tag = new InventoryTag("apple", 1, 2, LocalDate.now(), "f11", 100);
        panel.addAccount(tag, "testing", LocalDate.now());
        assert ledger.getAccounts(LocalDate.now()).size() == 1;
        //panel.accountTable.repaint();
        System.out.println(panel.accountTable.getRowCount());
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.setSize(300, 400);
        frame.setVisible(true);
    }

    public void displayAccountsOn(String selectedDate) {
        accountTable.display(selectedDate);
    }

    public List<Account> getAccountsOnDisplay() {
        return selectedAccountTable.getAccountsOndisplay();
    }
}
