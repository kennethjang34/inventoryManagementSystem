package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LedgerTest {
    private Ledger ledger;
    private static int sku = 111111111;
    private static double defaultCost = 100;
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void runBefore() {
        ledger = new Ledger();
    }

    private  LinkedList<ProductTag> createLocationTags(String itemCode, double cost, int qty, String location) {
        LinkedList<ProductTag> tags = new LinkedList<>();
        for (int i = 0; i < qty; i++) {
            tags.add(new ProductTag(new Product(itemCode, sku++, cost, TODAY, null), location));
        }
        return tags;
    }

    @Test
    void testConstructor() {
        ledger = new Ledger();
        assertEquals(9, ledger.getCodeSize());
        assertEquals(0, ledger.getSize());
        ledger = new Ledger(6);
        assertEquals(6, ledger.getCodeSize());
        assertEquals(0, ledger.getSize());
    }

    @Test
    void testAddAccount() {
        assertEquals(0, ledger.getSize());
        Map<String, LinkedList<ProductTag>> tempList = new HashMap<>();
        LinkedList<ProductTag> tags = createLocationTags("AAA", defaultCost, 100, "T");
        tempList.put("AAA", tags);
        ledger.addAccount(tempList, "test", TODAY);
        assertEquals(1, ledger.getSize());
        assertEquals("test", ledger.getAccount(111111));
        assertEquals(1, ledger.getAccounts().size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts().get(0));
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts(TODAY).get(0));
    }



}
