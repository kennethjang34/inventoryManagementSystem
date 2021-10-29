package ui;


import model.*;
import org.json.JSONObject;
import persistence.JsonConvertible;
import persistence.Reader;
import persistence.Writer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


//creates products based on the user's request and add it to inventory.
//for each load, create an account for record.
//make it possible to not just search for a specific product using Inventory class,
//but can check multiple products comparing them.
//Only a certain number of people who have login accounts in admin can use this
public class Manager implements JsonConvertible {
    private static final String fileLocation = "./data/inventory_management_system.json";
    private final Inventory inventory;
    private final Ledger ledger;
    private final Admin admin;
    //scanner will be used to receive input options from the user.
    private final Scanner scanner;
    //JSONObject where the data for the current manager was obtained
    //If there was no existing data, null
    private final JSONObject jsonObject;
    //temporary list is used to buffer a user's request for adding new products
    //Before the user manually select updating inventory option, newly created products will be stored in this list
    //for each element in the array list, the object array will contain
    //object[0]: Product
    //object[1]: String representing location
    //this object array will be called entry, and the list will often be called entries later
    private final ArrayList<InventoryTag> listToAdd;

    private final ArrayList<QuantityTag> listToRemove;

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
        jsonObject = null;
    }

    //REQUIRES: The data in JSON format must contain all the information for creating the manager with matching name
    //EFFECTS: create a manager with data in JSON format.
    public Manager(JSONObject json) {
        jsonObject = json;
        listToAdd = new ArrayList<>();
        listToRemove = new ArrayList<>();
        currentDate = LocalDate.now();
        currentProduct = null;
        scanner = new Scanner(System.in);
        inventory = new Inventory(json.getJSONObject("inventory"));
        ledger = new Ledger(json.getJSONObject("ledger"));
        admin = new Admin(json.getJSONObject("admin"));
    }



    //EFFECTS: return scanner
    public Scanner getScanner() {
        return scanner;
    }


    //REQUIRES: price, quantity must not be negative, item code must be in valid form
    //MODIFIES: this
    //EFFECTS: add a new product entry to the list to add to the inventory.
    //Note this method itself won't change inventory.
    private void addToListToAdd(String itemCode, double price,
                                LocalDate bestBeforeDate, String location, int qty) {
        if (!inventory.isValidItemCode(itemCode) || !inventory.isValidLocationCode(location)) {
            throw new IllegalArgumentException();
        }
        if (price < 0 || qty < 0) {
            throw new IllegalArgumentException();
        }
        itemCode = itemCode.toUpperCase();
        location = location.toUpperCase();
        InventoryTag inventoryTag = new InventoryTag(itemCode, price, bestBeforeDate, location, qty);
        listToAdd.add(inventoryTag);
    }


    //MODIFIES: this
    //EFFECTS: add a tag to the list so the specified products can be deleted from the inventory later.
    //Note this method itself won't change inventory.
    private void addToListToRemove(QuantityTag tag) {
        listToRemove.add(tag);
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
    private void updateInventory(String description) {
        if (listToAdd.size() == 0 && listToRemove.size() == 0) {
            throw new NothingToUpdateWithException();
        }
        inventory.addProducts(listToAdd);
        LinkedList<QuantityTag> receipt = inventory.removeProducts(listToRemove);
        LinkedList<QuantityTag> added = new LinkedList<>();
        for (InventoryTag tag: listToAdd) {
            added.add(new QuantityTag(tag.getItemCode(),tag.getLocation(), tag.getQuantity()));
        }
        updateLedger(added, receipt, description);
        listToAdd.clear();
        listToRemove.clear();
    }



    //MODIFIES: this
    //EFFECTS: add an account that contains information about the update of the inventory when it was called
    private void updateLedger(List<QuantityTag> added, LinkedList<QuantityTag> removed, String description) {
        ledger.addAccount(added, removed, description, currentDate);
    }



    //EFFECTS: create and return a label containing brief information for each account existing in the ledger.
    //if ledger is empty, empty string list will be returned
    private List<String> printGeneralAccountInfo() {
        ArrayList<String> list = new ArrayList<>();
        for (Account account: ledger.getAccounts()) {
            String s = "Account code: " + account.getCode() + "\n";
            s += "Generated date: " + account.getDate() + "\n";
            s += "extra info available: ";
            if (account.getDescription().length() > 0) {
                s += account.getDescription() + '\n';
            } else {
                s += "none\n";
            }
            s += "Total quantity processed: " + account.getTotalQuantity() + "\n";
            list.add(s);
            list.add("-----------------------------------\n");
        }
        return list;
    }

    //REQUIRES: accountCode cannot be negative
    //EFFECTS: if there is an account with the specified account code,
    //make a detailed label for that particular account (code, quantity, location, date)
    //each element of the returned list will contain each line of the label.
    //If there isn't, list will just contain information that there isn't such an account.
    private List<String> checkAccount(int accountCode) {
        if (accountCode < 0) {
            throw new IllegalArgumentException("Negative number cannot be an account code");
        }
        List<String> list = new ArrayList<>();
        Account account = null;
        for (Account e: ledger.getAccounts()) {
            if (e.getCode() == accountCode) {
                account = e;
                break;
            }
        }
        if (account == null) {
            list.add("There is no such account with the code");
        }
        assert account != null;
        list.addAll(account.getQuantitiesInfo());
        return list;
    }

    //EFFECTS: return a list of labels that indicate locations of products belonging to the item code.
    //If there isn't such products with the specified item code, return an empty list
    private List<String> getLocationListOfProduct(String itemCode) {
        itemCode = itemCode.toUpperCase();
        LinkedList<Integer> numericList = inventory.findLocations(itemCode);
        LinkedList<String> locationList = new LinkedList<>();
        for (Integer e: numericList) {
            if (inventory.getStringLocationCode(e).equalsIgnoreCase("T")) {
                locationList.add("Temporary storage space");
            } else {
                locationList.add(inventory.getStringLocationCode(e));
            }
        }
        return locationList;
    }

    //REQUIRES: item code must be in a valid form. sku must not be negative.
    //EFFECTS: return the location of the product specified by the code and SKU.
    //If no such product has been found, return null.
    private String getLocationOfProduct(String itemCode, int sku) {
        itemCode = itemCode.toUpperCase();
        ProductTag tag = inventory.findProduct(itemCode, sku);
        if (tag == null) {
            return null;
        }
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
    private String checkInventory() {
        if (inventory.getTotalQuantity() == 0) {
            return "Inventory is empty";
        }
        StringBuilder info = new StringBuilder("The inventory contains a total number of "
                + inventory.getTotalQuantity() + " of products");
        info.append('\n' + "Quantity of each item code: ");
        info.append('\n');
        List<String> codes = inventory.getListOfCodes();
        for (String code: codes) {
            info.append(code).append(": ").append(inventory.getQuantity(code)).append("\n");
        }
        return info.toString();
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
        List<String> locationList = this.getLocationListOfProduct(itemCode);
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
                    System.out.println("The cost of the product: " + currentProduct.getPrice());
                    break;
                case "h":
                    printProductOptions();
            }
            System.out.println("enter one of the options you'd like to use. If none, press q. For help, press h");
            option = scanner.nextLine();
        }
    }

    //EFFECTS: prompt the user to enter info for finding a particular product. Print location by default.
    private void promptFindProduct() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter the SKU of the product");
        int sku = scanner.nextInt();
        scanner.nextLine();
        if (!inventory.isValidItemCode(itemCode) || sku < 0) {
            System.out.println("Input is not valid");
            return;
        }
        currentProduct = inventory.getProduct(itemCode, sku);
        String location = this.getLocationOfProduct(itemCode, sku);
        if (location != null) {
            System.out.println("The product: " + itemCode.toUpperCase() + sku + " is located at " + location);
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
        List<QuantityTag> quantities = inventory.getQuantitiesAtLocations(itemCode);
        if (quantities == null) {
            System.out.println("There is no such products");
        } else {
            int totalQty = inventory.getQuantity(itemCode);

            System.out.println("Stocks in the inventory belonging to item code " + itemCode + ": " + totalQty);
            for (QuantityTag tag : quantities) {
                String location  = tag.getLocation();
                System.out.println(location + ": " + tag.getQuantity());
            }
        }
    }

    //EFFECTS: print the information of the inventory.
    private void printInventoryInfo() {
        System.out.println(checkInventory());
    }


    //EFFECTS: print the entries of the list of products to add
    private void printListToAdd() {
        for (InventoryTag tag: listToAdd) {
            String itemCode = tag.getItemCode().toUpperCase();
            int qty = tag.getQuantity();
            double price = tag.getPrice();
            LocalDate bestBeforeDate = tag.getBestBeforeDate();
            String location = tag.getLocation().toUpperCase();
            System.out.println(itemCode + " " + ", quantity: " + qty);
            System.out.println("Cost: " + price);
            if (bestBeforeDate != null) {
                System.out.println(bestBeforeDate);
            }
            System.out.println("Location assigned: " + (location.equalsIgnoreCase("T")
                    ? "Temporary storage space in the inventory" : location));
        }
    }

    //EFFECTS: print the entries of the list of products to remove
    private void printListToRemove() {
        for (QuantityTag tag: listToRemove) {
            String itemCode = tag.getItemCode().toUpperCase();
            String location = tag.getLocation().toUpperCase();
            int qty = tag.getQuantity();
            System.out.println(itemCode + " ");
            System.out.println("Location : " + (location.equalsIgnoreCase("T")
                    ? "Temporary storage space in the inventory" : location));
            System.out.println("Quantity: " + qty);
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

    //EFFECTS: prompt the user to enter a valid birthday in YYYY MM DD format until they enter a proper one
    //and return it
    private LocalDate promptEnterBirthday() {
        LocalDate birthDay = null;
        while (birthDay == null) {
            try {
                System.out.println("enter your birth day in the form: YYYY MM DD");
                int year = scanner.nextInt();
                int month = scanner.nextInt();
                int day = scanner.nextInt();
                birthDay = LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                System.out.println("the date info is incorrect");
            }
        }
        scanner.nextLine();
        return birthDay;
    }

    //MODIFIES: this
    //EFFECTS: create a new login account prompting the user to enter info to create the account.
    private void promptCreateLoginAccount() {
        String id;
        System.out.println("enter ID");
        id = scanner.nextLine();
        System.out.println("enter PW");
        String pw = scanner.nextLine();
        System.out.println("enter your name");
        String name = scanner.nextLine();
        LocalDate birthDay = promptEnterBirthday();
        System.out.println("enter your personal code");
        int personalCode = scanner.nextInt();
        while (personalCode < 0) {
            System.out.println("input is not valid");
            personalCode = scanner.nextInt();
        }
        if (this.createLoginAccount(id, pw, name, birthDay, personalCode)) {
            System.out.println("Successfully created");
            //saveAdmin();
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
        try {
            if (description.equalsIgnoreCase("n")) {
                this.updateInventory("");
            } else {
                this.updateInventory(description);
            }
            System.out.println("Successfully updated");
        } catch (NothingToUpdateWithException e) {
            System.out.println(e);
        }
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
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void promptRemoveProducts() {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        ArrayList<QuantityTag> tags = inventory.getQuantitiesAtLocations(itemCode);
        printTags(tags);
        while (tags == null) {
            System.out.println("enter the item code. If you'd like to quit, enter q");
            itemCode = scanner.nextLine();
            if (itemCode.equalsIgnoreCase("q")) {
                return;
            }
            tags = inventory.getQuantitiesAtLocations(itemCode);
            printTags(tags);
        }
        System.out.println("Choose the index. If you'd like to quit, please type q");
        String index = scanner.nextLine();
        while (!index.equalsIgnoreCase("q")) {
            try {
                QuantityTag tag = tags.get(Integer.parseInt(index));
                System.out.println("Enter the quantity. ");
                int quantity = scanner.nextInt();
                scanner.nextLine();
                while (quantity > tag.getQuantity()) {
                    System.out.println("Quantity too big. Enter quantity again");
                    quantity = scanner.nextInt();
                    scanner.nextLine();
                }
                addToListToRemove(new QuantityTag(tag.getItemCode(), tag.getLocation(), quantity));
                System.out.println("Successfully added to the list for removal");
                printTags(tags);
                System.out.println("Choose the index. If you'd like to quit, please type q");
                index = scanner.nextLine();
            } catch (NumberFormatException e) {
                System.out.println("You entered an invalid index");
                System.out.println("Choose the index. If you'd like to quit, please type q");
                index = scanner.nextLine();
            }
        }
    }


    //EFFECTS: print tags in the list. if the list is null or empty, print "nothing to print"
    private void printTags(ArrayList<QuantityTag> tags) {
        if (tags == null || tags.size() == 0) {
            System.out.println("Nothing to print");
        } else {
            for (QuantityTag tag : tags) {
                System.out.println(tag.toString());
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: remove a product from  list of products to add prompting the user to enter item code.
    private void promptRemoveProductFromListToAdd() {
        if (listToAdd.size() == 0) {
            System.out.println("There is no entry to remove in the list");
            return;
        }
        printListToAdd();
        System.out.println("enter the index of the entry. The indices start from 0. To end, enter non-digit");
        int index;
        try {
            index = scanner.nextInt();
        } catch (NumberFormatException e) {
            return;
        }
        scanner.nextLine();
        try {
            removeEntryFromListToAdd(index);
            System.out.println("Successfully removed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //MODIFIES: this
    //EFFECTS: remove a product from  list of products to remove prompting the user to enter item code.
    private void promptRemoveProductFromListToRemove() {
        if (listToRemove.size() == 0) {
            System.out.println("There is no entry to remove in the list");
            return;
        }
        printListToRemove();
        System.out.println("enter the index of the entry. The indices start from 0");
        int index = scanner.nextInt();
        scanner.nextLine();
        try {
            removeEntryFromListToRemove(index);
            System.out.println("Successfully removed");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //REQUIRES: index must be a valid one
    //MODIFIES: this
    //EFFECTS: remove an entry on the list of products to add
    private void removeEntryFromListToAdd(int index) throws Exception {
        try {
            listToAdd.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("There is no such entry");
        }
    }

    //REQUIRES: index must be a valid one
    //MODIFIES: this
    //EFFECTS: remove an entry on the list of products to remove
    private void removeEntryFromListToRemove(int index) throws Exception {
        try {
            listToRemove.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Exception("There is no such entry");
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
        scanner.nextLine();
        String retrieved = this.retrievePassword(id, name, birthday, personalNum);
        if (retrieved != null) {
            System.out.println(retrieved);
        } else {
            System.out.println("No account exists with such information");
        }
    }




    //EFFECTS: prompt the user to enter a best-before date of a product and return it
    private LocalDate promptEnterBestBeforeDate() {
        LocalDate bestBeforeDate = null;
        System.out.println("enter best-before date in the form: YYYY MM DD");
        int year = scanner.nextInt();
        int month = scanner.nextInt();
        int day = scanner.nextInt();
        while (bestBeforeDate == null) {
            try {
                bestBeforeDate = LocalDate.of(year, month, day);
            } catch (DateTimeException e) {
                System.out.println("the date is in invalid format");
                System.out.println("enter best-before date in the form: YYYY MM DD");
                year = scanner.nextInt();
                month = scanner.nextInt();
                day = scanner.nextInt();
            }
        }
        scanner.nextLine();
        return bestBeforeDate;
    }

    //EFFECTS: prompt the user to enter cost of the product and return it
    private double promptEnterCost() {
        double cost;
        System.out.println("enter cost");
        cost = scanner.nextDouble();
        while (cost < 0) {
            System.out.println("Cost cannot be negative");
            System.out.println("enter cost");
            cost = scanner.nextDouble();
        }
        scanner.nextLine();
        return cost;
    }

    //EFFECTS:prompt the user to enter quantity of the products and return it
    private int promptEnterQty() {
        System.out.println("enter quantity");
        int qty = scanner.nextInt();
        while (qty < 0) {
            System.out.println("Quantity cannot be negative");
            System.out.println("enter cost");
            qty = scanner.nextInt();
        }
        scanner.nextLine();
        return qty;
    }

    //EFFECTS: prompt the user to enter itemCode until they enter a valid item code and return it.
    private String promptEnterValidItemCode() {
        String itemCode;
        System.out.println("enter itemCode");
        itemCode = scanner.nextLine();
        while (!inventory.isValidItemCode(itemCode)) {
            System.out.println("the item code is in invalid format");
            System.out.println("enter itemCode");
            itemCode = scanner.nextLine();
        }

        return itemCode;
    }

    //prompt the user to enter location of the product to be assigned until they enter a proper location
    // and return it.
    private String promptEnterLocation() {
        System.out.println("enter Location in the form: ADD, where A represents an alphabet, D represents a digit"
                + " if you'd like to put the product in the temporary storage area of the inventory "
                + "press 'T/t'");
        String location = scanner.nextLine();
        while (!inventory.isValidLocationCode(location)) {
            System.out.println("The location code is in invalid format");
            System.out.println("enter Location in the form: ADD, where A represents an alphabet, D represents a digit"
                    + " if you'd like to put the product in the temporary storage area of the inventory "
                    + "press 'T/t'");
            location = scanner.nextLine();
        }
        return location;
    }


    //MODIFIES: this
    //EFFECTS: create new products prompting the user to enter info for creating them.
    private void promptCreateProduct() {
        String itemCode;
        LocalDate bestBeforeDate = null;
        double cost;
        itemCode = promptEnterValidItemCode();
        System.out.println("Does the product have a best-before date? enter Y/N");
        if (scanner.nextLine().equalsIgnoreCase(("Y"))) {
            bestBeforeDate = promptEnterBestBeforeDate();
        }
        cost = promptEnterCost();
        int qty = promptEnterQty();
        String location = promptEnterLocation();
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
        System.out.println(this.printGeneralAccountInfo());
        System.out.println("If you'd like to check a particular account more in detail, enter the account code. "
                + "Otherwise, enter q");
        String option = scanner.nextLine();
        while (!option.equalsIgnoreCase("q")) {
            try {
                int accountCode = Integer.parseInt(option);
                System.out.println(checkAccount(accountCode));
                System.out.println("If you'd like to check a particular account more in detail, enter the account code."
                        + " Otherwise, enter q");
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
        System.out.println("Enter PW. If you cannot recall your password, enter \"retrieve\"");
        String pw = scanner.nextLine();
        if (pw.equalsIgnoreCase("retrieve")) {
            promptRetrievePassword();
            return promptLogin();
        }
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


    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("inventory", inventory.toJson());
        json.put("ledger", ledger.toJson());
        json.put("admin", admin.toJson());
        return json;
    }


    //MODIFIES: this
    //EFFECTS: save the current state of the application
    public void save() {
        try {
            Writer writer = new Writer(fileLocation);
            writer.write(this);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("The current file cannot be found");
        }
    }

    //MODIFIES: this
    //EFFECTS: save current login accounts in admin
//    public void saveAdmin() {
//
//       JSONObject jsonAdmin = admin.toJson();
//       jsonObject.put("admin", jsonAdmin);
//
//    }


    //MODIFIES: this
    //EFFECTS: change the inventory and ledger in the current json object storing data of the application
    //current state of the inventory and accounts in the ledger
//    public void saveInventory() {
//
//      JSONObject jsonInventory = inventory.toJson();
//      jsonObject.put("inventory", jsonInventory);
//      JSONObject jsonLedger = ledger.toJson();
//      jsonObject.put("ledger", jsonLedger);
//
//    }








    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public static void main(String[] args) {
        Manager stockManager = null;
        try {
            boolean login = false;
            try {
                Reader reader = new Reader(fileLocation);
                stockManager = new Manager(reader.read());
            } catch (IOException e) {
                System.out.println("No existing data can be found. please create a new inventory manager");
                stockManager = new Manager();
                System.out.println("Please create a login account first");
                stockManager.promptCreateLoginAccount();
                login = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Data for manager is in wrong format. please create a new inventory manager");
                stockManager = new Manager();
                System.out.println("Please create a login account first");
                stockManager.promptCreateLoginAccount();
                login = true;
            }
            Scanner scanner = stockManager.getScanner();
            String option = "h";
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
        } finally {
            if (stockManager != null) {
                stockManager.save();
            }
        }
    }
}
