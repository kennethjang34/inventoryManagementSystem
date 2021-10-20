package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

//represents an account that contains information about the change that happens on a particular date.
//will have account code, description, date, and quantities for each different item code.
//Note this account must be distinguished from Login accounts, which are completely different
public class Account {
    //Code will be used for finding a particular account
    private final int code;
    private final String description;
    private final LocalDate date;

    //entries is a list of entries each of which contains item code(String), cost(double), quantity(int)
    private final ArrayList<AccountEntry> entries;

    private static class  AccountEntry {
        private  final String itemCode;
        //price of each product
        private final double price;
        private final int quantity;
        private LinkedList<? extends InventoryTag> tags;

        //tags must have the same item code. quantities on the tags cannot be negative
        private AccountEntry(LinkedList<InventoryTag> tags) {

        }


        private String getItemCode() {
            return itemCode;
        }


        private double getPrice() {
            return price;
        }



        private int getQuantity() {
            return quantity;
        }
    }

    //REQUIRES: code must be positive integer
    //EFFECTS: create an account that describe the transaction that occurred on the specified date.
    public Account(int code, String description, LocalDate date) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.entries = new ArrayList<>();
    }

    public Account(int code, String description, LocalDate date, Map<String, LinkedList<AdditionTag>> itemTags) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.entries = new ArrayList<>();
        for (Map.Entry<String, LinkedList<AdditionTag>> entry: itemTags.entrySet()) {
            addEntries(entry.getKey(), entry.getValue());
        }
    }

    //REQUIRES: tags must have the same item code.
    //MODIFIES: this
    //EFFECTS: add a new entry for this item code.
    public void addEntries(LinkedList<AdditionTag> tags) {
        entries.add(new AccountEntry(itemCode, tags));
    }





    //EFFECTS: return the total dollar amount of this
    public double getDollarAmount() {
        double amount = 0;
        for (AccountEntry e: entries) {
            int quantity = e.getQuantity();
            amount += (e.getPrice() * quantity);
        }
        return amount;
    }

    //EFFECTS: return total product quantity processed in this
    public int getTotalQuantity() {
        int qty = 0;
        for (AccountEntry e: entries) {
            qty += e.getQuantity();
        }
        return qty;
    }

    //REQUIRES: item code needs to exist in the item list.
    //EFFECTS: return  quantity of the product specified by item code processed in this
    public int getQuantity(String itemCode) {
        int qty = 0;
        for (AccountEntry e: entries) {
            String code = e.getItemCode();
            if (code.equalsIgnoreCase(itemCode)) {
                qty =  e.getQuantity();
                break;
            }
        }
        return qty;
    }


    //REQUIRES: item code must exist in the account.
    //EFFECTS: return the price at which each product belonging to the item code was bought
    public double getPrice(String itemCode) {
        double price = 0;
        for (AccountEntry e: entries) {
            String code = e.getItemCode();
            if (code.equalsIgnoreCase(itemCode)) {
                price = e.getPrice();
                break;
            }
        }
        return price;
    }

    //REQUIRES: item code must exist in the account.
    //EFFECTS: return the total cost paid for all the products belonging to the item code
    public double getTotalCost(String itemCode) {
        double totalCost = 0;
        for (AccountEntry e: entries) {
            String code = e.getItemCode();
            int qty = e.getQuantity();
            if (code.equalsIgnoreCase(itemCode)) {
                totalCost = (e.getPrice()) * qty;
                break;
            }
        }
        return totalCost;
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



    public ArrayList<String> getItemCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (AccountEntry entry: entries) {
            codes.add(entry.getItemCode());
        }
        return codes;
    }
}
