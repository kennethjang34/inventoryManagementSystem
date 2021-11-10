package model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

//represents each item in the inventory.
//contains product list belonging to it
//item can be thought of a super-category of products
public class Item implements  TableEntryConvertible {
    private static int count = 0;
    private final String id;
    private final String name;
    private Category category;
    private String description;
    private int quantity;
    private double averageCost;
    private double listPrice;
    private String note;
    //key: sku, value: product
    private LinkedHashMap<String, Product> products;
    //key: location, value: stock
    private LinkedHashMap<String, List<Product>> stocks;

    //ID, name must be composed of digits or English letters
    //EFFECTS: create a new item containing an empty product list with given information
    public Item(String id, String name, Category category, double listPrice, String description, String note) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.listPrice = listPrice;
        this.description = description;
        this.note = note;
        quantity = 0;
        averageCost = 0;
        products = new LinkedHashMap<>();
        stocks = new LinkedHashMap<>();
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
    public Category getCategory() {
        return category;
    }

    //EFFECTS: return stock of this item
    public int getQuantity() {
        return quantity;
    }

    //EFFECTS: return the number of products at a particular location
    public int getQuantity(String location) {
        return 0;
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
    //If there was the product, and it has been removed, return true.
    //Otherwise, return false.
    public boolean removeProduct(String sku) {

        if (products.remove(sku) == null) {
            return false;
        }
        return true;
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

        for (Product product: products) {
            products.remove(product);
            this.products.remove(product.getSku());
        }
        assert getQuantity(location) == originalQty;
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
    public void setCategory(Category category) {
        if (this.category != null && !this.category.getName().equalsIgnoreCase(category.getName())) {
            Category oldCategory = this.category;
            this.category = category;
            oldCategory.removeItem(getName());
        } else {
            this.category = category;
        }
    }





    //MODIFIES: this
    //EFFECTS: add products to this with the given inventory tag
    public void addProducts(InventoryTag tag) {

    }

    //MODIFIES: this
    //EFFECTS: add products to this with the given info
    public void addProducts(double cost, double price, LocalDate bestBeforeDate,
                            LocalDate dateGenerated, String location, int qty) {
        //stub
    }

    //MODIFIES: this
    //EFFECTS: add products to this with the given info without best before date
    public void addProducts(String id, double cost, double price, LocalDate dateGenerated,
                            String location, int qty) {
        //stub
    }

    //EFFECTS: return a list of products belonging to this item
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();

        return products;
    }

    //EFFECTS: return a list of products at a specified location
    public List<Product> getProducts(String location) {
        List<Product> products = new ArrayList<>();

        return products;
    }

    //EFFECTS: return a list of quantity tags that contain stock information at different location
    public List<QuantityTag> getQuantities() {
        List<QuantityTag> tags = new ArrayList<>();
        return tags;
    }



    //EFFECTS: return an array of column names for table entry
    @Override
    public Object[] getColumnNames() {
        return new Object[0];
    }

    //EFFECTS: return an array of info segments for table entry
    @Override
    public Object[] convertToTableEntry() {
        return new Object[0];
    }
}
