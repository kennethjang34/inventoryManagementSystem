package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.table.TableEntryConvertibleDataFactory;
import ui.table.TableEntryConvertibleModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.*;

//represents each item in the inventory.
//contains product list belonging to it
//item can be thought of a super-category of products
public class Item extends TableEntryConvertibleDataFactory implements JsonConvertible, PropertyChangeListener {
    private int count = 0;
    private final String id;
    private final String name;
    private String category;
    private String description;
    private double averageCost;
    private double listPrice;
    private String note;

    public enum DataList {
        CATEGORY, ID, NAME, DESCRIPTION, NOTE, QUANTITY, AVERAGE_COST, LIST_PRICE
    }

    public static final String[] DATA_LIST = new String[]{
            DataList.CATEGORY.toString(), DataList.ID.toString(), DataList.NAME.toString(),
            DataList.DESCRIPTION.toString(), DataList.NOTE.toString(), DataList.QUANTITY.toString(),
            DataList.AVERAGE_COST.toString(), DataList.LIST_PRICE.toString()
    };
    //key: sku, value: product
    private LinkedHashMap<String, Product> products;
    //key: location, value: stock
    private LinkedHashMap<String, List<Product>> stocks;

    //REQUIRES: ID, name must be composed of digits or English letters
    //EFFECTS: create a new item containing an empty product list with given information
    public Item(String id, String name, String category, double listPrice, String description, String note) {
        super(DATA_LIST);
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
    //EFFECTS: create a new item based on the given json data
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public Item(JSONObject json) {
        super(DATA_LIST);
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
        for (Object obj : jsonStocks) {
            JSONArray jsonList = (JSONArray) obj;
            List<Product> productList = new ArrayList<>();
            for (Object toBeJson : jsonList) {
                Product product = new Product((JSONObject) toBeJson);
                productList.add(product);
                products.put(product.getSku(), product);
            }
            if (productList.size() != 0) {
                stocks.put(productList.get(0).getLocation(), productList);
            }
        }
    }


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
    public Product removeProduct(String sku) {
        Product product = products.remove(sku);
        if (product == null) {
            return null;
        } else {
            stocks.get(product.getLocation()).remove(product);
            EventLog.getInstance().logEvent(new Event("Product with SKU: " + sku + " removed "));
//            changeFirer.firePropertyChange(DATA_LIST[5], product, null);
            return product;
        }
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
        EventLog.getInstance().logEvent(new Event(qty + " products belonging to ID: "
                + id + " removed from " + location));
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


    //REQUIRES: the tag must have id of this
    //MODIFIES: this
    //EFFECTS: add products to this with the given inventory tag
    public void addProducts(InventoryTag tag) {
        int originalQty = getQuantity();
        int quantity = tag.getQuantity();
        String location = tag.getLocation();
        List<Product> toBeAdded = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Product product = new Product(id, createSku(), tag.getUnitCost(), tag.getUnitPrice(),
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
        EventLog.getInstance().logEvent(new Event(quantity + " new products belonging to ID: "
                + id + " added at " + location));
        if (originalQty != 0) {
            averageCost = (averageCost * originalQty + tag.getUnitCost() * quantity) / (originalQty + quantity);
        } else {
            averageCost = tag.getUnitCost();
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
        EventLog.getInstance().logEvent(new Event(qty + " new products belonging to ID: "
                + id + " added at " + location));
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
        for (Map.Entry<String, List<Product>> entry : stocks.entrySet()) {
            tags.add(new QuantityTag(id, entry.getKey(), entry.getValue().size()));
        }
        return tags;
    }


    //EFFECTS: return an array of info segments for table entry
    @Override
    public Object[] convertToTableEntry() {
        return new Object[]{
                category, id, name, description, note, getQuantity(), averageCost, listPrice
        };
    }

    @Override
    public String[] getColumnNames() {
        return DATA_LIST;
    }


    //EFFECTS: return a JSONObject of this
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
        JSONArray jsonStocks = new JSONArray();
        for (Map.Entry<String, List<Product>> entry : stocks.entrySet()) {
            jsonStocks.put(convertToJsonArray(entry.getValue()));
        }
        json.put("stocks", jsonStocks);
        return json;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Product.DataList.LOCATION.toString())) {
            List<Product> stock = stocks.get(evt.getOldValue());
            stock.remove(evt.getSource());
            List<Product> newLocation = stocks.get(evt.getNewValue());
            if (newLocation == null) {
                newLocation = new ArrayList<>();
            }
            newLocation.add((Product) evt.getSource());
        }
    }

    public List<TableEntryConvertibleModel> getEntryModels() {
        return new ArrayList<>(products.values());
    }



    public List<String> getContentsOf(String property) {
        List<String> contents = new ArrayList<>();

        //Case-insensitive
        switch (DataList.valueOf(property.toUpperCase())) {
            case CATEGORY:
                contents.add(category);
                break;
            case ID:
                contents.add(id);
                break;
            case NAME:
                contents.add(name);
                break;
            case NOTE:
                contents.add(note);
                break;
            case QUANTITY:
                contents.add(String.valueOf(getQuantity()));
                break;
            case AVERAGE_COST:
                contents.add(String.valueOf(listPrice));
                break;
            case LIST_PRICE:
                contents.add(String.valueOf(listPrice));
                break;
            case DESCRIPTION:
                contents.add(description);
                break;

        }
        return contents;
    }

    public Object[] getDataList() {
        return new Object[0];
    }
}
