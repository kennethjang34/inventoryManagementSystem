package ui;

import javafx.geometry.HorizontalDirection;
import model.Inventory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ItemGenerator extends JPanel {
    private Inventory inventory;
    private JTextField idField = new JTextField(10);
    private JTextField nameField = new JTextField(10);
    private JTextField categoryField = new JTextField(10);
    private JTextField priceField = new JTextField(10);
    private JTextField description = new JTextField(10);
    private JTextField note = new JTextField(10);
    private JButton button = new JButton("Create");

    public ItemGenerator(Inventory inventory) {
        this.inventory = inventory;
        JPanel fieldPanel = new JPanel();
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
        button.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String category = categoryField.getText();
                if (id.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Every item needs ID");
                    //clearFields();
                } else if (category.equals("")) {
                    JOptionPane.showMessageDialog(null,
                            "Every item needs category");
                    //clearFields();
                }
                if (inventory.containsItem(id)) {
                    JOptionPane.showMessageDialog(null,
                            "There is already an item with id: " + id);
                    clearFields();
                } else if (!inventory.containsCategory(category)) {
                    JOptionPane.showMessageDialog(null,
                            "There is no such category. " + category);
                    //categoryField.removeAll();
                } else {
                    inventory.createItem(id, nameField.getText(), category,
                            Integer.parseInt(priceField.getText()), description.getText(), note.getText()
                    );
                    clearFields();
                    JOptionPane.showMessageDialog(null,
                            "Item: " + id + " has been successfully created");
                }
            }
        });
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        add(fieldPanel, gc);
        gc.gridx = 1;
        gc.gridy = 1;
        add(button, gc);
    }

    public void clearFields() {
        idField.removeAll();
        categoryField.removeAll();
        description.removeAll();
        note.removeAll();
        priceField.removeAll();
        nameField.removeAll();
    }
}
