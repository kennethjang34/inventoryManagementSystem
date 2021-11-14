package ui.inventorypanel;

import model.Inventory;
import ui.CategoryGenerator;
import ui.ItemGenerator;
import ui.inventorypanel.productpanel.ProductPanel;
import ui.inventorypanel.stockpanel.StockPanel;

import javax.swing.*;
import java.awt.*;

public class InventoryPanel extends JPanel {
    private final Inventory inventory;
    //private final SearchPanel searchPanel;
    private StockPanel stockPanel;
    private ProductPanel productPanel;
    private CategoryGenerator categoryGenerator;
    private String add = "add";
    private String remove = "remove";
    private String update = "update";
    private String search = "search";

    public InventoryPanel(Inventory inventory) {
        this.inventory = inventory;
        productPanel = new ProductPanel(inventory);
        stockPanel = new StockPanel(inventory, productPanel);
//        JPanel typeCreator = new JPanel();
//        typeCreator.add(new CategoryGenerator(inventory, stockSearchPanel));
//        typeCreator.add(new ItemGenerator(inventory, stockSearchPanel));
        setLayout(new BorderLayout());
        //add(typeCreator, BorderLayout.NORTH);
        add(productPanel, BorderLayout.NORTH);
        add(stockPanel, BorderLayout.CENTER);
    }














}
