package ui.ledgerpanel;

import model.Account;
import model.Ledger;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SelectedAccountTableModel extends AbstractTableModel {
    private List<Account> accountList;
    private LedgerPanel ledgerPanel;

    public SelectedAccountTableModel(LedgerPanel panel) {
        ledgerPanel = panel;
        accountList = new ArrayList<>();
    }

    private String[] columnNames = new String[]{
            "Date", "Code", "ID", "Cost", "Price", "Location", "Qty", "description"
    };

    @Override
    public int getRowCount() {
        return accountList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }


    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Account account = accountList.get(rowIndex);
        switch (getColumnName(columnIndex)) {
            case "Date":
                return "" + account.getDate();
            case "Code":
                return account.getCode();
            case "ID":
                return account.getID();
            case "Cost":
                return "" + account.getAverageCost();
            case "Price":
                return "" + account.getAveragePrice();
            case "Location":
                return account.getLocation();
            case "Qty":
                return "" + account.getQuantity();
            case "description":
                return account.getDescription();
        }
        return "ERROR";
    }

    public void addAccountsOf(LocalDate date) {
        ledgerPanel.getAccountsOn(date);
        accountList.addAll(ledgerPanel.getAccountsOn(date));
    }

    public void addAccounts(List<Account> newAccounts) {
        for (Account account: newAccounts) {
            if (!accountList.contains(account)) {
                accountList.add(account);
            }
        }
        fireTableDataChanged();
    }

    public boolean removeAccountFromList(Account account) {
        if (accountList.remove(account)) {
            return true;
        }
        return false;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

//    public boolean removeAccount(Account account) {
//        if (accountList.remove(account)) {
//            ledgerPanel.removeAccountFromLedger(account);
//        }
//    }
}
