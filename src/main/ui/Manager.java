package ui;


import model.Account;
import model.Admin;
import model.Inventory;
import model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Manager class creates products based on the user's request and add it to inventory.
//for each load, create an account for record.
//make it possible to not just search for a specific product using Inventory class,
//but can check multiple products comparing them.
//Maybe make it a private class?
public class Manager {
    private final Inventory inventory;
    private final ArrayList<Account> ledger;
    private final Admin admin;
    //Object[0] = product
    //[1] = location
    private final ArrayList<Object[]> temporaryList;
    private LocalDate currentDate;
    private int nextSKU;
    private int nextAccountNumber;
    private static final int MAX_SKU = 999999999;
    private static final int FIRST_SKU = 111111111;
    private static final int FIRST_ACCOUNT_NUMBER = 111111;

    //EFFECTS: create a manager with an empty inventory, ledger, and admin.
    public Manager() {
        inventory = new Inventory();
        ledger = new ArrayList<>();
        admin = new Admin();
        temporaryList = new ArrayList<>();
        nextSKU = FIRST_SKU;
        nextAccountNumber = FIRST_ACCOUNT_NUMBER;
        currentDate = LocalDate.now();
    }


    //Will be called by UI main method.
    //Caller needs to supply item code, best-before-date, and cost
    //REQUIRES: item code must be in a valid form specified by the user, cost must be positive.
    //best-before date must be today or later than today
    //MODIFIES: this
    //EFFECTS: create a product according to the input information.
    public Product createProduct(String itemCode, LocalDate bestBeforeDate, double cost, String location) {
        int sku = createSku();
        itemCode = itemCode.toUpperCase();
        Product product = new Product(itemCode, sku, cost, currentDate, bestBeforeDate);
        Object[] newProductInfo = new Object[2];
        newProductInfo[0] = product;
        newProductInfo[1] = location;
        temporaryList.add(newProductInfo);
        return product;
    }

    //EFFECTS: return temporary List.
    public ArrayList<Object[]> getTemporaryList() {
        return temporaryList;
    }


    //MODIFIES: this
    //EFFECTS: remove one product belonging to this item code from the temporary list.
    //return true if it succeeds. return false otherwise.
    public boolean removeProductFromTemporaryList(String itemCode) {
        for (Object[] newProductInfo: temporaryList) {
            Product e = (Product)newProductInfo[0];
            if (e.getItemCode().equalsIgnoreCase(itemCode)) {
                temporaryList.remove(newProductInfo);
                return true;
            }
        }
        return false;
    }

    //MODIFIES: this
    //EFFECTS: remove a number specified by qty of products
    //belonging to this item code that is specified by qty from the inventory.
    //return true when it succeeds.
    //return true if it succeeds. return false otherwise.
    public boolean removeProducts(String itemCode, int qty) {
        return inventory.removeProducts(itemCode, qty);
    }


    //MODIFIES:this
    //EFFECTS: remove a specific product that has this item code and SKU.
    //Return ture if it succeeds. return false otherwise.
    public boolean removeProduct(String itemCode, int sku) {
        Product product = inventory.getProduct(itemCode, sku);
        return inventory.removeProduct(product);
    }


    //Will update the inventory with a new list of products that have been created.
    //MODIFIES: this
    //EFFECTS: the new product list will be added to the inventory, creating a new transaction account.
    public boolean updateInventory(String description) {
        boolean succeed = updateLedger(description);
        inventory.addProducts(temporaryList);
        temporaryList.clear();
        return succeed;
    }


    //REQUIRES: account code must be in a valid form. cannot be negative.
    //MODIFIES: this
    //EFFECTS: create a new transaction account.
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public Account createAccount(int accountCode, String description, LocalDate date) {
        Map<String, Integer>  hash = new HashMap<>();
        ArrayList<Object[]> entries = new ArrayList<>();
        for (Object[] fromTemp: temporaryList) {
            Product product = (Product)fromTemp[0];
            if (hash.containsKey(product.getItemCode())) {
                int existing = hash.get(product.getItemCode());
                hash.put(product.getItemCode(), existing + 1);
            } else {
                hash.put(product.getItemCode(), 1);
            }
        }
        if (hash.entrySet().size() == 0) {
            return null;
        }
        for (Map.Entry<String, Integer> count: hash.entrySet()) {
            Object[] entry = new Object[3];
            entry[0] = count.getKey();
            entry[2] = count.getValue();
            for (Object[] fromTemp: temporaryList) {
                Product product = (Product)fromTemp[0];
                if (product.getItemCode().equalsIgnoreCase((String)entry[0])) {
                    entry[1] = product.getCost();
                    break;
                }
                entry[1] = 0;
            }
            entries.add(entry);
        }
        return new Account(accountCode, description, date, entries);
    }


