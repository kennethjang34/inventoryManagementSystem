package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Item {
    protected static int productNumber;
    protected String name;
    protected String code;
    protected int quantity;
    private double dollarAmount;
    private double standardPrice;

    protected int lowStockThreshold;
    protected ArrayList<Product> products;
    protected ArrayList<Transaction> transactions;
    protected String location;
    private boolean isExpiring;

    public Item(String name, String code, int qty, double priceIn, double priceOut, int stockThreshold, String location) {
        products = new ArrayList<>();
        this.location = location;
        productNumber = 111111111;
        this.name = name;
        this.code = code;
        this.quantity = qty;
        this.dollarAmount = priceIn * qty;
        this.standardPrice = priceOut;
        this.lowStockThreshold = stockThreshold;
        isExpiring = false;
        for (int i = 0; i < qty; i++) {
            products.add(new Product(code, productNumber, priceIn, priceOut, location));
        }
    }

    public Item(String name, String code, String location) {
        products = new ArrayList<>();
        this.location = location;
        productNumber = 111111111;
        this.name = name;
        this.code = code;
        this.quantity = 0;
        dollarAmount = 0;
        standardPrice = 0;
        this.lowStockThreshold = 0;
        isExpiring = false;
    }

    public Item(String name, String code, int qty, double priceIn, double priceOut, int stockThreshold, String location, LocalDate exp) {
        products = new ArrayList<>();
        this.location = location;
        productNumber = 111111111;
        this.name = name;
        this.code = code;
        this.quantity = qty;
        this.dollarAmount = standardPrice * qty;
        this.standardPrice = priceOut;
        this.lowStockThreshold = stockThreshold;
        isExpiring = true;
        for (int i = 0; i < qty; i++) {
            products.add(new Product(code, productNumber++,priceIn, priceOut, location, exp));
        }
    }

    public Item(String name, String code, int qty, double priceIn, double priceOut, String location, LocalDate exp) {
        products = new ArrayList<>();
        this.location = location;
        productNumber = 111111111;
        this.name = name;
        this.code = code;
        this.quantity = qty;
        this.dollarAmount = priceIn * qty;
        this.standardPrice = priceOut;
        this.lowStockThreshold = 0;
        isExpiring = true;
        for (int i = 0; i < qty; i++) {
            products.add(new Product(code, productNumber++, priceIn, priceOut, location, exp));
        }
    }

    public Item(String name, String code, int qty, double priceIn, double priceOut, String location) {
        products = new ArrayList<>();
        this.location = location;
        productNumber = 111111111;
        this.name = name;
        this.code = code;
        this.quantity = qty;
        this.dollarAmount = priceIn * qty;
        this.standardPrice = priceOut;
        this.lowStockThreshold = 0;
        isExpiring = true;
        for (int i = 0; i < qty; i++) {
            products.add(new Product(code, productNumber++, priceIn, priceOut, location));
        }
    }


    public Item(String name, String code) {
        this.name = name;
        this.code = code;
        products = new ArrayList<>();
        this.location = "TBD";
        productNumber = 111111111;
        this.name = name;
        this.code = code;
        this.quantity = 0;
        this.lowStockThreshold = 0;
        this.dollarAmount = 0;
        this.standardPrice = 0;
        isExpiring = false;
    }

    //REQUIRES: the item must be one of those that have expiration dates.
    public int getExpiringQuantity(int withIn) {
        if (!isExpiring) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < products.size(); i++) {
            if (ChronoUnit.DAYS.between(LocalDate.now(), products.get(i).getExpirationDate()) <= withIn) {
                count++;
            }
        }

        return count;

    }

    public int getQuantity() {
        return quantity;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void addProducts(int quantity, double priceIn, double priceOut, LocalDate exp) {
        for (int i = 0; i < quantity; i++) {
            products.add(new Product(code, productNumber++, priceIn, priceOut, location, exp));
        }
        increaseQuantity((quantity));
        dollarAmount += standardPrice;
    }

    public void addProducts(int quantity, double priceIn, double priceOut, String pos, LocalDate exp) {
        if (location.length() == 0) {
            location = pos;
        }
        for (int i = 0; i < quantity; i++) {
            products.add(new Product(code, productNumber++, priceIn, priceOut, pos, exp));
        }
        increaseQuantity((quantity));
        dollarAmount += standardPrice;
    }


    public void addProducts(int quantity, double priceIn, double priceOut) {
        for (int i = 0; i < quantity; i++) {
            products.add(new Product(code, productNumber++, priceIn, priceOut,  location));
        }
        increaseQuantity((quantity));
        increaseQuantity((quantity));
    }

    public void addProducts(int quantity, double priceIn) {
        for (int i = 0; i < quantity; i++) {
            products.add(new Product(code, productNumber++, priceIn, getStandardPrice(),  location));
        }
        increaseQuantity((quantity));
        increaseQuantity((quantity));
    }

    public void addProducts(ArrayList<Product> products) {
        double dollarAmount = 0;
        for (Product p: products) {
            this.products.add(p);
            dollarAmount += p.getBuyingPrice();
        }
        this.dollarAmount = dollarAmount;
        sort(this.products);
    }

    public void addProduct() {
        products.add(new Product(code, productNumber++, getAverageBuyingPrice(), getStandardPrice(), "ABC"));
    }

    public void addProduct(double priceIn, LocalDate exp) {
        products.add(new Product(code, productNumber++, priceIn, getStandardPrice(),"ABC", exp));
    }

    public double getStandardPrice() {
        return standardPrice;
    }

    public double getAverageBuyingPrice() {
        return dollarAmount / quantity;
    }



    //REQUIRES: num must be positive
    //MODIFIES: this
    //EFFECTS: the quantity of this item will be updated by the amount specified by num
    public void increaseQuantity(int num) {
        quantity += num;
    }


    public void print() {
        System.out.println("ToBeImplemented");
    }


    private static void sort(ArrayList<Product> list){

    }

    public boolean lowStock() {
        if (quantity <= lowStockThreshold) {
            return true;
        }
        return false;
    }

    public String getItemCode() {
        return code;
    }
}
