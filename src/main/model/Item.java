package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;

//represents each item in the inventory.
//contains product list belonging to it
//item can be thought of a super-category of products
public class Item implements  TableEntryConvertible, JsonConvertible {
    private static int count = 0;
    private final String id;
    private final String name;
    private String category;
    private String description;
    private double averageCost;
    private double listPrice;
    private String note;
    //key: sku, value: product
    private LinkedHashMap<String, Product> products;
    //key: location, value: stock
    private LinkedHashMap<String, List<Product>> stocks;

    //REQUIRES: ID, name must be composed of digits or English letters
    //EFFECTS: create a new item containing an empty product list with given information
    public Item(String id, String name, String category, double listPrice, String description, String note) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.listPrice = listPrice;
        this.description = description;
        this.note = note;
        averageCost = 0;
        products = new LinkedHashMap<>();
        stocks = new LinkedHashMap<>();
    }

    //REQUIRES: json must be in a valid JSONObject format containing all necessary info for item class
    public Item(JSONObject json) {
        count = json.getInt("count");
        id = json.getString("id");
        name = json.getString("name");
        category = json.getString("category");
        description = json.getString("description");
        note = json.getString("note");
        listPrice = json.getDouble("listPrice");
        averageCost = json.getDouble("averageCost");
        products = new LinkedHashMap<>();
        stocks = new LinkedHashMap<>();
        JSONArray jsonStocks = json.getJSONArray("stocks");
        for (Object obj: jsonStocks) {
            JSONArray jsonList = (JSONArray) obj;
            List<Product> productList = new ArrayList<>();
            for (Object toBeJson: jsonList) {
                JSONObject jsonObject = (JSONObject)toBeJson;
                Product product = new Product(jsonObject);
                productList.add(product);
                products.put(product.getSku(), product);
            }
            stocks.put(productList.get(0).getLocation(), productList);
        }
    }

