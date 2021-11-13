package ui.inventorypanel.stockpanel;

import model.*;
import ui.inventorypanel.productpanel.ProductPanel;

import javax.swing.*;

//A panel that will display stock situation of the inventory
public class StockPanel extends JPanel {
    private Inventory inventory;
    private Object[] columnNames;


    //EFFECTS: create new stock panel with given inventory
    public StockPanel(Inventory inventory) {
        JTable table = new StockButtonTable(inventory, null);
        add(table);
    }



    //EFFECTS: create new stock panel with given inventory and link it to the given product panel
    public StockPanel(Inventory inventory, ProductPanel productPanel) {
        StockButtonTable table = new StockButtonTable(inventory, productPanel);
        productPanel.setStockTable(table);
        add(table);
    }
//
//    public static void main(String[] args) {
//        Inventory inventory = new Inventory();
//        inventory.createCategory("Fruit");
//        inventory.createItem("APP", "apple", "Fruit", 4, "test", "test");
//        List<InventoryTag> tags = new ArrayList<>();
//        tags.add(new InventoryTag("APP", 20, 30, LocalDate.now(), "F13", 1));
//        inventory.addProducts(tags);
//        inventory.createItem("BNN", "banana", "Fruit", 12, "test", "test");
//        tags = new ArrayList<>();
//        tags.add(new InventoryTag("BNN", 1, 3, LocalDate.now(), "F13", 1));
//        inventory.addProducts(tags);
//        ProductPanel productPanel = new ProductPanel(inventory);
//        StockPanel stockPanel = new StockPanel(inventory, productPanel);
//
//        stockPanel.setSize(500, 600);
//        productPanel.setPreferredSize(new Dimension(300, 400));
//        JFrame frame = new JFrame();
//        frame.setLayout(new BorderLayout());
//        frame.add(productPanel, BorderLayout.NORTH);
//        frame.add(stockPanel, BorderLayout.SOUTH);
//        frame.pack();
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
//
//

}
