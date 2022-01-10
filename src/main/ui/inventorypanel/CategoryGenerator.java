package ui.inventorypanel;

import model.Inventory;
import ui.inventorypanel.controller.InventoryController;
import ui.inventorypanel.view.InventoryViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//represents a panel that makes it possible to create a new category as requested by the user
public class CategoryGenerator extends JPanel {
    private JTextField categoryField = new JTextField(10);
    private JLabel categoryLabel = new JLabel("Category name: ");
    private JButton button = new JButton("create");


    public CategoryGenerator(InventoryViewPanel viewPanel) {
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
        Action action = new AbstractAction("Create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = categoryField.getText();
                viewPanel.getController().categoryGeneratorButtonClicked(name);
            }
        };
        setAction(action);
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
