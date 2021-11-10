package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.*;


public class Inventory implements JsonConvertible {

    //hashmap with key and value being id of the item and item object respectively
    private final Map<String, Item> items;
    private Map<String, Category> categories;

    private int quantity;
    private LocalDate currentDate;







    //EFFECTS: create an empty inventory.
    public Inventory() {
        items = new LinkedHashMap<>();
        categories = new HashMap<>();
        quantity = 0;
        currentDate = LocalDate.now();
    }


    //REQUIRES: the data in JSON format must contain all necessary information
    //required for creating inventory with matching names.
    //EFFECTS: create a new inventory with the given information from the data in JSON format
    public Inventory(JSONObject jsonInventory) {
        items = new HashMap<>();
        categories = new HashMap<>();
        JSONArray jsonItems = jsonInventory.getJSONArray("items");
        for (Object obj: jsonItems) {
            Item item = new Item((JSONObject)obj);
            items.put(item.getId(), item);
        }
        JSONArray jsonCategories = jsonInventory.getJSONArray("categories");
        for (Object obj: jsonCategories) {
            Category category = new Category((JSONObject)obj);
            categories.put(category.getName(), category);
        }

    }


    //EFFECTS: return  a list of tags that contain information about quantities at each location
    //Only locations with at least one product belonging to the item code will be added to the list
    public List<QuantityTag> getQuantitiesAtLocations(String id) {
        if (!items.containsKey(id)) {
            return Collections.emptyList();
        }
        return items.get(id).getQuantities();
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


    //MODIFIES: this
    //EFFECTS: create a new category if there isn't any
    public boolean createCategory(String name) {
        if (categories.containsKey(name)) {
            return false;
        }
        categories.put(name, new Category(name));
        return true;
    }

    //EFFECTS: return true if this inventory contains a category with the given name
    public boolean containsCategory(String name) {
        List<Category> categories = new ArrayList<>(this.categories.values());
        for (Category category: categories) {
//            Category category = (Category)obj;
            if (category.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }


    //MODIFIES: this
    //EFFECTS: create a new item if there isn't any item with the same id
    public boolean createItem(String id, String name, String category, double listPrice,
                              String description, String note) {
        if (!containsCategory(category)) {
            return false;
        }
        if (items.containsKey(id)) {
            return false;
        }
        items.put(id, new Item(id, name, categories.get(category).getName(), listPrice, description, note));
        return true;
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
        return item.removeStocks(tag.getLocation(), tag.getQuantity());
    }


    //MODIFIES: this
    //EFFECTS: remove as many products in the item as specified
    //return true if succeeded. return false otherwise.
    public boolean removeStock(String id, String location, int qty) {
        Item item = items.get(id);
        if (item == null) {
            return false;
        }
        return item.removeStocks(location, qty);
    }

    //MODIFIES: this
    //EFFECTS: remove stocks specified by the list of quantity tags
    //return quantity tags that have been successfully processed
    public List<QuantityTag> removeStocks(List<QuantityTag> tags) {
        List<QuantityTag> succeeded = new ArrayList<>();
        for (QuantityTag tag: tags) {
            String id = tag.getId();
            Item item = items.get(id);
            if (item != null) {
                if (item.removeStocks(tag.getLocation(), tag.getQuantity())) {
                    succeeded.add(tag);
                }
            }
        }
        if (succeeded.size() == 0) {
            return Collections.emptyList();
        }
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
        return new ArrayList<>(items.values());
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

//    //EFFECTS: convert this to JSONObject and return it.
//    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray jsonItems = new JSONArray();
        for (Map.Entry<String, Item> entry: items.entrySet()) {
            jsonItems.put(entry.getValue().toJson());
        }
        json.put("items", jsonItems);
        JSONArray jsonCategories = new JSONArray();
        for (Map.Entry<String, Category> entry: categories.entrySet()) {
            jsonCategories.put(entry.getValue().toJson());
        }
        json.put("categories", jsonCategories);
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

