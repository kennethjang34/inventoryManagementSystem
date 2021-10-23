package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ledger implements JsonConvertable {
    private ArrayList<Account> accounts;
    private int nextAccountNumber;
    private int codeSize;


    public Ledger() {
        codeSize = 6;
        accounts = new ArrayList<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    public Ledger(int accountNumberSize) {
        this.codeSize = accountNumberSize;
        accounts = new ArrayList<>();
        nextAccountNumber = ((int)Math.pow(10, codeSize) - 1) / 9;
    }

    public Ledger(JSONObject jsonLedger) {
        nextAccountNumber = jsonLedger.getInt("nextAccountNumber");
        codeSize = jsonLedger.getInt("codeSize");
        accounts = new ArrayList<>();
        JSONArray jsonAccounts = jsonLedger.getJSONArray("accounts");
        for (int i = 0; i < jsonAccounts.length(); i++) {
            accounts.add(new Account(jsonAccounts.getJSONObject(i)));
        }
    }

    public int getCodeSize() {
        return codeSize;
    }

    public int getSize() {
        return accounts.size();
    }

    public Account getAccount(int code) {
        for (Account account: accounts) {
            if (account.getCode() == code) {
                return account;
            }
        }
        return null;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

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



    //tagBox: hash map composed of listToAdd and listToRemove
    public Account addAccount(List<QuantityTag> added, List<QuantityTag> removed, String description, LocalDate date) {
        if (added.size() == 0 && removed.size() == 0) {
            return null;
        }

        Account account = new Account(nextAccountNumber++, description, date, added, removed);
        accounts.add(account);
        return account;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("accounts", convertToJsonArray(accounts));
        json.put("nextAccountNumber", nextAccountNumber);
        json.put("codeSize", codeSize);
        return json;
    }
}
