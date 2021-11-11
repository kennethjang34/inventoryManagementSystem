package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProductTest {
    private String id;
    private String sku;
    private LocalDate today;
    private LocalDate bestBeforeDate;
    private double cost;
    private double price;

    @BeforeEach
    void runBefore() {
        id = "ABE";
        sku = "111111111";
        today = LocalDate.now();
        bestBeforeDate = LocalDate.of(2021, 11, 20);
        cost = 1005.20;
        price = 2000;
    }
    //test
    @Test
    void testConstructor() {
        Product product = new Product(id, sku, cost, price, today, bestBeforeDate, "a");
        assertEquals(id, product.getId());
        assertEquals(sku, product.getSku());
        assertEquals(today, product.getDateGenerated());
        assertEquals(bestBeforeDate, product.getBestBeforeDate());
        assertEquals(cost, product.getCost());
        Product product2 = new Product(id, sku, cost, price, today, null, "a");
        assertEquals(id, product2.getId());
        assertEquals(sku, product2.getSku());
        assertEquals(today, product2.getDateGenerated());
        assertEquals(cost, product2.getCost());
        assertEquals(price, product2.getPrice());
        assertNull(product2.getBestBeforeDate());
    }

    @Test
    void testJsonConversion() {
        Product product = new Product(id, sku, cost, price, today, bestBeforeDate, "a");
        assertEquals(id, product.getId());
        assertEquals(sku, product.getSku());
        assertEquals(today, product.getDateGenerated());
        assertEquals(bestBeforeDate, product.getBestBeforeDate());
        assertEquals(price, product.getPrice());
        assertEquals(cost, product.getCost());
        product = new Product(product.toJson());
        assertEquals(id, product.getId());
        assertEquals(sku, product.getSku());
        assertEquals(today, product.getDateGenerated());
        assertEquals(bestBeforeDate, product.getBestBeforeDate());
        assertEquals(cost, product.getCost());
        assertEquals(price, product.getPrice());
        Product product2 = new Product(id, sku, cost, price, today, null, "a");
        assertEquals(id, product2.getId());
        assertEquals(sku, product2.getSku());
        assertEquals(today, product2.getDateGenerated());
        assertEquals(cost, product2.getCost());
        assertEquals(price, product2.getPrice());
        assertNull(product2.getBestBeforeDate());
        product2 = new Product(product2.toJson());
        assertEquals(id, product2.getId());
        assertEquals(sku, product2.getSku());
        assertEquals(today, product2.getDateGenerated());
        assertEquals(price, product2.getPrice());
        assertEquals(cost, product2.getCost());
        assertNull(product2.getBestBeforeDate());
    }
}
