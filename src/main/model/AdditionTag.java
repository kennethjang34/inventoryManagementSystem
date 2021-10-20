package model;

import java.time.LocalDate;

public class AdditionTag extends InventoryTag {
    private String location;
    private double price;
    private LocalDate bestBeforeDate;

    //Date: inventory will make it
    public AdditionTag(String itemCode, double price, LocalDate date, String location, int quantity) {
        super(itemCode, price, quantity);
        this.location = location;
        this.price = price;
        bestBeforeDate = date;
    }


    public String getLocation() {
        return location;
    }


    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }
}
