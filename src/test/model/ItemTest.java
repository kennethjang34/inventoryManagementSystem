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
        item.addProducts(0, listPrice, LocalDate.now(),LocalDate.now(), location, quantity);
        assertEquals(cost/2, item.getAverageCost());
        assertEquals(quantity * 2, item.getQuantity());
    }

    @Test
    void testAddProductsWithoutBestBeforeDate() {
        item.addProducts(cost, listPrice, null, LocalDate.now(), location, quantity);
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
    void testAddProductsWithInventoryTag() {
        InventoryTag tag = new InventoryTag(item.getId(), cost,
                listPrice, LocalDate.now(), location, quantity);
        item.addProducts(tag);
        assertEquals(cost, item.getAverageCost());
        assertEquals(quantity, item.getQuantity());
        for (Product product: item.getProducts()) {
            assertEquals(cost, product.getCost());
            assertEquals(listPrice, product.getPrice());
            assertEquals(location, product.getLocation());
            assertEquals(LocalDate.now(), product.getDateGenerated());
        }

        tag = new InventoryTag(item.getId(), 0,
                listPrice, LocalDate.now(), location, quantity);
        item.addProducts(tag);
        assertEquals(cost/2, item.getAverageCost());
        assertEquals(2 * quantity, item.getQuantity());

    }

    @Test
    void testRemoveProducts() {
        item.addProducts(cost, listPrice, null, LocalDate.now(),location, quantity);
        item.removeStocks(location, 1);
        assertEquals(quantity - 1, item.getQuantity());
        assertEquals(quantity - 1, item.getQuantity(location));
//        assertFalse(item.removeStocks(location, quantity));

        item.removeStocks(location, quantity - 2);
        assertEquals(1, item.getQuantity());
        assertEquals(1, item.getQuantity(location));
//        assertFalse(item.removeStocks("A", 1));

    }

    @Test
    void testConvertToTableEntry() {
       // id, name, category, listPrice, description, note)
        item.addProducts(cost, listPrice,
                LocalDate.now(), LocalDate.now(), "f11", quantity);
        Object[] tableEntry = item.convertToTableEntry();
        assertEquals(category, tableEntry[0]);
        assertEquals(id, tableEntry[1]);
        assertEquals(name, tableEntry[2]);
        assertEquals(description, tableEntry[3]);
        assertEquals(note, tableEntry[4]);
        assertEquals(quantity, tableEntry[5]);
        assertEquals(cost, tableEntry[6]);
        assertEquals(listPrice, tableEntry[7]);
    }

    @Test
    void testGetProduct() {
        item.addProducts(cost, listPrice, null, LocalDate.now(), location, quantity);
        List<Product> products = item.getProducts(location);
        assertEquals(quantity, products.size());
        for (Product p: products) {
            assertEquals(p, item.getProduct(p.getSku()));
        }
        assertEquals(0, item.getProducts("NO stock").size());
    }

    @Test
    void testSetCategory() {
        item.setCategory("New category");
        assertEquals("New category", item.getCategory());
    }

    @Test
    void testJsonConversion() {
        item.addProducts(cost, listPrice, null, LocalDate.now(), location, quantity);
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
