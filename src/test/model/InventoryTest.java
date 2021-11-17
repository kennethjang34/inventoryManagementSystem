package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;
    private LinkedList<InventoryTag> tags;
    private static LocalDate today = LocalDate.now();
    private static final LocalDate bestBeforeDate = LocalDate.of(2021, 10, 20);
    private static int basicQty = 4;
    private static double cost = 3;
    private static double price = 5;
    private static Category category = new Category("FOOD");
    private static String description = "Description";
    private static String note = "note";

    @BeforeEach
    void runBeforeEveryTest() {
        inventory = new Inventory();
        basicQty = 4;
        tags = new LinkedList<>();
        tags.add(new InventoryTag("AAA", cost, price, bestBeforeDate, "F11", basicQty));
        tags.add(new InventoryTag("BNN", cost, price, bestBeforeDate, "F12", basicQty));
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "F13", basicQty));
        tags.add(new InventoryTag("MGO", cost, price, bestBeforeDate, "F14", basicQty));
        inventory.createCategory("FOOD");
        inventory.createItem("AAA", "aaa", category.getName(),
                price, description, note);
        inventory.createItem("BNN", "BnnD", category.getName(),
                price, description, note);
        inventory.createItem("CHI", "chi", category.getName(),
                price, description, note);
        inventory.createItem("MGO", "MGO", category.getName(),
                price, description, note);
        inventory.createItem("ASR", "ASR", category.getName(),
                price, description, note);
        inventory.createItem("ASD", "ASD", category.getName(),
                price, description, note);
    }




    @Test
    void testConstructor() {
        inventory = new Inventory();
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getIDs().size());
        assertEquals(0, inventory.getProductList("PIZ").size());

    }

    @Test
    void testAddCategory() {
        assertTrue(inventory.createCategory("Fruit"));
        assertTrue(inventory.containsCategory("Fruit"));
        assertFalse(inventory.createCategory("Fruit"));
    }

    @Test
    void testAddItems() {
        inventory = new Inventory();
        inventory.createCategory("FOOD");
        assertTrue(inventory.createItem("AAA", "aaa", category.getName(),
                price, description, note));
        assertTrue(inventory.createItem("BNN", "bnn", category.getName(),
                price, description, note));
        assertTrue(inventory.createItem("CHI", "chi", category.getName(),
                price, description, note));
        assertTrue(inventory.createItem("MGO", "mgo", category.getName(),
                price, description, note));

        assertFalse(inventory.createItem("MGO", "Duplicate",
                category.getName(), price, description, note));
        assertFalse(inventory.createItem("QQQWRW", "NO such category",
                ".?AEw", price, description, note));
    }


    @Test
    void testAddProducts() {
        inventory.addProducts(tags.get(0));
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("AAA").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
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
        inventory.addProducts(tags);
        //assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("AAA").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
        List<Product> products = inventory.getProductList("BNN");
        assertEquals(basicQty, products.size());
    }


    @Test
    void testGetItemListWithCategory() {
        inventory.addProducts(tags);
        List<Item> items = inventory.getItemList(category.getName());
        for (InventoryTag tag: tags) {
            boolean found = false;
            for (Item item: items) {
                if (item.getId().equals(tag.getId())) {
                    found = true;
                    break;
                }
            }
            if (found == false) {
                fail();
            }

        }
        assertEquals(0, inventory.getItemList
                ("No Category").size());
    }

    @Test
    void testGetCategoryNames() {
        assertEquals(1, inventory.getCategoryNames().length);
        inventory.createCategory("Chicken");
        assertEquals(2, inventory.getCategoryNames().length);
        assertEquals("Chicken", inventory.getCategoryNames()[1]);
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
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", cost, price,bestBeforeDate,"T", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111160.
        assertEquals(basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("ASR"));
        List<Product> products = inventory.getProductList("ASR");
        for (int i = 0; i < inventory.getQuantity("ASR"); i++) {
            Product product = inventory.getOldestProduct("ASR");
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getId());
            assertTrue(inventory.removeProduct(product.getSku()));
            //assertEquals(111111 + i, product.getSku());
        }
    }

    @Test
    void testGetProductAtDiffLocations() {
        tags.add(new InventoryTag("ASR", cost, price, bestBeforeDate, bestBeforeDate, "f10",basicQty));
        tags.add(new InventoryTag("ASR", cost, price, bestBeforeDate, bestBeforeDate, "f11",basicQty));
        tags.add(new InventoryTag("BNN", cost, price, LocalDate.now(), "f11", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111260.
        assertEquals(basicQty * 7, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("ASR"));
        assertEquals(basicQty * 2, inventory.getQuantity("BNN"));
        List<Product> products = inventory.getProductList("ASR");
        for (int i = 0; i < inventory.getQuantity("ASR"); i++) {
            Product product = inventory.getOldestProduct("ASR");
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getId());
            assertEquals(product, inventory.getProduct(product.getSku()));
            assertTrue(inventory.removeProduct(product.getSku()));
        }
    }


    @Test
    void testGetListOfCodes() {
        assertEquals(6, inventory.getIDs().size());
        inventory.addProducts(tags);
        List<String> list = inventory.getIDs();
        assertEquals(6, list.size());
        assertTrue(list.contains("AAA"));
        assertTrue(list.contains("BNN"));
        assertTrue(list.contains("CHI"));
        assertTrue(list.contains("MGO"));
    }


    @Test
    void testGetProducts() {
        inventory.addProducts(tags);
        List<Product> products = inventory.getProductList(tags.get(0).getId(), tags.get(0).getLocation());
        assertEquals(products.size(), tags.get(0).getQuantity());
        assertEquals(0, inventory.getProductList(tags.get(0).getId(), "qerwqrweewqr").size());
    }

    @Test
    void testRemoveProductsWithQuantityTag() {
        inventory.addProducts(tags);
         assertTrue(inventory.removeStock(new QuantityTag("AAA",
                "F11", basicQty/2)));
         assertEquals(basicQty/2, inventory.getQuantity("AAA"));
         assertFalse(inventory.removeStock(new QuantityTag("no such item",
                 "z22", basicQty)));
    }

    @Test
    void testRemoveWithStrings() {
        inventory.addProducts(tags);
        assertTrue(inventory.removeStock("AAA",
                "F11", basicQty/2));
        assertEquals(basicQty/2, inventory.getQuantity("AAA"));
        assertFalse(inventory.removeStock("no such stock",
                "f11", basicQty));
    }




    //Tags contain following items:
    //tags.add(new InventoryTag("aaa", price, bestBeforeDate, "F11", basicQty));
    //tags.add(new InventoryTag("BNN", price, bestBeforeDate, "F12", basicQty));
    //tags.add(new InventoryTag("CHI", price, bestBeforeDate, "F13", basicQty));
    //tags.add(new InventoryTag("MGO", price, bestBeforeDate, "F14", basicQty));
    @Test
    void testRemoveProductsWithListOfTags() {
        inventory.addProducts(tags);
        List<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AAA", "F11", basicQty/2));
        List<QuantityTag> removed = inventory.removeStocks(toBeRemoved);
        assertEquals(1, removed.size());
        QuantityTag removedTag = removed.get(0);
        String removedItemCode = removedTag.getId();
        String removedLocation = removedTag.getLocation();
        int removedQty = removedTag.getQuantity();
        assertEquals("AAA", removedItemCode);
        assertEquals("F11", removedLocation);
        assertEquals(2, removedQty);
    }

    @Test
    void testRemoveProductsMoreThanExisting() {
        inventory.addProducts(tags);
        LinkedList<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AAA", "F11", basicQty * 3));
        List<QuantityTag> removed = inventory.removeStocks(toBeRemoved);
        assertEquals(0, removed.size());
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        List<QuantityTag> list = inventory.findLocations("AAA");
        assertEquals(1, list.size());
        assertEquals(basicQty, list.get(0).getQuantity());
        assertEquals("F11", list.get(0).getLocation());
    }

    @Test
    void testRemoveSingleProduct() {
        tags.add(new InventoryTag("AAA", 50, 60, bestBeforeDate, "T", basicQty));
        tags.add(new InventoryTag("ASR", 50, 60, bestBeforeDate, "D11", basicQty));
        tags.add(new InventoryTag("ASD", 50, 60, bestBeforeDate, "A33", basicQty));
        inventory.addProducts(tags);
        assertEquals(basicQty * 7, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("AAA"));
        assertEquals(basicQty, inventory.getQuantity("ASR"));
        assertEquals(basicQty, inventory.getQuantity("ASD"));
        int initialQty = inventory.getQuantity("AAA");
        for (int i = 0; i < initialQty; i++) {
            int qty = inventory.getQuantity("AAA");
            Product product = inventory.getOldestProduct("AAA");
            assertEquals("AAA", product.getId());
            assertEquals(product, inventory.getProduct(product.getSku()));
            inventory.removeProduct(product.getSku());
            assertNull(inventory.getProduct(product.getSku()));
            assertEquals(qty - 1, inventory.getQuantity("AAA"));
        }
        initialQty = inventory.getQuantity("ASD");
        for (int i = 0; i < initialQty; i++) {
            int qty = inventory.getQuantity("ASR");
            Product product = inventory.getOldestProduct("ASR");
            assertEquals("ASR", product.getId());
            assertEquals(product, inventory.getProduct(product.getSku()));
            inventory.removeProduct(product.getSku());
            assertNull(inventory.getProduct(product.getSku()));
            assertEquals(qty - 1, inventory.getQuantity("ASR"));
        }
        assertFalse(inventory.removeProduct("fadskasfddfasj;"));

        assertEquals(0, inventory.getQuantity("AAA"));
    }

    @Test
    void testRemoveNonExistingStocks() {
        inventory.addProducts(tags);
        LinkedList<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("ZZZ", "A11", basicQty * 3));
        List<QuantityTag> removed = inventory.removeStocks(toBeRemoved);
        assertEquals(0, removed.size());
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("AAA").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
        List<Product> products = inventory.getProductList("BNN");
        assertEquals(basicQty, products.size());
        toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AAA", "F13", basicQty));
        removed = inventory.removeStocks(toBeRemoved);
        assertEquals(0, removed.size());
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("AAA").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
        products = inventory.getProductList("BNN");
        assertEquals(basicQty, products.size());

        assertEquals(0, inventory.getProductList("ZZZ").size());
        assertEquals(0, inventory.getProductList("zZZ", "F!123").size());
    }









    @Test
    void testGetProduct() {
        basicQty = 50;
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", 50, 60,bestBeforeDate,  "T", basicQty));
        //It is expected that sku for this "ASR" products range from 111111 to 111160.
        tags.add(new InventoryTag("ASR", 50, 60,  bestBeforeDate,"F11", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111200.
        assertEquals(basicQty * 2, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("ASR"));
        List<Product> products = inventory.getProductList("ASR");
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
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "F50", basicQty));
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "T", basicQty));
        inventory.addProducts(tags);
        List<QuantityTag> locations = inventory.findLocations("CHI");
        assertEquals(3, locations.size());
        assertEquals("T", locations.get(2).getLocation());
        assertEquals("F13", locations.get(0).getLocation());
        assertEquals("F50", locations.get(1).getLocation());
        locations = inventory.findLocations("MGO");
        assertEquals(1, locations.size());
        assertEquals("F14", locations.get(0).getLocation());
        assertEquals(0, inventory.findLocations("ZZZ").size());
    }

    @Test
    void testFindLocationsOfRemoved() {
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "f50", basicQty));
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "t", basicQty));
        inventory.addProducts(tags);
        List<QuantityTag> locations = inventory.findLocations("CHI");
        assertEquals(3, locations.size());
        assertEquals("T", locations.get(2).getLocation());
        assertEquals("F13", locations.get(0).getLocation());
        assertEquals("F50", locations.get(1).getLocation());
        locations = inventory.findLocations("MGO");
        assertEquals(1, locations.size());
        assertEquals("F14", locations.get(0).getLocation());
        ArrayList<QuantityTag> toBeRemoved = new ArrayList<>();
        toBeRemoved.add(new QuantityTag("CHI", "T", basicQty));
        inventory.removeStocks(toBeRemoved);
        locations = inventory.findLocations("CHI");
        assertEquals("F13", locations.get(0).getLocation());
        assertEquals("F50", locations.get(1).getLocation());
    }

    @Test
    void testGetQtysAtDiffLocations() {
        tags = new LinkedList<>();
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "F50", basicQty));
        tags.add(new InventoryTag("CHI", cost, price, bestBeforeDate, "T", basicQty * 2));
        inventory.addProducts(tags);
        List<QuantityTag> list = inventory.getQuantitiesAtLocations("CHI");
        assertEquals(basicQty * 2, list.get(1).getQuantity());
        assertEquals(basicQty, list.get(0).getQuantity());
        assertEquals(basicQty * 2, list.get(1).getQuantity());
        assertEquals(0, inventory.getQuantitiesAtLocations("ZZZ").size());
        List<QuantityTag> naExpected;
        naExpected = inventory.getQuantitiesAtLocations("BNN");
        assertEquals(1, naExpected.size());
        QuantityTag tag = naExpected.get(0);
        assertEquals("BNN", tag.getId());
        assertEquals("N/A", tag.getLocation());
        assertEquals(0, tag.getQuantity());

    }

    @Test
    void testJsonConversion() {
        inventory.addProducts(tags);
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("AAA").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
        List<Product> products = inventory.getProductList("BNN");
        assertEquals(basicQty, products.size());
        inventory = new Inventory(inventory.toJson());
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("AAA"));
        assertEquals("AAA", tags.get(0).getId());
        assertEquals(1, inventory.findLocations("AAA").size());
        assertEquals("F11",
                inventory.findLocations(tags.get(0).getId()).get(0).getLocation());
        assertEquals(basicQty, products.size());
    }


    @Test
    void testGetDataList() {
        String[] columns = new String[]{
                "Category", "ID", "Name", "Description", "Special Note", "Quantity",
                "Average Cost", "List Price"
        };
        String[] dataList = inventory.getDataList();
        for (int i = 0; i < columns.length; i++) {
            columns[i].equals(dataList[i]);
        }
    }

    @Test
    void testGetData() {
        inventory.addProducts(tags);
        List<Item> items = inventory.getItemList();
        for (Item item: items) {
            Object[] data = item.convertToTableEntry();
            Object[] dataFromInventory = inventory.getData(item.getId());
            for (int i = 0; i < data.length; i++) {
                data[i].equals(dataFromInventory[i]);
            }
        }
    }

    @Test
    void testGetIds() {
        inventory.createCategory("new");
        List<String> ids = inventory.getIDs("FOOD");
        assertEquals(6, inventory.getIDs().size());
        assertEquals(6, ids.size());
        assertTrue(ids.contains(tags.get(0).getId()));
        assertTrue(ids.contains(tags.get(1).getId()));
        assertTrue(ids.contains(tags.get(2).getId()));
        assertTrue(ids.contains(tags.get(3).getId()));
        ids = inventory.getIDs("new");
        assertEquals(0, ids.size());
    }

    @Test
    void testContains() {
        assertTrue(inventory.containsCategory("FOOD"));
        assertFalse(inventory.containsCategory("adsasd"));
        assertTrue(inventory.containsItem("AAA"));
        assertFalse(inventory.containsItem("ewewewew"));
    }

    @Test
    void testGetCategoryOfItem() {
        assertEquals("FOOD", inventory.getCategory(tags.get(0).getId()));
    }

    @Test
    void testGetIDs() {
        assertEquals(0, inventory.getIDs("adsfadfsd").size());
    }



}
