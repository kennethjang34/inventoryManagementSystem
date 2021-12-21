package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.table.TableEntryConvertibleModel;

import java.util.*;

//represents a category where similar items belong
public class Category extends TableEntryConvertibleModel implements JsonConvertible {
    private final String name;
    private int quantity;
    //set containing id's of items belonging to this category
    private final Set<String> items;
    public static final String[] DATA_LIST = new String[]{
            "Name", "Quantity"
    };


    //REQUIRES: category must be composed of English letters or digits only
    //EFFECTS: create a new category with no items belonging to it
    public Category(String category) {
        super(DATA_LIST);
        this.name = category;
        quantity = 0;
        items = new HashSet<>();
    }

    //REQUIRES: the data must be in valid JSONObject form
    //EFFECTS: create a new category form this json data
    public Category(JSONObject json) {
        super(DATA_LIST);
        name = json.getString("name");
        quantity = json.getInt("quantity");
        items = new HashSet<>();
        JSONArray jsonItems = json.getJSONArray("items");

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
    public List<String> getItemIDs() {
        List<String> ids = new ArrayList<>();
        ids.addAll(items);
        return ids;
    }

    //EFFECTS: return true if this category contains a particular item of the name specified
    //Otherwise, return false
    public boolean contains(String name) {
        return items.contains(name);
    }


    //REQUIRES: the item mustn't be existing in this category
    //MODIFIES: this
    //EFFECTS: if there is no such item, add a new item to this category, return true
    //Otherwise, return false
    public boolean addItem(Item item) {
        return items.add(item.getId());
    }


    //MODIFIES: this
    //EFFECTS: remove the item with the given name from this category
    //The item removed will also have its category changed from this
    public boolean removeItem(String id) {
        return  items.remove(id);
    }


//    //EFFECTS: return true if the two objects are the same or it has the same name field or it itself is the name
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof Category)) {
//            if (o instanceof String) {
//                return o.equals(getName());
//            }
//            return false;
//        }
//        if (((Category) o).getName().equals(getName())) {
//            return true;
//        }
//        return false;
//    }

    //EFFECTS: return JSONObject containing data of this
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("quantity", quantity);
        List<String> itemList = new ArrayList<>();
        itemList.addAll(items);
        json.put("items", itemList);
        return json;
    }

    @Override
    public Object[] convertToTableEntry() {
        return new Object[]{
                name, quantity
        };
    }

    @Override
    public String[] getDataList() {
        return new String[]{
                "Name", "Quantity"
        };
    }


    @Override
    public String toString() {
        return name;
    }

//    //EFFECTS: return the hash code of this.
//    //Same name means same category
//    @Override
//    public int hashCode() {
//        return getName().hashCode();
//    }




}
