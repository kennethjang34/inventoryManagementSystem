package model;


import java.util.ArrayList;

public class Inventory {
    private final int codeSize;
    private final ArrayList<Product> [] listByCode;
    //A0 : [0], temporary storage room
    //A1 : [1]
    private final ArrayList<Product> [] listByLocation;

    //this array list will represent a hashmap
    //where key is a numeric product code and the value is an array of strings indicating its location in the inventory.

    private final ArrayList<Integer> [] locationsOfProductCode;
    private final int numberOfSections;
    private static final int NUM_ALPHABETS = 26;
    private int quantity;

    //EFFECTS: create an empty inventory. By default, Item code and sections numbers will have three alphabets and
    // 100 numbers from 0 to 99
    public Inventory() {
        codeSize = 3;
        numberOfSections = 100;
        listByCode = new ArrayList[(int)Math.pow(NUM_ALPHABETS, codeSize)];
        locationsOfProductCode = new ArrayList[(int)Math.pow(NUM_ALPHABETS, codeSize)];
        //In this case, the location code starts at A0 and ends at Z99
        listByLocation = new ArrayList[NUM_ALPHABETS * numberOfSections];
        for (int i = 0; i < listByCode.length; i++) {
            listByCode[i] = new ArrayList<>();
            locationsOfProductCode[i] = new ArrayList<>();
        }
        for (int i = 0; i < listByLocation.length; i++) {
            listByLocation[i] = new ArrayList<>();
        }
        quantity = 0;
    }

    //REQUIRES: codeSize must be greater than 0;
    //EFFECTS: create an empty inventory. Item code will have a number of alphabets specified by codeSize.
    //By default, section number will range from 0 to 99 inclusive
    public Inventory(int codeSize) {
        this.codeSize = codeSize;
        numberOfSections = 100;
        listByCode = new ArrayList[(int)Math.pow(NUM_ALPHABETS, codeSize)];
        locationsOfProductCode = new ArrayList[(int)Math.pow(NUM_ALPHABETS, codeSize)];
        listByLocation = new ArrayList[NUM_ALPHABETS * numberOfSections];
        for (int i = 0; i < listByCode.length; i++) {
            listByCode[i] = new ArrayList<>();
            locationsOfProductCode[i] = new ArrayList<>();
        }
        for (int i = 0; i < listByLocation.length; i++) {
            listByLocation[i] = new ArrayList<>();
        }
        quantity = 0;
    }

    //REQUIRES: code size must be greater than 0, numberOfSections must be greater than 0.
    //EFFECTS: create an empty inventory. Item code and section numbers will have
    //a number of alphabets specified by codeSize and numbers range from 0 to numberOfSections-1
    public Inventory(int codeSize, int numberOfSections) {
        this.codeSize = codeSize;
        this.numberOfSections = numberOfSections;
        listByCode = new ArrayList[(int)Math.pow(NUM_ALPHABETS, codeSize)];
        listByLocation = new ArrayList[NUM_ALPHABETS * numberOfSections];
        locationsOfProductCode = new ArrayList[(int)Math.pow(NUM_ALPHABETS, codeSize)];
        for (int i = 0; i < listByCode.length; i++) {
            listByCode[i] = new ArrayList<>();
            locationsOfProductCode[i] = new ArrayList<>();
        }
        for (int i = 0; i < listByLocation.length; i++) {
            listByLocation[i] = new ArrayList<>();
        }
        quantity = 0;
    }