//    //REQUIRES:ID, name must be composed of digits or English letters
//    //EFFECTS: create a new item containing as many products as specified
//    public Item(String id, String name, String category, double listPrice,
//                String description, String note, List<Inventory> tags) {
//        this.id = id;
//        this.name = name;
//        this.category = category;
//        this.listPrice = listPrice;
//        this.description = description;
//        this.note = note;
//        quantity = qty;
//        averageCost = cost / qty;
//    }

    //EFFECTS: return the id
    public String getId() {
        return id;
    }

    //EFFECTS:return name
    public String getName() {
        return name;
    }

    //EFFECTS: return the category of this item
    public String getCategory() {
        return category;
    }

    //EFFECTS: return stock of this item
    public int getQuantity() {
        return products.size();
    }

    //EFFECTS: return the number of products at a particular location
    public int getQuantity(String location) {
        if (stocks.get(location) == null) {
            return 0;
        }
        return stocks.get(location).size();
    }

    //EFFECTS: return the list price
    public double getListPrice() {
        return listPrice;
    }

    //EFFECTS: return the average cost of each product belonging to this item
    public double getAverageCost() {
        return averageCost;
    }

    //EFFECTS: return the description of this item
    public String getDescription() {
        return description;
    }

    //EFFECTS: return the special note for this item
    public String getNote() {
        return note;
    }

    //EFFECTS: return the product with the specified sku
    public Product getProduct(String sku) {
        return products.get(sku);
    }

    //MODIFIES: this
    //EFFECTS: remove the product specified by the sku
    //If there was the product, and it has been removed, return true
    //Otherwise, return false.
    public boolean removeProduct(String sku) {
        Product product = products.remove(sku);
        if (product == null) {
            return false;
        } else {
            stocks.get(product.getLocation()).remove(product);
            return true;
        }
    }


    //REQUIRES: for each tag, there must be enough stock for removal.
    //MODIFIES: this
    //EFFECTS:remove as many products as specified by the quantity tags.
    //return  tags skipped because there was not enough stock
    public List<QuantityTag> removeStocks(List<QuantityTag> tags) {
        List<QuantityTag> failed = new ArrayList<>();
        for (QuantityTag tag: tags) {
            if (!removeStocks(tag.getLocation(), tag.getQuantity())) {
                failed.add(tag);
            }
        }
        if (failed.size() == 0) {
            return Collections.emptyList();
        }
        return failed;
    }

    //REQUIRES: there must be enough stock for removal
    //MODIFIES: this
    //EFFECTS:remove as many products as specified. If succeeded, return true
    //Otherwise if there is not enough stock, return false.
    public boolean removeStocks(String location, int qty) {
        if (getQuantity(location) < qty) {
            return false;
        }
        int originalQty = getQuantity(location);
        List<Product> products = getProducts(location);
        for (int i = 0; i < qty; i++) {
            Product product = products.get(0);
            this.products.remove(product.getSku());
            products.remove(product);
        }
        //assert getQuantity(location) == originalQty - qty;
        return true;
    }

    //MODIFIES: this
    //EFFECTS: create a next sku
    public String createSku() {
        return id + count++;
    }

    //EFFECTS: return true if this contains the product with the given SKU
    //Otherwise, return false
    public boolean contains(String sku) {
        return (products.containsKey(sku));
    }



    //MODIFIES: this
    //EFFECTS: change the category of this item
    public void setCategory(String category) {
        this.category = category;
    }




    //Inventory tag contains following info
    //

    //REQUIRES: the tag must have id of this
    //MODIFIES: this
    //EFFECTS: add products to this with the given inventory tag
    public void addProducts(InventoryTag tag) {
        int originalQty = getQuantity();
        int quantity = tag.getQuantity();
        double unitCost = tag.getUnitCost();
        String location = tag.getLocation();
        List<Product> toBeAdded = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Product product = new Product(id, createSku(), unitCost, tag.getUnitPrice(),
                    tag.getDateGenerated(), tag.getBestBeforeDate(), location);
            toBeAdded.add(product);
            products.put(product.getSku(), product);
        }
        List<Product> existing = stocks.get(location);
        if (existing == null) {
            stocks.put(location, toBeAdded);
        } else {
            existing.addAll(toBeAdded);
        }
        if (originalQty != 0) {
            averageCost = (averageCost * originalQty + unitCost * quantity) / (originalQty + quantity);
        } else {
            averageCost = unitCost;
        }
    }

    //MODIFIES: this
    //EFFECTS: add products to this with the given info
    public void addProducts(double cost, double price, LocalDate bestBeforeDate,
                            LocalDate dateGenerated, String location, int qty) {
        List<Product> toBeAdded = new ArrayList<>();
        int originalQty = getQuantity();
        for (int i = 0; i < qty; i++) {
            Product product = new Product(id, createSku(), cost, price,
                    dateGenerated, bestBeforeDate, location);
            toBeAdded.add(product);
            products.put(product.getSku(), product);
        }
        List<Product> existing = stocks.get(location);
        if (existing == null) {
            stocks.put(location, toBeAdded);
        } else {
            existing.addAll(toBeAdded);
        }
        if (originalQty != 0) {
            averageCost = (averageCost * originalQty + cost * qty) / (originalQty + qty);
        } else {
            averageCost = cost;
        }
    }

    //MODIFIES: this
    //EFFECTS: add products to this with the given info without best before date
    public void addProducts(String id, double cost, double price, LocalDate dateGenerated,
                            String location, int qty) {
        List<Product> toBeAdded = new ArrayList<>();
        int originalQty = getQuantity();
        for (int i = 0; i < qty; i++) {
            Product product = new Product(id, createSku(), cost, price,
                    dateGenerated, null, location);
            toBeAdded.add(product);
            products.put(product.getSku(), product);
        }
        List<Product> existing = stocks.get(location);
        if (existing == null) {
            stocks.put(location, toBeAdded);
        } else {
            existing.addAll(toBeAdded);
        }
        if (originalQty != 0) {
            averageCost = (averageCost * originalQty + cost * qty) / (originalQty + qty);
        } else {
            averageCost = cost;
        }
    }

    //EFFECTS: return a list of products belonging to this item
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<Product>(this.products.values());
        return products;
    }

    //EFFECTS: return a list of products at a specified location
    public List<Product> getProducts(String location) {
        List<Product> products = stocks.get(location);
        if (products == null) {
            return Collections.emptyList();
        }
        return products;
    }

    //EFFECTS: return a list of quantity tags that contain stock information at different location
    public List<QuantityTag> getQuantities() {
        List<QuantityTag> tags = new ArrayList<>();
        for (Map.Entry<String, List<Product>> entry: stocks.entrySet()) {
            tags.add(new QuantityTag(id, entry.getKey(), entry.getValue().size()));
        }
        return tags;
    }



    //EFFECTS: return an array of column names for table entry
    @Override
    public Object[] getColumnNames() {
        Object[] columns = new Object[]{
                "Category", "ID", "Name", "Description", "Special Note", "Quantity", "Average Cost", "List Price"
        };
        return columns;
    }

    //EFFECTS: return an array of info segments for table entry
    @Override
    public Object[] convertToTableEntry() {
        return new Object[]{
                category, id, name, description, note, getQuantity(), averageCost, listPrice, new JButton()
        };
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("count", count);
        json.put("id", id);
        json.put("name", name);
        json.put("category", category);
        json.put("description", description);
        json.put("note", note);
        json.put("listPrice", listPrice);
        json.put("averageCost", averageCost);
        //json.put("products", new JSONArray(convertToJsonArray(JsonConvertible.convertToList(products))));
        JSONArray jsonStocks = new JSONArray();
        for (Map.Entry<String, List<Product>> entry: stocks.entrySet()) {
            jsonStocks.put(convertToJsonArray(entry.getValue()));
        }
        json.put("stocks", jsonStocks);
        return json;
    }
}
