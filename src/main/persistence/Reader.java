package persistence;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Reader {
    private String location;

    public Reader(String location) {
        this.location = location;
    }
    //from workroom example


    public JSONObject read() throws IOException {
        String managerInfo = readFile(location);
        JSONObject jsonObject = new JSONObject(managerInfo);
        return jsonObject;
    }

    //from workroom example
    private String readFile(String location) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(location), StandardCharsets.UTF_8)) {
            stream.forEach(s -> stringBuilder.append(s));
        }
        return stringBuilder.toString();
    }

}
