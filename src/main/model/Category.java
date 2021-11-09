package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Category {
    private final String category;
    private int quantity;
    private final LinkedHashMap<String, Item> items;


    //REQUIRES: category must be composed of English letters or digits only
    //EFFECTS: create a new category with no items belonging to it
    public Category(String category) {
        this.category = category;
        quantity = 0;
        items = new LinkedHashMap<>();
    }

    //EFFECTS: return the total quantity of stocks in this category
    public int getTotalQuantity() {
        return quantity;
    }

    //EFFECTS: return the list of items belonging to this category
    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        return items;
    }

    //EFFECTS: return true if this category contains a particular item of the name specified
    //Otherwise, return false
    public boolean contains(String name) {
        return false;
    }

    


}
