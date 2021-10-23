package persistence;

import model.Inventory;
import model.InventoryTag;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class WriterTest {
    private final String location = "./data/writer_test.json";

    @Test
    void testConstructorWithValidFileLocation() {
        try {
            Writer writer = new Writer(location);
        } catch (FileNotFoundException e) {
            //There shouldn't be any exception thrown.
            fail();
        }
    }

    @Test
    void testConstructorWithInvalidFileLocation() {
        try {
            Writer writer = new Writer("adsf" + location);
            //Exception is expected
            fail();
        } catch (FileNotFoundException e) {
        }
    }

    @Test
    void testWriteInventory() {
        Writer writer = null;
        try {
            writer = new Writer(location);
        } catch (FileNotFoundException e) {
            fail();
        }
        ArrayList<InventoryTag> tags = new ArrayList<>();
        tags.add(new InventoryTag("abc", 12, "t", 30));
        tags.add(new InventoryTag("bnn", 13,  LocalDate.now(), "f11", 100));
        Inventory inventory = new Inventory();
        inventory.addProducts(tags);
        assertEquals(130, inventory.getTotalQuantity());
        writer.write(inventory);
        writer.close();
        Reader reader = new Reader(location);
        JSONObject jsonInventory = null;
        try {
            jsonInventory = reader.read();
        } catch (IOException e) {
            fail();
        }
        Inventory fromJson = new Inventory(jsonInventory);
        assertEquals(130, fromJson.getTotalQuantity());
        assertEquals(30, inventory.getQuantity("abc"));
        assertEquals(inventory.getQuantity("abc"), fromJson.getQuantity("abc"));
        assertEquals(100, inventory.getQuantity("bnn"));
        assertEquals(inventory.getQuantity("bnn"), fromJson.getQuantity("bnn"));
    }
}
