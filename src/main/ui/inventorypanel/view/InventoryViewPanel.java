package ui.inventorypanel.view;

import jdk.nashorn.internal.scripts.JD;
import model.*;
import ui.*;
import ui.inventorypanel.controller.InventoryController;
import ui.table.*;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.util.List;

public class InventoryViewPanel extends JPanel {
    private CategoryGenerator categoryGenerator;
    private ItemGenerator itemGenerator;
    private JButton productGeneratorButton;
    private JButton productRemovalButton;
    private SearchPanel searchPanel;
    private ButtonTable stockButtonTable;
    private JComboBox<String> itemFilter;
    private FilterBox categoryFilter;
    private InventoryController controller;
    private ButtonTable locationTable;
    private JTable productTable;
    private Inventory inventory;
    private JTextField categoryField;
    private JTextField itemField;
    private AddPanel addPanel;
    private AbstractAction locationTableAction;
    private boolean isLocationViewDialogDisplayed = false;
    private JDialog removalRegisterDialog;



    public JDialog getAddPanelDialog() {
        return getAddPanelDialog();
    }

    public void displayAddDialog(Component parentComponent) {
        addPanel.displayAdditionDialog(parentComponent);
    }

    public void clearAddPanelFields() {
        addPanel.clearFields();
    }

    public InventoryController getController() {
        return controller;
    }


    private enum LocationTableColumns {
        ID, LOCATION, QUANTITY
    }

    private static final String[] locationTableColumns = new String[]{
            LocationTableColumns.ID.toString(), LocationTableColumns.LOCATION.toString(),
            LocationTableColumns.QUANTITY.toString()
    };




    private final static KeyListener buttonEnterListener = new KeyListener() {
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
        categoryGenerator = new CategoryGenerator(this);
        categoryGenerator.getButton().addKeyListener(buttonEnterListener);
        itemGenerator = new ItemGenerator(this);
        itemGenerator.getButton().addKeyListener(buttonEnterListener);
        searchPanel = SearchPanel.getSearchPanel();
        //stockButtonTable is decoupled from inventory
        stockButtonTable = new ButtonTable(inventory, "Location view", Inventory.ITEM);
        //ID determines the equivalency
        stockButtonTable.setBaseColumnIndex(1);
        RowConverterViewerTableModel productTableModel = new RowConverterViewerTableModel();
        productTableModel.setColumnNames(Product.DATA_LIST);
        productTable = new EntryRemovableTable(productTableModel);
        setUpStockButtonTable(stockButtonTable);
        setUpProductTable(productTable);
        setUpSearchPanel(searchPanel);
//        setUpAddPanel(getAddPanel());
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
                            || categoryFilter.getSelectedItem().equals(FilterBox.ALL) || categoryFilter.getSelectedItem().equals(FilterBox.TYPE_MANUALLY)) {
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
        categoryField = new JTextField();
        categoryField.setPreferredSize(new Dimension(20, 10));
        itemField = new JTextField();
        itemField.setPreferredSize(new Dimension(20, 10));
        productGeneratorButton = new JButton("ADD");
        productGeneratorButton.addKeyListener(buttonEnterListener);
        productRemovalButton = new JButton("Remove");
        productRemovalButton.addKeyListener(buttonEnterListener);
        setUpItemField();
        setUpCategoryField();
        setUpItemFilter();
        setUpCategoryFilter();
        addPanel = new AddPanel(this);
        setUpProductGeneratorButton();
        setUpProductRemovalButton();
        deployComponents();
    }

    private void setUpCategoryField() {
        categoryField.setVisible(false);
        categoryField.addActionListener(e -> {
            String text = categoryField.getText();
            controller.categoryFieldFilled(text);
        });
    }

    private void setUpItemField() {
        itemField.setVisible(false);
        itemField.addActionListener(e -> {
            String text = itemField.getText().toString();
            controller.itemFieldFilled(text);

        });
    }


