package model;


import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class Inventory implements JsonConvertible {
    private final int codeSize;
    //hash map that has numeric item codes as its keys and array lists of products as its value.

    //hash map that has numeric location codes as its keys and array lists of products as its value.
    //Default code size will be set to 3.
    //index 0 represents the temporary storage room, where products without location tag will be stored.
    //For UI, A0 and T will both be referring to the temporary storage room.
    private final ArrayList<ItemList> locations;
    private final ArrayList<Integer> quantities;
    //hashmap where key is a numeric product code
    //and the value is an array of numeric location code.
    private final int numberOfSections;
    private static final int NUM_ALPHABETS = 26;
    private int quantity;
    private int nextSKU;
    private int skuSize;
    private LocalDate currentDate;


    //list of items, each of which is again a list of products.
    public class ItemList implements JsonConvertible {
        ArrayList<LinkedList<Product>> items;

        //EFFECTS: create a new empty item list
        public ItemList() {
            items = new ArrayList<>((int) Math.pow(NUM_ALPHABETS, codeSize));
            for (int i = 0; i < (int) Math.pow(NUM_ALPHABETS, codeSize); i++) {
                items.add(null);
            }
        }


        //testing



        //REQUIRES: the data in JSON format must be containing all necessary information
        //for creating an item list with matching names
        //EFFECTS: create a new item list from data in JSON format
        public ItemList(JSONObject jsonItemList) {
            items = new ArrayList<>((int) Math.pow(NUM_ALPHABETS, codeSize));
            JSONArray jsonItems = jsonItemList.getJSONArray("items");
            for (int i = 0; i < jsonItems.length(); i++) {
                if (jsonItems.get(i).equals(JSONObject.NULL)) {
                    items.add(null);
                } else {
                    JSONArray item = jsonItems.getJSONArray(i);
                    LinkedList<Product> products = new LinkedList<>();
                    for (int j = 0; j < item.length(); j++) {
                        products.add(new Product(item.getJSONObject(j)));
                    }
                    items.add(products);
                }
            }
        }

        //EFFECTS: return a list of products that belong to this item code in this
        //If there isn't any, return null.
        public List<Product> getProducts(String itemCode) {
            int numericItemCode = getItemCodeNumber(itemCode);
            try {
                return items.get(numericItemCode);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }

        //EFFECTS: return the number of products in this list that belong to the item cdoe
        public int getQuantity(String itemCode) {
            int numericItemCode = getItemCodeNumber(itemCode);
            if (items.get(numericItemCode) != null) {
                return items.get(numericItemCode).size();
            }
            return 0;
        }


        //EFFECTS: return true if this contains any products belonging to the item code.
        //Otherwise, return false.
        public boolean contains(String itemCode) {
            itemCode = itemCode.toUpperCase();
            List<Product> products = getProducts(itemCode);
            return products != null && products.size() != 0;
        }


        //return a product with the item code and SKU in the item list.
        //if there isn't such a product, return null.
        public Product getProduct(String itemCode, int sku) {
            int numericItemCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericItemCode);
            if (products == null) {
                return null;
            }
            for (Product product : products) {
                if (product.getSku() == sku) {
                    return product;
                }
            }
            return null;
        }

        //REQUIRES: item code must be in valid form. price, quantity cannot be negative
        //MODIFIES: this
        //EFFECTS: add products with the given information.
        //If any of the arguments are invalid, throw an IllegalArgumentException.
        public void addProducts(String itemCode, double price, LocalDate bestBeforeDate, int qty) {
            itemCode = itemCode.toUpperCase();
            if (!isValidItemCode(itemCode) || price < 0 || qty < 0) {
                throw new IllegalArgumentException();
            }
            int numericCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericCode);
            int existingQty = quantities.get(numericCode);
            if (products == null) {
                products = new LinkedList<>();
                items.set(numericCode, products);
            }
            for (int i = 0; i < qty; i++) {
                products.add(new Product(itemCode, createSku(), price, currentDate, bestBeforeDate));
            }
            quantities.set(numericCode, existingQty + qty);
        }

        //REQUIRES: itemCode must be in a valid form, quantity cannot be negative
        //MODIFIES: this
        //EFFECTS: remove as many products specified by the item code as qty from this
        public LinkedList<Product> removeProducts(String itemCode, int qty) {
            int numericCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericCode);
            int existingQty = quantities.get(numericCode);
            if (products == null || products.size() < qty) {
                return null;
            }
            LinkedList<Product> removed = new LinkedList<>(products.subList(0, qty));
            products.subList(0, qty).clear();
            quantities.set(numericCode, existingQty - qty);
            return removed;
        }

        //MODIFIES: this
        //EFFECTS: remove the product from this.
        public boolean remove(Product product) {
            String itemCode = product.getItemCode();
            int numericCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericCode);
            return products.remove(product);
        }

        //EFFECTS: convert this to JSONObject and return it
        @Override
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            JSONArray jsons = new JSONArray();
            for (LinkedList<Product> products : items) {
                if (products == null) {
                    jsons.put(JSONObject.NULL);
                } else {
                    jsons.put(convertToJsonArray(products));
                }
            }
            json.put("items", jsons);
            return json;
        }
    }







    //EFFECTS: create an empty inventory. By default, Item code and sections numbers will have three alphabets and
    // 100 numbers from 0 to 99
    public Inventory() {
        codeSize = 3;
        numberOfSections = 100;
        //In this case, the location code starts at A0 and ends at Z99
        quantities = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            quantities.add(0);
        }
        locations = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            locations.add(null);
        }
        quantity = 0;
        nextSKU = 111111111;
        skuSize = 999999999;
        currentDate = LocalDate.now();
    }

    //REQUIRES: codeSize must be greater than 0;
    //EFFECTS: create an empty inventory. Item code will have a number of alphabets specified by codeSize.
    //By default, section number will range from 0 to 99 inclusive
    public Inventory(int codeSize) {
        this.codeSize = codeSize;
        numberOfSections = 100;
        quantity = 0;
        quantities = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            quantities.add(0);
        }
        locations = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            locations.add(null);
        }
        nextSKU = 111111111;
        skuSize = 999999999;
        currentDate = LocalDate.now();
    }

    //REQUIRES: code size must be greater than 0, numberOfSections must be greater than 0.
    //EFFECTS: create an empty inventory. Item code and section numbers will have
    //a number of alphabets specified by codeSize and numbers range from 0 to numberOfSections-1
    public Inventory(int codeSize, int numberOfSections) {
        this.codeSize = codeSize;
        this.numberOfSections = numberOfSections;
        //In this case, the location code starts at A0 and ends at Z99
        quantity = 0;
        quantities = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            quantities.add(0);
        }
        locations = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            locations.add(null);
        }
        nextSKU = 111111111;
        skuSize = 999999999;
        currentDate = LocalDate.now();
    }

    //REQUIRES: the data in JSON format must contain all necessary information
    //required for creating inventory with matching names.
    //EFFECTS: create a new inventory with the given information from the data in JSON format
    public Inventory(JSONObject jsonInventory) {
        currentDate = LocalDate.now();
        nextSKU = jsonInventory.getInt("nextSKU");
        skuSize = jsonInventory.getInt("skuSize");
        codeSize = jsonInventory.getInt("codeSize");
        numberOfSections = jsonInventory.getInt("numberOfSections");
        quantity = jsonInventory.getInt("quantity");
        quantities = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        JSONArray jsonQuantities = jsonInventory.getJSONArray("quantities");
        for (Object json: jsonQuantities) {
            quantities.add((int)json);
        }
        JSONArray jsonLocations = jsonInventory.getJSONArray("locations");
        locations = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
        for (int i = 0; i < jsonLocations.length(); i++) {
            if (jsonLocations.get(i).equals(JSONObject.NULL)) {
                locations.add(null);
            } else {
                locations.add(new ItemList(jsonLocations.getJSONObject(i)));
            }
        }
    }


    //EFFECTS: return  a list of tags that contain information about quantities at each location
    //Only locations with at least one product belonging to the item code will be added to the list
    public ArrayList<QuantityTag> getQuantitiesAtLocations(String itemCode) {
        itemCode = itemCode.toUpperCase();
        ArrayList<QuantityTag> tags = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            ItemList items = locations.get(i);
            if (items != null) {
                int qty = items.getQuantity(itemCode);
                if (qty > 0) {
                    tags.add(new QuantityTag(itemCode, getStringLocationCode(i), qty));
                }
            }
        }
        if (tags.size() == 0) {
            return null;
        }
        return tags;
    }


    //EFFECTS: return the currentDate
    public LocalDate getCurrentDate() {
        return currentDate;
    }


    //MODIFIES: this
    //EFFECTS: set the current Date
    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    //next sku must be greater than 0;
    //MODIFIES: this
    //EFFECTS: set the next sku
    public void setNextSKU(int sku) {
        if (sku < 0) {
            throw new IllegalArgumentException("SKU cannot be negative");
        }
        nextSKU = sku;
    }

    //sku size must be greater than 0;
    //MODIFIES: this
    //EFFECTS: set the maximum value for sku.
    public void setSkuSize(int size) {
        skuSize = size;
    }

    //Will create 9digit SKU. The first digit will start from 0.
    //EFFECTS: return a new 9 digit SKU if next sku number is less than or equal to 999999999.
    //If SKU overflows, set sku to 1.
    public int createSku() {
        if (nextSKU > (int)Math.pow(10, skuSize) - 1) {
            nextSKU = 0;
            for (int i = 0; i < skuSize; i++) {
                nextSKU *= 10;
                nextSKU++;
            }
        }
        return nextSKU++;
    }

    //REQUIRES: the code size must be set positive integer
    //EFFECTS: return true if the code is in the valid form
    //return false otherwise.
    public boolean isValidItemCode(String itemCode) {
        if (itemCode.length() > codeSize || itemCode.length() < codeSize) {
            return false;
        }
        try {
            getItemCodeNumber(itemCode);
        } catch (InvalidItemCodeException e) {
            return false;
        }
        return true;
    }

    //If the given location code is in a valid form, return true
    //else return false.
    public boolean isValidLocationCode(String location) {
        int numeric;
        try {
            numeric = getLocationCodeNumber(location);
        } catch (LocationFormatException e) {
            return false;
        }

        if (numeric >= ('z' - 'a' + 1) * numberOfSections || numeric < 0) {
            return false;
        }
        return true;
    }


    //REQUIRES: tags need to be a list of entries that contain information for new products and its location
    //MODIFIES: this
    //EFFECTS: will put new products in the inventory list using information in the tags
    public void addProducts(List<InventoryTag> tags) {
        for (InventoryTag tag: tags) {
            int qty = tag.getQuantity();
            String itemCode = tag.getItemCode().toUpperCase();
            double price = tag.getPrice();
            LocalDate bestBeforeDate = tag.getBestBeforeDate();
            String location = tag.getLocation().toUpperCase();
            int numericLocationCode = getLocationCodeNumber(location);
            ItemList items = locations.get(numericLocationCode);
            if (items == null) {
                locations.set(numericLocationCode, new ItemList());
                items = locations.get(numericLocationCode);
            }
            items.addProducts(itemCode, price, bestBeforeDate, qty);
            quantity += tag.getQuantity();
        }
    }

    //MODIFIES: this
    //EFFECTS: remove a product with its item code and SKU. if the product is removed, return true
    //if there is no such product, return false.

    public boolean removeProduct(String itemCode, int sku) {
        ProductTag tag = findProduct(itemCode, sku);
        if (tag == null) {
            return false;
        }
        String location = tag.getLocation();
        int numericLocationCode = getLocationCodeNumber(location);
        Product product = tag.getProduct();
        ItemList items = locations.get(numericLocationCode);
        items.remove(product);
        int numericItemCode = getItemCodeNumber(itemCode);
        int qtyBefore = quantities.get(numericItemCode);
        quantities.set(numericItemCode, qtyBefore - 1);

        return true;
    }



    //MODIFIES: this
    //EFFECTS: remove the product indicated from the inventory.
    //return a list of products that have been removed. If quantity to remove exceeds that inside the inventory,
    //skip the tag(quantity)
    public LinkedList<QuantityTag> removeProducts(List<QuantityTag> tags) {
        LinkedList<QuantityTag> receipt = new LinkedList<>();
        LinkedList<Product> removed;
        ArrayList<QuantityTag> failed = new ArrayList<>();
        for (QuantityTag tag: tags) {
            int qty = tag.getQuantity();
            String itemCode = tag.getItemCode();
            String location = tag.getLocation().toUpperCase();
            int numericLocationCode = getLocationCodeNumber(location);
            ItemList items = locations.get(numericLocationCode);
            if (items == null) {
                failed.add(tag);
            } else {
                removed = items.removeProducts(itemCode, qty);
                if (removed != null) {
                    quantity -= removed.size();
                    receipt.add(new QuantityTag(itemCode, location, -removed.size()));
                }
            }
        }
        return receipt;
    }




    //numeric form of this item code must be existing in inventory.
    //for example, if valid form of item code is "AAA", passed item code cannot be "QQQQ"
    //EFFECTS: find the location of a product indicated by this item code and SKU.
    //if it cannot be found, return -1
    public ProductTag findProduct(String itemCode, int sku) {
        ProductTag tag = null;
        for (int i = 0; i < locations.size(); i++) {
            ItemList items = locations.get(i);
            if (items != null) {
                Product product = items.getProduct(itemCode, sku);
                if (product != null) {
                    tag = new ProductTag(product, getStringLocationCode(i));
                    break;
                }
            }
        }
        return tag;
    }

    //numeric form of this item code must be existing in inventory.
    //For instance, if valid form of item code is "AAA", passed item code cannot be "AAAA"
    //EFFECTS: return a list of locations where products belonging to the item code are located.
    public List<Integer> findLocations(String itemCode) throws InvalidItemCodeException {
        if (!isValidItemCode(itemCode)) {
            throw new InvalidItemCodeException();
        }
        itemCode = itemCode.toUpperCase();
        LinkedList<Integer> foundLocations = new LinkedList<>();
        for (int i = 0; i < locations.size(); i++) {
            ItemList items = locations.get(i);
            if (items != null) {
                if (items.contains(itemCode)) {
                    foundLocations.add(i);
                }
            }
        }
        return foundLocations;
    }


    //REQUIRES: itemCode must be in valid form(a combination of english alphabets (upper case),
    //of size used by the inventory
    //EFFECTS: return a numeric code converted from the string code
    public int getItemCodeNumber(String itemCode)  {
        if (itemCode.length() > codeSize) {
            throw new InvalidItemCodeException();
        }
        int numericCode = 0;
        itemCode = itemCode.toUpperCase();
        for (int i = 0; i < itemCode.length(); i++) {
            numericCode *= NUM_ALPHABETS;
            if (itemCode.charAt(i) - 'A' > NUM_ALPHABETS - 1 || itemCode.charAt(i) - 'A' < 0) {
                throw new InvalidItemCodeException();
            }
            numericCode += itemCode.charAt(i) - 'A';
        }
        return numericCode;
    }

    //REQUIRES: location must be in the valid form(a combination of one english alphabet and digits,
    //in the form used by the inventory (upper case))
    //EFFECTS: return a numeric code converted from the string code. If location is null, return 0;
    public int getLocationCodeNumber(String location) {
        int alphabetValue;
        location = location.toUpperCase();
        if (location.length() == 1) {
            if (location.equalsIgnoreCase("T")) {
                return 0;
            } else {
                alphabetValue = location.charAt(0) - 'A';
                return alphabetValue * numberOfSections;
            }
        }
        //Only one alphabet at first is allowed
        alphabetValue = location.charAt(0) - 'A';
        int numericCode = alphabetValue * numberOfSections;
        //If A99: 99
        try {
            if ((Integer.parseInt(location.substring(1)) >= numberOfSections)) {
                throw new LocationFormatException();
            }
            numericCode += Integer.parseInt(location.substring(1));
        } catch (NumberFormatException e) {
            throw new LocationFormatException();
        }
        return numericCode;
    }


    //REQUIRE: numeric Code must be in a valid form
    //EFFECTS: return string type item code
    public String getStringItemCode(int numericCode) {
        if (numericCode < 0) {
            return null;
        }
        String stringCode = "";
        stringCode += (char)((numericCode / (NUM_ALPHABETS * NUM_ALPHABETS)) + 'A');
        numericCode = numericCode % (NUM_ALPHABETS * NUM_ALPHABETS);
        stringCode += (char)(numericCode / NUM_ALPHABETS + 'A');
        numericCode %= NUM_ALPHABETS;
        stringCode += (char)(numericCode + 'A');
        return stringCode;
    }

    //REQUIRES: numeric Code must be in a valid form
    //EFFECTS: return string type location code
    public String getStringLocationCode(int numericCode) {
        if (numericCode < 0) {
            return null;
        }
        if (numericCode == 0) {
            return "T";
        }
        String stringCode = "";
        stringCode += (char)((numericCode / (numberOfSections)) + 'A');
        numericCode %= numberOfSections;
        stringCode += "" + numericCode;
        return stringCode;
    }

    //REQUIRES: code must be in valid form.
    //EFFECTS: return the number of products belonging to the code.
    public int getQuantity(String itemCode) {
        int numericCode = getItemCodeNumber(itemCode);
        return quantities.get(numericCode);
    }

    //EFFECTS: return the number of total quantities in the inventory.
    public int getTotalQuantity() {
        return quantity;
    }

    //EFFECTS: return the list of products specified by the item code.
    //If there isn't any of those products, return null
    public List<Product> getProductList(String itemCode) {
        itemCode = itemCode.toUpperCase();
        LinkedList<Product> list = new LinkedList<>();
        for (ItemList items : locations) {
            if (items != null) {
                try {
                    list.addAll(items.getProducts(itemCode));
                } catch (NullPointerException e) {
                    //if getProducts is null, don't add null pointer value to the product list
                }
            }
        }
        return list;
    }

    //EFFECTS: return the product specified by the item code and stock keeping unit(SKU).
    //If there isn't such a product, return null.
    public Product getProduct(String itemCode, int sku) {
        for (ItemList items: locations) {
            if (items != null) {
                Product product = items.getProduct(itemCode, sku);
                if (product != null) {
                    return product;
                }
            }
        }
        return null;
    }

    //EFFECTS: return a list of item codes existing in the inventory.
    public List<String> getListOfCodes() {
        LinkedList<String> codes = new LinkedList<>();

        for (int i = 0; i < quantities.size(); i++) {
            int qty = quantities.get(i);
            if (qty != 0) {
                codes.add(getStringItemCode(i));
            }
        }
        return codes;
    }



    //EFFECTS: convert this to JSONObject and return it.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray jsonLocations = convertToJsonArray(locations);
        json.put("locations", jsonLocations);
        json.put("quantities", new JSONArray(quantities));
        json.put("nextSKU", nextSKU);
        json.put("skuSize", skuSize);
        json.put("quantity", quantity);
        json.put("codeSize", codeSize);
        json.put("numberOfSections", numberOfSections);
        return json;
    }

}

