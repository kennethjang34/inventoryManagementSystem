package ui.inventorypanel;

import model.Inventory;
import ui.inventorypanel.controller.InventoryController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//represents a panel that can generate items
public class ItemGenerator extends JPanel implements ActionListener {
    private Inventory inventory;
    private JTextField idField = new JTextField(10);
    private JTextField nameField = new JTextField(10);
    private JTextField categoryField = new JTextField(10);
    private JTextField priceField = new JTextField(10);
    private JTextField description = new JTextField(10);
    private JTextField note = new JTextField(10);
    private JButton button = new JButton("Create");
    private List<JComponent> components = new ArrayList<>();

    //EFFECTS: create a new panel that generates items
    public ItemGenerator() {
        JPanel fieldPanel = new JPanel();
        initializeFieldPanel(fieldPanel);
        //button.addActionListener(stockPanel);
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        add(fieldPanel, gc);
        gc.gridx = 0;
        gc.gridy = 1;
        add(button, gc);
        components.add(idField);
        components.add(nameField);
        components.add(categoryField);
        components.add(priceField);
        components.add(description);
        components.add(note);
        components.add(button);

    }


//    public void setButtonActionListener(ActionListener actionListener) {
//        button.addActionListener(actionListener);
//    }

    public JButton getButton() {
        return button;
    }

    //MODIFIES: this
    //EFFECTS: initialize the field panel
    private void initializeFieldPanel(JPanel fieldPanel) {
        fieldPanel.setLayout(new GridLayout(6, 2));
        fieldPanel.add(new JLabel("ID: "));
        fieldPanel.add(idField);
        fieldPanel.add(new JLabel("NAME: "));
        fieldPanel.add(nameField);
        fieldPanel.add(new JLabel("CATEGORY: "));
        fieldPanel.add(categoryField);
        fieldPanel.add(new JLabel("LIST PRICE: "));
        fieldPanel.add(priceField);
        fieldPanel.add(new JLabel("DESCRIPTION: "));
        fieldPanel.add(description);
        fieldPanel.add(new JLabel("NOTE: "));
        fieldPanel.add(note);

    }

    //MODIFIES: this
    //EFFECTS: clear all fields of field panel
    public void clearFields() {
        idField.setText("");
        categoryField.setText("");
        description.setText("");
        note. setText("");
        priceField.setText("");
        nameField.setText("");
    }

    //MODIFIES: this
    //EFFECTS: when the button is pressed, attempt to create a new item
    //If error happens, display proper error messages
    @Override
    public void actionPerformed(ActionEvent e) {
        String id = idField.getText().toUpperCase();
        String category = categoryField.getText().toUpperCase();
        double listPrice;
        try {
            listPrice = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException exception) {
            listPrice = 0;
        }
        if (id.equals("") || inventory.containsItem(id)) {
            JOptionPane.showMessageDialog(null,
                    "ID is invalid or duplicate");
        } else if (!inventory.containsCategory(category)) {
            JOptionPane.showMessageDialog(null,
                    "The given category: " + category + " is invalid");
        } else {
            inventory.createItem(id, nameField.getText(), category,
                    listPrice, description.getText(), note.getText()
            );
//            stockPanel.itemAddedUpdate(id);
            JOptionPane.showMessageDialog(null,
                    "Item: " + id + " has been successfully created");
        }
        clearFields();
    }

    public String getPriceFieldValue() {
        return priceField.getText();
    }

    public String getIDFieldValue() {
        return idField.getText();
    }

    public String getNameFieldValue() {
        return nameField.getText();
    }

    public String getCategoryFieldValue() {
        return categoryField.getText();
    }

    public String getDescriptionFieldValue() {
        return description.getText();
    }

    public String getNoteFieldValue() {
        return note.getText();
    }

    public void setAction(Action textFieldAction, Action buttonAction) {
        for (JComponent component: components) {
            if (component instanceof JButton) {
                ((JButton) component).setAction(buttonAction);
            } else if (component instanceof JTextField) {
                ((JTextField) component).setAction(textFieldAction);
            }
        }
    }
}
