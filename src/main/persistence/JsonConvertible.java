package persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//This interface was modelled after JsonSerializationDemo
//a sample application for using JSON package provided by UBC Computer science Faculty
//for CPSC 210 Students (2021/10/19).


public interface JsonConvertible {

    //EFFECTS: convert JsonConvertible objects in the list into JSONObject and return newly built list
    default JSONArray convertToJsonArray(List<? extends JsonConvertible> list) {
        org.json.JSONArray jsons = new JSONArray();
        for (JsonConvertible convertable: list) {
            if (convertable == null) {
                jsons.put(JSONObject.NULL);
            } else {
                jsons.put(convertable.toJson());
            }
        }
        return jsons;
    }

    //EFFECTS: return a list form of maps
    public static <K, V> List<V> convertToList(Map<K, V> map) {
        return new ArrayList<>(map.values());
    }

    //EFFECTS: convert to JSONObject and return it
    JSONObject toJson();
}
