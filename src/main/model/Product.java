package model;

import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;

//represents product. Each product will have item code, SKU (Stock keeping unit),
//date generated, best before date, and cost it was bought for.
//No data about the product can be changed after creation.
public class Product implements JsonConvertible {
    //item code represents the code composed only of English alphabets that indicates
    //the category of the product. Multiple products can have the same item code.
    private final String itemCode;
    //SKU is unique to each product
    private final int sku;
    private final LocalDate dateGenerated;
    //Only products given best-before date will have a valid best-before date.
    private final LocalDate bestBeforeDate;
    //cost is the money paid for this product
    private final double price;


    //It is possible for date generated and best before date to be null,
    //However, it is strongly recommended to ensure date generated is a valid Local Date instance.
    //REQUIRES: sku must be 9-digit natural number, cost must be positive.
    //EFFECTS: create a product with specified data.
    public Product(String itemCode, int sku, double price, LocalDate dateGenerated, LocalDate bestBeforeDate) {
        itemCode = itemCode.toUpperCase();
        this.itemCode = itemCode;
        this.sku = sku;
        this.price = price;
        this.dateGenerated = dateGenerated;
        this.bestBeforeDate = bestBeforeDate;
    }

    //REQUIRES: data in JSONObject format must contain all the information necessary for creating a new product
    //EFFECTS: create a new product with data in JSON format
    public Product(JSONObject json) {
        itemCode = json.getString("itemCode");
        sku = json.getInt("sku");
        price = json.getDouble("price");
        JSONObject jsonDate = json.getJSONObject("dateGenerated");
        dateGenerated = LocalDate.of(jsonDate.getInt("year"),
                jsonDate.getInt("month"), jsonDate.getInt("day"));
        if (json.get("bestBeforeDate").toString().equalsIgnoreCase("null")) {
            bestBeforeDate = null;
        } else {
            jsonDate = json.getJSONObject("bestBeforeDate");
            bestBeforeDate = LocalDate.of(jsonDate.getInt("year"),
                    jsonDate.getInt("month"), jsonDate.getInt("day"));
        }
    }

    //EFFECTS: return the string item code of this product
    public String getItemCode() {
        return itemCode;
    }

    //EFFECTS: return the stock keeping unit(SKU) of this product
    public int getSku() {
        return sku;
    }

    //EFFECTS: return the date the product was created
    public LocalDate getDateGenerated() {
        return dateGenerated;
    }

    //EFFECTS: return the best-before-date of this product. if the best-before-date wasn't set, return null.
    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }

    //EFFECTS: return the cost paid for this product
    public double getPrice() {
        return price;
    }

    //EFFECTS: convert this to JSONObject and return it.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("itemCode", itemCode);
        json.put("sku", sku);
        json.put("price", price);
        JSONObject jsonDate = new JSONObject();
        jsonDate.put("year", dateGenerated.getYear());
        jsonDate.put("month", dateGenerated.getMonthValue());
        jsonDate.put("day", dateGenerated.getDayOfMonth());
        json.put("dateGenerated", jsonDate);
        if (bestBeforeDate == null) {
            json.put("bestBeforeDate", "null");
        } else {
            jsonDate = new JSONObject();
            jsonDate.put("year", bestBeforeDate.getYear());
            jsonDate.put("month", bestBeforeDate.getMonthValue());
            jsonDate.put("day", bestBeforeDate.getDayOfMonth());
            json.put("bestBeforeDate", jsonDate);
        }
        return json;
    }
}


