package persistence;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Writer {
    String fileName;
    PrintWriter writer;


    public  Writer(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        writer = new PrintWriter(fileName);
    }

    public void write(JsonConvertable obj) {
        JSONObject jsonObject = obj.toJson();
        writer.print(jsonObject);
    }

    public void close() {
        writer.close();
    }

}
