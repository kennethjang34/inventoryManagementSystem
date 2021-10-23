package persistence;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
//This interface was modelled after JsonSerializationDemo
//a sample application for using JSON package provided by UBC Computer science Faculty
//for CPSC 210 Students (2021/10/19).

public interface JsonConvertable {

    default JSONArray convertToJsonArray(List<? extends JsonConvertable> list) {
        org.json.JSONArray jsons = new JSONArray();
        for (JsonConvertable convertable: list) {
            if (convertable == null) {
                jsons.put("null");
            } else {
                jsons.put(convertable.toJson());
            }
        }
        return jsons;
    }

    default JSONObject convertMapToJson(Map<String, ? extends JsonConvertable> map) {
        JSONObject json = new JSONObject();
        for (Map.Entry<String, ? extends JsonConvertable> entry: map.entrySet()) {
            json.put(entry.getKey(), entry.getValue().toJson());
        }
        return json;
    }

    JSONObject toJson();
}
