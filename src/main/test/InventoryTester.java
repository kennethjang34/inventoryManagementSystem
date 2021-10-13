package test;

import model.Inventory;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

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
        for (int i = 0; i < qty; i++) {
            Object[] entry = new Object[2];
            entry[0] = new Product(code, sku++, cost, today, bestBeforeDate);
            entry[1] = location;
            products.add(entry);
        }
        return products;
    }

    ArrayList<Object[]> createProducts(String code, double cost, int qty, String location, LocalDate date) {
        ArrayList<Object[]> products = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            Object[] entry = new Object[2];
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
    void testGetNumericItemCode() {
        String itemCode1 = "ZZZ";
        String itemCode2 = "aaa";
        String itemCode3 = "Daa";
        assertEquals(26 * 26 * 25 + 26 * 25 + 25, inventory.getItemCodeNumber(itemCode1));
        assertEquals(0, inventory.getItemCodeNumber(itemCode2));
        assertEquals(26 * 26 * 3, inventory.getItemCodeNumber(itemCode3));
        assertEquals(inventory.getItemCodeNumber("AAA"), inventory.getItemCodeNumber(itemCode2));
    }

    @Test
    void testGetStringItemCode() {
        assertEquals("AAA", inventory.getStringItemCode(0));
        assertEquals("DAA", inventory.getStringItemCode(26 * 26 * 3));
        assertEquals("ZZZ", inventory.getStringItemCode(26 * 26 * 25 + 26 * 25 + 25));
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
        ArrayList<Object[]> list = new ArrayList<>();
        Object[] entry = new Object[2];
        entry[0] = product;
        entry[1] = "D32";
        list.add(entry);
        inventory.addProducts(list);
        assertEquals("D32", inventory.getStringLocationCode(inventory.findLocation(product)));
        entry = new Object[2];
        entry[0] = new Product("DDD", sku++, 50, today, bestBeforeDate);
        entry[1] = "Z99";
        list.add(entry);
        inventory.addProducts(list);
        assertEquals("Z99", inventory.getStringLocationCode(inventory.findLocation((Product)entry[0])));
        entry = new Object[2];
        entry[0] = new Product("adi", sku++, 2, today, bestBeforeDate);
        //A00 is the same as T
        entry[1] = "A00";
        list.add(entry);
        inventory.addProducts(list);
        assertEquals("T", inventory.getStringLocationCode(inventory.findLocation((Product)entry[0])));
    }

    @Test
    void testRemoveProduct() {
        inventory.addProducts(productsToAddWLocation);
        for (Object[] entry: productsToAddWLocation) {
            Product product = (Product) (entry[0]);
            String itemCode = product.getItemCode();
            int sku = product.getSku();
            assertEquals(1, inventory.getQuantity(itemCode));
            assertEquals(product, inventory.getProduct(itemCode, sku));
            assertTrue(inventory.removeProduct(product));
            assertEquals(0, inventory.getQuantity(product.getItemCode()));
            assertEquals(null, inventory.getProduct(itemCode, sku));
        }
    }




    @Test
    void testRemoveProducts() {
        ArrayList<Object[]> list = createProducts("NIK", 100.1, 544, "T");
        assertEquals(0, inventory.getQuantity("NIK"));
        inventory.addProducts(list);
        assertEquals(544, inventory.getQuantity("NIK"));
        assertTrue(inventory.removeProducts("NIK", 3));
        assertEquals(544 - 3, inventory.getQuantity("NIK"));
        assertTrue(inventory.removeProducts("NIK", 1000));
        assertEquals(0, inventory.getQuantity("NIK"));
    }

    @Test
    void testRemoveNonExistingProduct() {
        inventory = new Inventory();
        productsToAddWLocation = new ArrayList<>();
        Product product = new Product("BBB", sku++, 0.04, today, null);
        assertEquals(0, inventory.getQuantity(product.getItemCode()));
        assertFalse(inventory.removeProduct(product));
        assertFalse(inventory.removeProducts(product.getItemCode(), 100));
    }

    @Test
    void testGetProductList() {
        inventory.addProducts(createProducts("APP", 2.1, 10, "T"));
        inventory.addProducts(createProducts("ASD", 5555, 100000, "Z0"));
        ArrayList<Product> appList = inventory.getProductList("APP");
        ArrayList<Product> asdList = inventory.getProductList("ASD");
        assertEquals(10, appList.size());
        assertEquals(appList.size(), inventory.getQuantity("APP"));
        assertEquals(100000,asdList.size());
        assertEquals(asdList.size(), inventory.getQuantity("ASD"));
        assertEquals("APP",appList.get(0).getItemCode());
        assertEquals("ASD",asdList.get(0).getItemCode());
        assertEquals(99999, asdList.get(asdList.size() - 1).getSku() - asdList.get(0).getSku());
        assertEquals(9, appList.get(appList.size() - 1).getSku() - appList.get(0).getSku());
    }



    //method getListCodes() returns a list of item codes in alphabetical order.
    @Test
    void testGetListOfCodes() {
        //productsToAddWLocation contains : APP, BNN, CHI, MGO
        inventory.addProducts(productsToAddWLocation);
        inventory.addProducts(createProducts("BDS", 2.1, 10, "T"));
        inventory.addProducts(createProducts("ASD", 5555, 100000, "Z0"));
        ArrayList<String> list = inventory.getListOfCodes();
        assertEquals(6, list.size());
        assertEquals("APP", list.get(0));
        assertEquals("ASD", list.get(1));
        assertEquals("BDS", list.get(2));
        assertEquals("BNN", list.get(3));
        assertEquals("CHI", list.get(4));
        assertEquals("MGO", list.get(5));
    }





}
