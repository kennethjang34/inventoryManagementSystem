package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Sale extends Transaction {
    private ArrayList<Item> itemList;
    private double dollarAmount;

    Sale(int transactionNumber, String description, LocalDate date, ArrayList<Item> itemList) {
        super(transactionNumber, description, date);
        double dollars = 0;
        for (Item e: itemList) {
            dollars += e.getDollarAmount();
        }
        dollarAmount = dollars;
        this.itemList = itemList;
    }

    public double getDollarAmount() {
        return dollarAmount;
    }
}
