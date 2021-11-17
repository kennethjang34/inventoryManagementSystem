package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LedgerTest {
    private Ledger ledger;
    private static int sku = 111111111;
    private static double defaultCost = 100;
    private final LocalDate TODAY = LocalDate.now();
    private List<InventoryTag> tags;
    double cost = 12;
    double price = 13;
    String location = "T";
    int quantity = 100;
    LocalDate currentDate = LocalDate.now();

    @BeforeEach
    void runBeforeEach() {
        ledger = new Ledger();
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
        ledger = new Ledger();
        assertEquals(6, ledger.getCodeSize());
        assertEquals(0, ledger.getSize());
        ledger = new Ledger(9);
        assertEquals(9, ledger.getCodeSize());
        assertEquals(0, ledger.getSize());
    }

    @Test
    void testAddAccount() {
        assertEquals(0, ledger.getSize());
        ledger.addAccount(tags.get(0),"nothing", LocalDate.now());
        assertEquals(1, ledger.getSize());
        assertEquals(1, ledger.getAccountLists().size());
        assertEquals("111111", ledger.getAccount(111111).getCode());
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts(TODAY).get(0));
        Account account = ledger.getAccounts(currentDate).get(0);
        InventoryTag tag = tags.get(0);
        assertEquals("nothing", account.getDescription());
        assertEquals(tag.getId(), account.getID());
        assertEquals(tag.getLocation(), account.getLocation());
        assertEquals(tag.getQuantity(), account.getQuantity());
        assertEquals(tag.getDateGenerated(), account.getDate());
        assertEquals(tag.getUnitCost(), account.getAverageCost());
        assertEquals(tag.getUnitPrice(), account.getAveragePrice());


    }


//    @Test
//    void testGetAccountsWithDate() {
//        LocalDate today = LocalDate.now();
//        LinkedList<QuantityTag> tags = new LinkedList<>();
//        tags.add(new QuantityTag("aaa", "T", 100));
//        ledger.addAccount(tags,new LinkedList<>(), "nothing", today);
//        assertEquals(1, ledger.getSize());
//        //assertEquals("AAA", ledger.getAccounts(today).get(0).getItemCodes().get(0));
//        assertEquals(tags.get(0).getQuantity(),
//                ledger.getAccounts(today).get(0).getQuantity("aaa"));
//        tags.clear();
//        tags.add(new QuantityTag("CHI", "A", 20));
//        ledger.addAccount(tags, new LinkedList<>(), "something", LocalDate.of(2021, 12 ,12));
//        assertEquals(2, ledger.getSize());
//        assertEquals("CHI",
//                ledger.getAccounts(LocalDate.of(2021, 12 ,12)).get(0).getItemCodes().get(0));
//        assertEquals(tags.get(0).getQuantity(),
//                ledger.getAccounts(LocalDate.of(2021, 12 ,12)).get(0).getQuantity("CHI"));
//        assertEquals(0, ledger.getAccounts(LocalDate.of(2030, 12, 12)).size());
//    }

    @Test
    void testGetNonExistingAccount() {
        //By default, Account number in Ledger will start from 111111
        ledger.addAccount(tags.get(0),"nothing", currentDate);
        assertEquals(1, ledger.getSize());
        assertNull(ledger.getAccount(1212111));
    }

    @Test
    void testGetListsFromNonExistingDate() {
        //By default, Account number in Ledger will start from 111111
        ledger.addAccount(tags.get(0),"nothing", currentDate);
        assertNull(ledger.getAccounts(LocalDate.of(1999, 9, 23)));
    }

    @Test
    void testJsonConversion() {
        ledger.addAccount(tags.get(0), "nothing", currentDate);
        assertEquals(1, ledger.getSize());
        assertEquals(1, ledger.getAccountLists().size());
        assertEquals(ledger.getAccount(111111), ledger.getAccountLists().get(0).get(0));
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals("" + 111111, ledger.getAccounts(TODAY).get(0).getCode());
//        assertEquals(null,
//                ledger.addAccount(new LinkedList<QuantityTag>(),
//                        new LinkedList<>(),"test", LocalDate.now()));
        ledger = new Ledger(ledger.toJson());
        assertEquals(1, ledger.getSize());
        assertEquals(1, ledger.getAccountLists().size());
        assertEquals(ledger.getAccount("111111"), ledger.getAccountLists().get(0).get(0));
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals(ledger.getAccount("111111"), ledger.getAccounts(TODAY).get(0));
    }



}
