package ui.inventorypanel.stockpanel;

import model.*;
import ui.inventorypanel.CategoryGenerator;
import ui.InventoryManagementSystemApplication;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.ProductPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//A panel that will display stock situation of the inventory
public class StockPanel extends JPanel {
//    private Inventory inventory;
    private Object[] columnNames;
    private StockButtonTable stockButtonTable;
    private StockSearchPanel searchPanel;
    private JScrollPane tableScrollPane;
    private InventoryManagementSystemApplication application;
//    //EFFECTS: create new stock panel with given inventory
//    public StockPanel(Inventory inventory) {
//        searchPanel = new StockSearchPanel(inventory, this);
//        JTable table = new StockButtonTable(inventory, null);
//        add(table);
//    }



    //EFFECTS: create new stock panel with given inventory and link it to the given product panel
    public StockPanel(Inventory inventory, ProductPanel productPanel,
                      InventoryManagementSystemApplication application) {
        this.application = application;
//        this.inventory = inventory;
        setLayout(new BorderLayout());
        stockButtonTable = new StockButtonTable(inventory, productPanel);
        searchPanel = new StockSearchPanel(inventory, this);
        JPanel typeCreator = new JPanel();
        typeCreator.add(new CategoryGenerator(inventory, this));
        typeCreator.add(new ItemGenerator(inventory, this));
        productPanel.setStockTable(stockButtonTable);
        add(typeCreator, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        tableScrollPane =  new JScrollPane(stockButtonTable);
//        tableScrollPane.getViewport().addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                StockPanel.this.tableScrollPane = new JScrollPane(stockButtonTable);
//                tableScrollPane.setPreferredSize(new Dimension(500, 200));
//                add(tableScrollPane);
////                scrollPane.repaint();
////                scrollPane.revalidate();
////                stockButtonTable.repaint();
//////                scrollPane.revalidate();
////                scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
//            }
//        });
        tableScrollPane.setPreferredSize(new Dimension(500, 200));
        add(tableScrollPane, BorderLayout.SOUTH);
//        add(stockButtonTable, BorderLayout.SOUTH);
    }


    //MODIFIES: this
    //EFFECTS: if there exists such a category name, display all the items belonging to the category
    //Otherwise, do nothing
    public void displayItems(String categoryName) {
        stockButtonTable.setCategory(categoryName);
        stockButtonTable.repaint();
//        StockPanel.this.tableScrollPane = new JScrollPane(stockButtonTable);
//        tableScrollPane.setPreferredSize(new Dimension(500, 200));
//        add(tableScrollPane, BorderLayout.SOUTH);
    }

    //MODIFIES: this
    //EFFECTS: display all items in the inventory
    public void displayAllItems() {
        stockButtonTable.setCategory(null);
        stockButtonTable.repaint();
    }

    //MODIFIES: this
    //EFFECTS: display the item with the given id.
    //if there is no item with the specified id, display no item
    public void displayItem(String id) {
        stockButtonTable.setItem(id);
        stockButtonTable.repaint();
    }


    //MODIFIES: this
    //EFFECTS: when a new item is added to this, update related fields accordingly
    public void itemAddedUpdate(String id) {
        searchPanel.addItem(id);
        stockButtonTable.repaint();
        //(AbstractTableModel)(stockButtonTable.getModel()).fi
    }

    ///MODIFIES: this
    //EFFECTS: when a new category is added to this, udpate related fields accordingly
    public void categoryAddedUpdate(String categoryName) {
        searchPanel.addCategory(categoryName);
        stockButtonTable.repaint();

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
