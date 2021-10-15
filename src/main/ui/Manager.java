package ui;


import model.Account;
import model.Admin;
import model.Inventory;
import model.Product;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


//creates products based on the user's request and add it to inventory.
//for each load, create an account for record.
//make it possible to not just search for a specific product using Inventory class,
//but can check multiple products comparing them.
//Only a certain number of people who have login accounts in admin can use this
public class Manager {
    private final Inventory inventory;
    private final ArrayList<Account> ledger;
    private final Admin admin;
    //scanner will be used to receive input options from the user.
    private final Scanner scanner;

    //temporary list is used to buffer a user's request for adding new products
    //Before the user manually select updating inventory option, newly created products will be stored in this list
    //for each element in the array list, the object array will contain
    //object[0]: Product
    //object[1]: String representing location
    //this object array will be called entry, and the list will often be called entries later
    private final ArrayList<Object[]> temporaryList;

    //current product represents a product that has been located from the user's request.
    //User will be given several options to check the product-specific information
    private Product currentProduct;
    private LocalDate currentDate;
    //next sku is the sku number that will be assigned to the new product.
    private int nextSKU;
    //next account number is the account number that will be assigned to the new account.
    //Note this account refers to the account that represents an update history of the inventory,
    //not login account.
    private int nextAccountNumber;
    private final int maxSku = 999999999;
    private final int firstSku = 111111111;
    private final int firstAccountNumber = 111111;



    //EFFECTS: create a manager with an empty inventory, ledger, and admin.
    public Manager() {
        //scanner is set to receive inputs from console
        scanner = new Scanner(System.in);
        inventory = new Inventory();
        ledger = new ArrayList<>();
        admin = new Admin();
        temporaryList = new ArrayList<>();
        nextSKU = firstSku;
        nextAccountNumber = firstAccountNumber;
        currentDate = LocalDate.now();
        currentProduct = null;
    }


    //Caller needs to supply item code, best-before-date, and cost
    //best-before date can be any date, and even null,
    //but it is recommended to use a date today or later if there is best-before date
    //REQUIRES: item code must be in a valid form specified by the user, cost must be positive.
    //best-before date must be today or later than today
    //MODIFIES: this
    //EFFECTS: create a product according to the input information.
    public Product createProduct(String itemCode, LocalDate bestBeforeDate, double cost, String location) {
        int sku = createSku();
        itemCode = itemCode.toUpperCase();
        Product product = new Product(itemCode, sku, cost, currentDate, bestBeforeDate);
        Object[] entry = new Object[2];
        entry[0] = product;
        entry[1] = location;
        temporaryList.add(entry);
        return product;
    }

    //EFFECTS: return scanner
    public Scanner getScanner() {
        return scanner;
    }


    //EFFECTS: return temporary List with entries.
    private ArrayList<Object[]> getTemporaryList() {
        return temporaryList;
    }


