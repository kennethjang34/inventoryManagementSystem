package model;

import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;

//represents product. Each product will have item code, SKU (Stock keeping unit),
//date generated, best before date, and cost it was bought for.
//No data about the product can be changed after creation.
public class Product extends ViewableTableEntryConvertibleModel implements JsonConvertible {
    //item code represents the code composed only of English alphabets that indicates
    //the category of the product. Multiple products can have the same item code.
    private String id;
    //SKU is unique to each product
    private final String sku;
    private final LocalDate dateGenerated;
    //Only products given best-before date will have a valid best-before date.
    private LocalDate bestBeforeDate;
    //cost is the money paid for this product
    private double price;
    private double cost;
    private String location;

//
//    @Override
//    public List<String> getContentsOf(String property) {
//        property = property.toUpperCase();
//        List<String> contents = new ArrayList<>();
//        switch (property) {
//            case "ID":
//                contents.add(id);
//                break;
//            case "SKU":
//                contents.add(sku);
//                break;
//            case "BESTBEFOREDATE":
//                contents.add(bestBeforeDate.toString());
//            case "PRICE":
//                contents.add(String.valueOf(price));
//                break;
//            case "COST":
//                contents.add(String.valueOf(cost));
//                break;
//            case "LOCATION":
//                contents.add(location);
//        }
//        return contents;
//    }

    public enum DataList {
        ID, SKU, COST, PRICE, BEST_BEFORE_DATE, DATE_GENERATED, LOCATION
    }

    public static final String[] DATA_LIST = new String[]{
            DataList.ID.toString(), DataList.SKU.toString(), DataList.COST.toString(),
            DataList.PRICE.toString(), DataList.BEST_BEFORE_DATE.toString(),
            DataList.DATE_GENERATED.toString(), DataList.LOCATION.toString()
    };


    //It is possible for date generated and best before date to be null,
    //However, it is strongly recommended to ensure date generated is a valid Local Date instance.
    //REQUIRES: sku must be 9-digit natural number, cost must be positive.
    //EFFECTS: create a product with specified data.
    public Product(String id, String sku, double cost, double price,
                   LocalDate dateGenerated, LocalDate bestBeforeDate, String location) {
        super(DATA_LIST);
        id = id.toUpperCase();
        this.id = id;
        this.sku = sku;
        this.cost = cost;
        this.price = price;
        this.dateGenerated = dateGenerated;
        this.bestBeforeDate = bestBeforeDate;
        this.location = location;
    }

    //REQUIRES: data in JSONObject format must contain all the information necessary for creating a new product
    //EFFECTS: create a new product with data in JSON format
    public Product(JSONObject json) {
        super(DATA_LIST);
        id = json.getString("id");
        sku = json.getString("sku");
        price = json.getDouble("price");
        cost = json.getDouble("cost");
        JSONObject jsonDate = json.getJSONObject("dateGenerated");
        dateGenerated = LocalDate.of(jsonDate.getInt("year"),
                jsonDate.getInt("month"), jsonDate.getInt("day"));
        if (json.get("bestBeforeDate").equals(JSONObject.NULL)) {
            bestBeforeDate = null;
        } else {
            jsonDate = json.getJSONObject("bestBeforeDate");
            bestBeforeDate = LocalDate.of(jsonDate.getInt("year"),
                    jsonDate.getInt("month"), jsonDate.getInt("day"));
        }
        location = json.getString("location");
    }



    //EFFECTS: return the string item code of this product
    public String getId() {
        return id;
    }


    //EFFECTS: return the stock keeping unit(SKU) of this product
    public String getSku() {
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
    public double getCost() {
        return cost;
    }

    //EFFECTS: return the default price set for this product
    public double getPrice() {
        return price;
    }

    //MODIFIES: this
    //EFFECTS: set the price of this product to the given value.
    //If the given price is negative, throw an exception
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (price == this.price) {
            return;
        }
        double oldPrice = this.price;
        this.price = price;
        changeFirer.fireUpdateEvent(this, DataList.PRICE.toString(), oldPrice, price);
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative");
        }
        if (cost == this.cost) {
            return;
        }
        double oldCost = this.cost;
        this.cost = cost;
        changeFirer.fireUpdateEvent(this, DataList.COST.toString(), oldCost, cost);
    }

    public void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be left empty");
        }
        if (id.equals(this.id)) {
            return;
        }
        String oldId = this.id;
        this.id = id;
        changeFirer.fireUpdateEvent(this, DataList.ID.toString(), oldId, id);
    }


    public void setBestBeforeDate(LocalDate date) {
        if (date.equals(this.bestBeforeDate)) {
            return;
        }
        LocalDate oldDate = this.bestBeforeDate;
        this.bestBeforeDate = date;
        changeFirer.fireUpdateEvent(this, DataList.BEST_BEFORE_DATE.toString(), oldDate, date);
    }


    //EFFECTS: return the location of this product
    public String getLocation() {
        return location;
    }


    //MODIFIES: this
    //EFFECTS: change the location of this product
    public void setLocation(String location) {
        if (location.equals(this.location)) {
            return;
        }
        String oldLocation = this.location;
        this.location = location;
        changeFirer.fireUpdateEvent(this, DataList.LOCATION.toString(), oldLocation, location);
    }



    //EFFECTS: convert this to JSONObject and return it.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("sku", sku);
        json.put("cost", cost);
        json.put("price", price);
        JSONObject jsonDate = new JSONObject();
        jsonDate.put("year", dateGenerated.getYear());
        jsonDate.put("month", dateGenerated.getMonthValue());
        jsonDate.put("day", dateGenerated.getDayOfMonth());
        json.put("dateGenerated", jsonDate);
        if (bestBeforeDate == null) {
            json.put("bestBeforeDate", JSONObject.NULL);
        } else {
            jsonDate = new JSONObject();
            jsonDate.put("year", bestBeforeDate.getYear());
            jsonDate.put("month", bestBeforeDate.getMonthValue());
            jsonDate.put("day", bestBeforeDate.getDayOfMonth());
            json.put("bestBeforeDate", jsonDate);
        }
        json.put("location", location);
        return json;
    }



    @Override
    public Object[] convertToTableEntry() {
        Object[] entry = new Object[7];
        entry[0] = id;
        entry[1] = sku;
        entry[2] = cost;
        entry[3] = price;
        entry[4] = bestBeforeDate == null ? "N/A" : bestBeforeDate.toString();
        entry[5] = dateGenerated.toString();
        entry[6] = location;
        return entry;
    }


}


