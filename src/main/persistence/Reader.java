package persistence;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

//represents a file reader that reads JSON-format files
public class Reader {
    //file location
    private String location;

    //EFFECTS:create a new reader with the given file location
    public Reader(String location) {
        this.location = location;
    }
    //from workroom example

    //This method references code from JsonSerializationDemo
    //Link: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
    //REQUIRES: the file at the given file location must contain data in valid JSONObject format
    //EFFECTS: read the file and turn it into the JSONObject.
    public JSONObject read() throws IOException {
        String managerInfo = readFile(location);
        JSONObject jsonObject = new JSONObject(managerInfo);
        return jsonObject;
    }

    //This method references code from JsonSerializationDemo
    //Link: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo.git
    //REQUIRES: file at the file location must contain valid data in text format
    //EFFECTS: read the file in text format and convert all lines in the file into one string and return it
    private String readFile(String location) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(location), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s));
        }
        return stringBuilder.toString();
    }

}
