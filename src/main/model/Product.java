package model;

import java.time.LocalDate;

public class Product {
    protected final String itemCode;
    protected final int sku;
    protected String location;
    private LocalDate expirationDate;
    private final double buyingPrice;
    private double sellingPrice;
    public Product(String itemCode, int sku, double buyingPrice, double sellingPrice, String location) {
        this.itemCode = itemCode;
        this.sku = sku;
        this.location = location;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        expirationDate = null;
    }

    public Product(String itemCode, int sku, double buyingPrice, double sellingPrice, String location, LocalDate expirationDate) {
        this.itemCode = itemCode;
        this.sku = sku;
        this.location = location;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.expirationDate = expirationDate;
    }


    public double getSellingPrice() {
        return sellingPrice;
    }

    public double getBuyingPrice() {
        return buyingPrice;
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
