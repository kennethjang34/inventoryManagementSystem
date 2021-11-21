package model;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    int code = 111111111;
    LocalDate currentDate = LocalDate.now();
    LocalDate today = LocalDate.now();
    ArrayList<InventoryTag> tags;
    Account account;
    double price = 100;
    double cost = 200;
    int quantity = 100;
    String location = "T";
    @BeforeEach
    void runBeforeEach() {
        tags = new ArrayList<>();
        InventoryTag tag = new InventoryTag("SAD",  cost, price, currentDate, null, location, quantity) ;
        tags.add(tag);
        tag = new InventoryTag("ADS", cost, price, currentDate, null, location, quantity);
        tags.add(tag);
        tag = new InventoryTag("STR", cost, price, currentDate, null, location, quantity);
        tags.add(tag);
        tag = new InventoryTag("BNN", cost, price, currentDate, null, location, 10);
        tags.add(tag);
    }

    @Test
    void testConstructor() {
        account = new Account(code++, "constructorTest", currentDate, tags.get(0));
        assertEquals(code - 1, Integer.parseInt(account.getCode()));
        assertEquals(currentDate, account.getDate());
        assertEquals("constructorTest", account.getDescription());
        InventoryTag tag = tags.get(0);
        assertEquals(tag.getId(), account.getID());
        assertEquals(tag.getLocation(), account.getLocation());
        assertEquals(tag.getQuantity(), account.getQuantity());
        assertEquals(tag.getDateGenerated(), account.getDate());
        assertEquals(tag.getUnitCost(), account.getAverageCost());
        assertEquals(tag.getUnitPrice(), account.getAveragePrice());
        //account = new Account(code++, "ConstructionTest", currentDate, tags, null);
    }

    @Test
    void testConstructorWithNegativeQty() {
        //tag for removed products

        InventoryTag tag = tags.get(0);
        tag.setQuantity(-tag.getQuantity());
        account = new Account(code++, "testTotalQtyWithRemoved", today, tag);
        assertEquals(code - 1, Integer.parseInt(account.getCode()));
        assertEquals(currentDate, account.getDate());
        assertEquals("testTotalQtyWithRemoved", account.getDescription());
        assertEquals(tag.getId(), account.getID());
        assertEquals(tag.getLocation(), account.getLocation());
        assertEquals(-quantity, account.getQuantity());
        assertEquals(tag.getDateGenerated(), account.getDate());
    }




//    @Test
//    void testGetQuantitiesInfo() {
//        QuantityTag tag = new QuantityTag("chi", "z99", -50);
//        ArrayList<QuantityTag> removed = new ArrayList<>();
//        removed.add(tag);
//        account = new Account(code++, "testTotalQtyWithRemoved", today, tags, removed);
////        List<String> info = account.getQuantitiesInfo();
////        assertEquals(10, info.size());
////        assertEquals("ADS: 666", info.get(2));
////        assertEquals("\tE0: 666", info.get(3));
//    }




//
//    @Test
//    void testGetItemCodes() {
//        account = new Account(code++, "itemCodesTest", currentDate, tags, null);
////        assertEquals(4, account.getItemCodes().size());
////        List<String> codes = account.getItemCodes();
////        for (int i = 0; i < codes.size(); i++) {
////            assertEquals(tags.get(i).getId(), codes.get(i));
////        }
//    }

    @Test
    void testToJson() {
        account = new Account(code++, "JSONTEST", currentDate, tags.get(0));
        JSONObject jsonObject = account.toJson();
        Account fromJson = new Account(jsonObject);
        assertEquals(account.getCode(), fromJson.getCode());
        assertEquals(account.getDate(), fromJson.getDate());
        assertEquals("JSONTEST", fromJson.getDescription());
        assertEquals(account.getID(), fromJson.getID());
        assertEquals(account.getLocation(), fromJson.getLocation());
        assertEquals(account.getQuantity(), fromJson.getQuantity());
        assertEquals(account.getDate(), fromJson.getDate());
    }

}
