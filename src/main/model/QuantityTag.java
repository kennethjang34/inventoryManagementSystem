package model;

public class QuantityTag {
    private String itemCode;
    private String location;
    private int quantity;

    public QuantityTag(String itemCode, String location, int quantity) {
        this.itemCode = itemCode.toUpperCase();
        this.location = location.toUpperCase();
        this.quantity = quantity;
    }



    public String getLocation() {
        return location;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public String toString() {
        String s = "Item Code: " + itemCode + ", Location: " + location + ", Quantity: " + quantity;
        return s;
    }
}
