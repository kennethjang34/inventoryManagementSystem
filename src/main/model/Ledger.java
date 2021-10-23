package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//represents a ledger that contains several transaction accounts
public class Ledger implements JsonConvertible {
    private ArrayList<Account> accounts;
    private int nextAccountNumber;
    private int codeSize;

    //EFFECTS:create a new empty ledger.
    //default code size is 6.
    public Ledger() {
        codeSize = 6;
        accounts = new ArrayList<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    //EFFECTS:create a new empty ledger with the given code size
    public Ledger(int accountNumberSize) {
        this.codeSize = accountNumberSize;
        accounts = new ArrayList<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    //REQUIRES: the data in JSON format must contain all necessary
    //information for creating a ledger with matching names.
    //EFFECTS: create a new ledger with data in JSON format
    public Ledger(JSONObject jsonLedger) {
        nextAccountNumber = jsonLedger.getInt("nextAccountNumber");
        codeSize = jsonLedger.getInt("codeSize");
        accounts = new ArrayList<>();
        JSONArray jsonAccounts = jsonLedger.getJSONArray("accounts");
        for (int i = 0; i < jsonAccounts.length(); i++) {
            accounts.add(new Account(jsonAccounts.getJSONObject(i)));
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
        for (Account account: accounts) {
            if (account.getCode() == code) {
                return account;
            }
        }
        return null;
    }

    //EFFECTS: return the list of accounts in this ledger
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    //EFFECTS: return a list of accounts that were generated on the specified date.
    //If there isn't any, return null.
    public ArrayList<Account> getAccounts(LocalDate date) {
        ArrayList<Account> accountOnDate = new ArrayList<>();
        for (Account account: accounts) {
            if (account.getDate().equals(date)) {
                accountOnDate.add(account);
            }
        }
        if (accountOnDate.size() == 0) {
            return null;
        }
        return accountOnDate;
    }



    //MODIFIES: this
    //EFFECTS: create and add an account to this. return the created account
    public Account addAccount(List<QuantityTag> added, List<QuantityTag> removed, String description, LocalDate date) {
        if (added.size() == 0 && removed.size() == 0) {
            return null;
        }
        Account account = new Account(nextAccountNumber++, description, date, added, removed);
        accounts.add(account);
        return account;
    }

    //REQUIRES: the data in JSON format must contain  all necessary information
    //for creating a ledger with matching names
    //EFFECTS: create and return JSONObject that contains represents this
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("accounts", convertToJsonArray(accounts));
        json.put("nextAccountNumber", nextAccountNumber);
        json.put("codeSize", codeSize);
        return json;
    }
}
