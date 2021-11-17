package ui;

import model.Inventory;
import model.Product;
import model.QuantityTag;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SearchPanel extends JPanel implements ActionListener {
    private final Inventory inventory;
    private final JTextField itemCodeField;
    private final JTextField skuField;

    public SearchPanel(Inventory inventory) {
        this.inventory = inventory;
        itemCodeField = new JTextField(10);
        skuField = new JTextField(10);
        add(new JLabel("Item code"));
        add(itemCodeField);
        add(new JLabel("SKU"));
        add(skuField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        add(searchButton);
        setVisible(true);
    }

    //EFFECTS: search a particular product or quantities belonging to the specified item code
    @Override
    public void actionPerformed(ActionEvent e) {
        String itemCode = itemCodeField.getText();
        String stringSku = skuField.getText();
        if (stringSku.length() == 0) {
            JDialog dl = new JDialog();
            dl.add(createStockPanel(itemCode));
            dl.setVisible(true);
        } else {
            String sku = (stringSku);
            JDialog dl = new JDialog();
            dl.add(createProductPanel(itemCode, sku));
            dl.setVisible(true);
        }
    }


    //EFFECTS: create a new panel that is used
    //to report the information about quantities belonging to the specified item code
    private JPanel createStockPanel(String itemCode) {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Item Code: "));
        panel.add(new JLabel(itemCode));
        int total = inventory.getQuantity(itemCode);
        List<QuantityTag> locations = inventory.getQuantitiesAtLocations(itemCode);
        panel.add(new JLabel("Total Quantity: "));
        panel.add(new JLabel(String.valueOf(total)));
        for (QuantityTag tag: locations) {
            panel.add(new JLabel(tag.getLocation() + '\t'));
            panel.add(new JLabel(String.valueOf(tag.getQuantity())));
        }
        panel.setVisible(true);
        return panel;
    }

    //EFFECTS: create a new panel that is used
    //to show the given product's information
    private JPanel createProductPanel(String id, String sku) {
        JPanel panel = new JPanel();
        Product product = inventory.getProduct(sku);
        if (product == null) {
            panel.add(new JLabel("There is no such product"));
            panel.setVisible(true);
            return panel;
        }
        panel.add(new JLabel("Product Code: "));
        panel.add(new JLabel(id + sku));
        String location = product.getLocation();
        panel.add(new JLabel("Price: " + product.getPrice()));
        panel.add(new JLabel("Best-before Date: "
                + (product.getBestBeforeDate() == null ? "N/A" : product.getBestBeforeDate())));
        panel.add(new JLabel("Location: " + location));
        panel.add(new JLabel("Date generated: " + product.getDateGenerated()));
        panel.setVisible(true);
        return panel;
    }

}
