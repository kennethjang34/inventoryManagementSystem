package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.DataViewer;
import ui.table.DataFactory;
import ui.table.TableEntryConvertibleDataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;
import java.util.*;

//represents each item in the inventory.
//contains product list belonging to it
//item can be thought of a super-category of products
public class Item extends TableEntryConvertibleDataFactory implements JsonConvertible, DataViewer {
    private int processedQty = 0;
    private int count = 0;
    private final String id;
    private String name;
    private String category;
    private String description;
    private double averageCost;
    private double listPrice;
    private String note;

    public enum DataList {
        CATEGORY, ID, NAME, DESCRIPTION, NOTE, QUANTITY, AVERAGE_COST, LIST_PRICE, PRODUCT
    }

    public enum ColumnNames {
        CATEGORY, ID, NAME, DESCRIPTION, NOTE, QUANTITY, AVERAGE_COST, LIST_PRICE
    }

    public static final String[] DATA_LIST = new String[]{
            ColumnNames.CATEGORY.toString(), ColumnNames.ID.toString(), ColumnNames.NAME.toString(),
            ColumnNames.DESCRIPTION.toString(), ColumnNames.NOTE.toString(), ColumnNames.QUANTITY.toString(),
            ColumnNames.AVERAGE_COST.toString(), ColumnNames.LIST_PRICE.toString()
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

    @Override
    public void entryRemoved(ui.table.ViewableTableEntryConvertibleModel o) {

    }

    @Override
    public void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed) {

    }

    @Override
    public void entryAdded(ui.table.ViewableTableEntryConvertibleModel o) {

    }

    @Override
    public void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added) {

    }

    @Override
    public void entryAdded(List<? extends ViewableTableEntryConvertibleModel> list) {

    }

    @Override
    public void entryUpdated(ui.table.ViewableTableEntryConvertibleModel updatedEntry) {
//        if (products.containsValue(updatedEntry)) {
//            productLocationChanged((Product)updatedEntry);
//        }
    }

    @Override
    public void entryUpdated(ui.table.ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2) {
        Product product = (Product) source;
        Product.DataList dataType = Product.DataList.valueOf(property);
        switch (dataType) {
            case ID:
                productIDChange(product, (String)o1, (String)o2);
                break;
            case LOCATION:
                productLocationChanged(product, (String) o1, (String) o2);
                break;
            case COST:
                productCostChanged(product, (Double) o1, (Double) o2);
                break;
//            case PRICE:
//
//                break;
//            case BEST_BEFORE_DATE:
//
//                break;
        }
    }

    @Override
    public void entryUpdated(ui.table.ViewableTableEntryConvertibleModel source, Object old, Object newObject) {

    }

    @Override
    public void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel removed) {

    }


    public int getProcessedQty() {
        return processedQty;
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




    public void setCategory(String category) {
        String old = this.category;
        this.category = category;
        changeFirer.fireUpdateEvent(this, ColumnNames.CATEGORY.toString(), old, category);
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        changeFirer.fireUpdateEvent(this, ColumnNames.NAME.toString(), old, name);
    }

    public void setDescription(String description) {
        String old = this.description;
        this.description = description;
        changeFirer.fireUpdateEvent(this, ColumnNames.DESCRIPTION.toString(), old, description);
    }

    public void setNote(String note) {
        String old = this.note;
        this.note = note;
        changeFirer.fireUpdateEvent(this, ColumnNames.NOTE.toString(), old, note);
    }

    public void setListPrice(double listPrice) {
        double old = this.listPrice;
        this.listPrice = listPrice;
        changeFirer.fireUpdateEvent(this, ColumnNames.LIST_PRICE.toString(), old, listPrice);
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
            product.removeListener(this);
            EventLog.getInstance().logEvent(new Event("Product with SKU: " + sku + " removed "));
            changeFirer.fireUpdateEvent(this);
            changeFirer.fireRemovalEvent(DataList.PRODUCT.toString(), product);
            return product;
        }
    }

    public Product removeProduct(Product toBeRemoved) {
        String sku = toBeRemoved.getSku();
        Product product = products.remove(sku);
        if (product == null) {
            return null;
        } else {
            stocks.get(product.getLocation()).remove(product);
            product.removeListener(this);
            EventLog.getInstance().logEvent(new Event("Product with SKU: " + sku + " removed "));
            changeFirer.fireUpdateEvent(this);
            changeFirer.fireRemovalEvent(DataList.PRODUCT.toString(), product);
            return product;
        }

    }


    //REQUIRES: there must be enough stock for removal
    //MODIFIES: this
    //EFFECTS:remove as many products as specified. If succeeded, return true
    //Otherwise if there is not enough stock, return false.
    public List<Product> removeStocks(String location, int qty) {
        List<Product> productList = getProducts(location, qty);
        for (int i = 0; i < qty; i++) {
            Product product = productList.get(i);
            products.remove(product.getSku());
            stocks.get(product.getLocation()).remove(product);
            product.removeListener(this);
        }
        changeFirer.fireRemovalEvent(DataList.PRODUCT.toString(), productList);
        changeFirer.fireUpdateEvent(this);
        return productList;
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



    public void addProduct(Product product) {
        products.put(product.getSku(), product);
        List<Product> list = stocks.get(product.getLocation());
        if (list == null) {
            list = new LinkedList<>();
        }
        list.add(product);
        stocks.put(product.getLocation(), list);
        product.addDataChangeListener(this);
        averageCost = (averageCost * processedQty + product.getCost())/(++processedQty);
//        changeFirer.fireAdditionEvent(DataList.QUANTITY.toString(), this);
        changeFirer.fireAdditionEvent(DataList.PRODUCT.toString(), product);
        changeFirer.fireUpdateEvent(this);
    }


    //REQUIRES: the tag must have id of this
    //MODIFIES: this
    //EFFECTS: add products to this with the given inventory tag
    public List<Product> addProducts(InventoryTag tag) {
        int quantity = tag.getQuantity();
        String location = tag.getLocation();
        List<Product> added = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Product product = new Product(id, createSku(), tag.getUnitCost(), tag.getUnitPrice(),
                    tag.getDateGenerated(), tag.getBestBeforeDate(), location);
            product.addDataChangeListener(this);
            added.add(product);
            products.put(product.getSku(), product);
        }
        List<Product> existing = stocks.get(location);
        if (existing == null) {
            stocks.put(location, added);
        } else {
            existing.addAll(added);
        }
        EventLog.getInstance().logEvent(new Event(quantity + " new products belonging to ID: "
                + id + " added at " + location));
        if (processedQty != 0) {
            averageCost = (averageCost * processedQty + tag.getUnitCost() * quantity) / (processedQty + quantity);
        } else {
            averageCost = tag.getUnitCost();
        }
        processedQty += quantity;
//        changeFirer.fireUpdateEvent(this, DataList.QUANTITY.toString(), processedQty - quantity, processedQty);
        changeFirer.fireUpdateEvent(this);
        changeFirer.fireAdditionEvent(DataList.PRODUCT.toString(), added);
        return added;
    }

    //MODIFIES: this
    //EFFECTS: add products to this with the given info
    public void addProducts(double cost, double price, LocalDate bestBeforeDate,
                            LocalDate dateGenerated, String location, int qty) {
        List<Product> added = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            Product product = new Product(id, createSku(), cost, price,
                    dateGenerated, bestBeforeDate, location);
            product.addDataChangeListener(this);
            added.add(product);
            products.put(product.getSku(), product);
        }
        List<Product> existing = stocks.get(location);
        if (existing == null) {
            stocks.put(location, added);
        } else {
            existing.addAll(added);
        }
        EventLog.getInstance().logEvent(new Event(qty + " new products belonging to ID: "
                + id + " added at " + location));
        if (processedQty != 0) {
            averageCost = (averageCost * processedQty + cost * qty) / (processedQty + qty);
        } else {
            averageCost = cost;
        }
        processedQty += qty;
