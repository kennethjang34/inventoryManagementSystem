package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.DataViewer;
import ui.table.AbstractTableDataFactory;
import ui.table.DataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;
import java.util.*;



//represents a ledger that contains several transaction accounts
public class Ledger extends AbstractTableDataFactory implements JsonConvertible, DataViewer {
    //key: LocalDate.toString(), value: list of accounts that occurred that day
    private Map<String, List<Account>> accounts;
    //dates will replace accounts;
    private Map<LocalDate, RecordedDate> dates;
    //private ArrayList<Account> accounts;
    private int nextAccountNumber;
    private int codeSize;

    @Override
    public void entryRemoved(ViewableTableEntryConvertibleModel o) {
        if (o instanceof Product) {
            Product removed = (Product) o;
            String id = removed.getID();
            InventoryTag tag = new InventoryTag(id, removed.getCost(), removed.getPrice(), LocalDate.now(),
            removed.getLocation(), -1);
            addAccount(tag, "REMOVAL", LocalDate.now());
        }
    }

    @Override
    public void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed) {
        double averageCost = calculateAverageCost((List<Product>) removed);
        double averagePrice = calculateAveragePrice((List<Product>) removed);
        String id = ((Product)(removed.get(0))).getID();
        InventoryTag tag = new InventoryTag(id, averageCost, averagePrice, LocalDate.now(),
                ((Product)(removed.get(0))).getLocation(), -removed.size());
        addAccount(tag, "REMOVAL", LocalDate.now());
    }

    //for new item
    @Override
    public void entryAdded(ViewableTableEntryConvertibleModel o) {
        if (o instanceof Item) {
            Item item = (Item) o;
            item.addDataChangeListener(this);
        }
    }

    @Override
    public void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added) {

    }


    //for new products
    @Override
    public void entryAdded(List<? extends ViewableTableEntryConvertibleModel> added) {
        double averageCost = calculateAverageCost((List<Product>) added);
        double averagePrice = calculateAveragePrice((List<Product>) added);
        String id = ((Product)(added.get(0))).getID();
        InventoryTag tag = new InventoryTag(id, averageCost, averagePrice, LocalDate.now(),
                ((Product)(added.get(0))).getLocation(), added.size());
        addAccount(tag, "ADDITION", LocalDate.now());
    }


    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel updatedEntry) {

    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2) {

    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, Object old, Object newObject) {

    }

    @Override
    public void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel removed) {

    }


    public double calculateAverageCost(List<Product> products) {
        double sum = 0;
        for (Product p: products) {
            sum += p.getCost();
        }
        return sum/products.size();
    }

    public  double calculateAveragePrice(List<Product> products) {
        double sum = 0;
        for (Product p: products) {
            sum += p.getPrice();
        }
        return sum/products.size();
    }

    public enum DataList {
        RECORDED_DATE, PROCESSED_ID, TOTAL_ACCOUNTS, BROUGHT_IN, TAKEN_OUT
    }

    public static final String[] DATA_LIST = new String[]{
            RecordedDate.ColumnName.DATE.toString(), RecordedDate.ColumnName.ID.toString(), RecordedDate.ColumnName.TOTAL_ACCOUNTS.toString(),
            RecordedDate.ColumnName.BROUGHT_IN.toString(), RecordedDate.ColumnName.TAKEN_OUT.toString()
    };


    //EFFECTS:create a new empty ledger.
    //default code size is 6.
    public Ledger() {
        dates = new LinkedHashMap<>();
        codeSize = 6;
        accounts = new LinkedHashMap<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }


    //EFFECTS:create a new empty ledger with the given code size
    public Ledger(int accountNumberSize) {
//        super(new String[]{DataList.RECORDED_DATE.toString(), DataList.ID.toString(), DataList.SIZE.toString()});
        dates = new LinkedHashMap<>();
        this.codeSize = accountNumberSize;
        accounts = new LinkedHashMap<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    //REQUIRES: the data in JSON format must contain all necessary
    //information for creating a ledger with matching names.
    //EFFECTS: create a new ledger with data in JSON format
    public Ledger(JSONObject jsonLedger) {
//        super(new String[]{DataList.RECORDED_DATE.toString(), DataList.SIZE.toString()});
        dates = new LinkedHashMap<>();
        nextAccountNumber = jsonLedger.getInt("nextAccountNumber");
        codeSize = jsonLedger.getInt("codeSize");
        accounts = new LinkedHashMap<>();
        JSONArray jsonAccountsMap = jsonLedger.getJSONArray("accounts");
        for (int i = 0; i < jsonAccountsMap.length(); i++) {
            JSONObject jsonEntry = jsonAccountsMap.getJSONObject(i);
            String date = jsonEntry.getString("date");
            List<Account> accountList = new ArrayList<>();
            JSONArray jsonAccountList = jsonEntry.getJSONArray("accountList");
            for (Object obj: jsonAccountList) {
                JSONObject jsonObject = (JSONObject)obj;
                accountList.add(new Account(jsonObject));
            }
            accounts.put(date, accountList);
            dates.put(LocalDate.parse(date), new RecordedDate(LocalDate.parse(date), accountList));
        }
    }

    //EFFECTS: return code size
    public int getCodeSize() {
        return codeSize;
    }

    //EFFECTS: return the number of accounts in this
    public int getSize() {
        return accounts.size();
    }

    //Effects: return an account that has the specified account code number
    //If there isn't any, return null.
    public Account getAccount(int code) {
        for (List<Account> accountList: accounts.values()) {
            for (Account account: accountList) {
                if (account.getCode().equalsIgnoreCase("" + code)) {
                    return account;
                }
            }
        }
        return null;
    }

    //Effects: return an account that has the specified account code number
    //If there isn't any, return null.
    public Account getAccount(String code) {
        for (List<Account> accountList: accounts.values()) {
            for (Account account: accountList) {
                if (account.getCode().equalsIgnoreCase(code)) {
                    return account;
                }
            }
        }
        return null;
    }

    //EFFECTS: return the list of accounts in this ledger
    public List<List<Account>> getAccountLists() {
        return new ArrayList<>(accounts.values());
    }

    //EFFECTS: return a list of accounts that were generated on the specified date.
    //If there isn't any, return null.
    public List<Account> getAccounts(LocalDate date) {
        return dates.get(date).getAccounts();
//        return accounts.get(date.toString());
    }

    public List<Account> getAccounts() {
        if (dates.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Account> accountList = new ArrayList<>();
        for (RecordedDate date: dates.values()) {
            accountList.addAll(date.getAccounts());
        }
        return accountList;
    }

    public List<String> getProcessedItemList() {
        if (dates.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> idSet = new HashSet<>();
        for (RecordedDate date: dates.values()) {
            idSet.addAll(date.getIDList());
        }
        return new ArrayList<>(idSet);
    }


    public List<String> getProcessedItemList(LocalDate date) {
        if (dates.get(date) == null) {
            return new ArrayList<>();
        }
        return dates.get(date).getIDList();
    }






    //MODIFIES: this
    //EFFECTS: create and aa a new account to this, return the created account
    public Account addAccount(InventoryTag tag, String description, LocalDate date) {
        Account account = new Account(nextAccountNumber++, description, date, tag.getId(), tag.getLocation(),
                tag.getUnitCost(), tag.getUnitPrice(), tag.getQuantity());

        RecordedDate recordedDate = dates.get(date);
        if (recordedDate == null) {
            recordedDate = new RecordedDate(date);
            dates.put(recordedDate.getDate(), recordedDate);
            changeFirer.fireAdditionEvent(DataList.RECORDED_DATE.toString(), recordedDate);
        } else {

        }
        recordedDate.addAccount(account);
        return account;
    }


    //EFFECTS: return a list of dates that have at least one account related to it
    public List<LocalDate> getDates() {
        return new ArrayList<>(dates.keySet());
    }



    //REQUIRES: the data in JSON format must contain  all necessary information
    //for creating a ledger with matching names
    //EFFECTS: create and return JSONObject that contains represents this
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray accountsMap = new JSONArray();
        for (Map.Entry<String, List<Account>> entry: accounts.entrySet()) {
            JSONObject jsonEntry = new JSONObject();
            jsonEntry.put("date", entry.getKey());
            jsonEntry.put("accountList", convertToJsonArray(entry.getValue()));
            accountsMap.put(jsonEntry);
        }
        json.put("accounts", accountsMap);
        json.put("nextAccountNumber", nextAccountNumber);
        json.put("codeSize", codeSize);
        return json;
    }

    //EFFECTS: return a list of IDs of items processed on the selected date
    public List<String> getIDs(LocalDate selectedDate) {
        List<Account> accounts = dates.get(selectedDate).getAccounts();
        if (accounts == null) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        for (Account account: accounts) {
            ids.add(account.getID());
        }
        return ids;
    }

    public Set<String> getIDs() {
        Set set = new HashSet();
        for (RecordedDate date: dates.values()) {
            set.addAll(date.getIDList());
        }
        return set;
    }

    //EFFECTS: return a list of account codes recorded in this
    public List<String> getCodes() {
        List<String> codes = new ArrayList<>();
        List<List<Account>> accountsLists = new ArrayList<>(accounts.values());
        for (int i = 0; i < accountsLists.size(); i++) {
            for (int j = 0; j < accountsLists.get(i).size(); j++) {
                codes.add(accountsLists.get(i).get(j).getCode());
            }
        }
        return codes;
    }


    @Override
    public Object[] getDataList() {
        return DataList.values();
    }

    @Override
    public List<Object> getContentsOf(String property) {
        List<Object> contents = new ArrayList<>();
        switch (DataList.valueOf(property)) {
            case RECORDED_DATE:
                for (LocalDate date: dates.keySet()) {
                    contents.add(date.toString());
                }
                break;
//            case PROCESSED_ID:
//                contents.addAll(getIDs());
//                break;
//            case TOTAL_ACCOUNTS:
//                contents.add(String.valueOf(getSize()));
        }

        return contents;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
                DataList.RECORDED_DATE.toString(), DataList.PROCESSED_ID.toString(), DataList.TOTAL_ACCOUNTS.toString(),
                DataList.BROUGHT_IN.toString(), DataList.TAKEN_OUT.toString()
        };
    }


    //return list of recordedDate object
    @Override
    public List<? extends ViewableTableEntryConvertibleModel> getEntryModels() {
        return new ArrayList<>(dates.values());
    }
}
