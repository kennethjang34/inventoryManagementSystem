package model;

public class ProductTag {
    private Product product;
    private String location;

    public ProductTag() {
        product = null;
        location = null;
    }

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

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String toString() {
        String s = product.getItemCode() + product.getSku() + ", " + location;
        return s;
    }


}