    private void setUpSearchPanel(SearchPanel searchPanel) {
        JButton searchButton = searchPanel.getSearchButton();
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField searchField = searchPanel.getSearchField();
                String input = searchField.getText().toUpperCase();
                controller.inputSearch(input);
            }
        });
    }


    public void displayFoundItemWithProducts(Item item) {
        if (item != null) {
            List<Product> products;
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            EntryRemovableTable itemEntry = new EntryRemovableTable();
            //for the tableModel
            List<Item> itemList = new ArrayList<>();
            itemList.add(item);
            RowConverterViewerTableModel itemEntryModel = new RowConverterViewerTableModel(itemList, Item.DATA_LIST);
            itemEntry.setModel(itemEntryModel);
            itemEntry.setComponentPopupMenu(createPopUpMenuForItemsTable(itemEntry));
            products = item.getProducts();
            RowConverterViewerTableModel productsFound = new RowConverterViewerTableModel(products, Product.DATA_LIST);
            item.addDataChangeListener(Item.DataList.PRODUCT.toString(), productsFound);
            EntryRemovableTable productsTable = new EntryRemovableTable(productsFound);
            productsTable.setComponentPopupMenu(createPopUpMenuForProductTable(productsTable));
            panel.add(new JLabel("Item Found:"), gbc);
            gbc.gridx++;
            gbc.gridwidth = 3;
            panel.add(itemEntry, gbc);
            gbc.gridx = gbc.gridx + gbc.gridwidth;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("Products found"), gbc);
            gbc.gridx++;
            gbc.gridwidth = 3;
            panel.add(new JScrollPane(productsTable), gbc);
            JDialog dialog = new JDialog();
            dialog.add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(InventoryManagementSystemApplication.getApplication());
            dialog.setVisible(true);
            return;
        }
    }

    public void displayFoundProductWithSKU(Product product) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
        List<Product> products = new ArrayList<>();
        if (product == null) {
            JOptionPane.showMessageDialog(searchPanel, "There is no such product at all");
            return;
        }
        products.add(product);
        RowConverterViewerTableModel productsFound = new RowConverterViewerTableModel(products);
        EntryRemovableTable productsTable = new EntryRemovableTable(productsFound);
        productsTable.setComponentPopupMenu(createPopUpMenuForProductTable(productsTable));
        panel.add(new JLabel("Products found"));
        panel.add(productsTable);
        JDialog dialog = new JDialog();
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(InventoryManagementSystemApplication.getApplication());
        dialog.setVisible(true);
    }


    private void setUpProductTable(JTable productTable) {
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) productTable.getModel();
        tableModel.setColumnNames(Product.DATA_LIST);
        tableModel.setDuplicateAllowed(false);
        tableModel.setBaseColumnIndex(tableModel.findColumn("SKU"));
        productTable.setRowSorter(createRowSorter(productTable, null, Comparator.<String>naturalOrder()));
        JPopupMenu menu = createPopUpMenuForProductTable(productTable);
        productTable.setComponentPopupMenu(menu);
    }

    private JPopupMenu createPopUpMenuForProductTable(JTable productTable) {
        JPopupMenu menu = productTable.getComponentPopupMenu();
        if (menu == null) {
            menu = new JPopupMenu();
        }
        JMenuItem remove = new JMenuItem("remove");
        RowConverterViewerTableModel tableModel = ((RowConverterViewerTableModel)(productTable.getModel()));
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> entries = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                controller.productRemovalHelper(entries);
            }
        });

        JMenuItem location = new JMenuItem("change location");
        location.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> entries = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                controller.productLocationChangeHelper(entries);
            }
        });

        JMenuItem price = new JMenuItem("change price");
        price.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                controller.productPriceChangeHelper(products);
            }
        });

        JMenuItem cost = new JMenuItem("change cost");
        cost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                controller.productCostChangeHelper(products);
            }
        });

        JMenuItem bbd = new JMenuItem("change best-before-date");
        bbd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                controller.productBBDChangeHelper(products);
            }
        });

        JMenuItem id = new JMenuItem("change id");
        id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products =
                        tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                controller.productIdChangeHelper(products);
            }
        });


        menu.add(remove);
        menu.add(location);
        menu.add(price);
        menu.add(cost);
        menu.add(bbd);
        menu.add(id);
        return menu;
    }

    private void setUpStockButtonTable(ButtonTable stockTable) {
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) stockTable.getModel();
        stockTable.setButtonAction(new AbstractAction("Location") {
            @Override
            public void actionPerformed(ActionEvent e) {
                    int row = stockTable.findModelRowIndex(e.getSource());
                    ViewableTableEntryConvertibleModel corresponding = tableModel.getRowEntryModel(stockTable.convertRowIndexToModel(row));
                    controller.stockTableLocationButtonClicked(corresponding);
            }
        });

        stockTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 2) {
                        Point p = e.getPoint();
                        int row = stockTable.rowAtPoint(p);
                        if (row == -1) {
                            return;
                        }
                        ViewableTableEntryConvertibleModel selected = tableModel.getRowEntryModel(stockTable.convertRowIndexToModel(row));
                        controller.stockTableRowDoubleClicked(selected);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON2) {

                }
            }
        });

        tableModel.setBaseColumnIndex(tableModel.findColumn(Inventory.ID));
        stockTable.setRowSorter(createRowSorter(stockTable, null, Comparator.<String>naturalOrder()));
        JPopupMenu menu = createPopUpMenuForItemsTable(stockTable);
        stockTable.setComponentPopupMenu(menu);
    }



    public JPopupMenu createPopUpMenuForItemsTable(JTable stockTable) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem category = new JMenuItem("change category");
        JMenuItem name = new JMenuItem("change name");
        JMenuItem description = new JMenuItem("change description");
        JMenuItem note = new JMenuItem("change note");
        JMenuItem listPrice = new JMenuItem("change list price");
        JMenuItem remove = new JMenuItem("remove products");
