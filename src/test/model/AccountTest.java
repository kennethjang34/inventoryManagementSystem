package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    int code = 111111111;
    LocalDate currentDate = LocalDate.now();
    String description = "accountCreatedForTest";
    LocalDate today = LocalDate.now();
    ArrayList<QuantityTag> tags;
    Account account;

    @BeforeEach
    void runBeforeEach() {
        tags = new ArrayList<>();
        QuantityTag tag = new QuantityTag("SAD", "T", 250);
        tags.add(tag);
        tag = new QuantityTag("ADS", "e0", 666);
        tags.add(tag);
        tag = new QuantityTag("STR", "f11", 1);
        tags.add(tag);
        tag = new QuantityTag("bnn", "f00", 10);
        tags.add(tag);
    }

    @Test
    void testConstructor() {
        account = new Account(code++, "constructorTest", currentDate, tags, null);
        assertEquals(250 + 666 + 1 + 10, account.getTotalQuantity());
        assertEquals(code - 1, account.getCode());
        assertEquals(666, account.getQuantity("ads"));
        assertEquals(currentDate, account.getDate());
        assertEquals("constructorTest", account.getDescription());
        tags.add(new QuantityTag("Bnn", "f24", 20));
        account = new Account(code++, "ConstructionTest", currentDate, tags, null);
        assertEquals(30, account.getQuantity("bnn"));
        assertEquals(20, account.getQuantityAtLocation("bnn", "f24"));
        assertEquals(2, account.getLocations("bnn").size());
        assertEquals("F00", account.getLocations("bnn").get(0));
        assertEquals("F24", account.getLocations("bnn").get(1));
    }

    @Test
    void testConstructorWithSameCodeSameQty() {
        tags.add(new QuantityTag("SAD", "T", 150));
        account = new Account(code++, "constructorTest", currentDate, tags, null);
        assertEquals(250 + 666 + 1 + 10 + 150, account.getTotalQuantity());
        assertEquals(code - 1, account.getCode());
        assertEquals(400, account.getQuantity("sad"));
    }

    @Test
    void testConstructorWithRemovedList() {
        //tag for removed products
        QuantityTag tag = new QuantityTag("chi", "z99", -50);
        ArrayList<QuantityTag> removed = new ArrayList<>();
        removed.add(tag);
        account = new Account(code++, "testTotalQtyWithRemoved", today, tags, removed);
        assertEquals(250 + 666 + 1 + 10 + 50, account.getTotalQuantity());
        assertEquals(code - 1, account.getCode());
        assertEquals(666, account.getQuantity("ads"));
        assertEquals(currentDate, account.getDate());
        assertEquals("testTotalQtyWithRemoved", account.getDescription());
        tags.add(new QuantityTag("Bnn", "f24", 20));
        assertEquals(1, account.getLocations("chi").size());
        assertEquals("Z99", account.getLocations("chi").get(0));
        assertEquals(-50, account.getQuantity("chi"));
    }

    @Test
    void testGetQtyAtLocation() {
        tags.add(new QuantityTag("ADS", "e12", 30));
        account = new Account(code++, "testGetQtyAtLocation", currentDate, tags, null);
        assertEquals(30, account.getQuantityAtLocation("ads", "e12"));
        assertEquals(0, account.getQuantityAtLocation("ads", "e"));
        assertEquals(0, account.getQuantityAtLocation("ads", "e50"));
        assertEquals(0, account.getQuantityAtLocation("ascd", "e50"));
        assertEquals(666, account.getQuantityAtLocation("ADS", "e0"));
    }


    @Test
    void testGetQuantitiesInfo() {
        QuantityTag tag = new QuantityTag("chi", "z99", -50);
        ArrayList<QuantityTag> removed = new ArrayList<>();
        removed.add(tag);
        account = new Account(code++, "testTotalQtyWithRemoved", today, tags, removed);
        ArrayList<String> info = account.getQuantitiesInfo();
        assertEquals(10, info.size());
        assertEquals("ADS: 666", info.get(2));
        assertEquals("\tE0: 666", info.get(3));
    }





    @Test
    void testGetItemCodes() {
        account = new Account(code++, "itemCodesTest", currentDate, tags, null);
        assertEquals(4, account.getItemCodes().size());
        List<String> codes = account.getItemCodes();
        for (int i = 0; i < codes.size(); i++) {
            assertEquals(tags.get(i).getItemCode(), codes.get(i));
        }
    }

}
