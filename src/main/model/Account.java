package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Account {
    private final int code;
    private String description;
    private final LocalDate date;
    //item code, price, qty.
    private final ArrayList<Object[]> entries;


    //REQUIRES: code must be positive integer
    //EFFECTS: create an account that describe the transaction that occurred on the specified date.
    public Account(int code, String description, LocalDate date, ArrayList<Object[]> entries) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.entries = entries;
    }

    //EFFECTS: return the total dollar amount of this
    public double getDollarAmount() {
        double amount = 0;
        for (Object[] e: entries) {
            int quantity = (int)e[2];
            amount += ((double)e[1] * quantity);
        }
        return amount;
    }

    //EFFECTS: return total product quantity processed in this
    public int getTotalQuantity() {
        int qty = 0;
        for (Object[] e: entries) {
            qty += (int)e[2];
        }
        return qty;
    }

    //REQUIRES: item code needs to exist in the item list.
    //EFFECTS: return  quantity of the product specified by item code processed in this
    public int getQuantity(String itemCode) {
        for (Object[] e: entries) {
            String code = (String)e[0];
            if (code.equalsIgnoreCase(itemCode)) {
                return (int)e[2];
            }
        }
        return 0;
    }


    //REQUIRES: item code must exist in the account.
    //EFFECTS: return the price at which each product belonging to the item code was bought
    public double getPrice(String itemCode) {
        for (Object[] e: entries) {
            String code = (String)e[0];
            if (code.equalsIgnoreCase(itemCode)) {
                return (double)e[1];
            }
        }
        return 0;
    }

    //REQUIRES: item code must exist in the account.
    //EFFECTS: return the total cost paid for all the products belonging to the item code
    public double getTotalCost(String itemCode) {
        for (Object[] e: entries) {
            String code = (String) e[0];
            int qty = (int) e[2];

            if (code.equalsIgnoreCase(itemCode)) {
                return ((double)e[1]) * qty;
            }
        }
        return -1;
    }

    //EFFECTS: return date
    public LocalDate getDate() {
        return date;
    }

    //EFFECTS: return the description for this
    public String getDescription() {
        return description;
    }

    //EFFECTS: return the account code;
    public int getCode() {
        return code;
    }

    public ArrayList<Object[]> getEntries() {
        return entries;
    }
}
