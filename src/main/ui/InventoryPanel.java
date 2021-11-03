package ui;

import model.Inventory;
import model.InventoryTag;
import model.QuantityTag;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class InventoryPanel extends JPanel implements ActionListener {
    private final Inventory inventory;
    private final SearchPanel searchPanel;
    private JPanel addPanel;
    private JPanel removePanel;
    private JPanel stockPanel;
    private String add = "add";
    private String remove = "remove";
    private String update = "update";
    private final ArrayList<InventoryTag> listToAdd;
    private final ArrayList<QuantityTag> listToRemove;



    //EFFECTS: create a panel for accessing inventory
    public InventoryPanel(Inventory inventory) {
        this.inventory = inventory;
        listToAdd = new ArrayList<>();
        listToRemove = new ArrayList<>();
        searchPanel = new SearchPanel(inventory);
        addPanel = createAddPanel();
        removePanel = createRemovePanel();
        stockPanel = createStockPanel();
        add(searchPanel);
        add(addPanel);
        add(removePanel);
        JButton updateButton = new JButton("Update");
        updateButton.setActionCommand(update);
        updateButton.addActionListener(this);
        add(stockPanel);
    }

    //MODIFIES: this
    //If add button is pressed, add new products. If remove button pressed, remove products
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(add)) {
            //stub
        } else if (e.getActionCommand().equals(remove)) {
            //stub
        } else if (e.getActionCommand().equals(update)) {
            //stub
        }
    }

    //EFFECTS: create a new panel for adding new products to the inventory
    public JPanel createAddPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Products to add"));
        panel.add(new JLabel("Item Code"));
        panel.add(new JLabel("Cost"));
        panel.add(new JLabel("Best-before date"));
        panel.add(new JLabel("Location"));
        JButton addButton = new JButton("Add");
        addButton.setActionCommand(add);
        addButton.addActionListener(this);
        JTable table = new JTable();
        String[] columnNames = {"Products to add", "Item code", "Cost", "Best-before date", "Location"};
        Object[][] entries = new Object[listToAdd.size()][];
        for (InventoryTag tag: listToAdd) {

        }
        return panel;
    }


    //EFFECTS: create a new panel for removing products from the inventory
    public JPanel createRemovePanel() {
        JPanel panel = new JPanel();
        //The word to add is used to indicate pressing the button will lead to adding the product to the list to remove
        JButton removeButton = new JButton("add");
        removeButton.setActionCommand(remove);
        removeButton.addActionListener(this);
        return panel;
    }

    //EFFECTS: create a new panel for displaying a list of stocks
    public JPanel createStockPanel() {
        JPanel panel = new JPanel();



        return panel;
    }









}
