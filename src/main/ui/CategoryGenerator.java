package ui;

import model.Inventory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoryGenerator extends JPanel {
    private JTextField categoryField = new JTextField(10);
    private JLabel categoryLabel = new JLabel("Category name: ");
    private JButton button = new JButton("create");
    private Inventory inventory;
//    private JTextField itemField = new JTextField(10);
//    private JLabel itemLabel = new JLabel("Item name: ");
//    private JLabel
//    private JButton itemButton = new JButton("create");
//    private Inventory inventory;

    public CategoryGenerator(Inventory inventory) {
        this.inventory = inventory;
        add(categoryLabel);
        add(categoryField);
        add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = categoryField.getText();
                categoryField.removeAll();
                if (inventory.createCategory(name)) {
                    JOptionPane.showMessageDialog(null, "New Category: "
                            + name + " has been successfully created");
                } else {
                    JOptionPane.showMessageDialog(null, "The category with the name: "
                            + name + " is already existing");
                }
            }
        });
    }
}
