package ui;

import model.Product;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void printTemporaryList(Manager stockManager) {
        ArrayList<Product> list = stockManager.getTemporaryList();
        for (Product e: list) {
            System.out.println(e.getItemCode() + " " + e.getSku());
            System.out.println("Cost: " + e.getCost());
            if (e.getBestBeforeDate() != null) {
                System.out.print(e.getBestBeforeDate());
            }
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
        stockManager.createLoginAccount(id, pw, name, birthDay, personalCode);
        System.out.println("Successfully created");
        scanner.nextLine();
    }

    public static void updateInventory(Manager stockManager, Scanner scanner) {
        System.out.println("Would you like to specify location?");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            if (stockManager.updateInventory(scanner.next())) {
                System.out.println("Successfully updated");
                return;
            }
        } else {
            if (stockManager.updateInventory()) {
                System.out.println("Successfully updated");
                return;
            }
        }
        System.out.println("update failed");
    }

    public static void removeProducts(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.next();
        System.out.println("enter quantity");
        int qty = scanner.nextInt();
        if (stockManager.removeProducts(itemCode, qty)) {
            System.out.println("Failed removing. the product cannot be found in the list");
        }
    }

    public static void removeProductFromTemporary(Manager stockManager, Scanner scanner) {
        System.out.println("enter the item code");
        String itemCode = scanner.next();
        if (stockManager.removeProductFromTemporaryStorage(itemCode)) {
            System.out.println("Successfully removed");
            return;
        }
        System.out.println("Failed removing. the product cannot be found in the list");
    }

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
        if (stockManager.createProduct(itemCode, bestBeforeDate, cost)) {
            System.out.println("The product has been successfully created");
            System.out.println("Current temporary storage situation");
            printTemporaryList(stockManager);
            System.out.println("Would you like to put all the products created  into the inventory? enter Y/N");
            if (scanner.nextLine().equalsIgnoreCase("Y")) {
                updateInventory(stockManager, scanner);
            }
            return;
        }
        System.out.println("An error occurred");
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
            } else {
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
                    case "removeT":
                        removeProductFromTemporary(stockManager, scanner);
                        break;

                    case "logout":
                        login = false;
                }
            }
            printOptions();
            option = scanner.nextLine();

        }
    }

}
