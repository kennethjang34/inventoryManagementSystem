package ui.productpanel;

import model.Inventory;
import model.Product;
import model.QuantityTag;
import ui.ClickableButtonTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProductPanel extends JPanel implements ActionListener {
    private List<Product> products;
    private Inventory inventory;
    private ProductButtonTable table;
    private static String add = "ADD";
    private static String remove = "remove";

    private class ProductButtonTable extends ClickableButtonTable {



    }


    public ProductPanel(Inventory inventory) {
        this.inventory = inventory;
        table = new ProductButtonTable();
        add(table);
        JButton addButton = new JButton(add);
        addButton.setActionCommand(add);
        addButton.addActionListener(this);
        add(addButton);
        JButton removeButton = new JButton(remove);
        removeButton.setActionCommand(remove);
        removeButton.addActionListener(this);
        add(removeButton);

    }


    public void addToList(QuantityTag quantityTag) {
        //stub
    }

    //MODIFIES: this
    //EFFECTS: add new products to the panel.
    public void addToList(String id, String location) {
        //stub
    }



    //Action listener for add/remove buttons
    //EFFECTS: if the action command is "add", add new products
    //if the action command is "remove", remove the selected products;
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase(add)) {
            //stub
        } else if (e.getActionCommand().equalsIgnoreCase(remove)) {
            //stub
        }
    }


}
