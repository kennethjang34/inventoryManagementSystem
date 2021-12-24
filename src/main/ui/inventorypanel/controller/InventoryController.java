package ui.inventorypanel.controller;

import model.Inventory;
import model.InventoryTag;
import model.Item;
import model.Product;
import ui.*;
import ui.table.ButtonTable;
import ui.table.ButtonTableModel;
import ui.table.RowConverterViewerTableModel;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.AddPanel;
import ui.inventorypanel.view.FilterBox;
import ui.inventorypanel.view.InventoryViewPanel;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventoryController extends AbstractController<Inventory, InventoryViewPanel> {


    private class StockTableButtonAction extends AbstractAction {
        private int buttonColumnIndex;

        public StockTableButtonAction(ButtonTable table) {
            super("Location");
            buttonColumnIndex = table.findColumn("ID");
        }

        //EFFECTS: create a location table
        @Override
        public void actionPerformed(ActionEvent e) {
            ButtonTable table = view.getStockButtonTable();
            int row = table.findRow(e.getSource());
            int column = table.findColumn("ID");
            String id = (String) table.getValueAt(row, column);
            if (view.isLocationTableDialogDisplayed()) {
                view.addToLocationStockView(id);
            } else {
                view.displayLocationStockView(id);
            }
        }
    }

    private class LocationTableButtonAction extends AbstractAction {
        private LocationTableButtonAction() {
            super("Add");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            InventoryViewPanel.StockLocationTable locationTable =
                    (InventoryViewPanel.StockLocationTable) view.getLocationTableOnDisplay();
            String id = locationTable.getSelectedId();
            String location = locationTable.getSelectedLocation();
            RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel)(view.getProductTable().getModel());
            tableModel.addRowsWithDataList(model.getProductList(id, location));
        }
    }





    public InventoryController(Inventory model, InventoryViewPanel view) {
        super(model, view);
    }




    @Override
    public void setUpView() {
//        ButtonTable stockTable = view.getStockButtonTable();
        setUpStockTable(view.getStockButtonTable());
        setUpItemGenerator(view.getItemGenerator());
        setUpCategoryGenerator(view.getCategoryGenerator());
        setUpProductTable(view.getProductTable());
//        setUpSearchPanel(view.getSearchPanel());
        setUpCategoryFilter(view.getCategoryFilter());
        setUpItemFilter(view.getItemFilter());
        setUpCategoryField(view.getCategoryField());
        setUpItemField(view.getItemField());
        setUpRemovalButton();
        view.setLocationTableAction(new LocationTableButtonAction());
        setUpAddPanel();
    }

    private void setUpRemovalButton() {
        JButton button = view.getProductRemovalButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTable productTable = view.getProductTable();
                List<ViewableTableEntryConvertibleModel> entries = getProductTableModel().getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productRemovalHelper(entries);
            }
        });
    }

    private JTable createSelectedProductsTable(List<Object[]> rows) {
        JTable toBeRemoved = new JTable();
        toBeRemoved.setModel(new DefaultTableModel((rows.toArray(new Object[rows.size()][])), Product.DATA_LIST));
        toBeRemoved.setPreferredSize(new Dimension(600, 400));
        return toBeRemoved;
    }

    private JTable createSelectedItemTable(List<Object[]> rows) {
        JTable toBeRemoved = new JTable();
        toBeRemoved.setModel(new DefaultTableModel((rows.toArray(new Object[rows.size()][])), Item.DATA_LIST));
        toBeRemoved.setPreferredSize(new Dimension(600, 400));
        return toBeRemoved;
    }

    private void productRemovalHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable toBeRemoved = createSelectedProductsTable(getProductTableModel().getRowObjects(entries));
        JPanel tablePanel = new JPanel();
        tablePanel.add(new JLabel("The total quantity to be removed: " + entries.size()));
        tablePanel.add(toBeRemoved);
        int option = JOptionPane.showConfirmDialog(view, tablePanel,
                "Would you really like to remove the following products from the stocks?",
                JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            for (int i = 0; i < entries.size(); i++) {
                Product product = (Product) entries.get(i);
                model.removeProduct(product.getSku());
                //Product table will keep up its contents performing observer pattern(PropertyChangeEvent)
//                int correspondingTableIndex = tableModel.findRowIndex(sku, skuColumn);
//                tableModel.removeRow(correspondingTableIndex);
            }
        }
    }


    private void setUpCategoryFilter(JComboBox categoryFilter) {
        categoryFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (categoryFilter.getSelectedItem() != null) {
                    String selectedCategory = (String) categoryFilter.getSelectedItem();
                    if (selectedCategory.equals(FilterBox.TYPE_MANUALLY)) {
                        view.getCategoryField().setVisible(true);
                        return;
                    }
                    setUpItemFilter(view.getItemFilter(), selectedCategory);
                    TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
                    RowFilter<TableModel, Integer> filter = createCategoryRowFilter(selectedCategory);
                    sorter.setRowFilter(filter);
                }
            }

        });
    }


    private void setUpItemFilter(JComboBox itemFilter) {
        ////
        itemFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) itemFilter.getSelectedItem();
                if (selectedItem != null) {
                    if (selectedItem.equals("TYPE_MANUALLY")) {
                        view.getItemField().setVisible(true);
                    }
                    TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
                    RowFilter<TableModel, Integer> filter = createIDRowFilter(selectedItem);
                    sorter.setRowFilter(filter);
                }
            }
        });
    }

    //implemented
    //called only when category is newly selected by categoryFilter
    //MODIFIES: item filter
    //EFFECTS: set up the item filter, so it matches the newly selected category
    private void setUpItemFilter(FilterBox itemFilter, String selectedCategory) {
        List<String> ids;
        if (selectedCategory.equals(FilterBox.ALL)) {
            ids = model.getIDs();
        } else {
            //in case of TYPE_MANUALLY, it's the same as ids.addAll(null);
            ids = model.getIDs(selectedCategory);
        }

        if (ids.isEmpty()) {
            ids.add(FilterBox.EMPTY);
        } else {
            ids.add(0, FilterBox.ALL);
            ids.add(1, FilterBox.TYPE_MANUALLY);
//            itemFilter.setPropertyWatched(selectedCategory);
        }

        itemFilter.setModel(new DefaultComboBoxModel(ids.toArray(new String[0])));
    }



    //TBI
    //MODIFIES: productTable
    //EFFECTS: initialize productTable
    private void setUpProductTable(JTable productTable) {
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) productTable.getModel();
        tableModel.setColumnNames(Product.DATA_LIST);
        tableModel.setDuplicateAllowed(false);
        tableModel.setBaseColumnIndex(tableModel.findColumn("SKU"));
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    //
                } else if (e.getClickCount() == 2) {
                    //
                }
            }
        });
        productTable.setRowSorter(createRowSorter(productTable, null, Comparator.<String>naturalOrder()));
        JPopupMenu menu = createPopUpMenuForProductTable();
        productTable.setComponentPopupMenu(menu);
    }

    private JPopupMenu createPopUpMenuForProductTable() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem remove = new JMenuItem("remove");
        JTable productTable = view.getProductTable();
        RowConverterViewerTableModel tableModel = ((RowConverterViewerTableModel)(productTable.getModel()));
        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> entries = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productRemovalHelper(entries);
            }
        });
        JMenuItem location = new JMenuItem("change location");
        location.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> entries = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productLocationChangeHelper(entries);
            }
        });

        JMenuItem price = new JMenuItem("change price");
        price.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productPriceChangeHelper(products);
            }
        });

        JMenuItem cost = new JMenuItem("change cost");
        cost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productCostChangeHelper(products);
            }
        });

        JMenuItem bbd = new JMenuItem("change best-before-date");
        bbd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products = tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productBBDChangeHelper(products);
            }
        });

        JMenuItem id = new JMenuItem("change id");
        id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> products =
                        tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(productTable));
                productIdChangeHelper(products);
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

    private void productLocationChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(getProductTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String newLocation = JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new location for the following products");
        for (ViewableTableEntryConvertibleModel entry: entries) {
            Product product = (Product) entry;
            product.setLocation(newLocation);
        }
    }

    private void productPriceChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(getProductTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        double newPrice;
        try {
            newPrice = Double.parseDouble(JOptionPane.showInputDialog(view, toBeChanged,
                    "Enter the new price for the following products"));
        } catch (NullPointerException e) {
            return;
        }
        if (newPrice < 0) {
            InventoryViewPanel.displayErrorMessage(view, "Price cannot be negative");
            return;
        } else {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Product product = (Product) entry;
                product.setPrice(newPrice);
            }
        }
    }

    private void productCostChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(getProductTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        double newCost;
        try {
            newCost = Double.parseDouble(JOptionPane.showInputDialog(view, toBeChanged,
                    "Enter the new cost for the following products"));
        } catch (NullPointerException e) {
            return;
        }
        if (newCost < 0) {
            InventoryViewPanel.displayErrorMessage(view, "Cost cannot be negative");
            return;
        } else {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Product product = (Product) entry;
                product.setCost(newCost);
            }
        }
    }

    private void productBBDChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(getProductTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        LocalDate date;
        try {
            date = convertToLocalDate((JOptionPane.showInputDialog(view, toBeChanged,
                    "Enter the new best-before-date for the following products")));
        } catch (IllegalArgumentException e) {
            InventoryViewPanel.displayErrorMessage(view, e.getMessage());
            return;
        }
        for (ViewableTableEntryConvertibleModel entry: entries) {
            Product product = (Product) entry;
            product.setBestBeforeDate(date);
        }
    }

    private void productIdChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(getProductTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String id = (JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new id for the following products"));
        if (id == null) {
            return;
        }
        if (id.isEmpty()) {
            InventoryViewPanel.displayErrorMessage(view, "ID cannot be empty");
            return;
        }
        id = id.toUpperCase();
        if (!model.containsItem(id)) {
            InventoryViewPanel.displayErrorMessage(view, "ID: " + id + " doesn't exist");
            return;
        }
        for (ViewableTableEntryConvertibleModel entry: entries) {
            Product product = (Product) entry;
            product.setId(id);
        }
    }



    private RowConverterViewerTableModel getProductTableModel() {
        JTable productTable = view.getProductTable();
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) productTable.getModel();
        return tableModel;
    }




    private void setUpCategoryField(JTextField categoryField) {
        categoryField.setVisible(false);
        categoryField.addActionListener(e -> {
            DefaultComboBoxModel model = (DefaultComboBoxModel) view.getCategoryFilter().getModel();
            int index = model.getIndexOf(categoryField.getText());
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "There is no such category!");
            } else {
                view.getCategoryFilter().setSelectedIndex(index);
                categoryField.setVisible(false);
            }
            categoryField.removeAll();
            view.repaint();
        });
    }


    private void setUpItemField(JTextField itemField) {
        itemField.setVisible(false);
        itemField.addActionListener(e -> {
            DefaultComboBoxModel model = (DefaultComboBoxModel) view.getItemFilter().getModel();
            int index = model.getIndexOf(itemField.getText());
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "There is no such item!");
            } else {
                view.getItemFilter().setSelectedIndex(index);
                itemField.setVisible(false);
            }
            itemField.removeAll();
            view.repaint();
        });
    }

    //MODIFIES: Inventory
    //EFFECTS:create a new category
    private void setUpCategoryGenerator(CategoryGenerator categoryGenerator) {
        AbstractAction action = new AbstractAction("Create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = categoryGenerator.getCategoryField().getText();
                name = name.toUpperCase();
                if (name.equals("")) {
                    JOptionPane.showMessageDialog(null, "Category name cannot be empty");
                    return;
                }
                if (model.createCategory(name)) {
                    //stockPanel.categoryAddedUpdate(name);
                    JOptionPane.showMessageDialog(null, "New Category: "
                            + name + " has been successfully created");
                    categoryGenerator.clearFields();
                } else {
                    JOptionPane.showMessageDialog(null, "The category with the name: "
                            + name + " is already existing");
                }
            }
        };
        categoryGenerator.setAction(action);
    }

