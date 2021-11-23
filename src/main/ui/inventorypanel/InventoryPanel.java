package ui.inventorypanel;

import model.ApplicationConstantValue;
import model.Inventory;
import model.Observer;
import ui.InventoryManagementSystemApplication;
import ui.inventorypanel.productpanel.ProductPanel;
import ui.inventorypanel.stockpanel.StockPanel;

import javax.swing.*;
import java.awt.*;

//represents a panel that contains stock panel and product panel to display current inventory conditions
public class InventoryPanel extends JPanel implements Observer {
    //private final Inventory inventory;
    //private final SearchPanel searchPanel;
    private StockPanel stockPanel;
    private ProductPanel productPanel;
    private String add = "add";
    private String remove = "remove";
    private String update = "update";
    private String search = "search";

    //EFFECTS: create a new inventory making a new stock panel and product panel
    public InventoryPanel(Inventory inventory, InventoryManagementSystemApplication application) {
        //this.inventory = inventory;
        productPanel = new ProductPanel(inventory, application);
        stockPanel = new StockPanel(inventory, productPanel);
//        JPanel typeCreator = new JPanel();
//        typeCreator.add(new CategoryGenerator(inventory, stockSearchPanel));
//        typeCreator.add(new ItemGenerator(inventory, stockSearchPanel));
        setPreferredSize(new Dimension(600, 900));
        setLayout(new BorderLayout());
        add(productPanel, BorderLayout.CENTER);
        add(stockPanel, BorderLayout.SOUTH);
    }



    @Override
    public void update(int arg) {
        stockPanel.repaint();
        productPanel.repaint();
    }
}
