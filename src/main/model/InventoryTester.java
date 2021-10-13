package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InventoryTester {
    private Inventory inventory;
    private ArrayList<Object[]> productsToAddWLocation;
    private static int sku = 111111111;
    private static LocalDate today = LocalDate.now();
    private static final LocalDate bestBeforeDate = LocalDate.of(2021, 10, 20);

    @BeforeEach
    void runBeforeEveryTest() {
        inventory = new Inventory();
        productsToAddWLocation = new ArrayList<>();
        Object[] productInfo = new Object[2];
        productInfo[0] = new Product("App", sku++, 3.2, today, bestBeforeDate);
        productInfo[1] = "F11";
        productsToAddWLocation.add(productInfo);
        productInfo = new Object[2];
        productInfo[0] = new Product("BNN", sku++, 2.8, today, bestBeforeDate);
        productInfo[1] = "F12";
        productsToAddWLocation.add(productInfo);
        productInfo = new Object[2];
        productInfo[0] = new Product("CHI", sku++, 3.2, today, bestBeforeDate);
        productInfo[1] = "F13";
        productsToAddWLocation.add(productInfo);
        productInfo = new Object[2];
        productInfo[0] = new Product("MGO", sku++, 3.2, today, bestBeforeDate);
        productInfo[1] = "F14";
        productsToAddWLocation.add(productInfo);
    }

    ArrayList<Object[]> createProducts(String code, double cost, int qty, String location) {
        ArrayList<Object[]> products = new ArrayList<>();
        Object[] entry = new Object[2];
        for (int i = 0; i < qty; i++) {
            entry[0] = new Product(code, sku++, cost, today, bestBeforeDate);
            entry[1] = location;
            products.add(entry);
        }
        return products;
    }

    ArrayList<Object[]> createProducts(String code, double cost, int qty, String location, LocalDate date) {
        ArrayList<Object[]> products = new ArrayList<>();
        Object[] entry = new Object[2];
        for (int i = 0; i < qty; i++) {
            entry[0] = new Product(code, sku++, cost, today, date);
            entry[1] = location;
            products.add(entry);
        }
        return products;
    }


    @Test
    void testConstructor() {
        inventory = new Inventory();
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getListOfCodes().size());
        assertEquals(null, inventory.getProductList("PIZ"));
    }

    @Test
    void testAddProduct() {
        inventory = new Inventory();
        ArrayList<Object[]> entries = new ArrayList<>();
        Object[] productInfo = new Object[2];
        productInfo[0] = new Product("App", sku++, 3.2, today, bestBeforeDate);
        productInfo[1] = "F11";
        entries.add(productInfo);
        inventory.addProducts(entries);
        assertEquals(1, inventory.getTotalQuantity());
        assertEquals(1, inventory.getQuantity("app"));
        assertEquals(inventory.getLocationCodeNumber("F11"), inventory.findLocation((Product)productInfo[0]));
    }

    @Test
    void testGetNumericLocationCode() {
        String location = "F11";
        assertEquals(511, inventory.getLocationCodeNumber("F11"));
    }

    @Test
    void testGetStringLocationCode() {
        String location = inventory.getStringLocationCode(511);
        assertEquals("F11", location);
    }

    @Test
    void testFindLocations() {
        //inventory = new Inventory();
        inventory.addProducts(productsToAddWLocation);
        ArrayList<Integer> locations = inventory.findLocations("app");
        assertEquals(1, locations.size());
        //String code = inventory.getStringItemCode(locations.get(0));
        assertEquals("F11", inventory.getStringLocationCode(locations.get(0)));

        inventory.addProducts(createProducts("APP", 2.1, 10, "T"));
        inventory.addProducts(createProducts("APP", 2.1, 10, "A11"));
        //inventory.addProducts(createProducts("App", 2.3, 10, "F11"));
        locations = inventory.findLocations("app");
        assertEquals(locations.size(), 3);
        assertEquals(inventory.getLocationCodeNumber("F11"), locations.get(0));
        assertEquals(inventory.getLocationCodeNumber("T"), locations.get(1));
        assertEquals(inventory.getLocationCodeNumber("A11"), locations.get(2));
    }

    @Test
    void testFindLocationOfProduct() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        ArrayList<object[2]>

    }


}
