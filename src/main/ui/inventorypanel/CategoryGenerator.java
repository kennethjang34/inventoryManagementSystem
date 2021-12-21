package ui.inventorypanel;

import model.Inventory;

import javax.swing.*;
import java.awt.*;

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

    //Not ued
    //EFFECTS: create a panel that helps the user create a new category
    public CategoryGenerator(Inventory inventory) {
        //this is a job for the inventory controller
        add(categoryLabel);
        add(categoryField);
        add(button);

    }

    public CategoryGenerator() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(categoryLabel, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        add(categoryField, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        add(button, gbc);
    }

    //EFFECTS: return the button of this
    public JButton getButton() {
        return button;
    }

    //EFFECTS; return the category field
    public JTextField getCategoryField() {
        return categoryField;
    }

    public void setAction(Action action) {
        button.setAction(action);
        categoryField.setAction(action);
    }

    public void clearFields() {
        categoryField.setText("");
    }
}
