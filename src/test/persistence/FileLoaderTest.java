package persistence;

import model.Product;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class FileLoaderTest {
    private final String location = "./data/reader_test.json";

    @Test
    void testReadNonExistingFile() {
        FileLoader fileLoader = new FileLoader("./data/non_existing.json");
        try {
            JSONObject json = fileLoader.load();
            //reader should throw IOException as there is no file to read
            fail();
        } catch (JSONException e) {
            fail();
        } catch (IOException e) {
        }
    }

    @Test
    void testReadEmptyFile() {
        Writer writer = null;
        try {
            writer = new Writer(location);
        } catch (FileNotFoundException e) {
            //Writer is supposed to create a new file with this location
            fail();
        }
        writer.close();
        FileLoader fileLoader = new FileLoader(location);
        try {
            JSONObject json = fileLoader.load();
            //reader should throw JSONException as JSONObject cannot be made with empty text
            fail();
        } catch (JSONException e) {
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testReadJson() {
        Writer writer = null;
        try {
            writer = new Writer(location);
        } catch (FileNotFoundException e) {
            //writer is supposed to create the file on its own when there is no such file.
            fail();
        }
        Product product = new Product("abc", "abc11119", 20.1, 30,
                LocalDate.now(),  LocalDate.now(), "f11", "");
        writer.write(product);
        writer.close();
        FileLoader fileLoader = new FileLoader(location);
        JSONObject jsonProduct = null;
        //NO exception is required.
        try {
            jsonProduct = fileLoader.load();
        } catch (IOException e) {
            fail();
        } catch (JSONException e) {
            fail();
        }
        Product fromJson = new Product (jsonProduct);
        assertEquals(product.getID(), fromJson.getID());
        assertEquals(product.getSku(), fromJson.getSku());
        assertEquals(product.getBestBeforeDate(), fromJson.getBestBeforeDate());
        assertEquals(product.getDateGenerated(), fromJson.getDateGenerated());
        assertEquals(product.getPrice(), fromJson.getPrice());
    }
}
