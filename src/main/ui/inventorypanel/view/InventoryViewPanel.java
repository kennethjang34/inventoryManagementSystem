package ui.inventorypanel.view;

import model.Category;
import model.Inventory;
import model.Item;
import model.Product;
import ui.*;
import ui.table.ButtonTable;
import ui.table.DataFactory;
import ui.table.RowConverterViewerTableModel;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.AddPanel;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static ui.inventorypanel.controller.InventoryController.convertToLocalDate;

public class InventoryViewPanel extends JPanel {
    private CategoryGenerator categoryGenerator;
    private ItemGenerator itemGenerator;
    private JButton productGeneratorButton;
    private JButton productRemovalButton;
    private SearchPanel searchPanel;
    private ButtonTable stockButtonTable;
    private JComboBox<String> itemFilter;
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

    public static int[] getSelectedTableModelRows(JTable table) {
        int[] selectedViewIndices = table.getSelectedRows();
        int[] modelRows = new int[selectedViewIndices.length];
        for (int i = 0; i < selectedViewIndices.length; i++) {
            modelRows[i] = table.convertRowIndexToModel(selectedViewIndices[i]);
        }
        return modelRows;
    }


    private enum LocationTableColumns {
        ID, LOCATION, QUANTITY
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

        public StockLocationTable(String id) {
            super(inventory.getQuantitiesAtLocations(id), locationTableColumns, "To product table");
//            List<QuantityTag> stocks = inventory.getQuantitiesAtLocations(id);
//            //excludes the button column
//            locationTable = new ButtonTable(stocks, locationTableColumns, "To product table");
            assert locationTableAction != null;
            this.setButtonAction(locationTableAction);
            setPreferredSize(new Dimension(500, 600));
        }

        public String getSelectedId() {
            int row = getSelectedRow();
            return (String)getValueAt(row, getColumn(LocationTableColumns.ID.toString()).getModelIndex());
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
        RowConverterViewerTableModel productTableModel = new RowConverterViewerTableModel();
        productTableModel.setColumnNames(Product.DATA_LIST);
        productTable = new JTable(productTableModel) {
            @Override
            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                int column = columnAtPoint(p);
                if (row != -1 && column != -1) {
                    return getValueAt(row, column).toString();
                }
                return null;
            }

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        Point p = e.getPoint();
                        int column = columnAtPoint(p);
                        if (column == -1) {
                            return null;
                        }
                        return getColumnName(column);
                    }
                };
            }
        };
        inventory.addDataChangeListener(Inventory.PRODUCT, productTableModel);
        itemFilter = new JComboBox();
        categoryFilter = new FilterBox(inventory, Inventory.CATEGORY) {
            //For when a new item is added to the category or removed from it
            @Override
            public void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added) {
                if (added instanceof Category) {
                    added.addDataChangeListener(Inventory.ITEM, this);
                    if (getItemCount() == 1 && getItemAt(0).equals(EMPTY)) {
                        removeItemAt(0);
                        addItem(ALL);
                        addItem(TYPE_MANUALLY);
                    }
                    addItem(added.toString());
                } else if (added instanceof Item) {
                    Item item = (Item) added;
                    if (categoryFilter.getSelectedItem().equals(item.getCategory().toString())
                            || categoryFilter.getSelectedItem().equals(FilterBox.ALL)) {
                        if (itemFilter.getSelectedItem().equals(FilterBox.EMPTY)) {
                            itemFilter.removeAllItems();
                            itemFilter.addItem(ALL);
                            itemFilter.addItem(TYPE_MANUALLY);
                        }
                        itemFilter.addItem(item.getId());
                    }
                }
            }

            @Override
            public void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel entry) {
                if (entry instanceof  Category) {
                    entry.removeListener(this);
                    removeItem(entry.toString());
                } else if (entry instanceof  Item){
                    Item item = (Item) entry;
                    Category category = (Category) source;
                    if (categoryFilter.getSelectedItem().equals(category.getName())|| categoryFilter.getSelectedItem().equals(FilterBox.ALL)) {
                        itemFilter.removeItem(item.getId());
                        if (itemFilter.getItemCount() == 2) {
                            itemFilter.removeAllItems();
                            itemFilter.addItem(EMPTY);
                        } else {
                            itemFilter.setSelectedIndex(0);
                        }
                    }
                }
            }
        };
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
        addPanel = new AddPanel(inventory);
        addPanel.getButton().addKeyListener(buttonEnterListener);
        setUpProductGeneratorButton();
        setUpProductRemovalButton();
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
//        productTable.setPreferredSize(new Dimension(500, 600));
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
//        stockButtonTable.setPreferredSize(new Dimension(500, 600));
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
        gbc.gridheight = 40;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(panelForTables, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridy = 5;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 10;
        gbc.fill = GridBagConstraints.BOTH;
        add(panelForGenerators, gbc);
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
            public void windowClosing(WindowEvent e) {
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
    public JComboBox<String> getItemFilter() {
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

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        RowConverterViewerTableModel productTableModel = (RowConverterViewerTableModel) productTable.getModel();
        productTableModel.reset();
//        productTable.revalidate();
        categoryFilter.setDataFactory(inventory);
        updateItemFilter((String) categoryFilter.getSelectedItem());
//        categoryFilter.revalidate();
//        itemFilter.revalidate();
        RowConverterViewerTableModel stockTableModel = (RowConverterViewerTableModel) stockButtonTable.getModel();
        stockTableModel.setDataFactory(inventory);
    }


    //implemented
    //called only when category is newly selected by categoryFilter
    //MODIFIES: item filter
    //EFFECTS: set up the item filter, so it matches the newly selected category
    public void updateItemFilter(String selectedCategory) {
        List<String> ids;
        if (selectedCategory.equals(FilterBox.ALL)) {
            ids = inventory.getIDs();
        } else {
            //in case of TYPE_MANUALLY, it's the same as ids.addAll(null);
            ids = inventory.getIDs(selectedCategory);
        }

        if (ids.isEmpty()) {
            ids.add(FilterBox.EMPTY);
        } else {
            ids.add(0, FilterBox.ALL);
            ids.add(1, FilterBox.TYPE_MANUALLY);
        }
        itemFilter.setModel(new DefaultComboBoxModel(ids.toArray(new String[0])));
    }

}
