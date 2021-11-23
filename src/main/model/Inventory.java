package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.*;

//represents an inventory containing information of stocks of different items
public class Inventory extends Subject implements JsonConvertible {

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

        for (Item item: items.values()) {
            Category category = categories.get(item.getCategory());
            category.addItem(item);
        }

    }


    //EFFECTS: return  a list of tags that contain information about quantities at each location
    //Only locations with at least one product belonging to the item code will be added to the list
    public List<QuantityTag> getQuantitiesAtLocations(String id) {
        if (!items.containsKey(id)) {
            return Collections.emptyList();
        } else if (items.get(id).getQuantity() == 0) {
            List<QuantityTag> list = new ArrayList<>();
            list.add(new QuantityTag(id, "N/A", 0));
            return list;
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
        notifyObservers();
    }


    //MODIFIES: this
    //EFFECTS: create a new category if there isn't any
    public boolean createCategory(String name) {
        if (categories.containsKey(name)) {
            return false;
        }
        categories.put(name, new Category(name));
        setChanged(ApplicationConstantValue.CATEGORY);
        notifyObservers();
        EventLog.getInstance().logEvent(new Event("new category " + name + " is created"));
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
        Item item = new Item(id, name, category, listPrice, description, note);
        items.put(id, item);
        categories.get(category).addItem(item);
        setChanged(ApplicationConstantValue.ITEM);
        notifyObservers();
        EventLog.getInstance().logEvent(new Event("new item with ID: " + name
                + " and name " + name + " is created in category " + category));
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
        if (failed.size() != tags.size()) {
            setChanged(ApplicationConstantValue.STOCK);
            notifyObservers();
        }
        return failed;
    }

    //REQUIRES: tag needs to contain all necessary info for new products
    //MODIFIES: this
    //EFFECTS: will put new products in the inventory list using information in the tag
    //If there is no such item to include this product, return false.
    //if succeeded, return true
    public boolean addProducts(InventoryTag tag) {
        String id = tag.getId();
        Item item = items.get(id);
        if (item == null) {
            return false;
        } else {
            item.addProducts(tag);
            setChanged(ApplicationConstantValue.STOCK);
            notifyObservers();
            return true;
        }
    }


    //MODIFIES: this
    //EFFECTS: remove a product with its item code and SKU. if the product is removed, return true
    //if there is no such product, return false.
    public boolean removeProduct(String sku) {

        for (Item item: getItemList()) {
            if (item.contains(sku)) {
                item.removeProduct(sku);
                setChanged(ApplicationConstantValue.STOCK);
                notifyObservers();
                return true;
            }
        }
        return false;
    }




    //MODIFIES: this
    //EFFECTS: remove as many products in the item as specified
    //return true if succeeded. return false otherwise.
    public boolean removeStock(QuantityTag tag) {
        Item item = items.get(tag.getId());
        if (item == null) {
            return false;
        }
        if (item.removeStocks(tag.getLocation(), tag.getQuantity())) {
            setChanged(ApplicationConstantValue.STOCK);
            notifyObservers();
            return true;
        }
        return false;
    }


    //MODIFIES: this
    //EFFECTS: remove as many products in the item as specified
    //return true if succeeded. return false otherwise.
    public boolean removeStock(String id, String location, int qty) {
        Item item = items.get(id);
        if (item == null) {
            return false;
        }
        if (item.removeStocks(location, qty) == true) {
            item.removeStocks(location, qty);
            setChanged(ApplicationConstantValue.STOCK);
            notifyAll();
            return true;
        }
        return false;
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
        setChanged(ApplicationConstantValue.STOCK);
        notifyObservers();
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




    //EFFECTS: return the category name of the given item
    //return null if there is no such item with the given id
    public String getCategoryOf(String id) {
        if (items.containsKey(id)) {
            return items.get(id).getCategory();
        }
        return null;
    }


    //EFFECTS: return the category having the given name
    public Category getCategory(String category) {
        return categories.get(category);
    }

    //EFFECTS: return a list of entries of item hashmap
    public List<Item> getItemList() {
        return new ArrayList<>(items.values());
    }

    //EFFECTS: return a list of items that belong to the given category
    public List<Item> getItemList(String categoryName) {
        Category category = categories.get(categoryName);
        if (category == null) {
            return Collections.emptyList();
        }
        List<String> ids = category.getItemIDs();
        List<Item> inTheCategory = new ArrayList<>();
        for (String id: ids) {
            inTheCategory.add(items.get(id));
        }
        return inTheCategory;
    }



    //EFFECTS: return a list of item ids of the given category
    public List<String> getIDs(String categoryName) {
        Category category = categories.get(categoryName);
        if (category == null) {
            return Collections.emptyList();
        }
        return category.getItemIDs();
    }

    //EFFECTS: return a list of item codes existing in the inventory.
    public List<String> getIDs() {
        if (items.keySet().size() == 0) {
            return Collections.emptyList();
        }
        List<String> ids = new ArrayList<>();
        ids.addAll(items.keySet());
        return ids;
//        return (items.keySet().size() == 0 ? Collections.emptyList() : new ArrayList<>(items.keySet()));
    }


    //EFFECTS: return a String array of categories
    public String[] getCategoryNames() {
        List<Category> categoryList = getCategories();
        String[] names = new String[categoryList.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = categoryList.get(i).getName();
        }
        return names;
    }

    //EFFECTS: return a list of categories
    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
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

    //EFFECTS: return the list of products belonging to the given id at the specified location
    //If there isn't any of those, return empty list.
    public List<Product> getProductList(String id, String location) {
        List<Product> products = null;
        if (items.containsKey(id)) {
            Item item = items.get(id);
            products = item.getProducts(location);
        }
        if (products != null) {
            return products;
        }
        return Collections.emptyList();
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

    //EFFECTS: return column data for converting this to table
    public String[] getDataList() {

        String[] columns = new String[]{
                "Category", "ID", "Name", "Description", "Special Note", "Quantity",
                "Average Cost", "List Price"
        };
        return columns;
    }



    //EFFECTS: return data for the item with the given ID
    public Object[] getData(String id) {
        assert items.containsKey(id);
        Item item = items.get(id);
        return item.convertToTableEntry();
    }

    //EFFECTS: return true if this inventory contains an item with the given id
    public boolean containsItem(String id) {
        return items.containsKey(id);
    }

    //EFFECTS: return list price of the item with the given id
    //If there isn't any such item, throw an error
    public double getListPrice(String id) {
        Item item = items.get(id);
        if (item == null) {
            throw new IllegalArgumentException("There is no such item with the id");
        }
        return item.getListPrice();
    }
}

