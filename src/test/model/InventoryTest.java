package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryTest {
    private Inventory inventory;

    @BeforeEach
    void runBeforeEveryTest() {
        inventory = new Inventory();
        inventory.addItem("Salt", "SLT");
        inventory.addItem("Sanitizer", "STZ");
        inventory.addItem("Tablet", "TAB");
    }




    @Test
    void testConstructor() {
        inventory = new Inventory();
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getTotalExpense());
        assertEquals(0, inventory.getTotalRevenue());
        assertNotNull(inventory.getStocks());
        assertNotNull(inventory.getLedger());
        assertNotNull(inventory.getLowStocks());
    }

    @Test
    void testAddItemWithNameCode() {
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(0, inventory.getQuantity("TAB"));
        assertEquals(3, inventory.getInventorySize());
        assertEquals(0, inventory.getQuantity("SLT"));
        assertEquals(0, inventory.getQuantity("STZ"));
        inventory.addItem("Pizza", "PZA");
        assertEquals(0, inventory.getQuantity("Pizza"));
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(4, inventory.getInventorySize());
        inventory.addItem("Ramen", "RMN");
        assertEquals(0, inventory.getQuantity("RMN"));
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(5, inventory.getInventorySize());
        assertEquals(0, inventory.find("PIZ"));
        assertEquals(1, inventory.find("RMN"));
        assertEquals(2, inventory.find("SLT"));
        assertEquals(3, inventory.find("STZ"));
        assertEquals(4, inventory.find("TAB"));
        assertEquals(0, inventory.getLowStockThreshold("PIZ"));
        assertEquals(0, inventory.getLowStockThreshold("RMN"));
        assertEquals(0, inventory.getLowStockThreshold("STZ"));
        assertEquals(0, inventory.getLowStockThreshold("SLT"));
        assertEquals(0, inventory.getLowStockThreshold("TAB"));
    }


    @Test
    void testAddItemWithoutEXP() {
        inventory.addItem("Nike Jordan Edition Shoes", "NJE", 100, 70, 80 , "M1");
        assertEquals(100, inventory.getTotalQuantity());
        Item item = inventory.getItem("NJE");
        assertEquals(100, inventory.getQuantity(item));
        inventory.addItem("Nike Jordan Edition Shoes", "NJE", 100, 70, 90, "M2");
        assertEquals(200, inventory.getTotalQuantity());
        assertEquals(100, inventory.getQuantity(item));
        assertEquals(4, inventory.getInventorySize());
        inventory.addItem("Adidas T shirt", "ADT", 400, 20, 30, "A1");
        item = inventory.getItem("ADT");
        assertEquals(600, inventory.getTotalQuantity());
        assertEquals(400, inventory.getQuantity(item));
        assertEquals(5, inventory.getInventorySize());
        assertEquals(0, inventory.find("ADT"));
        assertEquals(1, inventory.find("NJE"));
        assertEquals(2, inventory.find("SLT"));
        assertEquals(3, inventory.find("STZ"));
        assertEquals(4, inventory.find("TAB"));
        assertEquals(100, inventory.getLowStockThreshold("NJE"));
        assertEquals(20, inventory.getLowStockThreshold("ADT"));
    }
    @Test
    void testAddItemWithNameCodeLocation() {
        inventory.addItem("Chicken", "CHK", "F11");
        assertEquals(0, inventory.getQuantity("CHK"));
        assertEquals(0, inventory.getTotalQuantity());
        assertEquals(4, inventory.getInventorySize());
        assertEquals(0, inventory.find("CHK"));
        assertEquals(1, inventory.find("SLT"));
        assertEquals(2, inventory.find("STZ"));
        assertEquals(3, inventory.find("TAB"));
        assertEquals(0, inventory.getLowStockThreshold("CHK"));
    }



    @Test
    void testAddItemWithEXP() {
        inventory.addItem("Banana", "BNN", 1000, 3, 4, 300, "F1", LocalDate.now());
        assertEquals(1000, inventory.getTotalQuantity());
        Item item = inventory.getItem("BNN");
        assertEquals(1000, inventory.getQuantity(item));
        inventory.addItem("Banana", "BNN", 1005, 3, 4,400, "F5", LocalDate.now());
        assertEquals(2005, inventory.getTotalQuantity());
        assertEquals(2005, inventory.getQuantity(item));
        assertEquals(4, inventory.getInventorySize());
        inventory.addItem("Apple", "APL", 4000, 2, 3700, "F5", LocalDate.now());
        item = inventory.getItem("APL");
        assertEquals(6005, inventory.getTotalQuantity());
        assertEquals(4000, inventory.getQuantity(item));
        assertEquals(5, inventory.getInventorySize());
        assertEquals(0, inventory.find("APL"));
        assertEquals(1, inventory.find("BNN"));
        assertEquals(2, inventory.find("SLT"));
        assertEquals(3, inventory.find("STZ"));
        assertEquals(4, inventory.find("TAB"));
        assertEquals(700, inventory.getLowStockThreshold("APL"));
        assertEquals(400, inventory.getLowStockThreshold("BMN"));
    }

    @Test
    void testLowThresholdUpdateByAddItem() {
        inventory.addItem("Banana", "BNN", 1000, 5, 7, 300, "F1", LocalDate.now());
        assertEquals(300, inventory.getItem("BNN").getLowStockThreshold());
        inventory.addItem("Banana", "BNN", 1005, 5, 8, 400, "F5", LocalDate.now());
        assertEquals(400, inventory.getItem("BNN").getLowStockThreshold());
    }



    @Test
    void testAddItemWithoutLowThreshold() {
        inventory.addItem("Beef", "BEF", 500,50, 60, "F36",LocalDate.now());
        assertEquals(500, inventory.getQuantity("BEF"));
        assertEquals(4, inventory.getStocks().size());
        assertEquals(0, inventory.getLowStockThreshold("BEF"));
        inventory.addItem("Nokia", "NOK", 1100, 10, 20, "E21");
        assertEquals(1100, inventory.getQuantity("NOK"));
        assertEquals(5, inventory.getStocks().size());
        assertEquals(0, inventory.getLowStockThreshold("NOK"));
    }


    @Test
    void testAddSingleProductWithoutEXP() {
        inventory.addItem("Galaxy", "GXY");
        inventory.addProduct("GXY");
        assertEquals(1, inventory.getQuantity("GXY"));
        inventory.addProduct("GXY");
        assertEquals(2, inventory.getQuantity("GXY"));
    }
    @Test
    void testAddMultipleProductsWithoutEXP() {
        inventory.addItem("Iphone", "IPN");
        inventory.addProducts("IPN", 1000, 10, 20);
        assertEquals(1000, inventory.getTotalQuantity());
        assertEquals(1000, inventory.getQuantity("IPN"));
    }

    @Test
    void testAddSingleProductWithEXP() {
        inventory.addItem("Eggs", "EGG", "F35");
        inventory.addProduct("EGG", LocalDate.now());
        assertEquals(151, inventory.getQuantity("EGG"));
    }

    @Test
    void testAddMultipleProductsWithEXP() {
        inventory.addItem("Pork", "PRK", 150, 20, 30, "F35",LocalDate.now());
        inventory.addProducts("PRK", 1000, 25, 35);
        assertEquals(1150, inventory.getQuantity("PRK"));
    }

    @Test
    void testCheckLowStocksWithEnoughStocks() {
        inventory = new Inventory();
        inventory.addItem("Pork", "PRK", 150, 10, 20,  50,"F35",LocalDate.now());
        inventory.addItem("Eggs", "EGG", 150, 20, 30, 10,"F35",LocalDate.now());
        inventory.addProduct("EGG", LocalDate.now());
        assertEquals(null, inventory.checkLowStocks());

    }

    @Test
    void testCheckLowStocksWithInsufficientStocks() {
        inventory = new Inventory();
        inventory.addItem("Eggs", "EGG", 150, 2.5,5, 50,  "F35", LocalDate.now());
        inventory.checkLowStocks();
        inventory.addItem("Pork", "PRK", 30, 35, 37, 10, "F35", LocalDate.now());
        ArrayList<Item> runningLow = inventory.checkLowStocks();
        assertEquals(2, runningLow.size());
        assertEquals(runningLow.get(0).getItemCode(), "EGG");
        assertEquals(runningLow.get(1).getItemCode(), "PRK");
    }

    @Test
    void testCheckLowStocksWithSomeEnoughSomeNot() {
        inventory = new Inventory();
        inventory.addItem("Eggs", "EGG", 1000, 2.5, 5,55,"F35", LocalDate.now());
        inventory.checkLowStocks();
        inventory.addItem("Pork", "PRK", 30, 35,38,1100, "F35", LocalDate.now());
        ArrayList<Item> runningLow = inventory.checkLowStocks();
        assertEquals(1, runningLow.size());
        assertEquals(runningLow.get(0).getItemCode(), "PRK");
    }

    @Test
    void testProcessRegularPurchase() {
        double initialExpense = inventory.getTotalExpense();
        int transactionNum = 11111;
        String description = "testPurchase";
        LocalDate date = LocalDate.now();
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(new Item("Pizza", "PZA", 1000, 40, 60,"F60"));
        assertEquals(40000, inventory.getTotalExpense() - initialExpense);
        itemList.add(new Item("Chicken", "CHK", 4000, 50, 70, "F60"));
        Purchase purchase = new Purchase(transactionNum, "description", date, itemList);
        inventory.processPurchases(purchase);
        assertEquals(1000, inventory.getQuantity("PZA"));
        assertEquals(4000, inventory.getQuantity("CHK"));
        assertEquals(16000, inventory.getTotalExpense() - initialExpense);
    }

    @Test
    void testProcessZeroQuantityPurchase() {
        inventory = new Inventory();
        int transactionNum = 11111;
        String description = "testPurchase";
        LocalDate date = LocalDate.now();
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(new Item("Pizza", "PZA", 0,5,10,"F60"));
        assertEquals(0, inventory.getTotalExpense());
        itemList.add(new Item("Chicken", "CHK", 0,5, 10, "F60"));
        assertEquals(0, inventory.getTotalExpense());
        Purchase purchase = new Purchase(transactionNum, description, date, itemList);
        inventory.processPurchases(purchase);
        assertEquals(2, inventory.getInventorySize());
        assertEquals(0, inventory.getQuantity("PZA"));
        assertEquals(0, inventory.getQuantity("CHK"));
    }









}