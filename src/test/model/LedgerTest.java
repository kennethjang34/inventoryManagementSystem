package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LedgerTest {
    private Ledger ledger;
    private static int sku = 111111111;
    private static double defaultCost = 100;
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void runBefore() {
        ledger = new Ledger();
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
        LinkedList<QuantityTag> tags = new LinkedList<>();
        tags.add(new QuantityTag("CHI", "T", 100));
        ledger.addAccount(tags, new LinkedList<>(),"nothing", LocalDate.now());
        assertEquals(1, ledger.getSize());
        assertEquals(1, ledger.getAccounts().size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts().get(0));
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts(TODAY).get(0));
        assertEquals(null,
                ledger.addAccount(new LinkedList<QuantityTag>(),
                        new LinkedList<>(),"test", LocalDate.now()));
    }



    @Test
    void testGetAccountsWithDate() {
        LocalDate today = LocalDate.now();
        LinkedList<QuantityTag> tags = new LinkedList<>();
        tags.add(new QuantityTag("aaa", "T", 100));
        ledger.addAccount(tags,new LinkedList<>(), "nothing", today);
        assertEquals(1, ledger.getSize());
        assertEquals("AAA", ledger.getAccounts(today).get(0).getItemCodes().get(0));
        assertEquals(tags.get(0).getQuantity(),
                ledger.getAccounts(today).get(0).getQuantity("aaa"));
        tags.clear();
        tags.add(new QuantityTag("CHI", "A", 20));
        ledger.addAccount(tags, new LinkedList<>(), "something", LocalDate.of(2021, 12 ,12));
        assertEquals(2, ledger.getSize());
        assertEquals("CHI",
                ledger.getAccounts(LocalDate.of(2021, 12 ,12)).get(0).getItemCodes().get(0));
        assertEquals(tags.get(0).getQuantity(),
                ledger.getAccounts(LocalDate.of(2021, 12 ,12)).get(0).getQuantity("CHI"));
        assertEquals(0, ledger.getAccounts(LocalDate.of(2030, 12, 12)).size());
    }

    @Test
    void testGetNonExistingAccount() {
        //By default, Account number in Ledger will start from 111111
        LocalDate today = LocalDate.now();
        LinkedList<QuantityTag> tags = new LinkedList<>();
        tags.add(new QuantityTag("aaa", "T", 100));
        ledger.addAccount(tags, new LinkedList<>(),"nothing", today);
        assertEquals(1, ledger.getSize());
        assertNull(ledger.getAccount(111112));
    }

    @Test
    void testJsonConversion() {
        LinkedList<QuantityTag> tags = new LinkedList<>();
        tags.add(new QuantityTag("CHI", "T", 100));
        ledger.addAccount(tags, new LinkedList<>(),"nothing", LocalDate.now());
        assertEquals(1, ledger.getSize());
        assertEquals(1, ledger.getAccounts().size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts().get(0));
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts(TODAY).get(0));
        assertEquals(null,
                ledger.addAccount(new LinkedList<QuantityTag>(),
                        new LinkedList<>(),"test", LocalDate.now()));
        ledger = new Ledger(ledger.toJson());
        assertEquals(1, ledger.getSize());
        assertEquals(1, ledger.getAccounts().size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts().get(0));
        assertEquals(1, ledger.getAccounts(TODAY).size());
        assertEquals(ledger.getAccount(111111), ledger.getAccounts(TODAY).get(0));
        assertEquals(null,
                ledger.addAccount(new LinkedList<QuantityTag>(),
                        new LinkedList<>(),"test", LocalDate.now()));
    }



}
