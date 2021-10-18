package model;

public class LocationTag {
    private Product product;
    private String location;

    public LocationTag() {
        product = null;
        location = null;
    }

    public LocationTag(Product product, String location) {
        this.product = product;
        this.location = location;
    }

    public Product getProduct() {
        return product;
    }

    public String getLocation() {
        return location;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
