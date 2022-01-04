package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.DataViewer;
import ui.table.DataFactory;
import ui.table.TableEntryConvertibleDataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.xml.crypto.Data;
import java.util.*;

//represents a category where similar items belong
public class Category extends TableEntryConvertibleDataFactory implements JsonConvertible, DataViewer {
    private final String name;
    private int quantity;
    //set containing id's of items belonging to this category
    private final Map<String, Item> items;



    public static final String[] DATA_LIST = new String[]{
            "Name", "Quantity"
    };


    //REQUIRES: category must be composed of English letters or digits only
    //EFFECTS: create a new category with no items belonging to it
    public Category(String category) {
        super(DATA_LIST);
        this.name = category;
        quantity = 0;
        items = new HashMap<>();
    }

    //REQUIRES: the data must be in valid JSONObject form
    //EFFECTS: create a new category form this json data
    public Category(JSONObject json) {
        super(DATA_LIST);
        name = json.getString("name");
        quantity = json.getInt("quantity");
        items = new HashMap();
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
        ids.addAll(items.keySet());
        return ids;
    }

    //EFFECTS: return true if this category contains a particular item of the name specified
    //Otherwise, return false
    public boolean contains(String name) {
        return items.containsKey(name);
    }

    public boolean contains(Item item) {
        return items.containsValue(item);
    }


    //REQUIRES: the item mustn't be existing in this category
    //MODIFIES: this
    //EFFECTS: if there is no such item, add a new item to this category, return true
    //Otherwise, return false
    public boolean addItem(Item item) {
        if (items.put(item.getId(), item) == null) {
            changeFirer.fireUpdateEvent(this);
            changeFirer.fireAdditionEvent(this, Inventory.ITEM, item);
            return true;
        }
        return false;
    }


    //MODIFIES: this
    //EFFECTS: remove the item with the given name from this category
    //The item removed will also have its category changed from this
    public boolean removeItem(String id) {
        Item item = items.remove(id);
        if (item != null) {
            changeFirer.fireUpdateEvent(this);
            changeFirer.fireRemovalEvent(this, Inventory.ITEM, item);
            return true;
        }
        return false;
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
        itemList.addAll(items.keySet());
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
    public Object[] getDataList() {
        return new Object[0];
    }

    @Override
    public List<String> getContentsOf(String property) {
        return null;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{
                "Name", "Quantity"
        };
    }

    @Override
    public List<? extends ViewableTableEntryConvertibleModel> getEntryModels() {
        return null;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public void entryRemoved(ViewableTableEntryConvertibleModel o) {

    }

    @Override
    public void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed) {

    }

    @Override
    public void entryAdded(ViewableTableEntryConvertibleModel o) {

    }

    @Override
    public void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added) {

    }

    @Override
    public void entryAdded(List<? extends ViewableTableEntryConvertibleModel> list) {

    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel updatedEntry) {

    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2) {

    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, Object old, Object newObject) {

    }

    @Override
    public void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel removed) {

    }


//    //EFFECTS: return the hash code of this.
//    //Same name means same category
//    @Override
//    public int hashCode() {
//        return getName().hashCode();
//    }




}
