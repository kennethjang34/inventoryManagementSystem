package model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class InventoryTagTest {


    @Test
    void testSetUnitPrice() {
        InventoryTag tag = new InventoryTag("AAA", 50,
                60, LocalDate.now(),"T", 100);
        assertEquals(60, tag.getUnitPrice());
        assertEquals(100, tag.getQuantity());
        tag.setUnitPrice(100);
        assertEquals(100, tag.getUnitPrice());
        tag.setQuantity(1000);
        assertEquals(1000, tag.getQuantity());
        try {
            tag.setUnitPrice(-1);
            fail();
        } catch(IllegalArgumentException e) {

        }
    }


    @Test
    void testCreateTagsForRemovedFromRemovedProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product("APP", "111",
                20, 30, null, LocalDate.now(),"F11"));
        products.add(new Product("APP", "1511",
                20, 30, null, LocalDate.now(),"F#"));
        products.add(new Product("APP", "412",
                20, 30, null, LocalDate.now(),"F11"));
        products.add(new Product("EEW", "123",
                20, 30, null, LocalDate.now(),"F!!"));
        products.add(new Product("EEW", "324",
                20, 30, null, LocalDate.now(),"F11"));
        List<InventoryTag> tags = InventoryTag.createTagsForRemoved(products);
        assertEquals(4, tags.size());
        for (int i = 0 ; i < tags.size(); i++) {
            InventoryTag tag = tags.get(i);
            Product product = products.get(i);
            if (tag.getId().equals("APP") && tag.getLocation().equalsIgnoreCase("F11")) {
                assertEquals(-2, tag.getQuantity());
            } else {
                assertEquals(-1, tag.getQuantity());
            }
            if (i <= 1) {
                assertEquals("APP", tag.getId());
            } else {
                assertEquals("EEW", tag.getId());
            }
//            assertEquals(product.getId(), tag.getId());

        }
    }
}
