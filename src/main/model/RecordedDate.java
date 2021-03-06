package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.record.Record;
import persistence.JsonConvertible;
import ui.table.TableEntryConvertibleDataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;
import java.util.*;

//Each recordedDate will have a list of accounts that occurred on the day this represents
public class RecordedDate extends TableEntryConvertibleDataFactory implements JsonConvertible {
    LocalDate date;
    //key: id of each item
    //value: accounts belonging to the item
    Map<String,List<Account>> accountMap;

    public enum JSONDataList {
        DATE, ACCOUNTS, IDS
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put(JSONDataList.DATE.toString(), date);
        JSONObject accountJSONMap = new JSONObject();
        for (Map.Entry<String, List<Account>> entry: accountMap.entrySet()) {
            JSONArray accountList = new JSONArray();
            for (Account account: entry.getValue()) {
                accountList.put(account.toJson());
            }
//            accountList.putAll(entry.getValue());
            accountJSONMap.put(entry.getKey(), accountList);
        }
        json.put(JSONDataList.ACCOUNTS.toString(), accountJSONMap);
        return json;
    }

    public Account getAccount(String code) {
        for (List<Account> accounts: accountMap.values()) {
            for (Account account: accounts) {
                if (account.getCode().equals(code)) {
                    return account;
                }
            }
        }
        return null;
    }

    public enum ColumnName {
        DATE, ID, TOTAL_ACCOUNTS, BROUGHT_IN, TAKEN_OUT
    }

    public enum EntryTypes {
        ACCOUNT
    }

    public static final String[] DATA_LIST = new String[]{
            ColumnName.DATE.toString(), ColumnName.ID.toString(), ColumnName.TOTAL_ACCOUNTS.toString(),
            ColumnName.BROUGHT_IN.toString(), ColumnName.TAKEN_OUT.toString()
    };

    public RecordedDate(LocalDate date) {
        super(new String[]{
            ColumnName.DATE.toString(), ColumnName.ID.toString(), ColumnName.TOTAL_ACCOUNTS.toString(),
                ColumnName.BROUGHT_IN.toString(), ColumnName.TAKEN_OUT.toString()
        });
        this.date = date;
        accountMap = new LinkedHashMap<>();
    }

    public RecordedDate(LocalDate date, List<Account> accounts) {
        super(new String[]{
                ColumnName.DATE.toString(), ColumnName.ID.toString(), ColumnName.TOTAL_ACCOUNTS.toString(),
                ColumnName.BROUGHT_IN.toString(), ColumnName.TAKEN_OUT.toString()
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

    public RecordedDate(JSONObject json) {
        super(new String[]{
                ColumnName.DATE.toString(), ColumnName.ID.toString(), ColumnName.TOTAL_ACCOUNTS.toString(),
                ColumnName.BROUGHT_IN.toString(), ColumnName.TAKEN_OUT.toString()
        });
        date = LocalDate.parse(json.getString(JSONDataList.DATE.toString()));
        this.accountMap = new LinkedHashMap<>();
        JSONObject accountJSONMap = json.getJSONObject(JSONDataList.ACCOUNTS.toString());
        for (Iterator<String> it = accountJSONMap.keys(); it.hasNext(); ) {
            String key = it.next();
            JSONArray accountArray = accountJSONMap.getJSONArray(key);
            List<Account> accounts = accountMap.get(key);
            if (accounts == null) {
                accounts = new ArrayList<>();
            }
            for (Object obj: accountArray) {
                JSONObject jsonObject = (JSONObject) obj;
                accounts.add(new Account(jsonObject));
            }
            accountMap.put(key, accounts);
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
//             if (accountMap.putIfAbsent(account.getID(), accountList) == null) {
//            changeFirer.fireAdditionEvent(DataList.ID.toString(), account);
//        }
        changeFirer.fireAdditionEvent(EntryTypes.ACCOUNT.toString(), account);
        changeFirer.fireUpdateEvent(this);
//        changeFirer.fireUpdateEvent(this);
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

    public LocalDate getDate() {
        return date;
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
        Object[] row = new Object[ColumnName.values().length];
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
        return ColumnName.values();
    }

    @Override
    public List<Object> getContentsOf(String property) {
        List<Object> contents = new LinkedList<>();
        switch (ColumnName.valueOf(property)) {
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


    @Override
    public String toString() {
        return date.toString();
    }
}
