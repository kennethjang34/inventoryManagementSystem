package ui.inventorypanel;

import model.Inventory;
import ui.inventorypanel.stockpanel.StockPanel;
import ui.inventorypanel.stockpanel.StockSearchPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//represents a panel that makes it possible to create a new category as requested by the user
public class CategoryGenerator extends JPanel {
    private JTextField categoryField = new JTextField(10);
    private JLabel categoryLabel = new JLabel("Category name: ");
    private JButton button = new JButton("create");
    private Inventory inventory;
//    private StockSearchPanel searchPanel;
//    private JTextField itemField = new JTextField(10);
//    private JLabel itemLabel = new JLabel("Item name: ");
//    private JLabel
//    private JButton itemButton = new JButton("create");
//    private Inventory inventory;

    //EFFECTS: create a panel that helps the user create a new category
    public CategoryGenerator(Inventory inventory, StockPanel stockPanel) {
        this.inventory = inventory;
        add(categoryLabel);
        add(categoryField);
        add(button);
        button.addActionListener(e -> {
            String name = categoryField.getText();
            if (name.equals("")) {
                JOptionPane.showMessageDialog(null, "Category name cannot be empty");
                return;
            }
            categoryField.removeAll();
            if (inventory.createCategory(name)) {
                stockPanel.categoryAddedUpdate(name);
                JOptionPane.showMessageDialog(null, "New Category: "
                        + name + " has been successfully created");
            } else {
                JOptionPane.showMessageDialog(null, "The category with the name: "
                        + name + " is already existing");
            }
        });
    }
}
