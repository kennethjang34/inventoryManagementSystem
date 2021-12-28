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
        DATE, ID, TOTAL_ACCOUNTS, BROUGHT_IN, TAKEN_OUT
    }

    public static final String[] DATA_LIST = new String[]{
            DataList.DATE.toString(), DataList.ID.toString(), DataList.TOTAL_ACCOUNTS.toString(),
            DataList.BROUGHT_IN.toString(), DataList.TAKEN_OUT.toString()
    };

    public RecordedDate(LocalDate date) {
        super(new String[]{
            DataList.DATE.toString(), DataList.ID.toString(), DataList.TOTAL_ACCOUNTS.toString(),
                DataList.BROUGHT_IN.toString(), DataList.TAKEN_OUT.toString()
        });
        this.date = date;
        accountMap = new LinkedHashMap<>();
    }

    public RecordedDate(LocalDate date, List<Account> accounts) {
        super(new String[]{
                DataList.DATE.toString(), DataList.ID.toString(), DataList.TOTAL_ACCOUNTS.toString(),
                DataList.BROUGHT_IN.toString(), DataList.TAKEN_OUT.toString()
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
//        int qty = getTotalNumAccounts();
        changeFirer.fireUpdateEvent(this);
    }

    public List<String> getIDList() {
        return new ArrayList<>(accountMap.keySet());
    }

    public int getTotalNumAccounts() {
        int count = 0;
        for (List<Account> accounts: accountMap.values()) {
            count += accounts.size();
        }
        return count;
    }

    public int getBroughtInQuantity() {
        int count = 0;
        for (List<Account> accounts: accountMap.values()) {
            for (Account account: accounts) {
                if (account.getQuantity() > 0) {
                    count += account.getQuantity();
                }
            }
        }
        return count;
    }

    public int getTakenOutQuantity() {
        int count = 0;
        for (List<Account> accounts: accountMap.values()) {
            for (Account account: accounts) {
                if (account.getQuantity() < 0) {
                    count += account.getQuantity();
                }
            }
        }
        return count;
    }

    @Override
    public Object[] convertToTableEntry() {
        Object[] row = new Object[DataList.values().length];
        row[0] = date;
        String ids = getIDList().toString();
        row[1] = ids;
        row[2] = getTotalNumAccounts();
        row[3] = getBroughtInQuantity();
        row[4] = getTakenOutQuantity();
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
            case TOTAL_ACCOUNTS:
                contents.add(String.valueOf(getTotalNumAccounts()));
            case BROUGHT_IN:
                contents.add(String.valueOf(getBroughtInQuantity()));
                break;
            case TAKEN_OUT:
                contents.add(String.valueOf(getTakenOutQuantity()));
        }
        return contents;
    }

    @Override
    public List<? extends ViewableTableEntryConvertibleModel> getEntryModels() {
        return getAccounts();
    }
}
