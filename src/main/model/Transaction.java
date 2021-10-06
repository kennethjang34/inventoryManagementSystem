package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Transaction {

    private final int transactionNumber;
    private final LocalDate date;
    private final ArrayList<Item> itemList;
    private final String description;
    private final double dollarAmount;
    Transaction(int transactionNumber, String description, LocalDate date, ArrayList<Item> itemList) {
        this.transactionNumber = transactionNumber;
        this.description = description;
        this.date = date;
        this.itemList = itemList;
        double dollars = 0;
        for (Item e: itemList) {
            dollars += e.getEachPrice();
        }
    }





    public int getInvoiceNumber() {
        return transactionNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}
