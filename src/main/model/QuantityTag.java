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

    //REQUIRES: quantity must be non-negative
    //MODIFIES: this
    //EFFECTS: change the quantity to the given value
    public void setQuantity(int qty) {
        quantity = qty;
    }

    //return true if the given objet is equal to this. Otherwise, return false.
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QuantityTag)) {
            return false;
        }
        QuantityTag tag = (QuantityTag)o;
        if (tag.getItemCode().equalsIgnoreCase(itemCode) && tag.getLocation().equalsIgnoreCase(location)) {
            return true;
        }
        return false;
    }

    //EFFECTS: return hash code of this
    @Override
    public int hashCode() {
        return itemCode.hashCode();
    }

    //EFFECTS: return a string description that contains all information of this
    @Override
    public String toString() {
        String s = "Item Code: " + itemCode + ", Location: " + location + ", Quantity: " + quantity;
        return s;
    }
}
