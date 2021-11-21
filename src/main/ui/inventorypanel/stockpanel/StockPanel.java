package ui.inventorypanel.stockpanel;

import model.*;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.ProductPanel;

import javax.swing.*;
import java.awt.*;

//A panel that will display stock situation of the inventory
public class StockPanel extends JPanel implements Observer {
    private StockButtonTable stockButtonTable;
    private StockSearchTool searchTool;
    private JPanel searchPanel;
    private JScrollPane tableScrollPane;


    //EFFECTS: create new stock panel with given inventory and link it to the given product panel
    public StockPanel(Inventory inventory, ProductPanel productPanel) {
        setLayout(new BorderLayout());
        searchTool = new StockSearchTool(inventory);
        stockButtonTable = new StockButtonTable(inventory, searchTool, productPanel);
        searchPanel = searchTool.getPanel();
        JPanel typeCreator = new JPanel();
        typeCreator.add(new CategoryGenerator(inventory, this));
        typeCreator.add(new ItemGenerator(inventory, this));
        productPanel.setStockTable(stockButtonTable);
        tableScrollPane =  new JScrollPane(stockButtonTable);
        tableScrollPane.setSize(400, 400);
        add(typeCreator, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);
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
        searchTool.update();
        stockButtonTable.repaint();
    }

    ///MODIFIES: this
    //EFFECTS: when a new category is added to this, udpate related fields accordingly
    public void categoryAddedUpdate(String categoryName) {
        //searchPanel.addCategory(categoryName);
        searchTool.update();
        stockButtonTable.repaint();
    }

    //MODIFIES: this
    //EFFECTS: update this, so it keeps up with the latest data
    @Override
    public void update() {

    }


}
