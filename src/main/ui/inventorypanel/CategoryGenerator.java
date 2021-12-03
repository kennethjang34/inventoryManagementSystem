package ui.inventorypanel;

import javafx.scene.control.ButtonType;
import model.Inventory;
import ui.View;
import ui.inventorypanel.stockpanel.StockPanel;

import javax.swing.*;
import java.beans.PropertyChangeEvent;

//represents a panel that makes it possible to create a new category as requested by the user
public class CategoryGenerator extends JPanel {
    private JTextField categoryField = new JTextField(10);
    private JLabel categoryLabel = new JLabel("Category name: ");
    private JButton button = new JButton("create");
//    private StockSearchPanel searchPanel;
//    private JTextField itemField = new JTextField(10);
//    private JLabel itemLabel = new JLabel("Item name: ");
//    private JLabel
//    private JButton itemButton = new JButton("create");
//    private Inventory inventory;

    //EFFECTS: create a panel that helps the user create a new category
    public CategoryGenerator(Inventory inventory) {
        add(categoryLabel);
        add(categoryField);
        add(button);
        //this is a job for the inventory controller
        button.addActionListener(e -> {
            String name = categoryField.getText();
            name = name.toUpperCase();
            if (name.equals("")) {
                JOptionPane.showMessageDialog(null, "Category name cannot be empty");
                return;
            }
            categoryField.removeAll();
            if (inventory.createCategory(name)) {
                //stockPanel.categoryAddedUpdate(name);
                JOptionPane.showMessageDialog(null, "New Category: "
                        + name + " has been successfully created");
            } else {
                JOptionPane.showMessageDialog(null, "The category with the name: "
                        + name + " is already existing");
            }
        });
    }

    public CategoryGenerator() {
        add(categoryLabel);
        add(categoryField);
        add(button);
    }

    //EFFECTS: return the button of this
    public JButton getButton() {
        return button;
    }

    //EFFECTS; return the category field
    public JTextField getCategoryField() {
        return categoryField;
    }
}
