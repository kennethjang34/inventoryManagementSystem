package model;

//represents a tag that is used for various purposes
//such as removing products, recording an account for quantity change.
public class QuantityTag {
    private String itemCode;
    private String location;
    private int quantity;

    //It is possible for quantity to be negative, but it shouldn't be
    //EFFECTS: create a new quantity tag with item code, assigned location, and quantity given.
    public QuantityTag(String itemCode, String location, int quantity) {
        this.itemCode = itemCode.toUpperCase();
        this.location = location.toUpperCase();
        this.quantity = quantity;
    }


    //EFFECTS: return the assigned location
    public String getLocation() {
        return location;
    }

    //EFFECTS: return the item code
    public String getItemCode() {
        return itemCode;
    }

    //EFFECTS: return quantity specified for this item
    public int getQuantity() {
        return quantity;
    }

    //EFFECTS: return a string description that contains all information of this
    @Override
    public String toString() {
        String s = "Item Code: " + itemCode + ", Location: " + location + ", Quantity: " + quantity;
        return s;
    }
}
