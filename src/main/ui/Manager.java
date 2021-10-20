package ui;


import model.*;
import org.json.JSONObject;
import persistence.JsonConvertable;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;


//creates products based on the user's request and add it to inventory.
//for each load, create an account for record.
//make it possible to not just search for a specific product using Inventory class,
//but can check multiple products comparing them.
//Only a certain number of people who have login accounts in admin can use this
public class Manager implements JsonConvertable {
    private final Inventory inventory;
    private final Ledger ledger;
    private final Admin admin;
    //scanner will be used to receive input options from the user.
    private final Scanner scanner;

    //temporary list is used to buffer a user's request for adding new products
    //Before the user manually select updating inventory option, newly created products will be stored in this list
    //for each element in the array list, the object array will contain
    //object[0]: Product
    //object[1]: String representing location
    //this object array will be called entry, and the list will often be called entries later
    private final ArrayList<AdditionTag> listToAdd;

    private final ArrayList<RemovalTag> listToRemove;

    //current product represents a product that has been located from the user's request.
    //User will be given several options to check the product-specific information
    private Product currentProduct;
    private LocalDate currentDate;

    private final int accountSize = 6;
    private final int skuSize = 9;


    //EFFECTS: create a manager with an empty inventory, ledger, and admin.
    public Manager() {
        //scanner is set to receive inputs from console
        scanner = new Scanner(System.in);
        inventory = new Inventory();
        inventory.setSkuSize(skuSize);
        ledger = new Ledger(accountSize);
        admin = new Admin();
        listToAdd = new ArrayList<>();
        listToRemove = new ArrayList<>();
        currentDate = LocalDate.now();
        currentProduct = null;
    }



    //EFFECTS: return scanner
    public Scanner getScanner() {
        return scanner;
    }


    private void addToListToAdd(String itemCode, double price,
                                LocalDate bestBeforeDate, String location, int qty) {
        itemCode = itemCode.toUpperCase();
        AdditionTag additionTag = new AdditionTag(itemCode, price, bestBeforeDate, location, qty);
        listToAdd.add(additionTag);
    }


    //MODIFIES: this
    //EFFECTS: remove one product belonging to this item code from the temporary list.
    //return true if it succeeds. return false otherwise.
    private void addToListToRemove(String itemCode, int qty, String location) throws Exception {
        itemCode = itemCode.toUpperCase();
        listToRemove.add(new RemovalTag(itemCode, location, qty));
    }



    //MODIFIES:this
    //EFFECTS: remove a specific product that has this item code and SKU.
    //Return ture if it succeeds. return false otherwise.
    private boolean removeProduct(String itemCode, int sku) {
        itemCode = itemCode.toUpperCase();
        return inventory.removeProduct(itemCode, sku);
    }


    //Will update the inventory with a new list of products that have been created and list of products to be removed
    //MODIFIES: this
    //EFFECTS: products on the list to add will be added to the inventory,
    //products on the list to remove will be removed from the inventory
    //creating a new transaction account.
    //the two temporary lists (list to add, list to remove) will be cleared
    private boolean updateInventory(String description) {
        boolean succeed = true;
        inventory.addProducts(listToAdd);
        try {
            inventory.removeProducts(listToRemove);
        } catch (RemovalFailedException e) {
            succeed = false;
            listToRemove.removeAll(e.getFailedList());
        }
        updateLedger(description);
        listToAdd.clear();
        listToRemove.clear();
        return succeed;
    }

    //MODIFIES: account
    //EFFECTS: new entries for recording new products loaded into the inventory will be created
    //and added to the account.
    private void addEntriesForNewProducts(Account account) {
        LinkedList<AdditionTag> tags = new ArrayList<>();
        for (AdditionTag tag: listToAdd) {
            account.addEntries(tags);
        }
    }



    //REQUIRES: account code must be in a valid form. cannot be negative.
    //MODIFIES: this
    //EFFECTS: create a new transaction account.
    private Account createAccount(int accountCode, String description, LocalDate date) {
        if (listToAdd.size() == 0 && listToRemove.size() == 0) {
            return null;
        }
        Account account = new Account(accountCode, description, date);
        addEntriesForNewProducts(account);

        addEntriesForRemovedProducts(account);
        return account;
    }


    //MODIFIES: this
    //EFFECTS: will update the ledger with the latest info.
    private boolean updateLedger(String description) {
        ledger.addAccount(description, currentDate);
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
        if (accountCode < 0) {
            throw new IllegalArgumentException("Negative number cannot be an account code");
        }
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
        for (String code: account.getItemCodes()) {
            double price = account.getPrice(code);
            int qty = account.getQuantity(code);
            String s = "Item code: " + code + "\n" + "Price: " + price + "\n";
            s += "Total quantity processed: " + qty + "\n" + "Total dollar worth of this item: " + qty * price + "\n";
            list.add(s + "-----------------------------------\n");
        }
        return list;
    }





    //EFFECTS: return the number of products belonging to the item code.
    private int countProduct(String itemCode) {
        itemCode = itemCode.toUpperCase();
        return inventory.getQuantity(itemCode);
    }