    //MODIFIES: this
    //EFFECTS: will update the ledger with the latest info.
    private boolean updateLedger(String description) {
        Account account = createAccount(nextAccountNumber++, description, currentDate);
        ledger.add(account);
        return true;
    }

    //EFFECTS: create and return a list of information labels for each account existing in the ledger.
    public ArrayList<String> getAccounts() {
        ArrayList<String> list = new ArrayList<>();
        for (Account account: ledger) {
            String s = "Account code: " + account.getCode() + '\n';
            s += "Generated date: " + account.getDate() + '\n';
            s += "extra info available: ";
            if (account.getDescription().length() > 0) {
                s += account.getDescription();
            } else {
                s += "none\n";
            }
            s += "Total quantity processed: " + account.getTotalQuantity() + '\n';
            s += "Total dollar worth: " + account.getDollarAmount() + '\n';
            //s += "Product code with price and quantity: \n";
            list.add(s);
            list.add("-----------------------------------\n");
        }
        return list;
    }

    //EFFECTS: if there is an account with the specified account code,
    //make a detailed label for that particular account,
    //each element of the returned list will contain each line of the label.
    //If there isn't, list will just contain information that there isn't such an account.
    public ArrayList<String> checkAccount(int accountCode) {
        ArrayList<String> list = new ArrayList<>();
        Account account = null;
        for (Account e: ledger) {
            if (e.getCode() == accountCode) {
                account = e;
                break;
            }
        }
        if (account == null) {
            list.add("There is no such account with the code");
        }
        assert account != null;
        for (Object[] fromAccount: account.getEntries()) {
            String s = "Product code: " + fromAccount[0] + '\n';
            s += "Price: " + fromAccount[1] + '\n';
            s += "Total quantity processed: " + fromAccount[2] + '\n';
            s += "Total dollar worth: " + account.getTotalCost((String)fromAccount[0]) + '\n';
            list.add(s);
            list.add("-----------------------------------\n");
        }
        return list;
    }




    //Will create 9digit SKU. The first digit will start from 0.
    //EFFECTS: return a new 9 digit SKU if next sku number is less than or equal to 999999999.
    //If SKU overflows, set sku to 1.
    public int createSku() {
        if (nextSKU > MAX_SKU) {
            nextSKU = FIRST_SKU;
        }
        return nextSKU++;
    }


    //EFFECTS: return the number of products belonging to the item code.
    public int countProduct(String itemCode) {
        return inventory.getQuantity(itemCode);
    }


    //EFFECTS: return a list of labels that indicate locations of products belonging to the item code.
    public ArrayList<String> getLocationListOfProduct(String itemCode) {
        ArrayList<Integer> numericList = inventory.findLocations(itemCode);
        ArrayList<String> locationList = new ArrayList<>();
        for (Integer e: numericList) {
            locationList.add(inventory.getStringLocationCode(e));
        }
        return locationList;
    }

    //REQUIRES: item code must be in a valid form. sku must not be negative.
    //EFFECTS: return the location of the product specified by the code and SKU.
    public String getLocationOfProduct(String itemCode, int sku) {
        return inventory.getStringLocationCode(inventory.findLocation(itemCode, sku));
    }


    //EFFECTS: return true if the id exist in the admin and pw matches the id.
    //return false otherwise.
    public boolean adminAccountCheck(String id, String pw) {
        return admin.checkLoginAccount(id, pw);
    }

    //EFFECTS: return the password matching the id if the given information can be found.
    //return null if it cannot be found
    public String retrievePassword(String id, String name, LocalDate birthDay, int personalCode) {
        return admin.retrievePassword(id, name, birthDay, personalCode);
    }


    //REQUIRES: id must be composed of english letters and digits only. id must typed case sensitively.
    //id cannot already be existing.
    //name must be composed of only english alphabets.
    //personal code cannot be negative.
    //MODIFIES: this
    //EFFECTS: creates a new login account with the given info.
    //if it succeeds creating a new login account, return true.
    //Otherwise, return false.
    public boolean createLoginAccount(String id, String pw, String name, LocalDate birthDay, int personalCode) {
        return admin.createLoginAccount(id, pw, name, birthDay, personalCode);
    }

    //return info that contains quantities, item codes existing in the inventory.
    //EFFECTS: return a string that contains general information about the inventory
    public String inventoryCheck() {
        if (inventory.getTotalQuantity() == 0) {
            return "Inventory is empty";
        }
        String info = "The inventory contains a total number of " + inventory.getTotalQuantity() + " of products";
        info += '\n' + "The item code list created is as follows: ";
        info += '\n';
        ArrayList<String> codes = inventory.getListOfCodes();
        if (codes.isEmpty()) {
            System.out.println("The inventory is empty");
        }
        for (String e: codes) {
            info += e + " ";
        }
        return info;
    }


}
