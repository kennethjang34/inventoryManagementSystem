package ui.inventorypanel.view;

import model.Inventory;
import model.Product;
import ui.*;
import ui.table.ButtonTable;
import ui.table.RowConverterTableModel;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.AddPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class InventoryViewPanel extends JPanel {
    private CategoryGenerator categoryGenerator;
    private ItemGenerator itemGenerator;
    private JButton productGeneratorButton;
    private JButton productRemovalButton;
    private SearchPanel searchPanel;
    private ButtonTable stockButtonTable;
    private FilterBox itemFilter;
    private FilterBox categoryFilter;
    //locationTable is created by the controller when needed
    private ButtonTable locationTable;
    private JTable productTable;
    private Inventory inventory;
    private JTextField categoryField;
    private JTextField itemField;
    private AddPanel addPanel;
    private AbstractAction locationTableAction;
    private boolean isLocationViewDialogDisplayed = false;



    private enum LocationTableColumns {
        ID, LOCATION, QUANTITY

//        @Override
//        public String toString() {
//
//        }

    }

    private static final String[] locationTableColumns = new String[]{
            LocationTableColumns.ID.toString(), LocationTableColumns.LOCATION.toString(),
            LocationTableColumns.QUANTITY.toString()
    };

    private KeyListener buttonEnterListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                JButton button = (JButton) e.getSource();
                button.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };



    public  class StockLocationTable extends ButtonTable {
        private String id;

        public StockLocationTable(String id) {
            super(inventory.getQuantitiesAtLocations(id), locationTableColumns, "To product table");
            this.id = id;
//            List<QuantityTag> stocks = inventory.getQuantitiesAtLocations(id);
//            //excludes the button column
//            locationTable = new ButtonTable(stocks, locationTableColumns, "To product table");
            assert locationTableAction != null;
            this.setButtonAction(locationTableAction);
            setPreferredSize(new Dimension(500, 600));
        }

        public String getId() {
            return id;
        }

        public String getSelectedLocation() {
            int row = getSelectedRow();
            return (String)getValueAt(row, getColumn(LocationTableColumns.LOCATION.toString()).getModelIndex());
        }
    }



    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public InventoryViewPanel(Inventory inventory) {
        this.inventory = inventory;
        //Generators don't get updated when the model changes
        categoryGenerator = new CategoryGenerator();
        categoryGenerator.getButton().addKeyListener(buttonEnterListener);
        itemGenerator = new ItemGenerator();
        itemGenerator.getButton().addKeyListener(buttonEnterListener);
        searchPanel = new SearchPanel(inventory);
        //stockButtonTable is decoupled from inventory
        stockButtonTable = new ButtonTable(inventory, "Location view", Inventory.ITEM);
        //ID determines the equivalency
        stockButtonTable.setBaseColumnIndex(1);
        RowConverterTableModel productTableModel = new RowConverterTableModel();
        productTableModel.setColumnNames(Product.DATA_LIST);
        productTable = new JTable(productTableModel);
        inventory.addDataModelListener(Inventory.PRODUCT, productTableModel);
        itemFilter = new FilterBox(inventory, Inventory.ITEM);
        categoryFilter = new FilterBox(inventory, Inventory.CATEGORY);
        categoryField = new JTextField(10);
        itemField = new JTextField(10);
        productGeneratorButton = new JButton("ADD");
        productGeneratorButton.addKeyListener(buttonEnterListener);
        productRemovalButton = new JButton("Remove");
        productRemovalButton.addKeyListener(buttonEnterListener);
        setUpItemFilter();
        setUpCategoryFilter();
        setUpCategoryField();
        setUpItemField();
//        inventory.addDataModelListener(Inventory.STOCK, (TableView) stockButtonTable.getModel());
//        inventory.addDataModelListener(Inventory.ITEM, (TableView) stockButtonTable.getModel());
        addPanel = new AddPanel(inventory);
        addPanel.getButton().addKeyListener(buttonEnterListener);
        setUpProductGeneratorButton();
        setUpProductRemovalButton();
        setPreferredSize(new Dimension(700, 800));
        deployComponents();
    }


    private void deployComponents() {
        JPanel panelForTables = new JPanel();
        panelForTables.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        //Keep (0,0) empty for the product sorter that will be developed later
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 40;
        gbc.gridheight = 50;
        gbc.weightx = 1;
        gbc.weighty = 0.45;
        gbc.fill = GridBagConstraints.BOTH;
        productTable.setPreferredScrollableViewportSize(new Dimension(500, 600));
//        productTable.setFillsViewportHeight(true);
        JScrollPane productScrollPane = new JScrollPane(productTable);
        panelForTables.add(productScrollPane, gbc);
//        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = gbc.gridx + gbc.gridwidth - 6;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridheight = 3;
        gbc.gridwidth = 3;
//        gbc.anchor = GridBagConstraints.EAST;
        panelForTables.add(productGeneratorButton, gbc);
        gbc.gridx = gbc.gridx + 3;
        panelForTables.add(productRemovalButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        panelForTables.add(categoryFilter, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panelForTables.add(itemFilter, gbc);
        gbc.gridx = 0;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.gridwidth = 40;
        gbc.gridheight = 50;
        gbc.weightx = 1;
        gbc.weighty = 0.45;
        gbc.fill = GridBagConstraints.BOTH;
        stockButtonTable.setPreferredSize(new Dimension(500, 600));
        panelForTables.add(new JScrollPane(stockButtonTable), gbc);
        panelForTables.setPreferredSize(new Dimension(700, 600));
        JPanel panelForGenerators = new JPanel();
        panelForGenerators.setPreferredSize(new Dimension(400, 400));
        gbc = new GridBagConstraints();
//        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForGenerators.add(categoryGenerator, gbc);
        gbc.gridy = gbc.gridy + 1;
        panelForGenerators.add(itemGenerator, gbc);
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 80;
        gbc.gridheight = 50;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelForTables, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridy = 5;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 10;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelForGenerators, gbc);
        setPreferredSize(new Dimension(1100, 1100));
    }


    private void setUpCategoryField() {
//        categoryField.setVisible(false);
    }



    private void setUpItemField() {
//        itemField.setVisible(true);
    }


    private void setUpCategoryFilter() {
//        List<String> items = new ArrayList<>();
//        if (inventory.getCategoryNames().isEmpty()) {
//            items.add("Empty");
//        } else {
//            items.add("All");
//            items.add("TYPE_MANUALLY");
//            items.addAll(inventory.getCategoryNames());
//        }
//        categoryFilter.setModel(new DefaultComboBoxModel(items.toArray(new String[0])));
    }

    private void setUpItemFilter() {
        List<String> items = new ArrayList<>();
        if (inventory.getIDs().isEmpty()) {
            items.add("Empty");
        } else {
            items.add("All");
            items.add("TYPE_MANUALLY");
            items.addAll(inventory.getIDs());
        }
        itemFilter.setModel((new DefaultComboBoxModel(items.toArray(new String[0]))));
    }



    //EFFECTS: create a new location view of a particular stock based on the request
    public void displayLocationStockView(String id) {
//        List<QuantityTag> stocks = inventory.getQuantitiesAtLocations(id);
//        //excludes the button column
//        String[] locationTableColumns = new String[]{
//                "ID", "Location"
//        };
        locationTable = new StockLocationTable(id);
        JScrollPane scrollPane = new JScrollPane(locationTable);
        JDialog dialog = new JDialog();
        dialog.setLayout(new FlowLayout());
        dialog.add(scrollPane);
        dialog.pack();
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                isLocationViewDialogDisplayed = false;
            }
            });
        isLocationViewDialogDisplayed = true;
    }

    public boolean isLocationTableDialogDisplayed() {
        return isLocationViewDialogDisplayed;
    }

    public void addToLocationStockView(String id) {
        locationTable.addDataWithNewButton(inventory.getQuantitiesAtLocations(id));
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
    public FilterBox getItemFilter() {
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

    public void setLocationTableAction(AbstractAction action) {
        locationTableAction = action;
    }

    public ButtonTable getLocationTableOnDisplay() {
        return locationTable;
    }

    private void setUpProductGeneratorButton() {
        productGeneratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                AddPanel addPanel = getAddPanel();
//                addPanel.addButtonActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
////                        dialog.setVisible(false);
//                        dialog.dispose();
//                    }
//                });
                dialog.setLayout(new FlowLayout());
                dialog.add(addPanel);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
    }

    public JButton getProductRemovalButton() {
        return productRemovalButton;
    }

    private void setUpProductRemovalButton() {
        //
    }
}