    //EFFECTS: return a list of labels that indicate locations of products belonging to the item code.
    private LinkedList<String> getLocationListOfProduct(String itemCode) {
        itemCode = itemCode.toUpperCase();
        LinkedList<Integer> numericList = inventory.findLocations(itemCode);
        LinkedList<String> locationList = new LinkedList<>();
        for (Integer e: numericList) {
            locationList.add(inventory.getStringLocationCode(e));
        }
        return locationList;
    }

    //REQUIRES: item code must be in a valid form. sku must not be negative.
    //EFFECTS: return the location of the product specified by the code and SKU.
    private String getLocationOfProduct(String itemCode, int sku) {
        itemCode = itemCode.toUpperCase();
        ProductTag tag = inventory.findProduct(itemCode, sku);
        return tag.getLocation();
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
        LinkedList<String> codes = inventory.getListOfCodes();
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
        LinkedList<String> locationList = this.getLocationListOfProduct(itemCode);
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


    private void printListToAdd() {
        for (Map.Entry<String, LinkedList<ProductTag>> entry: listToAdd.entrySet()) {
            for (ProductTag tag : entry.getValue()) {
                Product product = tag.getProduct();
                String location = tag.getLocation();
                System.out.println(product.getItemCode() + " " + product.getSku());
                System.out.println("Cost: " + product.getCost());
                if (product.getBestBeforeDate() != null) {
                    System.out.println(product.getBestBeforeDate());
                }
                System.out.println("Location assigned: " + (location.equalsIgnoreCase("T")
                        ? "Temporary storage space in the inventory" : location));
            }
        }
    }

    private void printListToRemove() {
        for (Map.Entry<String, LinkedList<ProductTag>> entry: listToRemove.entrySet()) {
            for (ProductTag tag : entry.getValue()) {
                Product product = tag.getProduct();
                String location = tag.getLocation();
                System.out.println(product.getItemCode() + " " + product.getSku());
                System.out.println("Cost: " + product.getCost());
                if (product.getBestBeforeDate() != null) {
                    System.out.println(product.getBestBeforeDate());
                }
                System.out.println("Location : " + (location.equalsIgnoreCase("T")
                        ? "Temporary storage space in the inventory" : location));
            }
        }
    }

    //EFFECTS: print the information of the products on the temporary list of this.
    private void printTemporaryList() {
        System.out.println("The list of newly created products: ");
        printListToAdd();
        System.out.println("----------------------------------");
        System.out.println("The list of products to remove: ");
        printListToRemove();
        System.out.println("----------------------------------");
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
        if (removeProduct(itemCode, sku)) {
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
        try {
            int qty = scanner.nextInt();
            scanner.nextLine();
            addToListToRemove(itemCode, qty);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //MODIFIES: this
    //EFFECTS: remove a product from  list of products to add prompting the user to enter item code.
    private void promptRemoveProductFromListToAdd() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        itemCode = itemCode.toUpperCase();
        System.out.println("enter the quantity");
        int qty = scanner.nextInt();
        scanner.nextLine();
        try {
            removeEntryFromListToAdd(itemCode, qty);
            System.out.println("Successfully removed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //MODIFIES: this
    //EFFECTS: remove a product from  list of products to remove prompting the user to enter item code.
    private void promptRemoveProductFromListToRemove() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter the quantity");
        int qty = scanner.nextInt();
        scanner.nextLine();
        try {
            removeEntryFromListToRemove(itemCode, qty);
            System.out.println("Successfully removed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void removeEntryFromListToAdd(String itemCode, int qty) throws Exception {
        itemCode = itemCode.toUpperCase();
        LinkedList<ProductTag> tags = listToAdd.get(itemCode);
        if (tags == null) {
            throw new Exception("There is no such products with such an item code");
        }
        if (tags.size() < qty) {
            tags.clear();
        } else {
            tags.subList(0, qty).clear();
        }
    }

    private void removeEntryFromListToRemove(String itemCode, int qty) throws Exception {
        itemCode = itemCode.toUpperCase();
        LinkedList<ProductTag> tags = listToAdd.get(itemCode);
        if (tags == null) {
            throw new Exception("There is no such products with such an item code");
        }
        if (tags.size() < qty) {
            tags.clear();
        } else {
            tags.subList(0, qty).clear();
        }
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
        addToListToAdd(itemCode, cost, bestBeforeDate, location, qty);
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
            try {
                int accountCode = Integer.parseInt(option);
                System.out.println(checkAccount(accountCode));
                System.out.println("If you'd like to check a particular account more in detail, enter the account code."
                        + "Otherwise, enter q");
                option = scanner.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("The input doesn't contain a valid account number");
                option = scanner.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e);
                option = scanner.nextLine();
            }
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
        System.out.println("removeFLA: remove a product belonging "
                + "to the specified item code from the temporary list");
        System.out.println("removeFLR: remove a product belonging "
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


    @Override
    public JSONObject toJson() {
        JSONObject inventoryJson = inventory.toJson();
        JSONObject adminJson = inventory.toJson();

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
                    case "removeFLA":
                        stockManager.promptRemoveProductFromListToAdd();
                        break;
                    case "removeFLR":
                        stockManager.promptRemoveProductFromListToRemove();
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
