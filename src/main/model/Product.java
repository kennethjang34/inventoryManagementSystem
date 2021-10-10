package model;

import java.time.LocalDate;

public class Product {
    private final String itemCode;
    private final int sku;
    private final LocalDate dateGenerated;
    private LocalDate bestBeforeDate;
    private double cost;



    //REQUIRES: sku must be 9-digit natural number, cost must be positive.
    //EFFECTS: create a product with specified data.
    public Product(String itemCode, int sku, double cost, LocalDate dateGenerated, LocalDate bestBeforeDate) {
        this.itemCode = itemCode;
        this.sku = sku;
        this.cost = cost;
        this.dateGenerated = dateGenerated;
        this.bestBeforeDate = bestBeforeDate;
    }

    //EFFECTS: return the string item code of this product
    public String getItemCode() {
        return itemCode;
    }

    //EFFECTS: return the stock keeping unit(SKU) of this product
    public int getSku() {
        return sku;
    }

    //EFFECTS: return the date the product was created
    public LocalDate getDateGenerated() {
        return dateGenerated;
    }

    //EFFECTS: return the best-before-date of this product. if the best-before-date wasn't set, return null.
    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }

    //EFFECTS: return the cost paid for this product
    public double getCost() {
        return cost;
    }
}


