package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.*;

//represents a ledger that contains several transaction accounts
public class Ledger implements JsonConvertible {
    //key: LocalDate.toString(), value: list of accounts that occurred that day
    private Map<String, List<Account>> accounts;
    //private ArrayList<Account> accounts;
    private int nextAccountNumber;
    private int codeSize;

    //EFFECTS:create a new empty ledger.
    //default code size is 6.
    public Ledger() {
        codeSize = 6;
        accounts = new LinkedHashMap<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    //EFFECTS:create a new empty ledger with the given code size
    public Ledger(int accountNumberSize) {
        this.codeSize = accountNumberSize;
        accounts = new LinkedHashMap<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    //REQUIRES: the data in JSON format must contain all necessary
    //information for creating a ledger with matching names.
    //EFFECTS: create a new ledger with data in JSON format
    public Ledger(JSONObject jsonLedger) {
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
        return accounts.get(date.toString());
    }

    //EFFECTS: return a list of accounts that were generated on the specified date.
    //If there isn't any, return null.
    public List<Account> getAccounts(String date) {
        return accounts.get(date);
    }


    //EFFECTS: return the map of date and account lists
    public Map<String, List<Account>> getAccountsMap() {
        return accounts;
    }






    //MODIFIES: this
    //EFFECTS: create and add an account to this. return the created account
    public Account addAccount(List<QuantityTag> added, List<QuantityTag> removed, String description, LocalDate date) {
        if (added.size() == 0 && removed.size() == 0) {
            return null;
        }
        Account account = new Account(nextAccountNumber++, description, date, added, removed);
        List<Account> accountList = accounts.get(date.toString());
        if (accountList == null) {
            accountList = new ArrayList<>();
        }
        accountList.add(account);
        accounts.putIfAbsent(date.toString(), accountList);
        return account;
    }


    //MODIFIES: this
    //EFFECTS: create and add an account to this. return the created account
    public Account addAccount(List<QuantityTag> added, String description, LocalDate date) {
        if (added.size() == 0) {
            return null;
        }
        Account account = new Account(nextAccountNumber++, description, date, added);
        List<Account> accountList = accounts.get(date.toString());
        if (accountList == null) {
            accountList = new ArrayList<>();
        }
        accountList.add(account);
        accounts.putIfAbsent(date.toString(), accountList);
        return account;
    }

    //MODIFIES: this
    //EFFECTS: create and aa a new account to this, return the created account
    public Account addAccount(InventoryTag tag, String description, LocalDate date) {
        Account account = new Account(nextAccountNumber++, description, date, tag.getId(), tag.getLocation(),
                tag.getUnitCost(), tag.getUnitPrice(), tag.getQuantity());

        List<Account> accountList = accounts.get(date.toString());
        if (accountList == null) {
            accountList = new ArrayList<>();
        }
        accountList.add(account);
        accounts.putIfAbsent(date.toString(), accountList);
        return account;
    }

    //MODIFIES: this
    //EFFECTS: create and aa a new account to this, return the created account
    public Account addAccount(QuantityTag tag, String description, LocalDate date) {
        return null;
    }


    //EFFECTS: return a list of dates that have at least one account related to it
    public String[] getDates() {
        return accounts.keySet().toArray(new String[0]);
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

    public List<String> getIDs(LocalDate selectedDate) {
        List<Account> accountList = accounts.get(selectedDate.toString());
        if (accountList == null) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        for (Account account: accountList) {
            ids.add(account.getID());
        }
        return ids;
    }

    public List<String> getCodes() {
        List<String> codes = new ArrayList<>();
        List<List<Account>> accountsLists = new ArrayList<>(accounts.values());
        for (int i = 0; i < accountsLists.size(); i++) {
            for (int j = 0; j < accountsLists.get(i).size(); i++) {
                codes.add(accountsLists.get(i).get(j).getCode());
            }
        }
        return codes;
    }
}
