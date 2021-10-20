package model;

public class ProductNotFoundException extends Exception {
    private int sku;


    public ProductNotFoundException(int sku) {
        super("The Product couldn't be found");
        this.sku = sku;
    }

    public ProductNotFoundException(int sku, String description) {
        super(description);
        this.sku = sku;
    }

}
