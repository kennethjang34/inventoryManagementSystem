package ui;

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
        setPreferredSize(new Dimension(500, 600));
        add(new JLabel("ID: "));
        add(idField);
        add(new JLabel("NAME: "));
        add(nameField);
        add(new JLabel("CATEGORY: "));
        add(categoryField);
        add(new JLabel("LIST PRICE: "));
        add(priceField);
        add(new JLabel("DESCRIPTION: "));
        add(description);
        add(new JLabel("NOTE: "));
        add(note);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String category = categoryField.getText();
                if (inventory.containsItem(id)) {
                    JOptionPane.showMessageDialog(null,
                            "There is already an item with id: " + id);
                } else if (!inventory.containsCategory(category)) {
                    JOptionPane.showMessageDialog(null,
                            "There is no such category. " + category);
                } else {
                    inventory.createItem(id, nameField.getName(), category,
                            Integer.parseInt(priceField.getText()), description.getText(), note.getText()
                    );
                }
            }
        });
        add(button);
    }
}