    //MODIFIES: this
    //EFFECTS: remove one product belonging to this item code from the temporary list.
    //return true if it succeeds. return false otherwise.
    private boolean removeProductFromTemporaryList(String itemCode) {
        for (Object[] entry: temporaryList) {
            Product e = (Product)entry[0];
            if (e.getItemCode().equalsIgnoreCase(itemCode)) {
                temporaryList.remove(entry);
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
    private boolean removeProducts(String itemCode, int qty) {
        return inventory.removeProducts(itemCode, qty);
    }


    //MODIFIES:this
    //EFFECTS: remove a specific product that has this item code and SKU.
    //Return ture if it succeeds. return false otherwise.
    private boolean removeProduct(String itemCode, int sku) {
        Product product = inventory.getProduct(itemCode, sku);
        return inventory.removeProduct(product);
    }


    //Will update the inventory with a new list of products that have been created.
    //MODIFIES: this
    //EFFECTS: the new product list will be added to the inventory, creating a new transaction account.
    //the temporary list will be cleared.
    private boolean updateInventory(String description) {
        boolean succeed = updateLedger(description);
        inventory.addProducts(temporaryList);
        temporaryList.clear();
        return succeed;
    }

    //REQUIRES: temporary list of this cannot be null
    //EFFECTS: create a hashmap that has item codes as keys
    //and number of products belonging to those item code as value.
    private Map<String, Integer> makeTemporaryCountHash() {
        Map<String, Integer>  hash = new HashMap<>();
        for (Object[] fromTemp: temporaryList) {
            Product product = (Product)fromTemp[0];
            if (hash.containsKey(product.getItemCode())) {
                int existing = hash.get(product.getItemCode());
                hash.put(product.getItemCode(), existing + 1);
            } else {
                hash.put(product.getItemCode(), 1);
            }
        }
        return hash;
    }


    //REQUIRES: account code must be in a valid form. cannot be negative.
    //MODIFIES: this
    //EFFECTS: create a new transaction account.
    private Account createAccount(int accountCode, String description, LocalDate date) {
        Map<String, Integer>  hash = makeTemporaryCountHash();
        ArrayList<Object[]> entries = new ArrayList<>();
        if (hash.entrySet().size() == 0) {
            return null;
        }
        for (Map.Entry<String, Integer> count: hash.entrySet()) {
            Object[] entry = new Object[3];
            entry[0] = count.getKey();
            entry[2] = count.getValue();
            for (Object[] entryFromTemp: temporaryList) {
                Product product = (Product)entryFromTemp[0];
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


    //EFFECTS: create and return a label containing brief information for each account existing in the ledger.
    private ArrayList<String> getAccounts() {
        ArrayList<String> list = new ArrayList<>();
        for (Account account: ledger) {
            String s = "Account code: " + account.getCode() + "\n";
            s += "Generated date: " + account.getDate() + "\n";
            s += "extra info available: ";
            if (account.getDescription().length() > 0) {
                s += account.getDescription() + '\n';
            } else {
                s += "none\n";
            }
            s += "Total quantity processed: " + account.getTotalQuantity() + "\n";
            s += "Total dollar worth: " + account.getDollarAmount() + "\n";
            list.add(s);
            list.add("-----------------------------------\n");
        }
        return list;
    }

    //EFFECTS: if there is an account with the specified account code,
    //make a detailed label for that particular account,
    //each element of the returned list will contain each line of the label.
    //If there isn't, list will just contain information that there isn't such an account.
    private ArrayList<String> checkAccount(int accountCode) {
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
            String s = "Product code: " + fromAccount[0] + "\n";
            s += "Price: " + fromAccount[1] + "\n";
            s += "Total quantity processed: " + fromAccount[2] + "\n";
            s += "Total dollar worth: " + account.getTotalCost((String)fromAccount[0]) + "\n";
            list.add(s);
            list.add("-----------------------------------\n");
        }
        return list;
    }




    //Will create 9digit SKU. The first digit will start from 0.
    //EFFECTS: return a new 9 digit SKU if next sku number is less than or equal to 999999999.
    //If SKU overflows, set sku to 1.
    private int createSku() {
        if (nextSKU > maxSku) {
            nextSKU = firstSku;
        }
        return nextSKU++;
    }


    //EFFECTS: return the number of products belonging to the item code.
    private int countProduct(String itemCode) {
        return inventory.getQuantity(itemCode);
    }


    //EFFECTS: return a list of labels that indicate locations of products belonging to the item code.
    private ArrayList<String> getLocationListOfProduct(String itemCode) {
        ArrayList<Integer> numericList = inventory.findLocations(itemCode);
        ArrayList<String> locationList = new ArrayList<>();
        for (Integer e: numericList) {
            locationList.add(inventory.getStringLocationCode(e));
        }
        return locationList;
    }

    //REQUIRES: item code must be in a valid form. sku must not be negative.
    //EFFECTS: return the location of the product specified by the code and SKU.
    private String getLocationOfProduct(String itemCode, int sku) {
        return inventory.getStringLocationCode(inventory.findLocation(itemCode, sku));
    }


    //EFFECTS: return true if the id exist in the admin and pw matches the id.
    //return false otherwise.
    private boolean adminAccountCheck(String id, String pw) {
        return admin.checkLoginAccount(id, pw);
    }

    //EFFECTS: return the password matching the id if the given information can be found.
    //return null if it cannot be found
    private String retrievePassword(String id, String name, LocalDate birthDay, int personalCode) {
        return admin.retrievePassword(id, name, birthDay, personalCode);
    }


    //REQUIRES: id should be composed of english letters and digits only. id must be typed case sensitively.
    //the same id cannot already be existing.
    //name should be composed of only english alphabets.
    //personal code cannot be negative.
    //MODIFIES: this
    //EFFECTS: creates a new login account with the given info.
    //if it succeeds creating a new login account, return true.
    //Otherwise, return false.
    private boolean createLoginAccount(String id, String pw, String name, LocalDate birthDay, int personalCode) {
        return admin.createLoginAccount(id, pw, name, birthDay, personalCode);
    }

    //return info that contains quantities, item codes existing in the inventory.
    //EFFECTS: return a string that contains general information about the inventory
    private String inventoryCheck() {
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


    //EFFECTS: prompt the user to enter info for finding locations of an item code.
    private void promptFindLocations() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        if (inventory.getItemCodeNumber(itemCode) < 0
                || inventory.getItemCodeNumber(itemCode) > 26 * 26 * 25 + 26 * 25 + 25) {
            System.out.println("Input is not valid");
            return;
        }
        ArrayList<String> locationList = this.getLocationListOfProduct(itemCode);
        System.out.println("The products belonging to the code are stored at: ");
        for (String e: locationList) {
            System.out.print(e + " ");
        }
        System.out.println();
    }

    //EFFECTS: print the information about options for a single product
    private void printProductOptions() {
        System.out.println("code: print the product code, which is composed of its item code and SKU");
        System.out.println("dateG: print the date the product was created");
        System.out.println("dateB: print the best-before date of the product. "
                + "if the product doesn't have any, print failure statement");
        System.out.println("cost: print the cost the product was bought for");
        System.out.println("h: print info about options available");
    }


    //REQUIRES: current product must not be null
    //EFFECTS: prompt the user to choose an option to process with the current product found
    private void promptProductInfo() {
        String option = "h";
        while (!option.equalsIgnoreCase("q")) {
            switch (option) {
                case "code":
                    System.out.println("The product code: " + currentProduct.getItemCode() + currentProduct.getSku());
                    break;
                case "dateG":
                    System.out.println("The date generated: " + currentProduct.getDateGenerated().toString());
                    break;
                case "dateB":
                    System.out.println("The best before date: "
                            +
                            (currentProduct.getBestBeforeDate() == null ? "N/A" : currentProduct.getBestBeforeDate()));
                    break;
                case "cost":
                    System.out.println("The cost of the product: " + currentProduct.getCost());
                    break;
                case "h":
                    printProductOptions();
            }
            System.out.println("enter one of the options you'd like to use. If none, press q. For help, press h");
            option = scanner.nextLine();
        }
    }

    //EFFECTS: return the maximum numeric item code value possible
    private boolean isValidItemCode(String itemCode) {
        return inventory.isValidItemCode(itemCode);
    }




    //EFFECTS: prompt the user to enter info for finding a particular product. Print location by default.
    private void promptFindProduct() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter the SKU of the product");
        int sku = scanner.nextInt();
        scanner.nextLine();
        if (!isValidItemCode(itemCode) || sku < 0) {
            System.out.println("Input is not valid");
            return;
        }
        currentProduct = inventory.getProduct(itemCode, sku);
        String location = this.getLocationOfProduct(itemCode, sku);
        if (location != null) {
            System.out.println("The product: " + itemCode + sku + " is located at " + location);
            System.out.println("Would you like to check the product in detail ? press Y/N");
            if ((scanner.nextLine().equalsIgnoreCase("Y"))) {
                promptProductInfo();
            }
            return;
        }
        System.out.println("The product doesn't exist in the warehouse");

    }


    //EFFECTS: prompt the user to enter info for checking quantity of an item
    private void promptCheckQuantity() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        if (inventory.getItemCodeNumber(itemCode) < 0
                || inventory.getItemCodeNumber(itemCode) > 26 * 26 * 25 + 26 * 25 + 25) {
            System.out.println("Input is not valid");
            return;
        }
        int qty = this.countProduct(itemCode);
        System.out.println("The quantity of " + qty + " of item code " + itemCode + " is stored in the inventory");
    }

    //EFFECTS: print the information of the inventory.
    private void printInventoryInfo() {
        System.out.println(this.inventoryCheck());
    }

    //EFFECTS: print the information of the products on the temporary list of this.
    private void printTemporaryList() {
        ArrayList<Object[]> list = this.getTemporaryList();
        for (Object[] productInfo : list) {
            Product product = (Product)productInfo[0];
            String location = (String)productInfo[1];
            System.out.println(product.getItemCode() + " " + product.getSku());
            System.out.println("Cost: " + product.getCost());
            if (product.getBestBeforeDate() != null) {
                System.out.println(product.getBestBeforeDate());
            }
            System.out.println("Location: " + (location.equalsIgnoreCase("T")
                    ? "Temporary storage space in the inventory" : location));
        }
        System.out.println();
    }

    //MODIFIES: this
    //EFFECTS: create a new login account prompting the user to enter info to create the account.
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void promptCreateLoginAccount() {
        String id;
        System.out.println("enter ID");
        id = scanner.nextLine();
        System.out.println("enter PW");
        String pw = scanner.nextLine();
        System.out.println("enter your name");
        String name = scanner.nextLine();
        LocalDate birthDay = null;
        boolean error = true;
        while (error) {
            try {
                error = false;
                System.out.println("enter your birth day in the form: YYYY MM DD");
                int year = scanner.nextInt();
                int month = scanner.nextInt();
                int day = scanner.nextInt();
                birthDay = LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                System.out.println("the date info is incorrect");
                error = true;
            }
        }
        System.out.println("enter your personal code");
        int personalCode = scanner.nextInt();
        while (personalCode < 0) {
            System.out.println("input is not valid");
            personalCode = scanner.nextInt();
        }
        if (this.createLoginAccount(id, pw, name, birthDay, personalCode)) {
            System.out.println("Successfully created");
        } else {
            System.out.println("Error occurred");
        }
        scanner.nextLine();
    }

    //MODIFIES: this
    //EFFECTS: prompt the user to enter a description for this update, and update the inventory
    private void promptUpdateInventory() {
        System.out.println("Would you add extra info about this update?"
                + "if yes, enter a line of description. If no, enter n");
        String description = scanner.nextLine();
        if (description.equalsIgnoreCase("n")) {
            this.updateInventory("");
        } else {
            this.updateInventory(description);
        }
        System.out.println("Successfully updated");
    }

    //MODIFIES: this
    //EFFECTS: remove a product prompting the user to enter the item code and SKU of the product
    private void promptRemoveProduct() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter SKU of the product");
        int sku = scanner.nextInt();
        scanner.nextLine();
        if (this.removeProduct(itemCode, sku)) {
            System.out.println("Successfully removed");
        } else {
            System.out.println("Failed removing. the product cannot be found in the list");
        }
    }


    //MODIFIES: this
    //EFFECTS: remove multiple products prompting the user to enter item code and quantity to remove.
    private void promptRemoveProducts() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter quantity");
        int qty = scanner.nextInt();
        if (this.removeProducts(itemCode, qty)) {
            System.out.println("Failed removing. the product cannot be found in the list");
        }
    }

    //MODIFIES: this
    //EFFECTS: remove a product from temporary list of this prompting the user to enter item code.
    private void removeProductFromTemporary() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        if (this.removeProductFromTemporaryList(itemCode)) {
            System.out.println("Successfully removed");
            return;
        }
        System.out.println("Failed removing. the product cannot be found in the list");
    }

