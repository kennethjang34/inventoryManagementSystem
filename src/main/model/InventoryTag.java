package model;

public class InventoryTag {
    protected final String itemCode;
    protected final int quantity;
    protected double price;

    public InventoryTag(String itemCode, double price, int quantity) {
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}
