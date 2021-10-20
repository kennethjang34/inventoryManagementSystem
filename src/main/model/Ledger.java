package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class Ledger {
    private ArrayList<Account> accounts;
    private int nextAccountNumber;
    private int codeSize;


    public Ledger() {
        codeSize = 9;
        accounts = new ArrayList<>();
    }

    public Ledger(int accountNumberSize) {
        this.codeSize = accountNumberSize;
        accounts = new ArrayList<>();
    }

    public int getCodeSize() {
        return codeSize;
    }

    public int getSize() {
        return accounts.size();
    }

    public Account getAccount(int code) {
        return null;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public ArrayList<Account> getAccounts(LocalDate date) {
        return null;
    }



    //tagBox: hash map composed of listToAdd and listToRemove
    public Account addAccount(Map<String, LinkedList<AdditionTag>> itemTags, String description, LocalDate date) {

        if (itemTags.size() == 0) {
            return null;
        }
        Account account = new Account(nextAccountNumber, description, date, itemTags);
        return account;
    }


}
