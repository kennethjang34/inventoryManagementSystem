package ui.inventorypanel.productpanel;

import model.Inventory;
import ui.InventoryManagementSystemApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//A panel that prompts the user to enter inputs for creating new products
public class AddPanel extends JPanel {
    private InventoryManagementSystemApplication application;
    Inventory inventory;
    //JTextField codeField = new JTextField(10);
    JTextField costField = new JTextField(10);
    JTextField bbdField = new JTextField(10);
    JTextField priceField = new JTextField(10);
    JTextField idField = new JTextField(10);
    JTextField locationField = new JTextField(10);
    JTextField quantityField = new JTextField(10);
    JTextField description = new JTextField(10);
    JButton button;
    private List<JTextField> textFields = new ArrayList<>(7);


    //EFFECTS: create a new panel that will pop up if the user attempts to add new stocks
    public AddPanel(Inventory inventory, JButton button, InventoryManagementSystemApplication application) {
        this.application = application;
        this.inventory = inventory;
        add(new JLabel("ID"));
        add(idField);
//        add(new JLabel("SKU: "));
//        add(codeField);
        add(new JLabel("Cost: "));
        add(costField);
        add(new JLabel("Price: "));
        add(priceField);
        add(new JLabel("Best-before date: "));
        add(bbdField);
        add(new JLabel("Location: "));
        add(locationField);
        add(new JLabel("Quantity: "));
        add(quantityField);
        add(new JLabel("Description: "));
        add(description);
        this.button = button;
        button.setText("Register");
//        button.addActionListener(this);
        add(button);
        setPreferredSize(new Dimension(700, 500));
        textFields.add(costField);
        textFields.add(bbdField);
        textFields.add(priceField);
        textFields.add(idField);
        textFields.add(locationField);
        textFields.add(quantityField);
        textFields.add(description);
    }

    public AddPanel(Inventory inventory) {
        this.inventory = inventory;
        add(new JLabel("ID"));
        add(idField);
//        add(new JLabel("SKU: "));
//        add(codeField);
        add(new JLabel("Cost: "));
        add(costField);
        add(new JLabel("Price: "));
        add(priceField);
        add(new JLabel("Best-before date: "));
        add(bbdField);
        add(new JLabel("Location: "));
        add(locationField);
        add(new JLabel("Quantity: "));
        add(quantityField);
        add(new JLabel("Description: "));
        add(description);
        this.button = new JButton();
        button.setText("Register");
//        button.addActionListener(this);
        add(button);
        setSize(600, 700);
        textFields.add(costField);
        textFields.add(bbdField);
        textFields.add(priceField);
        textFields.add(idField);
        textFields.add(locationField);
        textFields.add(quantityField);
        textFields.add(description);
    }

    //MODIFIES: this
    //EFFECTS: process string as cost and return a valid cost.
    //if any exception happens(ex. null or empty string), return 0
    public double convertToDoubleCost(String s) {
        double cost;
        try {
            cost = Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
        return cost;
    }

    //REQUIRES: ID must be valid
    //MODIFIES: this
    //EFFECTS: process string as price and return a valid price.
    //if any exception happens(ex. null or empty string), return list price of the item the product belongs to
    public double convertToDoublePrice(String s, String id) {
        double price;
        try {
            price = Double.parseDouble(s);
        } catch (Exception e) {
            return inventory.getListPrice(id);
        }
        return price;
    }

    //MODIFIES: this
    //EFFECTS: process string as location and return a valid location.
    public String trimLocationFormat(String s) {
        if (s == null || s.isEmpty()) {
            return "NOT SPECIFIED";
        }
        return s.toUpperCase();
    }


//    //MODIFIES: this
//    //EFFECTS: add a new Inventory tag and add it to the list to add
//    public void actionPerformed(ActionEvent e) {
////        String id = idField.getText().toUpperCase();
////
////        String location = trimLocationFormat(locationField.getText());
////        int qty = quantityField.getText().isEmpty() ? 0 : Integer.parseInt(quantityField.getText());
//        //The job done below is Data model's extended responsibility related to its property change listeners
////        if (qty > 0) {
////            InventoryTag tag = new InventoryTag(id, cost, price, LocalDate.now(), bestBeforeDate, location, qty);
////            if (inventory.addProducts(tag)) {
////                application.addAccount(tag, description.getText(), LocalDate.now());
////            }
////        }
//    }


    public String getId() {
        return idField.getText();
    }

    public String getDescription() {
        return description.getText();
    }

    public String getCostText() {
        return costField.getText();
    }

    public String getPriceText() {
        return priceField.getText();
    }

    public String getQuantityText() {
        return quantityField.getText();
    }

    public String getLocationText() {
        return locationField.getText();
    }

    public String getBestBeforeDateText() {
        return bbdField.getText();
    }

    public JButton getButton() {
        return button;
    }

    public List<JTextField> getTextFields() {
        return textFields;
    }

    public void setAction(Action textFieldAction, Action buttonAction) {
        for (JTextField textField: textFields) {
            textField.addActionListener(textFieldAction);
        }
        button.setAction(buttonAction);
    }

    public void addButtonActionListener(ActionListener listener) {
        button.addActionListener(listener);
    }

    public void clearFields() {
        for (JTextField textField: textFields) {
            textField.setText("");
        }
    }


//    //EFFECTS: create and display error message above this
//    public void displayErrorMessage(String message) {
//        JOptionPane.showMessageDialog(this, message);
//    }


}