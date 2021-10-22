package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Ledger {
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


}
