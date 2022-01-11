package persistence;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

//represents a writer
public class Writer {
    String fileName;
    PrintWriter writer;
    //this field is used to format JSONObject.toString()
    static final int indentFactorJson = 4;

    //REQUIRES: there must be a file existing with the given file name
    //EFFECTS:create a new writer with the given file name.
    public  Writer(String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        writer = new PrintWriter(fileName);
    }

    //MODIFIES: this
    //EFFECTS: convert the given JsonConvertible object to JSONObject and write it in the file
    //with the pre-set indent factor.
    public void write(JsonConvertible obj) {
        JSONObject jsonObject = obj.toJson();
        //This line of code was cited from JsonSerializationDemo
        //Link: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
        writer.print(jsonObject.toString(indentFactorJson));
    }

    public void write(JSONObject jsonObject) {
        writer.print(jsonObject.toString(indentFactorJson));
    }

    //MODIFIES: this
    //EFFECTS: save and close the file.
    public void close() {
        writer.close();
    }

}
