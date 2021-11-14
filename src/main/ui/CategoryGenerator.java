package ui;

import model.Inventory;
import ui.inventorypanel.stockpanel.StockSearchPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CategoryGenerator extends JPanel {
    private JTextField categoryField = new JTextField(10);
    private JLabel categoryLabel = new JLabel("Category name: ");
    private JButton button = new JButton("create");
    private Inventory inventory;
    private StockSearchPanel searchPanel;
//    private JTextField itemField = new JTextField(10);
//    private JLabel itemLabel = new JLabel("Item name: ");
//    private JLabel
//    private JButton itemButton = new JButton("create");
//    private Inventory inventory;

    public CategoryGenerator(Inventory inventory, StockSearchPanel searchPanel) {
        this.inventory = inventory;
        this.searchPanel = searchPanel;
        add(categoryLabel);
        add(categoryField);
        add(button);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = categoryField.getText();
                if (name.equals("")) {
                    JOptionPane.showMessageDialog(null, "Category name cannot be empty");
                    return;
                }
                categoryField.removeAll();
                if (inventory.createCategory(name)) {
                    searchPanel.addCategory(name);
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
