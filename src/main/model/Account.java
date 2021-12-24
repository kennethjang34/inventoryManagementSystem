package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;
import java.util.*;

//represents an account that contains information about the change that happens on a particular date.
//will have account code, description, date, and quantities for each different item code.
//Note this account must be distinguished from Login accounts, which are completely different
public class Account extends ViewableTableEntryConvertibleModel implements JsonConvertible {
    //Code will be used for finding a particular account
    private String id;
    private String code;
    private final String description;
    private LocalDate date;
    private int quantity;
    private double averageCost;
    private double averagePrice;
    private String location;
    public enum DataList {
        CODE, ID, DATE, LOCATION, AVERAGE_PRICE, AVERAGE_COST
    }

    //EFFECTS: create a new account with the given info
    public Account(int code, String description, LocalDate date,
                   String id, String location, double averageCost,
                   double averagePrice, int quantity) {
        super(new String[]{DataList.CODE.toString(), DataList.ID.toString(), DataList.DATE.toString(),
                DataList.LOCATION.toString(), DataList.AVERAGE_PRICE.toString(), DataList.AVERAGE_COST.toString()
            });
        this.code = "" + code;
        this.id = id;
        this.date = date;
        this.location = location;
        this.averagePrice = averagePrice;
        this.averageCost = averageCost;
        this.quantity = quantity;
        this.description = description;
    }

    //EFFECTS: create a new account with the given info
    public Account(int code, String description, LocalDate date, InventoryTag tag) {
        this.code = "" + code;
        this.id = tag.getId();
        this.location = tag.getLocation();
        this.averagePrice = tag.getUnitPrice();
        this.averageCost = tag.getUnitCost();
        this.date = date;
        this.quantity = tag.getQuantity();
        this.description = description;
    }


    //REQUIRES: data in the JSONObject format must contain
    //all the information needed for creating a new account with matching names
    //EFFECTS: create a new account with data in JSONObject format
    public Account(JSONObject json) {
        super(new String[]{DataList.CODE.toString(), DataList.ID.toString(), DataList.DATE.toString(),
                DataList.LOCATION.toString(), DataList.AVERAGE_PRICE.toString(), DataList.AVERAGE_COST.toString()
        });
        id = json.getString("id");
        description = json.getString("description");
        JSONObject jsonDate = json.getJSONObject("date");
        date = LocalDate.of(jsonDate.getInt("year"), jsonDate.getInt("month"), jsonDate.getInt("day"));
        //entries = new ArrayList<>();
        //JSONArray jsonEntries = json.getJSONArray("entries");
//        for (int i = 0; i < jsonEntries.length(); i++) {
//            JSONObject entry = jsonEntries.getJSONObject(i);
//            if (!entry.equals(JSONObject.NULL)) {
//                //entries.add(new AccountEntry(entry));
//            }
//        }
        location = json.getString("location");
        code = json.getString("code");
        averageCost = json.getDouble("averageCost");
        averagePrice = json.getDouble("averagePrice");
        quantity = json.getInt("quantity");
    }

    //MODIFIES: this
    //EFFECTS: add a new entry for this item code.
//    private void addEntries(List<QuantityTag> tags) {
//        if (tags == null) {
//            return;
//        }
//        for (QuantityTag tag : tags) {
//            String itemCode = tag.getId();
//            if (getEntry(itemCode) == null) {
//                //entries.add(new AccountEntry(tag));
//            } else {
//                //AccountEntry entry = getEntry(itemCode);
//                //entry.addTag(tag);
//            }
//        }
//    }

//
//
//    //EFFECTS: return the entry that contains products for the code
//    //return null if there isn't any
//    private AccountEntry getEntry(String itemCode) {
//        for (AccountEntry entry: entries) {
//            if (entry.itemCode.equalsIgnoreCase(itemCode)) {
//                return entry;
//            }
//        }
//        return null;
//    }
//
//    //EFFECTS: return total product quantity processed in this
//    public int getTotalQuantity() {
//        int qty = 0;
//        for (AccountEntry e: entries) {
//            if (e.getQuantity() < 0) {
//                qty -= e.getQuantity();
//            } else {
//                qty += e.getQuantity();
//            }
//        }
//        return qty;
//    }

//    //EFFECTS: return a list of strings that contains quantity change at each location
//    public List<String> getQuantitiesInfo() {
//        ArrayList<String> quantities = new ArrayList<>();
//        for (AccountEntry entry: entries) {
//            quantities.add(entry.getItemCode() + ": " + entry.getQuantity());
//            for (Map.Entry<String, Integer> qty: entry.getDistribution().entrySet()) {
//                quantities.add("\t" + qty.getKey() + ": " + qty.getValue());
//            }
//        }
//        return quantities;
//    }

    //EFFECTS: return  quantity of the product specified by item code processed in this
    public int getQuantity() {
        return quantity;
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
    public String getCode() {
        return code;
    }

    //EFFECTS: return the item id of this account
    public String getID() {
        return id;
    }

    //EFFECTS: return the location of this
    public String getLocation() {
        return location;
    }

    //EFFECTS: return the average cost
    public double getAverageCost() {
        return averageCost;
    }

    //EFFECTS: return the average price
    public double getAveragePrice() {
        return averagePrice;
    }


//    //EFFECTS: return a list of locations where products belonging to this item code exist
//    public List<String> getLocations(String itemCode) {
//        ArrayList<String> locations = new ArrayList<>();
//        for (AccountEntry entry: entries) {
//            if (entry.getItemCode().equalsIgnoreCase(itemCode)) {
//                Map<String, Integer> distribution = entry.getDistribution();
//                for (Map.Entry<String, Integer> stock: distribution.entrySet()) {
//                    String location = stock.getKey();
//                    locations.add(location);
//                }
//            }
//        }
//        return locations;
//    }
//
//    //EFFECTS: return quantity of item at the given location
//    public int getQuantityAtLocation(String itemCode, String location) {
//        itemCode = itemCode.toUpperCase();
//        location = location.toUpperCase();
//        for (AccountEntry entry: entries) {
//            if (entry.getItemCode().equalsIgnoreCase(itemCode)) {
//                return entry.getQuantityAtLocation(location);
//            }
//        }
//        return 0;
//    }


//    //EFFECTS: return a list of all item codes existing in this account
//    public List<String> getItemCodes() {
//        ArrayList<String> codes = new ArrayList<>();
//        for (AccountEntry entry: entries) {
//            codes.add(entry.getItemCode());
//        }
//        return codes;
//    }


    //REQUIRES: the data in JSON format must contain  all necessary information
    //for creating an account with matching names
    //EFFECTS: create and return JSONObject that contains represents this
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("id", id);
        json.put("location", location);
        json.put("averageCost", averageCost);
        json.put("averagePrice", averagePrice);
        json.put("description", description);
        JSONObject jsonDate = new JSONObject();
        jsonDate.put("year", date.getYear());
        jsonDate.put("month", date.getMonthValue());
        jsonDate.put("day", date.getDayOfMonth());
        json.put("date", jsonDate);
        json.put("quantity", quantity);
        return json;
    }

    @Override
    public Object[] convertToTableEntry() {
        return new Object[] {
                code, id, date, location, averagePrice, averageCost, quantity, description
        };
    }
//
//    @Override
//    public String[] getColumnNames() {
//        String[] columnNames = new String[DataList.values().length];
//        for (int i = 0; i < DataList.values().length; i++) {
//            columnNames[i] = DataList.values()[i].toString();
//        }
//        return columnNames;
//    }
}
