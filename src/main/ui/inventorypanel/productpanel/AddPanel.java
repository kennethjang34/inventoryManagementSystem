package ui.inventorypanel.productpanel;

import model.Inventory;
import model.InventoryTag;
import ui.InventoryManagementSystemApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

//A panel that prompts the user to enter inputs for creating new products
public class AddPanel extends JPanel implements ActionListener {
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
        button.setText("Register");
        button.addActionListener(this);
        add(button);
        setSize(600, 700);
    }

    //MODIFIES: this
    //EFFECTS: add a new Inventory tag and add it to the list to add
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText().toUpperCase();
        double cost = costField.getText().length() == 0 ? 0 : Double.parseDouble(costField.getText());
        double price = priceField.getText().length() == 0 ? 0 : Double.parseDouble(priceField.getText());
        LocalDate bestBeforeDate;
        String stringBBD = bbdField.getText();
        if (stringBBD.isEmpty()) {
            bestBeforeDate = null;
        } else {
            bestBeforeDate = InventoryManagementSystemApplication.convertToLocalDate(stringBBD);
        }
        String location = locationField.getText().toUpperCase();
        if (location.isEmpty()) {
            location = "NOT SPECIFIED";
        }
        int qty = quantityField.getText().isEmpty() ? 0 : Integer.parseInt(quantityField.getText());
        if (qty > 0) {
            InventoryTag tag = new InventoryTag(id, cost, price, LocalDate.now(), bestBeforeDate, location, qty);
            if (inventory.addProducts(tag)) {
                application.addAccount(tag, description.getText(), LocalDate.now());
            }
        }
    }

}