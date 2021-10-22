package model;

import java.time.LocalDate;

//tag used to add products to the inventory as opposed to Quantity tag
public class InventoryTag {
    private final String itemCode;
    private final int quantity;
    private final String location;
    private final double price;
    private final LocalDate bestBeforeDate;

    //Date: inventory will make it
    public InventoryTag(String itemCode, double price, LocalDate bestBeforeDate, String location, int quantity) {
        this.itemCode = itemCode.toUpperCase();
        this.quantity = quantity;
        this.price = price;
        this.location = location;
        this.bestBeforeDate = bestBeforeDate;
    }

    public InventoryTag(String itemCode, double price, String location, int quantity) {
        this.itemCode = itemCode.toUpperCase();
        this.quantity = quantity;
        this.price = price;
        this.location = location;
        this.bestBeforeDate = null;
    }



    public String getLocation() {
        return location;
    }


    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
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
