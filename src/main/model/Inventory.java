package model;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

public class Inventory {
    private final int codeSize;
    //hash map that has numeric item codes as its keys and array lists of products as its value.

    //hash map that has numeric location codes as its keys and array lists of products as its value.
    //Default code size will be set to 3.
    //index 0 represents the temporary storage room, where products without location tag will be stored.
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
    public class ItemList {
        ArrayList<LinkedList<Product>> items;

        public ItemList() {
            items = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
            for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
                items.add(null);
            }
        }

        public LinkedList<Product> getProducts(String itemCode) {
            int numericItemCode = getItemCodeNumber(itemCode);
            return items.get(numericItemCode);
        }

        public int getQuantity(String itemCode) {
            int numericItemCode = getItemCodeNumber(itemCode);
            return items.get(numericItemCode).size();
        }

        public boolean contains(String itemCode) {
            int numericItemCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericItemCode);
            if (products == null || products.size() == 0) {
                return false;
            }
            return true;
        }

        public Product getProduct(String itemCode, int sku) {
            int numericItemCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericItemCode);
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                if (product.getSku() == sku) {
                    return product;
                }
            }
            return null;
        }

        public void addProducts(String itemCode, double price, LocalDate bestBeforeDate,  int qty) {
            int numericCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericCode);
            int existingQty = quantities.get(numericCode);
            if (products == null) {
                products = new LinkedList<>();
            }
            for (int i = 0; i < qty; i++) {
                products.add(new Product(itemCode, createSku(), price, currentDate, bestBeforeDate));
            }
            quantities.set(numericCode, existingQty + qty);
        }

        public void removeProducts(String itemCode, int qty) throws Exception {
            int numericCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericCode);
            int existingQty = quantities.get(numericCode);
            if (products.size() < qty) {
                throw new Exception("Cannot remove more than existing products");
            }
            products.subList(0, qty).clear();
            quantities.set(numericCode, existingQty - qty);
            assert existingQty - qty >= 0;
        }

        public boolean remove(Product product) {
            String itemCode = product.getItemCode();
            int numericCode = getItemCodeNumber(itemCode);
            LinkedList<Product> products = items.get(numericCode);
            return products.remove(product);
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
        locations = new ArrayList<>();
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
        //In this case, the location code starts at A0 and ends at Z99
        quantity = 0;
        quantities = new ArrayList<>((int)Math.pow(NUM_ALPHABETS, codeSize));
        for (int i = 0; i < (int)Math.pow(NUM_ALPHABETS, codeSize); i++) {
            quantities.add(0);
        }
        locations = new ArrayList<>();
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
        locations = new ArrayList<>();
        for (int i = 0; i < NUM_ALPHABETS * numberOfSections; i++) {
            locations.add(null);
        }
        nextSKU = 111111111;
        skuSize = 999999999;
        currentDate = LocalDate.now();
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
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    //String itemCode, double price, LocalDate date, String location, int quantity
    //REQUIRES: tags need to be a list of entries that contain information for new products and its location
    //MODIFIES: this
    //EFFECTS: will put new products in the inventory list using information in the tags
    public void addProducts(ArrayList<AdditionTag> tags) {
        for (AdditionTag tag: tags) {
            int qty = tag.getQuantity();
            String itemCode = tag.getItemCode();
            double price = tag.getPrice();
            LocalDate bestBeforeDate = tag.getBestBeforeDate();
            String location = tag.getLocation();
            int numericLocationCode = getLocationCodeNumber(location);
            ItemList items = locations.get(numericLocationCode);
            if (items == null) {
                locations.add(numericLocationCode, new ItemList());
            }
            items.addProducts(itemCode, price, bestBeforeDate, qty);
            quantity += tag.getQuantity();
        }
    }

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
        return true;
    }


    //REQUIRES: product needs to exist in the inventory
    //MODIFIES: this
    //EFFECTS: remove the product indicated from the inventory. If the product is removed
    //return true. If the product cannot be found, return false.
    //implement try and catch block later (in case the products don't exist)
    public void removeProducts(ArrayList<RemovalTag> tags) throws RemovalFailedException {
        ArrayList<RemovalTag> failed = new ArrayList<>();
        for (RemovalTag tag: tags) {
            int qty = tag.getQuantity();
            String itemCode = tag.getItemCode();
            String location = tag.getLocation();
            int numericLocationCode = getLocationCodeNumber(location);
            ItemList items = locations.get(numericLocationCode);
            if (items == null) {
                failed.add(tag);
            } else {
                try {
                    items.removeProducts(itemCode, qty);
                } catch (Exception e) {
                    failed.add(tag);
                }
            }
        }
        if (failed.size() != 0) {
            throw new RemovalFailedException(failed, "cannot remove more than what exist in the location");
        }
    }


    //EFFECTS: return a list of product codes and corresponding location, which are composed of item code + SKU.
    public LinkedList<ProductTag> getProducts(String itemCode, int qty) {
        int numericCode = getItemCodeNumber(itemCode);
        int count = 0;
        LinkedList products = new LinkedList();
        for (ItemList items: locations) {
            LinkedList<Product> productsAtThisLocation = items.getProducts(itemCode);
            if (productsAtThisLocation != null) {
                if (qty - products.size() < productsAtThisLocation.size()) {
                    int gap = qty - products.size();
                    products.addAll(productsAtThisLocation.subList(0, gap));
                }
            }
        }
        return products;
    }




    //numeric form of this item code must be existing in inventory.
    //for example, if valid form of item code is "AAA", passed item code cannot be "QQQQ"
    //EFFECTS: find the location of a product indicated by this item code and SKU.
    //if it cannot be found, return -1
    public ProductTag findProduct(String itemCode, int sku) {
        ProductTag tag = null;
        for (int i = 0; i < locations.size(); i++) {
            ItemList items = locations.get(i);
            Product product = items.getProduct(itemCode, sku);
            if (product != null) {
                tag.setProduct(product);
                tag.setLocation(getStringLocationCode(i));
                break;
            }
        }
        return tag;
    }

    //numeric form of this item code must be existing in inventory.
    //For instance, if valid form of item code is "AAA", passed item code cannot be "AAAA"
    //EFFECTS: return a list of locations where products belonging to the item code are located.
    public LinkedList<Integer> findLocations(String itemCode) {
        LinkedList<Integer> foundLocations = new LinkedList<>();
        for (int i = 0; i < locations.size(); i++) {
            ItemList items = locations.get(0);
            if (items.contains(itemCode)) {
                foundLocations.add(i);
            }
        }
        return foundLocations;
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
        numericCode = alphabetValue * numberOfSections;
        try {
            //If A99: 99, B99:
            numericCode +=  Integer.parseInt(location.substring(1));
        } catch (Exception e) {
            return numericCode;
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
    public int getQuantity(String code) {
        int numericCode = getItemCodeNumber(code);
        return quantities.get(numericCode);
    }

    //EFFECTS: return the number of total quantities in the inventory.
    public int getTotalQuantity() {
        return quantity;
    }

    //EFFECTS: return the list of products specified by the item code.
    //If there isn't any of those products, return null
    public LinkedList<Product> getProductList(String itemCode) {
        LinkedList<Product> list = new LinkedList<>();
        for (ItemList items: locations) {
            if (items.getProducts(itemCode) != null) {
                list.addAll(items.getProducts(itemCode));
            }
        }
        if (list.size() == 0) {
            return null;
        }
        return list;
    }

    //EFFECTS: return the product specified by the item code and stock keeping unit(SKU).
    //If there isn't such a product, return null.
    public Product getProduct(String itemCode, int sku) {
        for (ItemList items: locations) {
            Product product = items.getProduct(itemCode, sku);
            if (product != null) {
                return product;
            }
        }
        return null;
    }

    //EFFECTS: return a list of item codes existing in the inventory.
    public LinkedList<String> getListOfCodes() {
        LinkedList<String> codes = new LinkedList<>();

        for (int i = 0; i < quantities.size(); i++) {
            int qty = quantities.get(i);
            if (qty != 0) {
                codes.add(getStringItemCode(i));
            }
        }
        return codes;
    }
}

