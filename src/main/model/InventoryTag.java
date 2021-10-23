package model;

import java.time.LocalDate;

//tag used to add products to the inventory as opposed to Quantity tags, which are used to remove products
public class InventoryTag {
    private final String itemCode;
    private final int quantity;
    private final String location;
    private final double price;
    private final LocalDate bestBeforeDate;

    //EFFECTS: create a new tag that contains all information needed for creating a product with its assigned location
    public InventoryTag(String itemCode, double price, LocalDate bestBeforeDate, String location, int quantity) {
        this.itemCode = itemCode.toUpperCase();
        this.quantity = quantity;
        this.price = price;
        this.location = location;
        this.bestBeforeDate = bestBeforeDate;
    }

    //EFFECTS: create a new tag that contains all information needed for creating a product with its assigned location
    //that's without best before date
    public InventoryTag(String itemCode, double price, String location, int quantity) {
        this.itemCode = itemCode.toUpperCase();
        this.quantity = quantity;
        this.price = price;
        this.location = location;
        this.bestBeforeDate = null;
    }



    //EFFECTS: return location assigned of the product
    public String getLocation() {
        return location;
    }

    //EFFECTS: return best-before date of the product
    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }

    //EFFECTS: return item code of the product
    public String getItemCode() {
        return itemCode;
    }

    //EFFECTS: return quantity specified by the tag
    public int getQuantity() {
        return quantity;
    }

    //EFFECTS:return price of the product
    public double getPrice() {
        return price;
    }
}
