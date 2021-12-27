package model;

import ui.table.TableEntryConvertibleDataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;
import java.util.*;

//Each recordedDate will have a list of accounts that occurred on the day this represents
public class RecordedDate extends TableEntryConvertibleDataFactory {
    LocalDate date;
    //key: id of each item
    //value: accounts belonging to the item
    Map<String,List<Account>> accountMap;

    public enum DataList {
        DATE, ID, QUANTITY
    }

    public static final String[] DATA_LIST = new String[]{
            DataList.DATE.toString(), DataList.ID.toString(), DataList.QUANTITY.toString()
    };

    public RecordedDate(LocalDate date) {
        super(new String[]{
            DataList.DATE.toString(), DataList.ID.toString(), DataList.QUANTITY.toString()
        });
        this.date = date;
        accountMap = new LinkedHashMap<>();
    }

    public RecordedDate(LocalDate date, List<Account> accounts) {
        super(new String[]{
                DataList.DATE.toString(), DataList.ID.toString(), DataList.QUANTITY.toString()
        });
        this.date = date;
        this.accountMap = new LinkedHashMap<>();
        for (Account account: accounts) {
            List<Account> accountList = accountMap.get(account.getID());
            if (accountList == null) {
                accountList = new LinkedList<>();
            }
            accountList.add(account);
            accountMap.putIfAbsent(account.getID(), accountList);
        }
    }

    public Map<String, List<Account>> getAccountMap() {
        return null;
    }

    public List<Account> getAccounts(String id) {
        return null;
    }

    public List<Account> getAccounts() {
        List<Account> all = new ArrayList<>();
        for (List<Account> accounts: accountMap.values()) {
            all.addAll(accounts);
        }
        return all;
    }

    public void addAccount(Account account) {
        List<Account> accountList = accountMap.get(account.getID());
        if (accountList == null) {
            accountList = new LinkedList<>();
        }
        accountList.add(account);
        accountMap.putIfAbsent(account.getID(), accountList);
        int qty = getQuantity();
//        changeFirer.fireUpdateEvent(DataList.QUANTITY.toString(), this, qty - 1, qty);
        changeFirer.fireUpdateEvent(DataList.QUANTITY.toString(), this);
    }

    public List<String> getIDList() {
        return new ArrayList<>(accountMap.keySet());
    }

    public int getQuantity() {
        int count = 0;
        for (List<Account> accounts: accountMap.values()) {
            count += accounts.size();
        }
        return count;
    }

    @Override
    public Object[] convertToTableEntry() {
        Object[] row = new Object[3];
        row[0] = date;
        String ids = getIDList().toString();
//        for (String id: getIDList()) {
//            ids.concat(id);
//        }
        row[1] = ids;
        row[2] = getQuantity();
        return row;
    }

    @Override
    public Object[] getDataList() {
        return DataList.values();
    }

    @Override
    public List<String> getContentsOf(String property) {
        List<String> contents = new LinkedList<>();
        switch (DataList.valueOf(property)) {
            case DATE:
                contents.add(date.toString());
                break;
            case ID:
                contents.addAll(getIDList());
                break;
            case QUANTITY:
                contents.add(String.valueOf(getQuantity()));
                break;
        }
        return contents;
    }

    @Override
    public List<? extends ViewableTableEntryConvertibleModel> getEntryModels() {
        return getAccounts();
    }
}
