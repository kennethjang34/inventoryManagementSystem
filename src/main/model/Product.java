package model;

import java.time.LocalDate;

//represents product. Each product will have item code, SKU (Stock keeping unit),
//date generated, best before date, and cost it was bought for.
//No data about the product can be changed after creation.
public class Product {
    //item code represents the code composed only of English alphabets that indicates
    //the category of the product. Multiple products can have the same item code.
    private final String itemCode;
    //SKU is unique to each product
    private final int sku;
    private final LocalDate dateGenerated;
    //Only products given best-before date will have a valid best-before date.
    private final LocalDate bestBeforeDate;
    //cost is the money paid for this product
    private final double cost;


    //It is possible for date generated and best before date to be null,
    //However, it is strongly recommended to ensure date generated is a valid Local Date instance.
    //REQUIRES: sku must be 9-digit natural number, cost must be positive.
    //EFFECTS: create a product with specified data.
    public Product(String itemCode, int sku, double cost, LocalDate dateGenerated, LocalDate bestBeforeDate) {
        itemCode = itemCode.toUpperCase();
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


