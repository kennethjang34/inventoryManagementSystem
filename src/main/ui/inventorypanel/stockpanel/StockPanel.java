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
//        add(typeCreator);
//        add(searchPanel);
//        add(typeCreator, BorderLayout.NORTH);
//        add(searchPanel, BorderLayout.CENTER);
//        searchPanel.setPreferredSize(new Dimension(400, 200));
//        stockButtonTable.setPreferredSize(new Dimension(400, 400));



        tableScrollPane =  new JScrollPane(stockButtonTable);
        tableScrollPane.setSize(400, 400);
        //tableScrollPane.setPreferredSize(new Dimension(500, 600));
        //tableScrollPane.setPreferredSize(new Dimension(500, 200));
        add(typeCreator, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);
        //add(tableScrollPane);
//        setPreferredSize(new Dimension(500, 600));
        //add(new JPanel(), BorderLayout.SOUTH);
//        add(stockButtonTable, BorderLayout.SOUTH);
//        setSize(500, 600);
//        repaint();
//        revalidate();
//        setPreferredSize(new Dimension(500, 300));
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
        ((StockButtonTableModel)(stockButtonTable.getModel())).fireTableDataChanged();
    }

    //MODIFIES: this
    //EFFECTS: display the item with the given id.
    //if there is no item with the specified id, display no item
    public void displayItem(String id) {
        stockButtonTable.setItem(id);
    }


    //MODIFIES: this
    //EFFECTS: when a new item is added to this, update related fields accordingly
    public void itemAddedUpdate(String id) {
        searchPanel.addItem(id);
        stockButtonTable.repaint();
    }

    ///MODIFIES: this
    //EFFECTS: when a new category is added to this, udpate related fields accordingly
    public void categoryAddedUpdate(String categoryName) {
        searchPanel.addCategory(categoryName);
        stockButtonTable.repaint();
    }


}
