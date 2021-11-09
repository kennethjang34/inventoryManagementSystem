package model;


import java.util.LinkedHashMap;
import java.util.List;

//represents each item in the inventory.
//contains product list belonging to it
//item can be thought of a super-category of products
public class Item {
    private static int count = 0;
    private final String id;
    private final String name;
    private final String category;
    private String description;
    private int quantity;
    private double averageCost;
    private double listPrice;
    private String note;
    private LinkedHashMap<String, Product> products;


    //ID, name must be composed of digits or English letters
    //EFFECTS: create a new item containing an empty product list with given information
    public Item(String id, String name, String category, double listPrice, String description, String note) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.listPrice = listPrice;
        this.description = description;
        this.note = note;
        quantity = 0;
        averageCost = 0;
        products = new LinkedHashMap<>();
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
        return quantity;
    }

    //EFFECTS: return the product with the specified sku
    public Product getProduct(String sku) {
        return null;
    }

    //MODIFIES: this
    //EFFECTS: remove the product specified by the sku
    public void removeProduct(String sku) {
        //stub
    }


    //REQUIRES: for each tag, there must be enough stock for removal.
    //MODIFIES: this
    //EFFECTS:remove as many products as specified by the quantity tags.
    public void removeProducts(List<QuantityTag> tags) {

    }

    //MODIFIES: this
    //EFFECTS: add products to this with the given inventory tags
    public void addProducts(List<Inventory> tags) {

    }






}