//        JMenuItem add = new JMenuItem("add products");
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) stockTable.getModel();
        category.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                controller.itemCategoryChangeHelper(items);
            }
        });

        name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                controller.itemNameChangeHelper(items);
            }
        });

        description.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                controller.itemDescriptionChangeHelper(items);
            }
        });

        note.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                controller.itemNoteChangeHelper(items);
            }
        });

        listPrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                controller.itemListPriceChangeHelper(items);
            }
        });

        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<? extends ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                controller.itemStockRemovalHelper((List<Item>) items);
            }
        });

        menu.add(remove);
        menu.add(category);
        menu.add(name);
        menu.add(description);
        menu.add(note);
        menu.add(listPrice);
        return menu;
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
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panelForTables.add(categoryFilter, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForTables.add(categoryField, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridwidth = 2;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panelForTables.add(itemFilter, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelForTables.add(itemField, gbc);
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

    private void setUpCategoryFilter() {
        categoryFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedItem = (String) categoryFilter.getSelectedItem();
                    controller.categoryFilterSelected(selectedItem);
                }
            }
        });

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

        itemFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selectedItem = (String) itemFilter.getSelectedItem();
                    controller.itemFilterSelected(selectedItem);
                }
            }
        });

    }

    //EFFECTS: create a new location view of a particular stock based on the request
    public void displayLocationStockView(String id) {
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


    public void displayRemovalRegisterTable(List<Object[]> selectedRowsCopy) {
        String[] columnNames = new String[Item.DATA_LIST.length + 1];
        System.arraycopy(Item.DATA_LIST, 0, columnNames, 0, Item.DATA_LIST.length);
        columnNames[columnNames.length - 1] = "Change in QTY";
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(selectedRowsCopy.toArray(new Object[0][]), columnNames);
        EntryRemovableTable table = new EntryRemovableTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == columnNames.length - 1) {
                    return true;
                }
                return false;
            }
        };
        table.setModel(tableModel);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.gridheight = 5;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(table), gbc);
        panel.setPreferredSize(new Dimension(900, 600));
        JButton button = new JButton("Register");
        gbc.gridx = gbc.gridx + gbc.gridwidth - 1;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        panel.add(button, gbc);
        removalRegisterDialog = new JDialog();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMaximumSize(new Dimension(600, 800));
        removalRegisterDialog.add(scrollPane);
        removalRegisterDialog.pack();
        List<Object[]> finalSelectedRowsCopy = selectedRowsCopy;
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<QuantityTag> toBeRemoved = new ArrayList<>();
//                List<Object[]> rows = new ArrayList<>();
//                rows = tableModel.getDataVector();
                int qtyChangeColumn = table.getColumnCount() - 1;
                for (int i = 0; i < finalSelectedRowsCopy.size(); i++) {
                    int idColumn = tableModel.findColumn(Item.DataList.ID.toString());
                    try {
                        int qtyChange = Integer.valueOf(table.getValueAt(i, qtyChangeColumn).toString());
                        if (qtyChange > 0) {
                            toBeRemoved.add(new QuantityTag((String) tableModel.getValueAt(i, idColumn), qtyChange));
                        }
                    } catch (NullPointerException exception) {

                    }
                }

                controller.removalRegisterButtonClicked(toBeRemoved);
            }
        });
        removalRegisterDialog.setVisible(true);
    }

    public JDialog getRemovalRegisterDialog() {
        return removalRegisterDialog;
    }




    private void setUpProductGeneratorButton() {
        productGeneratorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.addButtonClicked();
            }
        });
    }

    public JButton getProductRemovalButton() {
        return productRemovalButton;
    }

    private void setUpProductRemovalButton() {

    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        RowConverterViewerTableModel productTableModel = (RowConverterViewerTableModel) productTable.getModel();
        productTableModel.reset();
        categoryFilter.setDataFactory(inventory);
        updateItemFilter((String) categoryFilter.getSelectedItem());
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

    public void displaySearchDialog(Component parentFrame) {
        searchPanel.display(parentFrame);
    }

    public void setController(InventoryController controller) {
        this.controller = controller;
    }

    public TableRowSorter createRowSorter(JTable table,
                                          RowFilter<TableModel, Integer> filter, Comparator comparator) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        sorter.setRowFilter(filter);
        sorter.setComparator(0, comparator);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        for (int i = 0; i < table.getColumnCount(); i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
        sorter.setSortKeys(sortKeys);
        sorter.setSortsOnUpdates(true);
        return sorter;
    }

    public static KeyListener getButtonEnterKeyListener() {
        return buttonEnterListener;
    }


    public JTable createSelectedProductsTable(List<ViewableTableEntryConvertibleModel> rows) {
        JTable table = new EntryRemovableTable();
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel(rows, Product.DATA_LIST);
        table.setModel(tableModel);
        table.setPreferredSize(new Dimension(600, 400));
        return table;
    }

    public JTable createSelectedItemTable(List<ViewableTableEntryConvertibleModel> rows) {
        JTable table = new EntryRemovableTable();
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel(rows, Item.DATA_LIST);
        table.setModel(tableModel);
        table.setPreferredSize(new Dimension(600, 400));
        return table;
    }


    public static int[] getSelectedTableModelRows(JTable table) {
        int[] selectedViewIndices = table.getSelectedRows();
        int[] modelRows = new int[selectedViewIndices.length];
        for (int i = 0; i < selectedViewIndices.length; i++) {
            modelRows[i] = table.convertRowIndexToModel(selectedViewIndices[i]);
        }
        return modelRows;
    }

    public void displayRemovalConfirmDialog(List<List<Product>> productsLists) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        List<RowConverterViewerTableModel> tableModels = new ArrayList<>();
        for (List<Product> products: productsLists) {
            EntryRemovableTable productsToBeRemovedTable = new EntryRemovableTable();
            gbc.gridy = gbc.gridy + gbc.gridheight;
            gbc.gridx = 1;
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(new JLabel("ID: " + products.get(0).getID() + " Products to be removed: " + products.size()), gbc);
            RowConverterViewerTableModel toBeRemovedTableModel = new RowConverterViewerTableModel(products);
            tableModels.add(toBeRemovedTableModel);
            productsToBeRemovedTable.setModel(toBeRemovedTableModel);
            gbc.gridy = gbc.gridy + gbc.gridheight;
            gbc.gridwidth = 5;
            gbc.gridheight = 5;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            panel.add(new JScrollPane(productsToBeRemovedTable), gbc);
        }
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        int confirm = JOptionPane.showConfirmDialog(removalRegisterDialog, scrollPane, "Do you really want to remove following products?", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.productsRemovalConfirmed(productsLists);
            removalRegisterDialog.setVisible(false);
        }
    }

}
