package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuantityTagTest {
    QuantityTag tag;
    int qty;
    String location;
    String itemCode;

    @BeforeEach
    void runBefore() {
        qty = 100;
        location = "A00";
        itemCode = "AAA";
    }

    @Test
    void testConstructor() {
        tag = new QuantityTag(itemCode, location, qty);
        assertEquals(itemCode, tag.getId());
        assertEquals(location, tag.getLocation());
        assertEquals(qty, tag.getQuantity());
        assertEquals("Item Code: " + itemCode + ", Location: " + location + ", Quantity: " + qty,
                tag.toString());
    }
}