//done
    //MODIFIES: Inventory
    //EFFECTS:create a new item
    private void setUpItemGenerator(ItemGenerator itemGenerator) {
        Action buttonAction = new AbstractAction("Create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = itemGenerator.getIDFieldValue().toUpperCase();
                String category = itemGenerator.getCategoryFieldValue().toUpperCase();
                double listPrice;
                try {
                    listPrice = Double.parseDouble(itemGenerator.getPriceFieldValue());
                } catch (NumberFormatException exception) {
                    listPrice = 0;
                }
                if (id.equals("") || model.containsItem(id)) {
                    JOptionPane.showMessageDialog(null,
                            "ID is invalid or duplicate");
                } else if (!model.containsCategory(category)) {
                    JOptionPane.showMessageDialog(null,
                            "The given category: " + category + " is invalid");
                } else {
                    model.createItem(id, itemGenerator.getNameFieldValue(), category,
                            listPrice, itemGenerator.getDescriptionFieldValue(), itemGenerator.getNoteFieldValue());
                    //            stockPanel.itemAddedUpdate(id);
                    JOptionPane.showMessageDialog(null,
                            "Item: " + id + " has been successfully created");
                    itemGenerator.clearFields();
                }
            }
        };
        Action textFieldAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((JComponent)(e.getSource())).transferFocus();
            }
        };
        itemGenerator.setAction(textFieldAction, buttonAction);
    }


