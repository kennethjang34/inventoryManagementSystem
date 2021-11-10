package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;
    private LinkedList<InventoryTag> tags;
    private static LocalDate today = LocalDate.now();
    private static final LocalDate bestBeforeDate = LocalDate.of(2021, 10, 20);
    private static int basicQty = 3;
    private static double cost = 3;
    private static double price = 5;

    @BeforeEach
    void runBeforeEveryTest() {
        inventory = new Inventory();
        basicQty = 3;
        tags = new LinkedList<>();
        tags.add(new InventoryTag("aaa", cost, price, bestBeforeDate, "F11", basicQty));
        tags.add(new InventoryTag("BNN", cost, price, bestBeforeDate, "F12", basicQty));
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "F13", basicQty));
        tags.add(new InventoryTag("MGO", cost, price, bestBeforeDate, "F14", basicQty));
    }





//    LinkedList<ProductTag> createInventoryTag(String code, double cost, int qty, String location) {
//        LinkedList<ProductTag> products = new LinkedList<>();
//        for (int i = 0; i < qty; i++) {
//            ProductTag tag = new ProductTag(new Product(code, sku++, cost, today, bestBeforeDate), location);
//            products.add(tag);
//        }
//        return products;
//    }




    @Test
    void testConstructor() {
        inventory = new Inventory();
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getListOfCodes().size());
        assertEquals(0, inventory.getProductList("PIZ").size());

    }







    @Test
    void testCurrentDate() {
        inventory.setCurrentDate(LocalDate.now());
        assertEquals(LocalDate.now().toString(), inventory.getCurrentDate().toString());
        inventory.setCurrentDate(LocalDate.of(2022, 11, 11));
        assertEquals(LocalDate.of(2022, 11, 11),
                inventory.getCurrentDate());
    }

    @Test
    void testAddProduct() {
        inventory = new Inventory();
        inventory.addProducts(tags);
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0));
        List<Product> products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());

    }






    @Test
    void testGetProductNotInInventory() {
        inventory = new Inventory();
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", cost, price,bestBeforeDate, "T", basicQty));
        inventory.addProducts(tags);
        assertNull(inventory.getProduct("afdjlkfadsjsdf"));
    }

    @Test
    void testGetProductRegularCase() {
        basicQty = 50;
        inventory = new Inventory();
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", cost, price,bestBeforeDate,"T", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111160.
        assertEquals(basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("asr"));
        List<Product> products = inventory.getProductList("ASR");
        for (int i = 0; i < inventory.getQuantity("asr"); i++) {
            Product product = inventory.getOldestProduct("asr");
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getId());
            assertEquals(111111 + i, product.getSku());
        }
    }

    @Test
    void testGetProductAtDiffLocations() {
        basicQty = 50;
        inventory = new Inventory();
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", cost, price, bestBeforeDate, bestBeforeDate, "f10",basicQty));
        tags.add(new InventoryTag("ASR", cost, price, bestBeforeDate, bestBeforeDate, "f11",basicQty));
        tags.add(new InventoryTag("bnn", cost, price, LocalDate.now(), "f11", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111260.
        assertEquals(basicQty * 3, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("asr"));
        assertEquals(basicQty, inventory.getQuantity("bnn"));
        List<Product> products = inventory.getProductList("ASR");
        for (int i = 0; i < inventory.getQuantity("asr"); i++) {
            Product product = inventory.getOldestProduct("asr");
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getId());
            assertEquals( product, inventory.getProduct(product.getSku()));
        }
    }


    @Test
    void testGetListOfCodes() {
        assertEquals(0, inventory.getListOfCodes().size());
        inventory.addProducts(tags);
        List<String> list = inventory.getListOfCodes();
        assertEquals(4, list.size());
        assertEquals("AAA", list.get(0));
        assertEquals("BNN", list.get(1));
        assertEquals("CHI", list.get(2));
        assertEquals("MGO", list.get(3));
    }








    //Tags contain following items:
    //tags.add(new InventoryTag("aaa", price, bestBeforeDate, "F11", basicQty));
    //tags.add(new InventoryTag("BNN", price, bestBeforeDate, "F12", basicQty));
    //tags.add(new InventoryTag("CHI", price, bestBeforeDate, "F13", basicQty));
    //tags.add(new InventoryTag("MGO", price, bestBeforeDate, "F14", basicQty));
    @Test
    void testRemoveProducts() {
        inventory.addProducts(tags);
        LinkedList<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AaA", "f11", basicQty/2));
        List<QuantityTag> removed = inventory.removeStocks(toBeRemoved);
        assertEquals(1, removed.size());
        QuantityTag removedTag = removed.get(0);
        String removedItemCode = removedTag.getId();
        String removedLocation = removedTag.getLocation();
        int removedQty = removedTag.getQuantity();
        assertEquals("AAA", removedItemCode);
        assertEquals("F11", removedLocation);
        assertEquals(-1, removedQty);
    }

    @Test
    void testRemoveProductsMoreThanExisting() {
        inventory.addProducts(tags);
        LinkedList<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AaA", "f11", basicQty * 3));
        List<QuantityTag> removed = inventory.removeStocks(toBeRemoved);
        assertEquals(0, removed.size());
        assertEquals(basicQty, inventory.getQuantity("aAa"));
        List<QuantityTag> list = inventory.findLocations("aaa");
        assertEquals(1, list.size());
        assertEquals(basicQty, list.get(0).getQuantity());
        assertEquals("F11", list.get(0).getLocation());
    }

    @Test
    void testRemoveSingleProduct() {
        basicQty = 2;
        inventory = new Inventory();
        tags = new LinkedList<>();
        tags.add(new InventoryTag("aaa", 50, 60, bestBeforeDate, "T", basicQty));
        tags.add(new InventoryTag("asr", 50, 60, bestBeforeDate, "d11", basicQty));
        tags.add(new InventoryTag("asd", 50, 60, bestBeforeDate, "a33", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111260.
        assertEquals(basicQty * 3, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals(basicQty, inventory.getQuantity("asr"));
        assertEquals(basicQty, inventory.getQuantity("asd"));
        int initialQty = inventory.getQuantity("aaa");
        for (int i = 0; i < initialQty; i++) {
            int qty = inventory.getQuantity("aaa");
            Product product = inventory.getOldestProduct("aaa");
            assertEquals("AAA", product.getId());
            assertEquals(product, inventory.getProduct(product.getSku()));
            inventory.removeProduct(product.getSku());
            assertNull(inventory.getProduct(product.getSku()));
            assertEquals(qty - 1, inventory.getQuantity("aaa"));
        }

        for (int i = 0; i < initialQty; i++) {
            int qty = inventory.getQuantity("asr");
            Product product = inventory.getOldestProduct("asr");
            assertEquals("ASR", product.getId());
            assertEquals(product, inventory.getProduct(product.getSku()));
            inventory.removeProduct(product.getSku());
            assertNull(inventory.getProduct(product.getSku()));
            assertEquals(qty - 1, inventory.getQuantity("asr"));
        }


        assertEquals(0, inventory.getQuantity("aaa"));
    }

    @Test
    void testRemoveNonExistingStocks() {
        inventory.addProducts(tags);
        LinkedList<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("ZZZ", "A11", basicQty * 3));
        List<QuantityTag> removed = inventory.removeStocks(toBeRemoved);
        assertEquals(0, removed.size());
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0));
        List<Product> products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());
        toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AAa", "f13", basicQty));
        removed = inventory.removeStocks(toBeRemoved);
        assertEquals(0, removed.size());
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
        products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());

        assertEquals(0, inventory.getProductList("ZZZ").size());
        assertEquals(0, inventory.getProductList("AAa").size());

    }









    @Test
    void testGetProduct() {
        basicQty = 50;
        inventory = new Inventory();
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", 50, 60,bestBeforeDate,  "T", basicQty));
        //It is expected that sku for this "ASR" products range from 111111 to 111160.
        tags.add(new InventoryTag("ASR", 50, 60,  bestBeforeDate,"f11", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111200.
        assertEquals(basicQty * 2, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("asr"));
        List<Product> products = inventory.getProductList("asr");
        for (Product product: products) {
            String sku = product.getSku();
            assertEquals(product, inventory.getProduct(sku));
        }
    }

    //tags have inventory tags as following
    //tags.add(new InventoryTag("aaa", price, bestBeforeDate, "F11", basicQty));
    //tags.add(new InventoryTag("BNN", price, bestBeforeDate, "F12", basicQty));
    //tags.add(new InventoryTag("CHI", price, bestBeforeDate, "F13", basicQty));
    //tags.add(new InventoryTag("MGO", price, bestBeforeDate, "F14", basicQty));
    @Test
    void testFindLocations() {
        tags.add(new InventoryTag("chi", cost, price, bestBeforeDate, "f50", basicQty));
        tags.add(new InventoryTag("chi", cost, price, bestBeforeDate, "t", basicQty));
        inventory.addProducts(tags);
        List<QuantityTag> locations = inventory.findLocations("chi");
        assertEquals(3, locations.size());
        assertEquals("T", locations.get(0).getLocation());
        assertEquals("F13", locations.get(1).getLocation());
        assertEquals("F50", locations.get(2).getLocation());
        locations = inventory.findLocations("mgo");
        assertEquals(1, locations.size());
        assertEquals("F14", locations.get(0).getLocation());
        assertEquals(0, inventory.findLocations("zzz").size());
    }

    @Test
    void testFindLocationsOfRemoved() {
        tags.add(new InventoryTag("chi", cost, price, bestBeforeDate, "f50", basicQty));
        tags.add(new InventoryTag("chi", cost, price, bestBeforeDate, "t", basicQty));
        inventory.addProducts(tags);
        List<QuantityTag> locations = inventory.findLocations("chi");
        assertEquals(3, locations.size());
        assertEquals("T", locations.get(0).getLocation());
        assertEquals("F13", locations.get(1).getLocation());
        assertEquals("F50", locations.get(2).getLocation());
        locations = inventory.findLocations("mgo");
        assertEquals(1, locations.size());
        assertEquals("F14", locations.get(0).getLocation());
        ArrayList<QuantityTag> toBeRemoved = new ArrayList<>();
        toBeRemoved.add(new QuantityTag("chi", "t", basicQty));
        inventory.removeStocks(toBeRemoved);
        locations = inventory.findLocations("chi");
        assertEquals("F13", locations.get(0).getLocation());
        assertEquals("F50", locations.get(1).getLocation());
    }

    @Test
    void testGetQtysAtDiffLocations() {
        tags.add(new InventoryTag("chi", cost, price, bestBeforeDate, "f50", basicQty));
        tags.add(new InventoryTag("chi", cost, price, bestBeforeDate, "t", basicQty * 2));
        inventory.addProducts(tags);
        List<QuantityTag> list = inventory.getQuantitiesAtLocations("chi");
        assertEquals(basicQty * 2, list.get(0).getQuantity());
        assertEquals(basicQty, list.get(1).getQuantity());
        assertEquals(basicQty, list.get(2).getQuantity());
        assertNull(inventory.getQuantitiesAtLocations("zzz"));
    }

    @Test
    void testJsonConversion() {
        inventory = new Inventory();
        inventory.addProducts(tags);
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0));
        List<Product> products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());
        inventory = new Inventory(inventory.toJson());
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0));
        assertEquals(basicQty, products.size());
    }
}
