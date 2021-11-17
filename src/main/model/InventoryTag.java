package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//tag used to add products to the inventory as opposed to Quantity tags, which are used to remove products
public class InventoryTag {
    private final String id;
    private int quantity;
    private final String location;
    private final double unitCost;
    private double unitPrice;
    private final LocalDate bestBeforeDate;
    private final LocalDate dateGenerated;

    //EFFECTS: create a new tag that contains all information needed for creating a product with its assigned location
    public InventoryTag(String itemCode, double cost, double price, LocalDate dateGenerated,
                        LocalDate bestBeforeDate, String location, int quantity) {
        this.id = itemCode.toUpperCase();
        this.quantity = quantity;
        this.unitCost = cost;
        this.unitPrice = price;
        this.location = location;
        this.bestBeforeDate = bestBeforeDate;
        this.dateGenerated = dateGenerated;
    }

    //EFFECTS: create a new tag that contains all information needed for creating a product with its assigned location
    //that's without best before date
    public InventoryTag(String itemCode, double cost, double price,
                        LocalDate dateGenerated, String location, int quantity) {
        this.id = itemCode.toUpperCase();
        this.quantity = quantity;
        this.unitCost = cost;
        this.unitPrice = price;
        this.location = location;
        this.bestBeforeDate = null;
        this.dateGenerated = dateGenerated;
    }

    public static List<InventoryTag> createTagsForRemoved(List<Product> products) {
        List<InventoryTag> tags = new ArrayList<>();
        Map<String, List<Product>> productLists = createIdMap(products);
        for (Map.Entry<String, List<Product>> entry: productLists.entrySet()) {
            String id = entry.getKey();
            Map<String, List<Product>> productsWithLocation = createLocationMap(entry.getValue());
            for (Map.Entry<String, List<Product>> location: productsWithLocation.entrySet()) {
                double cost = calculateAverageCost(location.getValue());
                double price = calculateAveragePrice(location.getValue());
                int qty = -location.getValue().size();
                assert location.getValue().size() > 0;
                LocalDate dateGenerated = location.getValue().get(0).getDateGenerated();
                tags.add(new InventoryTag(id, cost, price,
                        dateGenerated, null, location.getKey(), qty));

            }
        }
        return tags;
    }

    private static double calculateAveragePrice(List<Product> products) {
        double sum = 0;
        for (Product product: products) {
            sum += product.getPrice();
        }
        return sum / products.size();
    }

    private static double calculateAverageCost(List<Product> products) {
        double sum = 0;
        for (Product product: products) {
            sum += product.getCost();
        }
        return sum / products.size();
    }

    private static Map<String, List<Product>> createLocationMap(List<Product> products) {
        Map<String, List<Product>> locationMap = new HashMap<>();
        for (Product product: products) {
            List<Product> list;
            if (!locationMap.containsKey(product.getLocation())) {
                list = new ArrayList<>();
                list.add(product);
                locationMap.put(product.getLocation(), list);
            } else {
                list = locationMap.get(product.getLocation());
                list.add(product);
            }
        }
        return locationMap;
    }

    private static Map<String, List<Product>> createIdMap(List<Product> products) {
        Map<String, List<Product>> productLists = new HashMap<>();
        for (Product product: products) {
            if (!productLists.containsKey(product.getId())) {
                List<Product> newList = new ArrayList<>();
                newList.add(product);
                productLists.put(product.getId(), newList);
            } else {
                List<Product> existing = productLists.get(product.getId());
                existing.add(product);
            }
        }
        return productLists;
    }

    //EFFECTS: return the date generated
    public LocalDate getDateGenerated() {
        return dateGenerated;
    }

    //MODIFIES: this
    //EFFECTS: change the unit price of this product


    public void setUnitPrice(double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.unitPrice = unitPrice;
    }



    //EFFECTS: return the unit price set for the new products
    public double getUnitPrice() {
        return unitPrice;
    }

    //EFFECTS: return location assigned of the product
    public String getLocation() {
        return location;
    }

    //EFFECTS: return best-before date of the product
    public LocalDate getBestBeforeDate() {
        return bestBeforeDate;
    }

    //EFFECTS: return item code of the product
    public String getId() {
        return id;
    }

    //EFFECTS: return quantity specified by the tag
    public int getQuantity() {
        return quantity;
    }

    //EFFECTS:return price of the product
    public double getUnitCost() {
        return unitCost;
    }

    //MODIFIES: this
    //EFFECTS: set the quantity to the given number
    public void setQuantity(int qty) {

        quantity = qty;
    }


//    //EFFECTS: return true if the given object is equal to this
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof InventoryTag)) {
//            return false;
//        }
//        InventoryTag tag = (InventoryTag)o;
//        if (tag.getId().equalsIgnoreCase(id) && tag.getLocation().equalsIgnoreCase(location)
//                 && tag.getUnitCost() == unitCost) {
//            if (bestBeforeDate == null && tag.getBestBeforeDate() == null) {
//                return true;
//            } else if (bestBeforeDate != null && tag.getBestBeforeDate() != null) {
//                return bestBeforeDate.equals(tag.getBestBeforeDate());
//            }
//            return false;
//        }
//        return false;
//    }
//
//    //EFFECTS: return the hashcode of this
//    @Override
//    public int hashCode() {
//        return id.hashCode();
//    }
}
