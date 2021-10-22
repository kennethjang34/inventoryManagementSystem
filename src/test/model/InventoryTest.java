package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;
    private LinkedList<InventoryTag> tags;
    private static LocalDate today = LocalDate.now();
    private static final LocalDate bestBeforeDate = LocalDate.of(2021, 10, 20);
    private static int basicQty = 3;
    private static double price = 5;

    @BeforeEach
    void runBeforeEveryTest() {
        inventory = new Inventory();
        basicQty = 3;
        tags = new LinkedList<>();
        tags.add(new InventoryTag("aaa", price, bestBeforeDate, "F11", basicQty));
        tags.add(new InventoryTag("BNN", price, bestBeforeDate, "F12", basicQty));
        tags.add(new InventoryTag("CHI", price, bestBeforeDate, "F13", basicQty));
        tags.add(new InventoryTag("MGO", price, bestBeforeDate, "F14", basicQty));
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
        assertNull(inventory.getListOfCodes());
        assertNull(inventory.getProductList("PIZ"));
        inventory = new Inventory(3);
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertNull(inventory.getListOfCodes());
        assertNull(inventory.getProductList("PIZ"));
        inventory = new Inventory(3, 100);
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalQuantity());
        assertNull(inventory.getListOfCodes());
        assertNull(inventory.getProductList("PIZ"));
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
        assertEquals("AAA", tags.get(0).getItemCode());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals(inventory.getLocationCodeNumber("F11"),
                inventory.findLocations(tags.get(0).getItemCode()).get(0));
        List<Product> products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());

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
    void testCreateSkuMaxedOut() {
        inventory.setSkuSize(7);
        inventory.setNextSKU(9999999);
        tags = new LinkedList<InventoryTag>();
        tags.add(new InventoryTag("abc", 10, "t", 2));
        inventory.addProducts(tags);
        List<Product> products = inventory.getProductList("abc");
        assertEquals(2, products.size());
        assertEquals(9999999, products.get(0).getSku());
        assertEquals(1111111, products.get(1).getSku());
    }

    @Test
    void testSetNextSku() {
        try {
            inventory.setNextSKU(-1);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    @Test
    void testGetProductNotInInventory() {
        inventory = new Inventory();
        tags = new LinkedList<>();
        tags.add(new InventoryTag("ASR", 50, "T", basicQty));
        inventory.addProducts(tags);
        assertNull(inventory.getProduct("BBB", 11113));
    }

    @Test
    void testGetProductRegularCase() {
        basicQty = 50;
        inventory = new Inventory();
        tags = new LinkedList<>();
        inventory.setSkuSize(6);
        inventory.setNextSKU(111111);
        tags.add(new InventoryTag("ASR", 50, "T", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111160.
        assertEquals(basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("asr"));
        List<Product> products = inventory.getProductList("ASR");
        for (int i = 0; i < inventory.getQuantity("asr"); i++) {
            Product product = inventory.getProduct("ASR", 111111 + i);
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getItemCode());
            assertEquals(111111 + i, product.getSku());
        }
    }

    @Test
    void testGetProductAtDiffLocations() {
        basicQty = 50;
        inventory = new Inventory();
        tags = new LinkedList<>();
        inventory.setSkuSize(6);
        inventory.setNextSKU(111111);
        tags.add(new InventoryTag("ASR", 50, "T", basicQty));
        tags.add(new InventoryTag("ASR", 50, "f11", basicQty));
        tags.add(new InventoryTag("bnn", 50, "f11", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111260.
        assertEquals(basicQty * 3, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("asr"));
        assertEquals(basicQty, inventory.getQuantity("bnn"));
        List<Product> products = inventory.getProductList("ASR");
        for (int i = 0; i < inventory.getQuantity("asr"); i++) {
            Product product = inventory.getProduct("ASR", 111111 + i);
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getItemCode());
            assertEquals(111111 + i, product.getSku());
        }
    }

    @Test
    void testGetProductsWithInvalidCode() {
        inventory.addProducts(tags);
        assertNull(inventory.getProductList("ASFKJDL"));
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
    void testGetListOfCodes() {
        assertNull(inventory.getListOfCodes());
        inventory.addProducts(tags);
        List<String> list = inventory.getListOfCodes();
        assertEquals(4, list.size());
        assertEquals("AAA", list.get(0));
        assertEquals("BNN", list.get(1));
        assertEquals("CHI", list.get(2));
        assertEquals("MGO", list.get(3));
    }

    @Test
    void testGetLocationCodeNumberOnlyWithLetter() {
        assertEquals(0, inventory.getLocationCodeNumber("A"));
        assertEquals(100, inventory.getLocationCodeNumber("b"));
        try {
            inventory.getLocationCodeNumber("}{{}13}");
            fail();
        } catch (NumberFormatException e) {

        }
    }

    @Test
    void testIsValidLocationCodeWithRightFormat() {
        assertTrue(inventory.isValidLocationCode("f"));
        assertTrue(inventory.isValidLocationCode("F"));
        assertTrue(inventory.isValidLocationCode("f0"));
        assertTrue(inventory.isValidLocationCode("F0"));
        assertTrue(inventory.isValidLocationCode("f11"));
        assertTrue(inventory.isValidLocationCode("F12"));
        inventory = new Inventory(3, 1000);
        assertTrue(inventory.isValidLocationCode("d"));
        assertTrue(inventory.isValidLocationCode("D"));
        assertTrue(inventory.isValidLocationCode("d0"));
        assertTrue(inventory.isValidLocationCode("D000"));
        assertTrue(inventory.isValidLocationCode("d999"));
    }

    @Test
    void testIsValidLocationCodeWithWrongFormat() {
        assertFalse(inventory.isValidLocationCode("{"));
        assertFalse(inventory.isValidLocationCode("1"));
        assertFalse(inventory.isValidLocationCode("Abb"));
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
        LinkedList<QuantityTag> removed = inventory.removeProducts(toBeRemoved);
        assertEquals(1, removed.size());
        QuantityTag removedTag = removed.get(0);
        String removedItemCode = removedTag.getItemCode();
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
        LinkedList<QuantityTag> removed = inventory.removeProducts(toBeRemoved);
        assertNull(removed);
        assertEquals(basicQty, inventory.getQuantity("aAa"));
        List<Integer> list = inventory.findLocations("aaa");
        assertEquals(1, list.size());
        assertEquals("F11", inventory.getStringLocationCode(list.get(0)));
    }

    @Test
    void testRemoveSingleProduct() {
        basicQty = 2;
        inventory = new Inventory();
        tags = new LinkedList<>();
        inventory.setSkuSize(6);
        inventory.setNextSKU(111111);
        tags.add(new InventoryTag("aaa", 50, "T", basicQty));
        tags.add(new InventoryTag("asr", 50, "d11", basicQty));
        tags.add(new InventoryTag("asd", 50, "a33", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111260.
        assertEquals(basicQty * 3, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals(basicQty, inventory.getQuantity("asr"));
        assertEquals(basicQty, inventory.getQuantity("asd"));
        int initialQty = inventory.getQuantity("aaa");
        for (int i = 0; i < initialQty; i++) {
            int qty = inventory.getQuantity("aaa");
            Product product = inventory.getProduct("aaa", 111111 + i);
            assertEquals("AAA", product.getItemCode());
            assertEquals(111111 + i, product.getSku());
            inventory.removeProduct(product.getItemCode(), product.getSku());
            assertNull(inventory.getProduct(product.getItemCode(), product.getSku()));
            assertEquals(qty - 1, inventory.getQuantity("aaa"));
        }
        for (int i = 0; i < initialQty; i++) {
            int qty = inventory.getQuantity("asr");
            Product product = inventory.getProduct("asr",  111111 + basicQty + i);
            assertEquals("ASR", product.getItemCode());
            assertEquals(111111 + basicQty + i, product.getSku());
            inventory.removeProduct(product.getItemCode(), product.getSku());
            assertNull(inventory.getProduct(product.getItemCode(), product.getSku()));
            assertEquals(qty - 1, inventory.getQuantity("asr"));
        }


        assertEquals(0, inventory.getQuantity("aaa"));
    }

    @Test
    void testRemoveNonExistingStocks() {
        inventory.addProducts(tags);
        LinkedList<QuantityTag> toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("ZZZ", "A11", basicQty * 3));
        LinkedList<QuantityTag> removed = inventory.removeProducts(toBeRemoved);
        assertNull(removed);
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getItemCode());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals(inventory.getLocationCodeNumber("F11"),
                inventory.findLocations(tags.get(0).getItemCode()).get(0));
        List<Product> products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());
        toBeRemoved = new LinkedList<>();
        toBeRemoved.add(new QuantityTag("AAa", "f13", basicQty));
        removed = inventory.removeProducts(toBeRemoved);
        assertNull(removed);
        assertEquals(4 * basicQty, inventory.getTotalQuantity());
        assertEquals(basicQty, inventory.getQuantity("aaa"));
        assertEquals("AAA", tags.get(0).getItemCode());
        assertEquals(1, inventory.findLocations("aaa").size());
        assertEquals(inventory.getLocationCodeNumber("F11"),
                inventory.findLocations(tags.get(0).getItemCode()).get(0));
        products = inventory.getProductList("bnn");
        assertEquals(basicQty, products.size());
        assertNull(inventory.getProduct("zzz", 111));
        assertNull(inventory.getProduct("AAa", 222222));
        assertFalse(inventory.removeProduct("ZZZ", 000));
    }









    @Test
    void testFindProduct() {
        basicQty = 50;
        inventory = new Inventory();
        tags = new LinkedList<>();
        inventory.setSkuSize(6);
        inventory.setNextSKU(111111);
        tags.add(new InventoryTag("ASR", 50, "T", basicQty));
        //It is expected that sku for this "ASR" products range from 111111 to 111160.
        tags.add(new InventoryTag("ASR", 50, "f11", basicQty));
        inventory.addProducts(tags);
        //It is expected that sku for this "ASR" products range from 111111 to 111200.
        assertEquals(basicQty * 2, inventory.getTotalQuantity());
        assertEquals(basicQty * 2, inventory.getQuantity("asr"));
        List<Product> products = inventory.getProductList("asr");
        for (int i = 0; i < inventory.getQuantity("asr"); i++) {
            ProductTag tag = inventory.findProduct("asr", 111111 + i);
            Product product = tag.getProduct();
            assertEquals(products.get(i), product);
            assertEquals("ASR", product.getItemCode());
            assertEquals(111111 + i, product.getSku());
            if (i < basicQty) {
                assertEquals("T", tag.getLocation());
            } else {
                assertEquals("F11", tag.getLocation());
            }
        }
    }

    //tags have inventory tags as following
    //tags.add(new InventoryTag("aaa", price, bestBeforeDate, "F11", basicQty));
    //tags.add(new InventoryTag("BNN", price, bestBeforeDate, "F12", basicQty));
    //tags.add(new InventoryTag("CHI", price, bestBeforeDate, "F13", basicQty));
    //tags.add(new InventoryTag("MGO", price, bestBeforeDate, "F14", basicQty));
    @Test
    void testFindLocations() {
        tags.add(new InventoryTag("chi", price, bestBeforeDate, "f50", basicQty));
        tags.add(new InventoryTag("chi", price, bestBeforeDate, "t", basicQty));
        inventory.addProducts(tags);
        List<Integer> locations = inventory.findLocations("chi");
        assertEquals(3, locations.size());
        assertEquals(inventory.getLocationCodeNumber("t"), locations.get(0));
        assertEquals(inventory.getLocationCodeNumber("f13"), locations.get(1));
        assertEquals(inventory.getLocationCodeNumber("f50"), locations.get(2));
        locations = inventory.findLocations("mgo");
        assertEquals(1, locations.size());
        assertEquals(inventory.getLocationCodeNumber("f14"), locations.get(0));
        assertNull(inventory.findLocations("zzz"));
    }


    @Test
    void testGetQtysAtDiffLocations() {
        tags.add(new InventoryTag("chi", price, bestBeforeDate, "f50", basicQty));
        tags.add(new InventoryTag("chi", price, bestBeforeDate, "t", basicQty * 2));
        inventory.addProducts(tags);
        List<QuantityTag> list = inventory.getQuantitiesAtLocations("chi");
        assertEquals(basicQty * 2, list.get(0).getQuantity());
        assertEquals(basicQty, list.get(1).getQuantity());
        assertEquals(basicQty, list.get(2).getQuantity());
        assertNull(inventory.getQuantitiesAtLocations("zzz"));
    }



    /*
    @Test
    void testFindLocationOfProductWithProduct() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        LinkedList<ProductTag> list = new LinkedList<>();
        ProductTag tag = new ProductTag(product, "D32");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("D32", inventory.getStringLocationCode(inventory.findProduct(product)));
        tag = new ProductTag(new Product("DDD", sku++, 50, today, bestBeforeDate), "Z99");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("Z99", inventory.getStringLocationCode(inventory.findProduct(tag.getProduct())));
        //A00 is the same as T
        tag = new ProductTag(new Product("adi", sku++, 2, today, bestBeforeDate), "A00");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("T", inventory.getStringLocationCode(inventory.findProduct(tag.getProduct())));
    }

    @Test
    void testFindLocationOfProductWithProductCode() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        String itemCode = product.getItemCode();
        LinkedList<ProductTag> list = new LinkedList<>();
        ProductTag tag = new ProductTag(product, "D32");
        list.add(tag);
        inventory.addProducts(list);
        assertEquals("D32", inventory.getStringLocationCode(inventory.findProduct(itemCode, sku - 1)));
        product = new Product("DDD", sku++, 50, today, bestBeforeDate);
        tag = new ProductTag(product, "Z99");
        list.add(tag);
        inventory.addProducts(list);
        itemCode = product.getItemCode();
        assertEquals("Z99", inventory.getStringLocationCode(inventory.findProduct(itemCode, sku - 1)));
        product = new Product("adi", sku++, 2, today, bestBeforeDate);
        tag = new ProductTag(product, "A00");
        list.add(tag);
        inventory.addProducts(list);
        itemCode = product.getItemCode();
        assertEquals("T", inventory.getStringLocationCode(inventory.findProduct(itemCode, sku - 1)));
    }

    @Test
    void testFindMethodsWithNonExistingProduct() {
        Product product = new Product("CHI", sku++, 11, today, bestBeforeDate);
        LinkedList<ProductTag> list = new LinkedList<>();
        ProductTag tag = new ProductTag(product, "D32");
        list.add(tag);
        inventory.addProducts(list);
        String itemCode = product.getItemCode();
        assertEquals("D32", inventory.getStringLocationCode(inventory.findProduct(itemCode, product.getSku())));
        Product notInInventory = new Product("ABC", sku++, 12, today, bestBeforeDate);
        assertEquals(-1, inventory.findProduct(notInInventory));
        assertEquals(-1, inventory.findProduct("ABC", notInInventory.getSku()));
    }

    @Test
    void testRemoveProduct() {
        inventory.addProducts(productsToAddWLocation);
        for (ProductTag tag: productsToAddWLocation) {
            Product product = tag.getProduct();
            String itemCode = product.getItemCode();
            int sku = product.getSku();
            assertEquals(1, inventory.getQuantity(itemCode));
            assertEquals(product, inventory.getProduct(itemCode, sku));
            assertTrue(inventory.removeProducts(product));
            assertEquals(0, inventory.getQuantity(product.getItemCode()));
            assertEquals(-1, inventory.findProduct(product));
            assertEquals(-1, inventory.findProduct(itemCode, product.getSku()));
            assertNull(inventory.getProduct(itemCode, sku));
        }
    }

    @Test
    void testRemoveProductsWithItemCode() {
        LinkedList<ProductTag> list = createProducts("NIK", 100.1, 544, "T");
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
        LinkedList<ProductTag> list = createProducts("NIK", 100.1, 100, "T");
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
        LinkedList<ProductTag> list1 = createProducts("NIK", 100.1, 100, "T");
        inventory.addProducts(list1);
        LinkedList<ProductTag> list2 = createProducts("NIK", 100.1, 100, "A11");
        inventory.addProducts(list2);
        assertEquals(200, inventory.getQuantity("NIK"));
        inventory.removeProducts("NIK", 110);
        assertEquals(90, inventory.getQuantity("NIK"));
        Product locatedAtT = list1.get(0).getProduct();
        assertEquals(-1, inventory.findProduct(locatedAtT));
    }


    @Test
    void testRemoveProductsWithProductList() {
        LinkedList<ProductTag> list = createProducts("NIK", 100.1, 544, "T");
        assertEquals(0, inventory.getQuantity("NIK"));
        inventory.addProducts(list);
        assertEquals(544, inventory.getQuantity("NIK"));
        inventory.removeProducts(list);
        assertEquals(0, inventory.getQuantity("NIK"));
    }

    @Test
    void testRemoveNonExistingProduct() {
        inventory = new Inventory();
        productsToAddWLocation = new LinkedList<>();
        Product product = new Product("BBB", sku++, 0.04, today, null);
        assertEquals(0, inventory.getQuantity(product.getItemCode()));
        assertFalse(inventory.removeProducts(product));
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



*/

}
