package model;

public class ProductTag {
    private Product product;
    private String location;


    public ProductTag(Product product, String location) {
        this.product = product;
        this.location = location.toUpperCase();
    }

    public Product getProduct() {
        return product;
    }

    public String getLocation() {
        return location;
    }




}
