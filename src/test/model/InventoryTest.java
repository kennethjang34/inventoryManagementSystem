package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;
    private ArrayList<LocationTag> productsToAddWLocation;
    private static int sku = 111111111;
    private static LocalDate today = LocalDate.now();
    private static final LocalDate bestBeforeDate = LocalDate.of(2021, 10, 20);

    @BeforeEach
    void runBeforeEveryTest() {
        inventory = new Inventory();
        productsToAddWLocation = new ArrayList<>();
        LocationTag tag = new LocationTag(new Product("App", sku++, 3.2, today, bestBeforeDate), "F11");
        productsToAddWLocation.add(tag);
        tag = new LocationTag(new Product("BNN", sku++, 2.8, today, bestBeforeDate), "F12");
        productsToAddWLocation.add(tag);
        tag = new LocationTag(new Product("CHI", sku++, 3.2, today, bestBeforeDate), "F13");
        productsToAddWLocation.add(tag);
        tag = new LocationTag(new Product("MGO", sku++, 3.2, today, bestBeforeDate), "F14");
        productsToAddWLocation.add(tag);
    }




    ArrayList<LocationTag> createProducts(String code, double cost, int qty, String location) {
        ArrayList<LocationTag> products = new ArrayList<>();
        for (int i = 0; i < qty; i++) {
            LocationTag tag = new LocationTag(new Product(code, sku++, cost, today, bestBeforeDate), location);
            products.add(tag);
        }
        return products;
    }




    @Test
    void testConstructor() {
        inventory = new Inventory();
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getListOfCodes().size());
        assertNull(inventory.getProductList("PIZ"));
        inventory = new Inventory(3);
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getListOfCodes().size());
        assertNull(inventory.getProductList("PIZ"));
        inventory = new Inventory(3, 100);
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getListOfCodes().size());
        assertNull(inventory.getProductList("PIZ"));
    }






    @Test
    void testAddProduct() {
        inventory = new Inventory();
        ArrayList<LocationTag> entries = new ArrayList<>();
        LocationTag tag = new LocationTag();
        tag.setProduct(new Product("App", sku++, 3.2, today, bestBeforeDate));
        tag.setLocation("F11");
        entries.add(tag);
        inventory.addProducts(entries);
        assertEquals(1, inventory.getTotalQuantity());
        assertEquals(1, inventory.getQuantity("app"));
        assertEquals(inventory.getLocationCodeNumber("F11"), inventory.findLocation(tag.getProduct()));
    }

    @Test
    void testGetStringItemCodeNegativeInt() {
        assertNull(inventory.getStringItemCode(-5));
    }

    @Test
    void testGetNumericLocationCode() {
        String location = "F11";
        assertEquals(511, inventory.getLocationCodeNumber(location));
    }

    @Test
    void testGetStringLocationCode() {
        String location = inventory.getStringLocationCode(511);
        assertEquals("F11", location);
    }

    @Test
    void testGetStringLocationCodeWithNegativeInt() {
        assertNull(inventory.getStringLocationCode(-3));
    }

    @Test
    void testGetProductNotInInventory() {
        inventory = new Inventory();
        inventory.addProducts(createProducts("ASR", 50, 100, "T"));
        assertNull(inventory.getProduct("BBB", 11113));
    }

    @Test
    void testGetProductInMiddleList() {
        inventory = new Inventory();
        this.sku = 1;
        inventory.addProducts(createProducts("ASR", 50, 100, "T"));
        assertEquals(101, sku);
        Product product = inventory.getProduct("ASR", 50);
        assertEquals("ASR", product.getItemCode());
        assertEquals(50, product.getSku());
    }

    @Test
    void testIsValidItemCode() {
        inventory = new Inventory();
        assertTrue(inventory.isValidItemCode("aaa"));
        assertFalse(inventory.isValidItemCode("cccc"));
        assertFalse(inventory.isValidItemCode("aaacc"));
        assertFalse(inventory.isValidItemCode("ab"));
        assertFalse(inventory.isValidItemCode(""));
        assertFalse(inventory.isValidItemCode("ab5"));
        assertFalse(inventory.isValidItemCode("[]a"));
        assertFalse(inventory.isValidItemCode("~~~"));
        assertFalse(inventory.isValidItemCode("a[b)"));

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
        LinkedList<Integer> locations = inventory.findLocations("app");
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
        assertEquals(inventory.getLocationCodeNumber("A11"), inventory.findLocation("APP", sku - 1));
    }


    @Test
    void testFindLocationOfProductWithProduct() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        ArrayList<LocationTag> list = new ArrayList<>();
        LocationTag tag = new LocationTag(product, "D32");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("D32", inventory.getStringLocationCode(inventory.findLocation(product)));
        tag = new LocationTag(new Product("DDD", sku++, 50, today, bestBeforeDate), "Z99");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("Z99", inventory.getStringLocationCode(inventory.findLocation(tag.getProduct())));
        //A00 is the same as T
        tag = new LocationTag(new Product("adi", sku++, 2, today, bestBeforeDate), "A00");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("T", inventory.getStringLocationCode(inventory.findLocation(tag.getProduct())));
    }

    @Test
    void testFindLocationOfProductWithProductCode() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        String itemCode = product.getItemCode();
        ArrayList<LocationTag> list = new ArrayList<>();
        LocationTag tag = new LocationTag(product, "D32");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("D32", inventory.getStringLocationCode(inventory.findLocation(itemCode, sku - 1)));
        product = new Product("DDD", sku++, 50, today, bestBeforeDate);
        tag = new LocationTag(product, "Z99");
        list.add(tag);
        inventory.addProducts(list);
        itemCode = product.getItemCode();
        assertEquals("Z99", inventory.getStringLocationCode(inventory.findLocation(itemCode, sku - 1)));
        product = new Product("adi", sku++, 2, today, bestBeforeDate);
        tag = new LocationTag(product, "A00");
        list.add(tag);
        inventory.addProducts(list);
        itemCode = product.getItemCode();
        assertEquals("T", inventory.getStringLocationCode(inventory.findLocation(itemCode, sku - 1)));
    }

    @Test
    void testFindMethodsWithNonExistingProduct() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        ArrayList<LocationTag> list = new ArrayList<>();
        LocationTag tag = new LocationTag(product, "D32");
        list.add(tag);
        inventory.addProducts(list);
        String itemCode = product.getItemCode();
        int sku = product.getSku();
        assertEquals("D32", inventory.getStringLocationCode(inventory.findLocation(itemCode, sku)));
        Product notInInventory = new Product("ABC", this.sku++, 12, today, bestBeforeDate);
        assertEquals(-1, inventory.findLocation(notInInventory));
        assertEquals(-1, inventory.findLocation("ABC", notInInventory.getSku()));
    }

    @Test
    void testRemoveProduct() {
        inventory.addProducts(productsToAddWLocation);
        for (LocationTag tag: productsToAddWLocation) {
            Product product = tag.getProduct();
            String itemCode = product.getItemCode();
            int sku = product.getSku();
            assertEquals(1, inventory.getQuantity(itemCode));
            assertEquals(product, inventory.getProduct(itemCode, sku));
            assertTrue(inventory.removeProduct(product));
            assertEquals(0, inventory.getQuantity(product.getItemCode()));
            assertEquals(-1, inventory.findLocation(product));
            assertEquals(-1, inventory.findLocation(itemCode, product.getSku()));
            assertNull(inventory.getProduct(itemCode, sku));
        }
    }

    @Test
    void testRemoveProductsWithItemCode() {
        ArrayList<LocationTag> list = createProducts("NIK", 100.1, 544, "T");
        assertEquals(0, inventory.getQuantity("NIK"));
        inventory.addProducts(list);
        assertEquals(544, inventory.getQuantity("NIK"));
        assertTrue(inventory.removeProducts("NIK", 3));
        assertEquals(544 - 3, inventory.getQuantity("NIK"));
        assertTrue(inventory.removeProducts("NIK", 1000));
        assertEquals(0, inventory.getQuantity("NIK"));
    }

    @Test
    void testRemoveProductsAtDifferentLocations() {
        ArrayList<LocationTag> list = createProducts("NIK", 100.1, 100, "T");
        inventory.addProducts(list);
        inventory.addProducts(createProducts("NIK", 100.1, 100, "A11"));
        inventory.addProducts(createProducts("NIK", 100.1, 200, "B11"));
        inventory.addProducts(createProducts("NIK", 100.1, 300, "C44"));
        assertEquals(700, inventory.getQuantity("NIK"));
        inventory.removeProducts("NIK", 650);
        assertEquals(50, inventory.getQuantity("NIK"));
    }

    @Test
    void testFindLocationOfProductsRemoved() {
        ArrayList<LocationTag> list1 = createProducts("NIK", 100.1, 100, "T");
        inventory.addProducts(list1);
        ArrayList<LocationTag> list2 = createProducts("NIK", 100.1, 100, "A11");
        inventory.addProducts(list2);
        assertEquals(200, inventory.getQuantity("NIK"));
        inventory.removeProducts("NIK", 110);
        assertEquals(90, inventory.getQuantity("NIK"));
        Product locatedAtT = list1.get(0).getProduct();
        assertEquals(-1, inventory.findLocation(locatedAtT));
    }


    @Test
    void testRemoveProductsWithProductList() {
        ArrayList<LocationTag> list = createProducts("NIK", 100.1, 544, "T");
        assertEquals(0, inventory.getQuantity("NIK"));
        inventory.addProducts(list);
        assertEquals(544, inventory.getQuantity("NIK"));
        inventory.removeProducts(list);
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
        LinkedList<Product> appList = inventory.getProductList("APP");
        LinkedList<Product> asdList = inventory.getProductList("ASD");
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
        LinkedList<String> list = inventory.getListOfCodes();
        assertEquals(6, list.size());
        assertEquals("APP", list.get(0));
        assertEquals("ASD", list.get(1));
        assertEquals("BDS", list.get(2));
        assertEquals("BNN", list.get(3));
        assertEquals("CHI", list.get(4));
        assertEquals("MGO", list.get(5));
    }





}
