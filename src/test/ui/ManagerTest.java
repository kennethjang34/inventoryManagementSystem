package ui;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class ManagerTest {
    private Manager manager;
    private LocalDate today;
    private LocalDate bestBeforeDate;
    private double cost;
    private String location;

    @BeforeEach
    void runBefore() {
        manager = new Manager();
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
    }



    @Test
    void testFindLocation() {
        manager.createProduct("app", bestBeforeDate, cost, location);
        String description = "TestDescription";
        assertTrue(manager.updateInventory(description));
    }




}
