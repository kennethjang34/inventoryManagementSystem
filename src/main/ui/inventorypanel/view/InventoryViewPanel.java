package ui.inventorypanel.view;

import model.Inventory;
import model.QuantityTag;
import ui.*;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.AddPanel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class InventoryViewPanel extends JPanel implements View {
    private CategoryGenerator categoryGenerator;
    private ItemGenerator itemGenerator;
    private JButton productGeneratorButton;
    private SearchPanel searchPanel;
    private ButtonTable stockButtonTable;
    private JComboBox itemFilter;
    private JComboBox categoryFilter;
    //locationTable is created by the controller when needed
    private ButtonTable locationTable;
    private JTable productTable;
    private Inventory inventory;
    private JTextField categoryField;
    private JTextField itemField;
    private AddPanel addPanel;


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public InventoryViewPanel(Inventory inventory) {
        this.inventory = inventory;
        //Generators don't get updated when the model changes
        categoryGenerator = new CategoryGenerator();
        itemGenerator = new ItemGenerator();
        searchPanel = new SearchPanel(inventory);
        //stockButtonTable is decoupled from inventory
        stockButtonTable = new ButtonTable(inventory, "Location view");
        stockButtonTable.setBaseColumnIndex(1);
        productTable = new JTable(new RowConverterTableModel());
        itemFilter = new JComboBox();
        categoryFilter = new JComboBox();
        categoryField = new JTextField(10);
        itemField = new JTextField(10);
        productGeneratorButton = new JButton("ADD");
        setUpItemFilter();
        setUpCategoryFilter();
        setUpCategoryField();
        setUpItemField();
        inventory.addListener(inventory.STOCK, stockButtonTable);
        inventory.addListener(Inventory.ITEM, stockButtonTable);
//        inventory.addListener(Inventory.CATEGORY, stockButtonTable);
        addPanel = new AddPanel(inventory);
        setPreferredSize(new Dimension(600, 900));
        setLayout(new FlowLayout());
        add(productTable);
        add(productGeneratorButton);
        add(itemGenerator);
        add(categoryGenerator);
        add(stockButtonTable);
//        setLayout(new GridLayout());
//        add(productTable, new GridLayout(0, 1));
//        add(productGeneratorButton, new GridLayout(1, 1));
//        add(itemGenerator, new GridLayout(1, 0));
//        add(categoryGenerator, new GridLayout(1, 2));
//        add(searchPanel, new GridLayout(2, 1));
//        add(stockButtonTable, new GridLayout(3, 1));
    }



    private void setUpCategoryField() {
//        categoryField.setVisible(false);
    }



    private void setUpItemField() {
//        itemField.setVisible(true);
    }


    private void setUpCategoryFilter() {
        List<String> items = new ArrayList<>();
        if (inventory.getCategoryNames().isEmpty()) {
            items.add("No Category");
        } else {
            items.add("All");
            items.add("TYPE_MANUALLY");
            items.addAll(inventory.getCategoryNames());
        }
        categoryFilter.setModel(new DefaultComboBoxModel(items.toArray(new String[0])));
    }

    private void setUpItemFilter() {
        List<String> items = new ArrayList<>();
        if (inventory.getIDs().isEmpty()) {
            items.add("No item");
        } else {
            items.add("All");
            items.add("TYPE_MANUALLY");
            items.addAll(inventory.getIDs());
        }
        itemFilter.setModel((new DefaultComboBoxModel(items.toArray(new String[0]))));
    }


    //EFFECTS: create a new location view of a particular stock based on the request
    public void displayLocationStockView(String id) {
        List<QuantityTag> stocks = inventory.getQuantitiesAtLocations(id);
        //excludes the button column
        String[] locationTableColumns = new String[]{
                "ID", "Location"
        };
        ButtonTable locationTable = new ButtonTable(stocks, locationTableColumns, "To product table");
        JDialog dialog = new JDialog();
        dialog.setLayout(new FlowLayout());
        dialog.add(locationTable);
        dialog.setPreferredSize(new Dimension(500, 600));
        dialog.setVisible(true);
    }


    //EFFECTS: return the itemGenerator
    public ItemGenerator getItemGenerator() {
        return itemGenerator;
    }

    //EFFECTS: return the searchPanel
    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    //EFFECTS: return the categoryGenerator
    public ButtonTable getStockButtonTable() {
        return stockButtonTable;
    }


    //MODIFIES: this
    //EFFECTS: update this when there is a major event concerning every component such as "clear"
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    //EFFECTS: return the category generator
    public CategoryGenerator getCategoryGenerator() {
        return categoryGenerator;
    }

    //EFFECTS: return the product table
    public JTable getProductTable() {
        return productTable;
    }

    //EFFECTS: return the item filter
    public JComboBox getItemFilter() {
        return itemFilter;
    }

    public JComboBox getCategoryFilter() {
        return categoryFilter;
    }

    public JTextField getCategoryField() {
        return categoryField;
    }

    public JTextField getItemField() {
        return itemField;
    }

    public JButton getProductGeneratorButton() {
        return productGeneratorButton;
    }

//    public void setProductGeneratorButton(JButton productGeneratorButton) {
////        this.productGeneratorButton = productGeneratorButton;
////    }

    public AddPanel getAddPanel() {
        return addPanel;
    }

    public void setAddPanel(AddPanel addPanel) {
        this.addPanel = addPanel;
    }

    //EFFECTS: create and display error message above this
    public static void displayErrorMessage(Component component, String message) {
        JOptionPane.showMessageDialog(component, message);
    }
}
