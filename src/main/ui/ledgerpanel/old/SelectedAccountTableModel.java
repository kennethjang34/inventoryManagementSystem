package ui.ledgerpanel.old;

import model.Account;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

//represents a table model for selected account table
public class SelectedAccountTableModel extends AbstractTableModel {
    private List<Account> accountList;

    //create a new table model
    public SelectedAccountTableModel() {
        accountList = new ArrayList<>();
    }

    //EFFECTS: return column names
    private String[] columnNames = new String[]{
            "Date", "Code", "ID", "Cost", "Price", "Location", "Qty", "description"
    };

    //EFFECTS: return the row size
    @Override
    public int getRowCount() {
        return accountList.size();
    }

    //EFFECTS: return the column size
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }


    //EFFECTS: return the column name
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    //EFFECTS: return the value at the specified cell
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

    //MODIFIES: this
    //EFFECTS: add the given accounts to this and fire table data changed event
    public void addAccounts(List<Account> newAccounts) {
        for (Account account : newAccounts) {
            if (!accountList.contains(account)) {
                accountList.add(account);
            }
        }
        fireTableDataChanged();
    }

    //MODIFIES: this
    //EFFECTS: remove the given account from this
    public boolean removeAccountFromList(Account account) {
        if (accountList.remove(account)) {
            return true;
        }
        return false;
    }

    //EFFECTS: return the list of accounts inside this
    public List<Account> getAccountList() {
        return accountList;
    }
}