//        changeFirer.fireUpdateEvent(this, DataList.QUANTITY.toString(), processedQty - qty, processedQty);
        changeFirer.fireUpdateEvent(this);
        changeFirer.fireAdditionEvent(DataList.PRODUCT.toString(), added);
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


    //location being null indicates all locations
    public List<Product> getProducts(String location, int qty) {
        List<Product> productList = (location == null ? new ArrayList<>(products.values()) : stocks.get(location));
        try {
            return productList.subList(0, qty);
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("The quantity of products to be found is fewer than that of the parameters");
        }
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

    public List<? extends ViewableTableEntryConvertibleModel> getEntryModels() {
        return new ArrayList<>(products.values());
    }



    public List<Object> getContentsOf(String property) {
        List<Object> contents = new ArrayList<>();

        //Case-insensitive
        switch (ColumnNames.valueOf(property.toUpperCase())) {
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
                contents.add(String.valueOf(averageCost));
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

    public void productLocationChanged(Product p, String old, String newLocation) {
        if (products.containsValue(p) && stocks.get(old) != null) {
            stocks.get(old).remove(p);
            List<Product> list = stocks.get(newLocation);
            if (list == null) {
                list = new LinkedList<>();
            }
            list.add(p);
            stocks.put(newLocation, list);
            changeFirer.fireUpdateEvent(this);
        }
    }

    public void productLocationChanged(Product p) {
        for (Map.Entry<String, List<Product>> entry: stocks.entrySet()) {
            List<Product> productList = entry.getValue();
            if (productList.remove(p)) {
                return;
            }
        }
        if (stocks.get(p.getLocation()) != null) {
            stocks.get(p.getLocation()).add(p);
        }
        changeFirer.fireUpdateEvent(this);
    }

    //ITEM ID is immutable. ID change of a product indicates moving product to another item class
    public void productIDChange(Product product) {
        if (product.getID().equals(id)) {
            return;
        }
        double sumCost = averageCost * processedQty;
        removeProduct(product.getSku());
        sumCost = sumCost - product.getCost();
        processedQty--;
        if (processedQty == 0) {
            averageCost = 0;
        } else {
            averageCost = sumCost/processedQty;
        }
        changeFirer.fireUpdateEvent(this);
    }

    public void productIDChange(Product product, String old, String newId) {
        if (product.getID().equals(id)) {
            return;
        }
        double sumCost = averageCost * processedQty;
        removeProduct(product.getSku());
        sumCost = sumCost - product.getCost();
        processedQty--;
        if (processedQty == 0) {
            averageCost = 0;
        } else {
            averageCost = sumCost/processedQty;
        }
//        changeFirer.fireUpdateEvent(this, old, newId);
        changeFirer.fireUpdateEvent(this);
    }

    public void productCostChanged(Product product, double o1, double o2) {
        averageCost = (averageCost * processedQty - o1 + o2) / processedQty;
        changeFirer.fireUpdateEvent(this);
    }


}
