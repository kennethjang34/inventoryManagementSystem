package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class ExpiringItem extends Item {
    private ArrayList<ExpiringProduct> expiringProducts;
    private int notificationTime;

    public ExpiringItem(String name, String code, int qty, int stockThreshold, String location, LocalDate exp, int alertTime) {
        super(name, code, qty, stockThreshold, location);
        this.notificationTime = alertTime;
    }


    //EFFECTS: return the number of products belonging to this item that are expiring soon (within a specified time)
    public int getExpiringQuantities() {
        return 0;
    }
}
