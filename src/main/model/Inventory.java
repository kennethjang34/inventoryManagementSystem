package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.sql.Array;
import java.time.LocalDate;
import java.util.*;


public class Inventory implements JsonConvertible {

    //hashmap with key and value being id of the item and item object respectively
    private final LinkedHashMap<String, Item> items;

    private int quantity;
    private LocalDate currentDate;







    //EFFECTS: create an empty inventory. By default, Item code and sections numbers will have three alphabets and
    // 100 numbers from 0 to 99
    public Inventory() {
        items = new LinkedHashMap<>();
        quantity = 0;
        currentDate = LocalDate.now();
    }


    //REQUIRES: the data in JSON format must contain all necessary information
    //required for creating inventory with matching names.
    //EFFECTS: create a new inventory with the given information from the data in JSON format
    public Inventory(JSONObject jsonInventory) {
        items = new LinkedHashMap<>();
//        currentDate = LocalDate.now();
//        nextSKU = jsonInventory.getInt("nextSKU");
//        skuSize = jsonInventory.getInt("skuSize");
//        codeSize = jsonInventory.getInt("codeSize");
//        numberOfSections = jsonInventory.getInt("numberOfSections");
//        quantity = jsonInventory.getInt("quantity");
//        quantities = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
//        JSONArray jsonQuantities = jsonInventory.getJSONArray("quantities");
//        for (Object json: jsonQuantities) {
//            quantities.add((int)json);
//        }
//        JSONArray jsonLocations = jsonInventory.getJSONArray("locations");
//        locations = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
//        for (int i = 0; i < jsonLocations.length(); i++) {
//            if (jsonLocations.get(i).equals(JSONObject.NULL)) {
//                locations.add(null);
//            } else {
//                locations.add(new ItemList(jsonLocations.getJSONObject(i)));
//            }
//        }
    }


    //EFFECTS: return  a list of tags that contain information about quantities at each location
    //Only locations with at least one product belonging to the item code will be added to the list
    public ArrayList<QuantityTag> getQuantitiesAtLocations(String itemCode) {
        itemCode = itemCode.toUpperCase();
        ArrayList<QuantityTag> tags = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            ItemList items = locations.get(i);
            if (items != null) {
                int qty = items.getQuantity(itemCode);
                if (qty > 0) {
                    tags.add(new QuantityTag(itemCode, getStringLocationCode(i), qty));
                }
            }
        }
        if (tags.size() == 0) {
            return null;
        }
        return tags;
    }


    //EFFECTS: return the currentDate
    public LocalDate getCurrentDate() {
        return currentDate;
    }


    //MODIFIES: this
    //EFFECTS: set the current Date
    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }




    //REQUIRES: tags need to be a list of entries that contain information for new products and its location
    //MODIFIES: this
    //EFFECTS: will put new products in the inventory list using information in the tags
    //return tags that failed to be added
    public List<InventoryTag> addProducts(List<InventoryTag> tags) {
        List<InventoryTag> failed = new ArrayList<>();
        for (InventoryTag tag: tags) {
            String id = tag.getId();
            Item item = items.get(id);
            if (item == null) {
                failed.add(tag);
            } else {
                item.addProducts(tag);
            }
        }
        return failed;
    }


    //MODIFIES: this
    //EFFECTS: remove a product with its item code and SKU. if the product is removed, return true
    //if there is no such product, return false.
    public boolean removeProduct(String sku) {

        for (Item item: getItemList()) {
            if (item.contains(sku)) {
                item.removeProduct(sku);
                return true;
            }
        }
        return false;
    }



//    //MODIFIES: this
//    //EFFECTS: remove the product indicated from the inventory.
//    //return a list of products that have been removed. If quantity to remove exceeds that inside the inventory,
//    //skip the tag(quantity)
//    public LinkedList<QuantityTag> removeProducts(List<QuantityTag> tags) {
//        LinkedList<QuantityTag> invoice = new LinkedList<>();
//        LinkedList<Product> removed;
//        ArrayList<QuantityTag> failed = new ArrayList<>();
//        for (QuantityTag tag: tags) {
//            int qty = tag.getQuantity();
//            String itemCode = tag.getId();
//            String location = tag.getLocation().toUpperCase();
//            int numericLocationCode = getLocationCodeNumber(location);
//            ItemList items = locations.get(numericLocationCode);
//            if (items == null) {
//                failed.add(tag);
//            } else {
//                removed = items.removeProducts(itemCode, qty);
//                if (removed != null) {
//                    quantity -= removed.size();
//                    invoice.add(new QuantityTag(itemCode, location, -removed.size()));
//                }
//            }
//        }
//        return invoice;
//    }


    //MODIFIES: this
    //EFFECTS: remove as many products in the item as specified
    //return true if succeeded. return false otherwise.
    public boolean removeStock(QuantityTag tag) {
        Item item = items.get(tag.getId());
        if (item == null) {
            return false;
        }
        return item.removeProducts(tag.getLocation(), tag.getQuantity());
    }


    //MODIFIES: this
    //EFFECTS: remove as many products in the item as specified
    //return true if succeeded. return false otherwise.
    public boolean removeStock(String id, String location, int qty) {
        Item item = items.get(id);
        if (item == null) {
            return false;
        }
        return item.removeProducts(location, qty);
    }

    //MODIFIES: this
    //EFFECTS: remove stocks specified by the list of quantity tags
    //return quantity tags that have been successfully processed
    public List<QuantityTag> removeStocks(List<QuantityTag> tags) {
        List<QuantityTag> succeeded = new ArrayList<>();
        return succeeded;
    }



//
//    /**
//    @return null
//     */
    //EFFECTS: find the product and return it
    //if it cannot be found, return null
    public Product getProduct(String sku) {
        for (Item item: getItemList()) {
            Product product = item.getProduct(sku);
            if (product != null) {
                return product;
            }
        }
        return null;
    }






    //EFFECTS: return a list of entries of item hashmap
    private List<Item> getItemList() {
        return new ArrayList<Item>(items.values());
    }



    //EFFECTS: return a list of quantityTags where products belonging to the item id are located.
    public List<QuantityTag> findLocations(String id) {
        Item item = items.get(id);
        if (item == null) {
            return Collections.emptyList();
        }
        return item.getQuantities();
    }




    //EFFECTS: return the number of products belonging to the item with the given id.
    public int getQuantity(String id) {
        return ((items.containsKey(id) ? items.get(id).getQuantity() : 0));
    }

    //EFFECTS: return the number of total quantities in the inventory.
    public int getTotalQuantity() {
        int count = 0;
        for (Item item: getItemList()) {
            count += item.getQuantity();
        }
        return count;
    }

    //EFFECTS: return the list of products specified by the id
    //If there isn't any of those products, return empty list
    public List<Product> getProductList(String id) {
        return ((items.containsKey(id) ? items.get(id).getProducts() : Collections.emptyList()));
    }



    //EFFECTS: return a list of item codes existing in the inventory.
    public List<String> getListOfCodes() {
        return (items.keySet().size() == 0 ? Collections.emptyList() : new ArrayList<>(items.keySet()));
    }

    //EFFECTS: convert this to JSONObject and return it.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray jsonLocations = convertToJsonArray(locations);
        json.put("locations", jsonLocations);
        json.put("quantities", new JSONArray(quantities));
        json.put("quantity", quantity);
        return json;
    }

    //EFFECTS: return the oldest product belonging to the id
    public Product getOldestProduct(String id) {
        if (getProductList(id).size() == 0) {
            return null;
        }
        return getProductList(id).get(0);
    }
}

