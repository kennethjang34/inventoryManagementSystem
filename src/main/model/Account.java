package model;

import java.time.LocalDate;
import java.util.*;

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
        private final String itemCode;
        //price of each product
        private int quantity;
        private Map<String, Integer> distribution;

        private AccountEntry(QuantityTag tag) {
            quantity = tag.getQuantity();
            itemCode = tag.getItemCode().toUpperCase();
            distribution = new HashMap<>();
            distribution.put(tag.getLocation(), tag.getQuantity());
        }

        private String getItemCode() {
            return itemCode;
        }

        private void addTag(QuantityTag tag) {
            if (distribution.containsKey(tag.getLocation())) {
                int existing = distribution.get(tag.getLocation());
                distribution.put(tag.getLocation(), tag.getQuantity() + existing);
            } else {
                distribution.put(tag.getLocation(), tag.getQuantity());
            }
            quantity += tag.getQuantity();
        }

        private int getQuantityAtLocation(String location) {
            location = location.toUpperCase();
            if (distribution.containsKey(location)) {
                return distribution.get(location);
            } else {
                return 0;
            }
        }

        private Map<String, Integer> getDistribution() {
            return distribution;
        }

        private int getQuantity() {
            return quantity;
        }
    }


    public Account(int code, String description, LocalDate date, List<QuantityTag> added, List<QuantityTag> removed) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.entries = new ArrayList<>();
        addEntries(added);
        addEntries(removed);
    }

    //MODIFIES: this
    //EFFECTS: add a new entry for this item code.
    private void addEntries(List<QuantityTag> tags) {
        if (tags == null) {
            return;
        }
        for (QuantityTag tag : tags) {
            String itemCode = tag.getItemCode();
            if (getEntry(itemCode) == null) {
                entries.add(new AccountEntry(tag));
            } else {
                AccountEntry entry = getEntry(itemCode);
                entry.addTag(tag);
            }
        }
    }




    private AccountEntry getEntry(String itemCode) {
        for (AccountEntry entry: entries) {
            if (entry.itemCode.equalsIgnoreCase(itemCode)) {
                return entry;
            }
        }
        return null;
    }

    //EFFECTS: return total product quantity processed in this
    public int getTotalQuantity() {
        int qty = 0;
        for (AccountEntry e: entries) {
            if (e.getQuantity() < 0) {
                qty -= e.getQuantity();
            } else {
                qty += e.getQuantity();
            }
        }
        return qty;
    }

    public ArrayList<String> getQuantitiesInfo() {
        ArrayList<String> quantities = new ArrayList<>();
        for (AccountEntry entry: entries) {
            quantities.add(entry.getItemCode() + ": " + entry.getQuantity());
            for (Map.Entry<String, Integer> qty: entry.getDistribution().entrySet()) {
                quantities.add("\t" + qty.getKey() + ": " + qty.getValue());
            }
        }
        return quantities;
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


    public ArrayList<String> getLocations(String itemCode) {
        ArrayList<String> locations = new ArrayList<>();
        for (AccountEntry entry: entries) {
            if (entry.getItemCode().equalsIgnoreCase(itemCode)) {
                Map<String, Integer> distribution = entry.getDistribution();
                for (Map.Entry<String, Integer> stock: distribution.entrySet()) {
                    String location = stock.getKey();
                    locations.add(location);
                }
            }
        }
        return locations;
    }

    public int getQuantityAtLocation(String itemCode, String location) {
        itemCode = itemCode.toUpperCase();
        location = location.toUpperCase();
        for (AccountEntry entry: entries) {
            if (entry.getItemCode().equalsIgnoreCase(itemCode)) {
                return entry.getQuantityAtLocation(location);
            }
        }
        return 0;
    }



    public ArrayList<String> getItemCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (AccountEntry entry: entries) {
            codes.add(entry.getItemCode());
        }
        return codes;
    }
}
