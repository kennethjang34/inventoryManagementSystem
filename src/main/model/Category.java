package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Category {
    private final String name;
    private int quantity;
    //the map will use items' names in upper case for keys.
    private final LinkedHashMap<String, Item> items;


    //REQUIRES: category must be composed of English letters or digits only
    //EFFECTS: create a new category with no items belonging to it
    public Category(String category) {
        this.name = category;
        quantity = 0;
        items = new LinkedHashMap<>();
    }

    //EFFECTS: return the name of this category
    public String getName() {
        return name;
    }

    //EFFECTS: return the total quantity of stocks in this category
    public int getTotalQuantity() {
        return quantity;
    }

    //EFFECTS: return the number of items belonging to this category
    public int getNumberOfItems() {
        return items.size();
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


    //REQUIRES: the item mustn't be existing in this category
    //MODIFIES: this
    //EFFECTS: add a new item to this category
    public void addItem(Item item) {
        if (items.containsKey(item.getId())) {
            throw new IllegalArgumentException();
        }
        items.put(item.getId(), item);
    }


    //MODIFIES: this
    //EFFECTS: remove the item with the given name from this category
    //The item removed will also have its category changed from this
    public void removeItem(String itemName) {
        Item item = items.get(itemName);
        if (item == null) {
            return;
        }
        item.setCategory(null);
        items.remove(itemName);
    }


    //EFFECTS: return true if the two objects are the same or it has the same name field or it itself is the name
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Category)) {
            if (o instanceof String) {
                return o.equals(getName());
            }
            return false;
        }
        if (((Category) o).getName().equals(getName())) {
            return true;
        }
        return false;
    }

//    //EFFECTS: return the hash code of this.
//    //Same name means same category
//    @Override
//    public int hashCode() {
//        return getName().hashCode();
//    }




}
