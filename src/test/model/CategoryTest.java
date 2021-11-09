package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {
    Category category;
    int quantity = 100;
    List<Item> items = new ArrayList<>();
    String name = "Fruit";
    double cost = 50.1;
    double price = 70.95;

    @BeforeAll
    void runBeforeAll() {
        items.add(new Item("APP54656", "apple", new Category("fruit"), price, "description","note"));
        items.add(new Item("BNN599A", "banana", new Category("fruit"), price * 2, "description","note"));
        items.add(new Item("CHIADFS", "strawberry", new Category("fruit"), price * 2, "description","note"));
        items.add(new Item("WEEWQW", "mango", new Category("fruit"), price, "description","note"));
    }

    @BeforeEach
    void runBefore() {
        category = new Category(name);
        for (Item item: items) {
            category.addItem(item);
        }
    }

    @Test
    void testConstructor() {
        category = new Category(name);
        assertEquals(name, category.getName());
        assertEquals(0, category.getTotalQuantity());
        assertEquals(0, category.getItems().size());
        assertFalse(category.contains("chicken"));
    }

    @Test
    void testAddItem() {
        category = new Category(name);
        for (Item item: items) {
            category.addItem(item);
        }
        assertEquals(name, category.getName());
        assertEquals(4, category.getNumberOfItems());
        assertEquals(4, category.getItems().size());
        assertTrue(category.contains("apple"));
        assertTrue(category.contains("banana"));
        assertTrue(category.contains("strawBerry"));
        assertTrue(category.contains("MANGO"));
        for (int i = 0; i < items.size(); i++) {
            assertEquals(items.get(i), category.getItems().get(i));
        }
    }


    @Test
    void testRemoveItem() {
        category = new Category(name);
        for (Item item: items) {
            category.addItem(item);
        }
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String name = item.getName();
            assertTrue(category.contains(name));
            category.removeItem(name);
            assertFalse(category.contains(name));
            if (item.getCategory().equals(category)) {
                fail();
            }
        }
    }





}