    //REQUIRES: products need to be a list of entries that contain Product, location in String.
    public void addProducts(ArrayList<Object[]> products) {
        for (Object[] productInfo: products) {
            Product product = (Product)productInfo[0];
            String location = (String)productInfo[1];
            int itemCode = getItemCodeNumber(product.getItemCode());
            int locationCode = getLocationCodeNumber(location);
            listByCode[itemCode].add(product);
            listByLocation[locationCode].add(product);
            if (!locationsOfProductCode[itemCode].contains(locationCode)) {
                locationsOfProductCode[itemCode].add(locationCode);
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
        if (listByCode[code].remove(product)) {
            int locationCode = findLocation(product);
            listByLocation[locationCode].remove(product);
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
        ArrayList<Product> products = listByCode[getItemCodeNumber(itemCode)];
        ArrayList<Object[]> toBeRemoved = new ArrayList<>();
        int count = 0;
        for (; count < qty && count < products.size(); count++) {
            Object[] entry = new Object[2];
            Product product = products.get(count);
            entry[0] = product;
            entry[1] = getStringLocationCode(findLocation(product));
            toBeRemoved.add(entry);
        }
        removeProducts(toBeRemoved);
        return (count > 0);
    }

    //Needs to be improved
    //REQUIRES: products need to exist in the inventory
    //MODIFIES: this
    //EFFECTS: remove all the products indicated from the inventory
    //implement try and catch block later (in case the products don't exist)
    public void removeProducts(ArrayList<Object[]> entries) {
        for (Object[] e: entries) {
            Product product = (Product)e[0];
            String location = (String)e[1];
            int code = getItemCodeNumber((product.getItemCode()));
            listByCode[code].remove(product);
            //try and catch block needed
            listByLocation[getLocationCodeNumber(location)].remove(e);
        }
    }





    //EFFECTS: return the numeric location code for the product.
    //if the product is stored in the temporary storage room, return 0;
    //If there is no such product in the inventory, return -1.
    public int findLocation(Product product) {
        ArrayList<Product> list;
        ArrayList<Integer> locations = locationsOfProductCode[getItemCodeNumber(product.getItemCode())];
        for (Integer e: locations) {
            list = listByLocation[e];
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
        for (Product e: listByCode[getItemCodeNumber(itemCode)]) {
            if (e.getSku() == sku) {
                return findLocation(e);
            }
        }

        return -1;
    }

    //numeric form of this item code must be existing in inventory.
    //For instance, if valid form of item code is "AAA", passed item code cannot be "AAAA"
    //EFFECTS: return a list of locations where products belonging to the item code are located.
    public ArrayList<Integer> findLocations(String itemCode) {
        return locationsOfProductCode[getItemCodeNumber(itemCode)];
    }


    //REQUIRES: itemCode must be in valid form(a combination of english alphabets (upper case),
    //of size used by the inventory
    //EFFECTS: return a numeric code converted from the string code
    public int getItemCodeNumber(String itemCode) {
        int numericCode = 0;
        itemCode = itemCode.toUpperCase();
        for (int i = 0; i < itemCode.length(); i++) {
            numericCode *= 26;
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
        stringCode += (char)((numericCode / (26 * 26)) + 'A');
        numericCode = numericCode % (26 * 26);
        stringCode += (char)(numericCode / 26 + 'A');
        numericCode %= 26;
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
        int itemNumber = getItemCodeNumber((code));
        return listByCode[itemNumber].size();
    }

    //EFFECTS: return the number of total quantities in the inventory.
    public int getTotalQuantity() {
        return quantity;
    }

    //EFFECTS: return the list of products specified by the item code.
    //If there isn't any of those products, return null
    public ArrayList<Product> getProductList(String itemCode) {
        ArrayList<Product> list = listByCode[getItemCodeNumber(itemCode)];
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    //EFFECTS: return the product specified by the item code and stock keeping unit(SKU).
    //If there isn't such a product, return null.
    public Product getProduct(String itemCode, int sku) {
        ArrayList<Product> list = listByCode[getItemCodeNumber(itemCode)];
        for (Product e: list) {
            if (e.getSku() == sku) {
                return e;
            }
        }
        return null;
    }

    //EFFECTS: return a list of item codes existing in the inventory.
    public ArrayList<String> getListOfCodes() {
        ArrayList<String> codes = new ArrayList<>();
        for (int i = 0; i < listByCode.length; i++) {
            ArrayList<Product> products = listByCode[i];
            if (products.size() != 0) {
                codes.add(getStringItemCode(i));
            }
        }
        return codes;
    }
}

