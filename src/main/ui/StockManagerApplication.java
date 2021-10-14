package ui;

import model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class StockManagerApplication {



    public static void findLocations(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        ArrayList<String> locationList = stockManager.getLocationListOfProduct(itemCode);
        System.out.println("The products belonging to the code are stored at: ");
        for (String e: locationList) {
            System.out.print(e + " ");
        }
        System.out.println();
    }

    public static void findProduct(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter the SKU of the product");
        int sku = scanner.nextInt();
        scanner.nextLine();
        String location = stockManager.getLocationOfProduct(itemCode, sku);
        if (location != null) {
            System.out.println("The product: " + itemCode + sku + " is located at " + location);
        } else {
            System.out.println("The product doesn't exist in the warehouse");
        }
    }



    public static void checkQuantity(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        int qty = stockManager.countProduct(itemCode);
        System.out.println("The quantity of " + qty + " of item code" + itemCode + " is stored in the inventory");
    }

    public static void checkInventory(Manager stockManager) {
        System.out.println(stockManager.inventoryCheck());
    }

    public static void printTemporaryList(Manager stockManager) {
        ArrayList<Object[]> list = stockManager.getTemporaryList();
        for (Object[] productInfo : list) {
            Product product = (Product)productInfo[0];
            String location = (String)productInfo[1];
            System.out.println(product.getItemCode() + " " + product.getSku());
            System.out.println("Cost: " + product.getCost());
            if (product.getBestBeforeDate() != null) {
                System.out.print(product.getBestBeforeDate());
            }
            System.out.println("Location: " + (location.equalsIgnoreCase("T")
                    ? "Temporary storage space in the inventory" : location));
        }
        System.out.println();
    }

    public static void createLoginAccount(Manager stockManager, Scanner scanner) {
        String id;
        System.out.println("enter ID");
        id = scanner.nextLine();
        System.out.println("enter PW");
        String pw = scanner.nextLine();
        System.out.println("enter your name");
        String name = scanner.nextLine();
        System.out.println("enter your birth day in the form: YYYY MM DD");
        int year = scanner.nextInt();
        int month = scanner.nextInt();
        int day = scanner.nextInt();
        LocalDate birthDay = LocalDate.of(year, month, day);
        System.out.println("enter your personal code");
        int personalCode = scanner.nextInt();
        if (stockManager.createLoginAccount(id, pw, name, birthDay, personalCode)) {
            System.out.println("Successfully created");
        } else {
            System.out.println("Error occurred");
        }
        scanner.nextLine();
    }

    public static void updateInventory(Manager stockManager, Scanner scanner) {
        System.out.println("Would you add extra info about this update?"
                + "if yes, enter a line of description. If no, enter n");
        String description = scanner.nextLine();
        if (description.equalsIgnoreCase("n")) {
            stockManager.updateInventory("");
        } else {
            stockManager.updateInventory(description);
        }
        System.out.println("Successfully updated");
    }

    public static void removeProduct(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter SKU of the product");
        int sku = scanner.nextInt();
        scanner.nextLine();
        if (stockManager.removeProduct(itemCode, sku)) {
            System.out.println("Successfully removed");
        } else {
            System.out.println("Failed removing. the product cannot be found in the list");
        }
    }


    public static void removeProducts(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        System.out.println("enter quantity");
        int qty = scanner.nextInt();
        if (stockManager.removeProducts(itemCode, qty)) {
            System.out.println("Failed removing. the product cannot be found in the list");
        }
    }

    public static void removeProductFromTemporary(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.nextLine();
        if (stockManager.removeProductFromTemporaryList(itemCode)) {
            System.out.println("Successfully removed");
            return;
        }
        System.out.println("Failed removing. the product cannot be found in the list");
    }


    public static void retrievePassword(Manager stockManager, Scanner scanner) {
        System.out.println("enter id");
        String id = scanner.nextLine();
        System.out.println("enter name");
        String name = scanner.nextLine();
        System.out.println("enter birthday in YYYY MM DD form");
        LocalDate birthday = LocalDate.of(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
        System.out.println("enter personal code");
        int personalNum = scanner.nextInt();
        String retrieved = stockManager.retrievePassword(id, name, birthday, personalNum);
        if (retrieved != null) {
            System.out.println(retrieved);
        } else {
            System.out.println("No account exists with such information");
        }
    }








    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public static void createProduct(Manager stockManager, Scanner scanner) {
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
        for (int i = 0; i < qty; i++) {
            stockManager.createProduct(itemCode, bestBeforeDate, cost, location);
        }
        System.out.println("The product has been successfully created" + '\n' + "Current temporary List: ");
        printTemporaryList(stockManager);
        System.out.println("Would you like to add another product? enter Y/N");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            createProduct(stockManager, scanner);
        }
    }


    public static void openLedger(Manager stockManager, Scanner scanner) {
        System.out.println(stockManager.getAccounts());
        System.out.println("If you'd like to check a particular account more in detail, enter the account code "
                + "Otherwise, enter q");
        String option = scanner.nextLine();
        while (!option.equalsIgnoreCase("q")) {
            int accountCode = Integer.parseInt(option);
            System.out.println(stockManager.checkAccount(accountCode));
            System.out.println("If you'd like to check a particular account more in detail, enter the account code "
                    + "Otherwise, enter q");
            option = scanner.nextLine();
        }
    }


    public static boolean promptLogin(Manager stockManager, Scanner scanner) {
        boolean login = false;
        System.out.println("Please sign in first");
        System.out.println("Enter ID");
        String id = scanner.nextLine();
        System.out.println("Enter PW");
        String pw = scanner.nextLine();
        if (stockManager.adminAccountCheck(id, pw)) {
            login = true;
        } else {
            System.out.println("Login failed");
        }
        return login;
    }

    public static void printOptions() {
        System.out.println("Please select an option:");
        System.out.println("createAccount: create a new login account");
        System.out.println("create: create a new product");
        System.out.println("update: store the newly created items in the storage");
        System.out.println("removeI: remove a certain number of products from the inventory that is "
                + "belonging to the specified item code");
        System.out.println("removeT: remove a product belonging "
                + "to the specified item code from the temporary storage");
        System.out.println("findLocations: find locations of products belonging to a specific item code");
        System.out.println("find: find the location of a particular product with its product code and sku");
        System.out.println("logout: Log out");
    }









    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public static void main(String[] args) {
        Manager stockManager = new Manager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please create a login account first");
        createLoginAccount(stockManager, scanner);
        printOptions();
        String option = scanner.nextLine();
        boolean login = true;
        while (!option.equalsIgnoreCase("q")) {
            if (!login) {
                login = promptLogin(stockManager, scanner);
            } else {
                System.out.println("Enter option");
                System.out.println("If you need help, press h");
                option = scanner.nextLine();
                switch (option) {
                    case "createAccount":
                        createLoginAccount(stockManager, scanner);
                        break;
                    case "create":
                        createProduct(stockManager, scanner);
                        break;
                    case "update":
                        updateInventory(stockManager, scanner);
                        break;
                    case "removeI":
                        removeProducts(stockManager, scanner);
                        break;
                    case "removeProduct":
                        removeProduct(stockManager, scanner);
                        break;
                    case "removeT":
                        removeProductFromTemporary(stockManager, scanner);
                        break;
                    case "findLocations":
                        findLocations(stockManager, scanner);
                        break;
                    case "findProduct":
                        findProduct(stockManager, scanner);
                        break;
                    case "checkQuantity":
                        checkQuantity(stockManager, scanner);
                        break;
                    case "checkI":
                        checkInventory(stockManager);
                        break;
                    case "retrievePW":
                        retrievePassword(stockManager, scanner);
                        break;
                    case "openLedger":
                        openLedger(stockManager, scanner);
                        break;
                    case "logout":
                        login = false;
                        break;
                    case "h":
                        printOptions();
                }
            }
        }
    }

}
