package model;

import java.time.LocalDate;

public class Product {
    protected final String itemCode;
    protected final int sku;
    protected String location;
    private LocalDate expirationDate;
    private final double cost;
    private double price;
    public Product(String itemCode, int sku, double buyingPrice, double price, String location) {
        this.itemCode = itemCode;
        this.sku = sku;
        this.location = location;
        this.cost = buyingPrice;
        this.price = price;
        expirationDate = null;
    }

    public Product(String itemCode, int sku, double buyingPrice, double price, String location, LocalDate expirationDate) {
        this.itemCode = itemCode;
        this.sku = sku;
        this.location = location;
        this.cost = buyingPrice;
        this.price = price;
        this.expirationDate = expirationDate;
    }


    public double getPrice() {
        return price;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getLocation() {
        return location;
    }

    public int getSku() {
        return sku;
    }

    public String getItemCode() {
        return itemCode;
    }



}