//done
    private void setUpStockTable(ButtonTable stockTable) {
        AbstractAction action = new StockTableButtonAction(stockTable);
        stockTable.setButtonAction(action);
        stockTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 2) {
                        Object[] row = stockTable.getSelectedRowData();
                        if (row != null) {
                            String id = (String) row[stockTable.findColumn(Inventory.ID)];
                            RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) (view.getProductTable().getModel());
                            tableModel.addRowsWithDataList(model.getProductList(id));
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    //
                }
            }
        });
        ButtonTableModel tableModel = (ButtonTableModel) stockTable.getModel();
        tableModel.setBaseColumnIndex(tableModel.findColumn(Inventory.ID));
        stockTable.setRowSorter(createRowSorter(stockTable, null, Comparator.<String>naturalOrder()));
        JPopupMenu menu = createPopUpMenuForStockTable();
        stockTable.setComponentPopupMenu(menu);
    }

    private JPopupMenu createPopUpMenuForStockTable() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem category = new JMenuItem("change category");
        JMenuItem name = new JMenuItem("change name");
        JMenuItem description = new JMenuItem("change description");
        JMenuItem note = new JMenuItem("change note");
        JMenuItem listPrice = new JMenuItem("change list price");
        JTable stockTable = view.getStockButtonTable();
        RowConverterViewerTableModel tableModel = getStockTableModel();
        category.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                itemCategoryChangeHelper(items);
            }
        });

        name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                itemNameChangeHelper(items);

            }
        });

        description.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                itemDescriptionChangeHelper(items);
            }
        });

        note.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                itemNoteChangeHelper(items);
            }
        });

        listPrice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                itemListPriceChangeHelper(items);
            }
        });


        menu.add(category);
        menu.add(name);
        menu.add(description);
        menu.add(note);
        menu.add(listPrice);
        return menu;
    }

    private void itemCategoryChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(getStockTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String newCategory = (JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new category for the following item"));
        if (newCategory == null) {
            return;
        }
        newCategory = newCategory.toUpperCase();
        if (model.containsCategory(newCategory)) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setCategory(newCategory);
            }
        }

    }

    private void itemNameChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(getStockTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String newName = (JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new name for the following item"));
        if (newName != null) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setName(newName);
            }
        }
    }

    private void itemDescriptionChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(getStockTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String newDescription = (JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new description for the following item"));
        if (newDescription != null) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setDescription(newDescription);
            }
        }
    }

    private void itemNoteChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(getStockTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String newNote = (JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new special note for the following item"));
        if (newNote != null) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setNote(newNote);
            }
        }

    }

    private void itemListPriceChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(getStockTableModel().getRowObjects(entries));
        JPanel toBeChanged = new JPanel();
        toBeChanged.add(table);
        String newPriceString = (JOptionPane.showInputDialog(view, toBeChanged,
                "Enter the new name for the following item"));
        try {
            double newListPrice = Double.parseDouble(newPriceString);
            if (newListPrice >= 0) {
                for (ViewableTableEntryConvertibleModel entry : entries) {
                    Item item = (Item) entry;
                    item.setListPrice(newListPrice);
                }
            } else {
                InventoryViewPanel.displayErrorMessage(new JLabel(newPriceString),
                        "The format of the price cannot be negative");
            }
        } catch (NumberFormatException e) {
            InventoryViewPanel.displayErrorMessage(new JLabel(newPriceString),
                    "The format of the price is not valid");
        }
    }




    private RowConverterViewerTableModel getStockTableModel() {
        return (RowConverterViewerTableModel) (view.getStockButtonTable().getModel());
    }


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void setUpAddPanel() {
        AddPanel addPanel = view.getAddPanel();
        Action buttonAction = new AbstractAction("Register") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Item ids: case-insensitive
                String id = addPanel.getId().toUpperCase();
                if (!model.containsItem(id)) {
                    view.displayErrorMessage(addPanel, "There is no such ID");
                    return;
                }
                double cost = convertToDoubleCost(addPanel.getCostText());
                double price = convertToDoublePrice(addPanel.getPriceText(), id);
                LocalDate bestBeforeDate;
                try {
                    bestBeforeDate =
                            InventoryManagementSystemApplication.convertToLocalDate(addPanel.getBestBeforeDateText());
                } catch (IllegalArgumentException ila) {
                    view.displayErrorMessage(addPanel, ila.getMessage());
                    return;
                }
                String location = trimLocationFormat(addPanel.getLocationText());
                int qty = addPanel.getQuantityText().isEmpty() ? 0 : Integer.parseInt(addPanel.getQuantityText());
                InventoryTag tag = new InventoryTag(id, cost, price,
                        LocalDate.now(), bestBeforeDate, location, qty);
                model.addProducts(tag);
                addPanel.clearFields();
            }
        };
        Action textFieldAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComponent component = (JComponent) e.getSource();
                component.transferFocus();
            }
        };
        addPanel.setAction(textFieldAction,buttonAction);
    }



    //PropertyChangeEvent is fired by InventoryView when the user has entered input through the view
    //EFFECTS: inspect the source of the propertyChangeEvent and then calls
    //the corresponding method to handle/propagate the event
    @Override
    public void propertyChange(PropertyChangeEvent evt) {


    }

    /**
     * @param category selected category
     * @return a new row filter
     * The filter won't filter any item if the selected is either "ALL" or "TYPE_MANUALLY"
     */
    public RowFilter<TableModel, Integer> createCategoryRowFilter(String category) {
        RowFilter<TableModel, Integer> rowFilter = new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                if (category.equals(FilterBox.ALL) || category.equals(FilterBox.TYPE_MANUALLY)) {
                    return true;
                }
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).equals(category)) {
                        return true;
                    }
                }
                return false;
            }
        };
        return rowFilter;
    }


    /**
     * @param id selected item id
     * @return a new row filter.
     * The filter won't filter any item if the selected is either "ALL" or "TYPE_MANUALLY"
     */
    public RowFilter<TableModel, Integer> createIDRowFilter(String id) {
        String category = (String) view.getCategoryFilter().getSelectedItem();
        RowFilter<TableModel, Integer> rowFilter = new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                if (id.equals(FilterBox.ALL) || id.equals(FilterBox.TYPE_MANUALLY)) {
                    if (category.equals(FilterBox.ALL)) {
                        return true;
                    }
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).equals(category)) {
                            return true;
                        }
                    }
                    return false;
                }
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).equals(id)) {
                        return true;
                    }
                }
                return false;
            }
        };
        return rowFilter;
    }


    /**
     * REQUIRES: there must be at least one column
     * @param comparator a comparator that is used to compare rows for sorting
     * @return a new row sorter that uses the given comparator
     */
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
        return sorter;
    }




    public void setSortingBasis(JTable table, int index, Comparator comparator) {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)table.getRowSorter();
        sorter.setComparator(index, comparator);
    }

    public void setSortingBasis(JTable table, Comparator comparator) {
        TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>)table.getRowSorter();
        sorter.setComparator(0, comparator);
    }

    //EFFECTS: process string as cost and return a valid cost.
    //if any exception happens(ex. null or empty string), return 0
    public double convertToDoubleCost(String s) {
        double cost;
        try {
            cost = Double.parseDouble(s);
        } catch (Exception e) {
            return 0;
        }
        return cost;
    }

    //REQUIRES: ID must be valid
    //EFFECTS: process string as price and return a valid price.
    //if any exception happens(ex. null or empty string), return list price of the item the product belongs to
    public double convertToDoublePrice(String s, String id) {
        double price;
        try {
            price = Double.parseDouble(s);
        } catch (Exception e) {
            return model.getListPrice(id);
        }
        return price;
    }

    //EFFECTS: process string as location and return a valid location.
    public String trimLocationFormat(String s) {
        if (s == null || s.isEmpty()) {
            return "NOT SPECIFIED";
        }
        return s.toUpperCase();
    }





    //TEST MAIN
    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        InventoryViewPanel viewPanel = new InventoryViewPanel(inventory);
        InventoryController controller = new InventoryController(inventory, viewPanel);
        JFrame frame = new JFrame();
        controller.setUpView();
        frame.add(viewPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1400, 1000);
        frame.setVisible(true);
    }

    //REQUIRES: the date must be a string type in YYYYMMDD form without any space in between
    //EFFECTS: convert the date in string form into LocalDate type
    public static LocalDate convertToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(4, 6));
            int day = Integer.parseInt(date.substring(6, 8));
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of the date is illegal");
        }
    }




}
