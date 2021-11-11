package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {


    Item item;
    String description = "This is a description for the test";
    String note = "This is a special note for the test";
    String name = "T";
    String category = "Test";
    String id = "T1000111";
    String location = "F11";
    int quantity = 1000;
    double cost = 50;
    double listPrice = 100;

    @BeforeEach
    void runBefore() {
        item = new Item(id, name, category, listPrice, description, note);
    }


    @Test
    void testConstructor() {
        Item item = new Item(id, name, category, listPrice, description, note);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(category, item.getCategory());
        assertEquals(listPrice, item.getListPrice());
        assertEquals(description, item.getDescription());
        assertEquals(note, item.getNote());
    }

    @Test
    void testAddProducts() {
        item.addProducts(cost, listPrice, LocalDate.now(),LocalDate.now(), location, quantity);
        assertEquals(cost, item.getAverageCost());
        assertEquals(quantity, item.getQuantity());
        for (Product product: item.getProducts()) {
            assertEquals(cost, product.getCost());
            assertEquals(listPrice, product.getPrice());
            assertEquals(LocalDate.now(), product.getBestBeforeDate());
            assertEquals(location, product.getLocation());
            assertEquals(LocalDate.now(), product.getDateGenerated());
        }
    }

    @Test
    void testAddProductsWithoutBestBeforeDate() {
        item.addProducts(id, cost, listPrice, LocalDate.now(), location, quantity);
        assertEquals(cost, item.getAverageCost());
        assertEquals(quantity, item.getQuantity());
        for (Product product: item.getProducts()) {
            assertEquals(cost, product.getCost());
            assertEquals(listPrice, product.getPrice());
            assertEquals(location, product.getLocation());
            assertEquals(LocalDate.now(), product.getDateGenerated());
        }
    }

    @Test
    void testRemoveProducts() {
        item.addProducts(id, cost, listPrice, LocalDate.now(),location, quantity);
        item.removeStocks(location, 1);
        assertEquals(quantity - 1, item.getQuantity());
        assertEquals(quantity - 1, item.getQuantity(location));
        assertFalse(item.removeStocks(location, quantity));


        item.removeStocks(location, quantity - 2);
        assertEquals(1, item.getQuantity());
        assertEquals(1, item.getQuantity(location));
        assertFalse(item.removeStocks("A", 1));

    }

    @Test
    void testGetProduct() {
        item.addProducts(id, cost, listPrice, LocalDate.now(), location, quantity);
        List<Product> products = item.getProducts(location);
        assertEquals(quantity, products.size());
        for (Product p: products) {
            assertEquals(p, item.getProduct(p.getSku()));
        }
    }

    @Test
    void testJsonConversion() {
        item.addProducts(id, cost, listPrice, LocalDate.now(), location, quantity);
        List<Product> products = item.getProducts(location);
        assertEquals(quantity, products.size());
        for (Product p: products) {
            assertEquals(p, item.getProduct(p.getSku()));
        }
        item = new Item(item.toJson());
        //assertEquals(products.size(), item.getQuantity());
        for (Product p: products) {
            assertEquals(p.getSku(), item.getProduct(p.getSku()).getSku());
        }
    }

}