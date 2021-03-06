package persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
//This interface was modelled after JsonSerializationDemo
//a sample application for using JSON package provided by UBC Computer science Faculty
//for CPSC 210 Students (2021/10/19).

//An interface that should be implemented by classes that are JSONObject convertible
public interface JsonConvertible {

    //EFFECTS: convert JsonConvertible objects in the list into JSONObject and return newly built list
    default JSONArray convertToJsonArray(List<? extends JsonConvertible> list) {
        org.json.JSONArray jsons = new JSONArray();
        for (JsonConvertible convertable: list) {
            jsons.put(convertable.toJson());

        }
        return jsons;
    }


    //EFFECTS: convert to JSONObject and return it
    JSONObject toJson();
}
