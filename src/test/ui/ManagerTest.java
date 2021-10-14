package ui;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class ManagerTest {
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
        String description = "TestDescription";
        assertTrue(manager.updateInventory(description));
        //ArrayList<Account> ledger = manager.getLedger();
        //assertEquals(1, ledger.size());
        //assertEquals(1, ledger.get(0).getEntries().size());
        //assertEquals("APP", (String)(ledger.get(0).getEntries().get(0)[0]));
        //assertEquals(description, ledger.get(0).getDescription());
    }



    @Test
    void testFindLocation() {
        Product product = manager.createProduct("app", bestBeforeDate, cost, location);
        String description = "TestDescription";
        assertTrue(manager.updateInventory(description));
    }




}
