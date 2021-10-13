package ui;

import model.Account;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagerTester {
    private Manager manager;
    private Scanner scanner;
    private LocalDate today;
    private LocalDate bestBeforeDate;
    private double cost;
    private String location;

    @BeforeEach
    void runBefore() {
        manager = new Manager();
        scanner = new Scanner(System.in);
        today = LocalDate.now();
        bestBeforeDate = LocalDate.of(2021, 11, 15);
        cost = 100.52;
        location = "F11";
    }

    @Test
    void testCreateProduct() {
        Product product = manager.createProduct("app", bestBeforeDate, cost, location);
        ArrayList<Object[]> list = manager.getTemporaryList();
        assertEquals(product, list.get(0)[0]);
        assertEquals(location, list.get(0)[1]);
    }

    @Test
    void testUpdate() {
        Product product = manager.createProduct("app", bestBeforeDate, cost, location);
        manager.updateInventory();
        assertEquals(1, manager.countProduct(product.getItemCode()));
    }

    @Test
    void testUpdateLedger() {
        Product product = manager.createProduct("app", bestBeforeDate, cost, location);
        assertTrue(manager.updateInventory());
        ArrayList<Account> ledger = manager.getLedger();
        assertEquals(1, ledger.size());
        assertEquals(1, ledger.get(0).getEntries().size());
        assertEquals("APP", (String)(ledger.get(0).getEntries().get(0)[0]));
    }


}
