package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.*;

//represents an account that contains information about the change that happens on a particular date.
//will have account code, description, date, and quantities for each different item code.
//Note this account must be distinguished from Login accounts, which are completely different
public class Account implements JsonConvertible {
    //Code will be used for finding a particular account
    private final int code;
    private final String description;
    private final LocalDate date;

    //entries is a list of entries each of which contains item code(String), cost(double), quantity(int)
    private final ArrayList<AccountEntry> entries;

    //represents each entry in account.
    //each entry will only contain products belonging to the same item code.
    private static class  AccountEntry implements JsonConvertible {
        private final String itemCode;
        private int quantity;
        private Map<String, Integer> distribution;

        //EFFECTS: create a new account entry with the given tag.
        private AccountEntry(QuantityTag tag) {
            quantity = tag.getQuantity();
            itemCode = tag.getItemCode().toUpperCase();
            distribution = new HashMap<>();
            distribution.put(tag.getLocation(), tag.getQuantity());
        }

        //REQUIRES: the data in JSON format must contain all necessary information for creating a new account entry
        //EFFECTS:create a new account entry with data in JSON format
        private AccountEntry(JSONObject json) {
            itemCode = json.getString("itemCode");
            quantity = json.getInt("quantity");
            distribution = new HashMap<>();
            JSONObject jsonDistribution = json.getJSONObject("distribution");
            Iterator<String> locations = jsonDistribution.keys();
            while (locations.hasNext()) {
                String location = locations.next();
                distribution.put(location, (Integer)jsonDistribution.get(location));
            }
        }

        //EFFECTS: return item code in this
        private String getItemCode() {
            return itemCode;
        }

        //REQUIRES: tag must contain the same item code as this
        //MODIFIES: this
        //EFFECTS: put a new tag at this
        //if the location specified by the tag is already existing in the entry, merge them.
        private void addTag(QuantityTag tag) {
            if (distribution.containsKey(tag.getLocation())) {
                int existing = distribution.get(tag.getLocation());
                distribution.put(tag.getLocation(), tag.getQuantity() + existing);
            } else {
                distribution.put(tag.getLocation(), tag.getQuantity());
            }
            quantity += tag.getQuantity();
        }

        //EFFECTS:return quantity change of the item code at specified location
        private int getQuantityAtLocation(String location) {
            location = location.toUpperCase();
            if (distribution.containsKey(location)) {
                return distribution.get(location);
            } else {
                return 0;
            }
        }

        //EFFECTS: return a map that contains location and its quantity of the item
        private Map<String, Integer> getDistribution() {
            return distribution;
        }

        //EFFECTS: return total quantity
        private int getQuantity() {
            return quantity;
        }

        //REQUIRES: the data in JSON format must contain  all necessary information
        //for creating a new account entry with matching names
        //EFFECTS: create and return JSONObject that contains represents this
        @Override
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("itemCode", itemCode);
            json.put("quantity", quantity);
            JSONObject jsonDistribution = new JSONObject(distribution);
            json.put("distribution", jsonDistribution.toMap());
            return json;
        }
    }

    //REQUIRES: code cannot be negative
    //EFFECTS: create a new account with given information
    public Account(int code, String description, LocalDate date, List<QuantityTag> added, List<QuantityTag> removed) {
        this.code = code;
        this.description = description;
        this.date = date;
        this.entries = new ArrayList<>();
        addEntries(added);
        addEntries(removed);
    }

    //REQUIRES: data in the JSONObject format must contain
    //all the information needed for creating a new account with matching names
    //EFFECTS: create a new account with data in JSONObject format
    public Account(JSONObject json) {
        code = json.getInt("code");
        description = json.getString("description");
        JSONObject jsonDate = json.getJSONObject("date");
        date = LocalDate.of(jsonDate.getInt("year"), jsonDate.getInt("month"), jsonDate.getInt("day"));
        entries = new ArrayList<>();
        JSONArray jsonEntries = json.getJSONArray("entries");
        for (int i = 0; i < jsonEntries.length(); i++) {
            JSONObject entry = jsonEntries.getJSONObject(i);
            if (!entry.toString().equalsIgnoreCase("Null")) {
                entries.add(new AccountEntry(entry));
            }
        }
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



    //EFFECTS: return the entry that contains products for the code
    //return null if there isn't any
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

    //EFFECTS: return a list of strings that contains quantity change at each location
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


    //EFFECTS: return a list of locations where products belonging to this item code exist
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

    //EFFECTS: return quantity of item at the given location
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


    //EFFECTS: return a list of all item codes existing in this account
    public ArrayList<String> getItemCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (AccountEntry entry: entries) {
            codes.add(entry.getItemCode());
        }
        return codes;
    }


    //REQUIRES: the data in JSON format must contain  all necessary information
    //for creating an account with matching names
    //EFFECTS: create and return JSONObject that contains represents this
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("description", description);
        JSONObject jsonDate = new JSONObject();
        jsonDate.put("year", date.getYear());
        jsonDate.put("month", date.getMonthValue());
        jsonDate.put("day", date.getDayOfMonth());
        json.put("date", jsonDate);
        json.put("entries", convertToJsonArray(entries));
        return json;
    }
}
