package model;

public class RemovalTag extends InventoryTag {
    private String location;

    public RemovalTag(String itemCode, double price, String location, int quantity) {
        super(itemCode, price, quantity);
        this.location = location.toUpperCase();
    }

    public String getLocation() {
        return location;
    }

}