    //EFFECTS: prompt the user to enter info to retrieve password. if the info is correct, print the password
    //Otherwise, print failure statement.
    private void promptRetrievePassword() {
        System.out.println("enter id");
        String id = scanner.nextLine();
        System.out.println("enter name");
        String name = scanner.nextLine();
        System.out.println("enter birthday in YYYY MM DD form");
        LocalDate birthday = LocalDate.of(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
        System.out.println("enter personal code");
        int personalNum = scanner.nextInt();
        String retrieved = this.retrievePassword(id, name, birthday, personalNum);
        if (retrieved != null) {
            System.out.println(retrieved);
        } else {
            System.out.println("No account exists with such information");
        }
    }







    //MODIFIES: this
    //EFFECTS: create new products prompting the user to enter info for creating them.
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void promptCreateProduct() {
        String itemCode;
        LocalDate bestBeforeDate = null;
        double cost;
        System.out.println("enter itemCode");
        itemCode = scanner.nextLine();
        System.out.println("Does the product have a best-before date? enter Y/N");
        if (scanner.nextLine().equalsIgnoreCase(("Y"))) {
            System.out.println("enter best-before date in the form: YYYY MM DD");
            bestBeforeDate = LocalDate.of(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
        }
        System.out.println("enter cost");
        cost = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("enter quantity");
        int qty = scanner.nextInt();
        scanner.nextLine();
        System.out.println("enter Location in the form: ADD, where A represents an alphabet, D represents a digit"
                + " if you'd like to put the product in the temporary storage area of the inventory "
                + "press 'T/t'");
        String location = scanner.nextLine();
        location = inventory.getStringLocationCode(inventory.getLocationCodeNumber(location));

        for (int i = 0; i < qty; i++) {
            this.createProduct(itemCode, bestBeforeDate, cost, location);
        }
        System.out.println("The product has been successfully created" + '\n' + "Current temporary List: ");
        printTemporaryList();
        System.out.println("Would you like to add another product? enter Y/N");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            promptCreateProduct();
        }
    }


    //EFFECTS: print accounts inside the ledger
    //If user enters a specific account number, print the specified account in detail
    private void openLedger() {
        System.out.println(this.getAccounts());
        System.out.println("If you'd like to check a particular account more in detail, enter the account code. "
                + "Otherwise, enter q");
        String option = scanner.nextLine();
        while (!option.equalsIgnoreCase("q")) {
            int accountCode = Integer.parseInt(option);
            System.out.println(this.checkAccount(accountCode));
            System.out.println("If you'd like to check a particular account more in detail, enter the account code. "
                    + "Otherwise, enter q");
            option = scanner.nextLine();
        }
    }


    //EFFECTS: prompt the user to login. if it succeeds signing the user in, return true
    //Otherwise, return false
    private boolean promptLogin() {
        boolean login = false;
        System.out.println("Please sign in first");
        System.out.println("Enter ID");
        String id = scanner.nextLine();
        System.out.println("Enter PW");
        String pw = scanner.nextLine();
        if (this.adminAccountCheck(id, pw)) {
            login = true;
        } else {
            System.out.println("Login failed");
        }
        return login;
    }

    //EFFECTS: print options
    private static void printOptions() {
        System.out.println("createAccount: create a new login account");
        System.out.println("create: create a new product");
        System.out.println("update: store the newly created items in the storage");
        System.out.println("removeI: remove a certain number of products from the inventory that is "
                + "belonging to the specified item code");
        System.out.println("removeT: remove a product belonging "
                + "to the specified item code from the temporary list");
        System.out.println("findLocations: find locations of products belonging to a specific item code");
        System.out.println("findProduct: find the location of a particular product with its product code and sku");
        System.out.println("checkQuantity: check the quantity of the specified product");
        System.out.println("checkI: check the inventory condition");
        System.out.println("checkT: check the current temporary list");
        System.out.println("retrievePW: retrieve password");
        System.out.println("openLedger: open the ledger and print labels of accounts in the ledger");
        System.out.println("h: print options");
        System.out.println("logout: Log out");
    }


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public static void main(String[] args) {
        Manager stockManager = new Manager();
        Scanner scanner = stockManager.getScanner();
        System.out.println("Please create a login account first");
        stockManager.promptCreateLoginAccount();
        String option  = "h";
        boolean login = true;
        while (!option.equalsIgnoreCase("q")) {
            if (!login) {
                login = stockManager.promptLogin();
                if (login) {
                    printOptions();
                }
            } else {
                System.out.println("Please select an option");
                System.out.println("if you need help, press h");
                option = scanner.nextLine();
                switch (option) {
                    case "h":
                        printOptions();
                        break;
                    case "createAccount":
                        stockManager.promptCreateLoginAccount();
                        break;
                    case "create":
                        stockManager.promptCreateProduct();
                        break;
                    case "update":
                        stockManager.promptUpdateInventory();
                        break;
                    case "removeI":
                        stockManager.promptRemoveProducts();
                        break;
                    case "removeProduct":
                        stockManager.promptRemoveProduct();
                        break;
                    case "removeT":
                        stockManager.removeProductFromTemporary();
                        break;
                    case "findLocations":
                        stockManager.promptFindLocations();
                        break;
                    case "findProduct":
                        stockManager.promptFindProduct();
                        break;
                    case "checkQuantity":
                        stockManager.promptCheckQuantity();
                        break;
                    case "checkI":
                        stockManager.printInventoryInfo();
                        break;
                    case "checkT":
                        stockManager.printTemporaryList();
                        break;
                    case "retrievePW":
                        stockManager.promptRetrievePassword();
                        break;
                    case "openLedger":
                        stockManager.openLedger();
                        break;
                    case "logout":
                        login = false;
                        break;
                }
            }
        }
    }
}
