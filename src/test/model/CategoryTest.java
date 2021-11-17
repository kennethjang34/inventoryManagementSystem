package model;

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

//    @BeforeAll
//    void runBeforeAll() {
//        items.add(new Item("APP54656", "apple", "Fruit", price, "description","note"));
//        items.add(new Item("BNN599A", "banana", "Fruit", price * 2, "description","note"));
//        items.add(new Item("CHIADFS", "strawberry", "Fruit", price * 2, "description","note"));
//        items.add(new Item("WEEWQW", "mango", "Fruit", price, "description","note"));
//    }

    @BeforeEach
    void runBefore() {
        category = new Category(name);
        items.add(new Item("APP54656", "apple", "Fruit", price, "description","note"));
        items.add(new Item("BNN599A", "banana", "Fruit", price * 2, "description","note"));
        items.add(new Item("CHIADFS", "strawberry", "Fruit", price * 2, "description","note"));
        items.add(new Item("WEEWQW", "mango", "Fruit", price, "description","note"));
        for (Item item: items) {
            category.addItem(item);
        }
    }

    @Test
    void testConstructor() {
        category = new Category(name);
        assertEquals(name, category.getName());
        assertEquals(0, category.getTotalQuantity());
        assertEquals(0, category.getItemIDs().size());
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
        assertEquals(4, category.getItemIDs().size());
        assertTrue(category.contains("APP54656"));
        assertTrue(category.contains("BNN599A"));
        assertTrue(category.contains("CHIADFS"));
        assertTrue(category.contains("WEEWQW"));

    }


    @Test
    void testRemoveItem() {
        category = new Category(name);
        for (Item item: items) {
            category.addItem(item);
        }
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String id = item.getId();
            assertTrue(category.contains(id));
            category.removeItem(id);
            assertFalse(category.contains(id));
            if (item.getCategory().equals(category)) {
                fail();
            }
        }
    }




}
