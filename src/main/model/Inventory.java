package model;


import java.util.ArrayList;
import java.util.LinkedList;

public class Inventory {
    private final int codeSize;
    //hash map that has numeric item codes as its keys and array lists of products as its value.
    private final ArrayList<LinkedList<Product>> listByCode;

    //hash map that has numeric location codes as its keys and array lists of products as its value.
    //Default code size will be set to 3.
    //index 0 represents the temporary storage room, where products without location tag will be stored.
    private final ArrayList<LinkedList<Product>> listByLocation;

    //hashmap where key is a numeric product code
    //and the value is an array of numeric location code.
    private final ArrayList<LinkedList<Integer>> locationsOfProductCode;
    private final int numberOfSections;
    private static final int NUM_ALPHABETS = 26;
    private int quantity;

    //EFFECTS: create an empty inventory. By default, Item code and sections numbers will have three alphabets and
    // 100 numbers from 0 to 99
    public Inventory() {
        codeSize = 3;
        numberOfSections = 100;
        listByCode = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        locationsOfProductCode = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        //In this case, the location code starts at A0 and ends at Z99
        listByLocation = new ArrayList<>(NUM_ALPHABETS * numberOfSections);

        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            listByCode.add(new LinkedList<>());
            locationsOfProductCode.add(new LinkedList<>());
        }
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            listByLocation.add(new LinkedList<>());
        }
        quantity = 0;
    }

    //REQUIRES: codeSize must be greater than 0;
    //EFFECTS: create an empty inventory. Item code will have a number of alphabets specified by codeSize.
    //By default, section number will range from 0 to 99 inclusive
    public Inventory(int codeSize) {
        this.codeSize = codeSize;
        numberOfSections = 100;
        listByCode = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        locationsOfProductCode = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        //In this case, the location code starts at A0 and ends at Z99
        listByLocation = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
        quantity = 0;
        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            listByCode.add(new LinkedList<>());
            locationsOfProductCode.add(new LinkedList<>());
        }
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            listByLocation.add(new LinkedList<>());
        }
    }

    //REQUIRES: code size must be greater than 0, numberOfSections must be greater than 0.
    //EFFECTS: create an empty inventory. Item code and section numbers will have
    //a number of alphabets specified by codeSize and numbers range from 0 to numberOfSections-1
    public Inventory(int codeSize, int numberOfSections) {
        this.codeSize = codeSize;
        this.numberOfSections = numberOfSections;
        listByCode = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        locationsOfProductCode = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        //In this case, the location code starts at A0 and ends at Z99
        listByLocation = new ArrayList<>(NUM_ALPHABETS * numberOfSections);
        quantity = 0;
        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            listByCode.add(new LinkedList<>());
            locationsOfProductCode.add(new LinkedList<>());
        }
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            listByLocation.add(new LinkedList<>());
        }
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
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }


    //REQUIRES: products need to be a list of entries that contain Product, location in String.
    public void addProducts(ArrayList<LocationTag> products) {
        for (LocationTag tag: products) {
            Product product = tag.getProduct();
            String location = tag.getLocation();
            int itemCode = getItemCodeNumber(product.getItemCode());
            int locationCode = getLocationCodeNumber(location);
            listByCode.get(itemCode).add(product);
            listByLocation.get(locationCode).add(product);
            if (!locationsOfProductCode.get(itemCode).contains(locationCode)) {
                locationsOfProductCode.get(itemCode).add(locationCode);
            }
        }
        quantity += products.size();
    }


    //REQUIRES: product needs to exist in the inventory
    //MODIFIES: this
    //EFFECTS: remove the product indicated from the inventory. If the product is removed
    //return true. If the product cannot be found, return false.
    //implement try and catch block later (in case the products don't exist)
    public boolean removeProduct(Product product) {
        int code = getItemCodeNumber((product.getItemCode()));
        if (listByCode.get(code).remove(product)) {
            int locationCode = findLocation(product);
            listByLocation.get(locationCode).remove(product);
            return true;
        }
        return false;
    }


    //REQUIRES: qty must not be negative. itemCode must be in the valid form
    //MODIFIES: this
    //EFFECTS: remove  the specified number of products belonging to the item code.
    //if there are fewer products than the number, remove all the products existing in the inventory.
    //if method removes any of the products, return true.
    //else return false.
    public boolean removeProducts(String itemCode, int qty) {
        LinkedList<Product> products = listByCode.get(getItemCodeNumber(itemCode));
        ArrayList<LocationTag> toBeRemoved = new ArrayList<>();
        int count = 0;
        for (; count < qty && count < products.size(); count++) {
            LocationTag tag = new LocationTag(products.get(count),
                    getStringLocationCode(findLocation(products.get(count))));

            toBeRemoved.add(tag);
        }
        removeProducts(toBeRemoved);
        return (count > 0);
    }

    //REQUIRES: products need to exist in the inventory
    //MODIFIES: this
    //EFFECTS: remove all the products indicated from the inventory
    //implement try and catch block later (in case the products don't exist)
    public void removeProducts(ArrayList<LocationTag> entries) {
        for (LocationTag tag: entries) {
            Product product = tag.getProduct();
            String location = tag.getLocation();
            int code = getItemCodeNumber((product.getItemCode()));
            listByCode.get(code).remove(product);
            //try and catch block needed
            listByLocation.get(getLocationCodeNumber(location)).remove(product);
        }
    }





    //EFFECTS: return the numeric location code for the product.
    //if the product is stored in the temporary storage room, return 0;
    //If there is no such product in the inventory, return -1.
    public int findLocation(Product product) {
        LinkedList<Product> list;
        LinkedList<Integer> locations = locationsOfProductCode.get(getItemCodeNumber(product.getItemCode()));
        for (Integer e: locations) {
            list = listByLocation.get(e);
            if (list.contains(product)) {
                return e;
            }
        }
        return -1;
    }

    //numeric form of this item code must be existing in inventory.
    //for example, if valid form of item code is "AAA", passed item code cannot be "QQQQ"
    //EFFECTS: find the location of a product indicated by this item code and SKU
    public int findLocation(String itemCode, int sku) {
        for (Product e: listByCode.get(getItemCodeNumber(itemCode))) {
            if (e.getSku() == sku) {
                return findLocation(e);
            }
        }
        return -1;
    }

    //numeric form of this item code must be existing in inventory.
    //For instance, if valid form of item code is "AAA", passed item code cannot be "AAAA"
    //EFFECTS: return a list of locations where products belonging to the item code are located.
    public LinkedList<Integer> findLocations(String itemCode) {
        return locationsOfProductCode.get(getItemCodeNumber(itemCode));
    }


    //REQUIRES: itemCode must be in valid form(a combination of english alphabets (upper case),
    //of size used by the inventory
    //EFFECTS: return a numeric code converted from the string code
    public int getItemCodeNumber(String itemCode) throws IllegalArgumentException {
        int numericCode = 0;
        itemCode = itemCode.toUpperCase();
        for (int i = 0; i < itemCode.length(); i++) {
            numericCode *= NUM_ALPHABETS;
            if (itemCode.charAt(i) - 'A' > NUM_ALPHABETS - 1 || itemCode.charAt(i) - 'A' < 0) {
                throw new IllegalArgumentException();
            }
            numericCode += itemCode.charAt(i) - 'A';
        }
        return numericCode;
    }

    //REQUIRES: location must be in the valid form(a combination of one english alphabet and digits,
    //in the form used by the inventory (upper case))
    //EFFECTS: return a numeric code converted from the string code. If location is null, return 0;
    public int getLocationCodeNumber(String location) {
        int numericCode;
        if (location.equalsIgnoreCase("T")) {
            return 0;
        }
        //alphabet's value
        location = location.toUpperCase();
        int alphabetValue = location.charAt(0) - 'A';
        //If A99: 99, B99:
        numericCode = alphabetValue * numberOfSections + Integer.parseInt(location.substring(1));
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
    public int getQuantity(String code) {
        int itemNumber = getItemCodeNumber(code);
        return listByCode.get(itemNumber).size();
    }

    //EFFECTS: return the number of total quantities in the inventory.
    public int getTotalQuantity() {
        return quantity;
    }

    //EFFECTS: return the list of products specified by the item code.
    //If there isn't any of those products, return null
    public LinkedList<Product> getProductList(String itemCode) {
        LinkedList<Product> list = listByCode.get(getItemCodeNumber(itemCode));
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    //EFFECTS: return the product specified by the item code and stock keeping unit(SKU).
    //If there isn't such a product, return null.
    public Product getProduct(String itemCode, int sku) {
        LinkedList<Product> list = listByCode.get(getItemCodeNumber(itemCode));
        for (Product e: list) {
            if (e.getSku() == sku) {
                return e;
            }
        }
        return null;
    }

    //EFFECTS: return a list of item codes existing in the inventory.
    public LinkedList<String> getListOfCodes() {
        LinkedList<String> codes = new LinkedList<>();
        for (int i = 0; i < listByCode.size(); i++) {
            LinkedList<Product> products = listByCode.get(i);
            if (products.size() != 0) {
                codes.add(getStringItemCode(i));
            }
        }
        return codes;
    }
}

