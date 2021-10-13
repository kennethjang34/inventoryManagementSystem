package test;

import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductTester {
    private String itemCode;
    private int sku;
    private LocalDate today;
    private LocalDate bestBeforeDate;
    private double cost;

    @BeforeEach
    void runBefore() {
        itemCode = "ABE";
        sku = 111111111;
        today = LocalDate.now();
        bestBeforeDate = LocalDate.of(2021, 11, 20);
        cost = 1005.20;
    }

    @Test
    void testConstructor() {
        Product product = new Product(itemCode, sku, cost, today, bestBeforeDate);
        assertEquals(itemCode, product.getItemCode());
        assertEquals(sku, product.getSku());
        assertEquals(today, product.getDateGenerated());
        assertEquals(bestBeforeDate, product.getBestBeforeDate());
        Product product2 = new Product(itemCode, sku, cost, today, null);
        assertEquals(itemCode, product2.getItemCode());
        assertEquals(sku, product2.getSku());
        assertEquals(today, product2.getDateGenerated());
        assertNull(product2.getBestBeforeDate());
    }
}
