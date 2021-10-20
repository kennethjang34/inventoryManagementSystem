package persistence;

import org.json.JSONObject;
//This interface was modelled after JsonSerializationDemo
//a sample application for using JSON package provided by UBC Computer science Faculty
//for CPSC 210 Students (2021/10/19).

public interface JsonConvertable {
    JSONObject toJson();
}
