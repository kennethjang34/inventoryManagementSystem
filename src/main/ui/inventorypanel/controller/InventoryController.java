package ui.inventorypanel.controller;

import model.*;
import ui.*;
import ui.table.*;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.view.AddPanel;
import ui.FilterBox;
import ui.inventorypanel.view.InventoryViewPanel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class InventoryController extends AbstractController<Inventory, InventoryViewPanel> {


    private class StockTableButtonAction extends AbstractAction {
        private int buttonColumnIndex;

        public StockTableButtonAction(ButtonTable table) {
            super("Location");
            buttonColumnIndex = table.findColumnModelIndex("ID");
        }

        //EFFECTS: create a location table
        @Override
        public void actionPerformed(ActionEvent e) {
            ButtonTable table = view.getStockButtonTable();
            int row = table.findModelRowIndex(e.getSource());
            int column = table.findColumnModelIndex("ID");
            String id = (String) table.getModel().getValueAt(row, column);
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

    private JTable createSelectedProductsTable(List<ViewableTableEntryConvertibleModel> rows) {
        JTable table = new EntryRemovableTable();
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel(rows, Product.DATA_LIST);
        table.setModel(tableModel);
        table.setPreferredSize(new Dimension(600, 400));
        return table;
    }

    private JTable createSelectedItemTable(List<ViewableTableEntryConvertibleModel> rows) {
        JTable table = new EntryRemovableTable();
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel(rows, Item.DATA_LIST);
        table.setModel(tableModel);
        table.setPreferredSize(new Dimension(600, 400));
        return table;
    }

    private void productRemovalHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JLabel("The total quantity to be removed: " + entries.size()));
        panel.add(new JScrollPane(table));
        int option = JOptionPane.showConfirmDialog(view, panel,
                "Would you really like to remove the following products from the stocks?",
                JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE);
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
        if (option == JOptionPane.YES_OPTION) {
            for (int i = 0; i < tableModel.getEntryModels().size(); i++) {
                Product product = (Product) tableModel.getEntryModels().get(i);
                model.removeProduct(product.getSku());
            }
        }
    }


    private void setUpCategoryFilter(JComboBox categoryFilter) {
        categoryFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (categoryFilter.getSelectedItem() != null) {
                    String selectedCategory = (String) categoryFilter.getSelectedItem();
                    if (selectedCategory.equals(FilterBox.TYPE_MANUALLY)) {
                        view.getCategoryField().setVisible(true);
                        return;
                    }
                    view.updateItemFilter(selectedCategory);
                    TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
                    RowFilter<TableModel, Integer> filter = createCategoryRowFilter(selectedCategory);
                    sorter.setRowFilter(filter);
                }
            }
        });
    }


    private void setUpItemFilter(JComboBox itemFilter) {

        itemFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
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
        JPopupMenu menu = view.getProductTable().getComponentPopupMenu();
        if (menu == null) {
            menu = new JPopupMenu();
        }
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
        JTable table = createSelectedProductsTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String newLocation = JOptionPane.showInputDialog(view, panel,
                "Enter the new location for the following products");
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
        for (int i = 0; i < tableModel.getEntryModels().size(); i++) {
            Product product = (Product) tableModel.getEntryModels().get(i);
            product.setLocation(newLocation);
        }
    }

    private void productPriceChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        double newPrice;
        try {
            newPrice = Double.parseDouble(JOptionPane.showInputDialog(view, panel,
                    "Enter the new price for the following products"));
        } catch (NullPointerException e) {
            return;
        }
        if (newPrice < 0) {
            InventoryViewPanel.displayErrorMessage(view, "Price cannot be negative");
            return;
        } else {
            RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
            for (int i = 0; i < tableModel.getEntryModels().size(); i++) {
                Product product = (Product) tableModel.getEntryModels().get(i);
                product.setPrice(newPrice);
            }
        }
    }

    private void productCostChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        double newCost;
        try {
            newCost = Double.parseDouble(JOptionPane.showInputDialog(view, panel,
                    "Enter the new cost for the following products"));
        } catch (NullPointerException e) {
            return;
        }
        if (newCost < 0) {
            InventoryViewPanel.displayErrorMessage(view, "Cost cannot be negative");
            return;
        } else {
            RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
            for (int i = 0; i < tableModel.getEntryModels().size(); i++) {
                Product product = (Product) tableModel.getEntryModels().get(i);
                product.setCost(newCost);
            }
        }
    }

    private void productBBDChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        LocalDate date;
        try {
            date = convertToLocalDate((JOptionPane.showInputDialog(view, panel,
                    "Enter the new best-before-date for the following products")));
        } catch (IllegalArgumentException e) {
            InventoryViewPanel.displayErrorMessage(view, e.getMessage());
            return;
        }

        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
        for (int i = 0; i < tableModel.getEntryModels().size(); i++) {
            Product product = (Product) tableModel.getEntryModels().get(i);
            product.setBestBeforeDate(date);
        }

    }

    private void productIdChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedProductsTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String id = (JOptionPane.showInputDialog(view, panel,
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

        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
        for (int i = 0; i < tableModel.getEntryModels().size(); i++) {
            Product product = (Product) tableModel.getEntryModels().get(i);
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
                            String id = (String) row[stockTable.findColumnModelIndex(Inventory.ID)];
                            RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) (view.getProductTable().getModel());
                            model.getItem(id).addDataChangeListener(Item.DataList.PRODUCT.toString(), tableModel);
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
        JMenuItem remove = new JMenuItem("remove products");
        JMenuItem add = new JMenuItem("add products");
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

        remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<? extends ViewableTableEntryConvertibleModel> items = (tableModel.getEntryModelList(InventoryViewPanel.getSelectedTableModelRows(stockTable)));
                itemStockRemovalHelper((List<Item>) items);

            }
        });

        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

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

    private void itemCategoryChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String newCategory = (JOptionPane.showInputDialog(view, panel,
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
        JTable table = createSelectedItemTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String newName = (JOptionPane.showInputDialog(view, panel,
                "Enter the new name for the following item"));
        if (newName != null) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setName(newName);
            }
        }
    }

    private void itemDescriptionChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String newDescription = (JOptionPane.showInputDialog(view, panel,
                "Enter the new description for the following item"));
        if (newDescription != null) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setDescription(newDescription);
            }
        }
    }

    private void itemNoteChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String newNote = (JOptionPane.showInputDialog(view, panel,
                "Enter the new special note for the following item"));
        if (newNote != null) {
            for (ViewableTableEntryConvertibleModel entry: entries) {
                Item item = (Item) entry;
                item.setNote(newNote);
            }
        }
    }

    private void itemListPriceChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
        JTable table = createSelectedItemTable(entries);
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(table));
        String newPriceString = (JOptionPane.showInputDialog(view, panel,
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


    private void itemStockRemovalHelper(List<Item> items) {
        List<Object[]> selectedRows = getStockTableModel().getRowObjects(items);
        List<Object[]> copy = new LinkedList<>();
        for (int i = 0; i < selectedRows.size(); i++) {
            Object[] newRow = (Arrays.copyOf(selectedRows.get(i), selectedRows.get(i).length));
            newRow[newRow.length - 1] = null;
            copy.add(newRow);
//            selectedRows.get(i)[selectedRows.get(0).length - 1] = null;
        }
        String[] columnNames = new String[Item.DATA_LIST.length + 1];
        System.arraycopy(Item.DATA_LIST, 0, columnNames, 0, Item.DATA_LIST.length);
        columnNames[columnNames.length - 1] = "Change in QTY";
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setDataVector(copy.toArray(new Object[0][]), columnNames);
        EntryRemovableTable table = new EntryRemovableTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == columnNames.length - 1) {
                    return true;
                }
                return false;
            }
        };
//        JTable table = new JTable() {
//
//            @Override
//            public String getToolTipText(MouseEvent e) {
//                Point p = e.getPoint();
//                int row = rowAtPoint(p);
//                int column = columnAtPoint(p);
//                if (row == -1 || column == -1) {
//                    return null;
//                }
//                String toolTip = getValueAt(row, column).toString();
//                return toolTip;
//            }
//
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                if (column == columnNames.length - 1) {
//                    return true;
//                }
//                return false;
//            }
//        };
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
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<QuantityTag> toBeRemoved = new ArrayList<>();
                int qtyChangeColumn = table.getColumnCount() - 1;
                for (int i = 0; i < selectedRows.size(); i++) {
                    int idColumn = tableModel.findColumn(Item.DataList.ID.toString());
                    try {
                        int qtyChange = Integer.valueOf(table.getValueAt(i, qtyChangeColumn).toString());
                        if (qtyChange > 0) {
                            toBeRemoved.add(new QuantityTag((String) tableModel.getValueAt(i, idColumn), qtyChange));
                        }
                    } catch (NullPointerException exception) {

                    }
                }
                JPanel panel = new JPanel();
                panel.setLayout(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                List<RowConverterViewerTableModel> tableModels = new ArrayList<>();
                for (QuantityTag tag: toBeRemoved) {
                    EntryRemovableTable productsToBeRemovedTable = new EntryRemovableTable();
                    List<Product> productsToBeRemoved;
                    try {
                        productsToBeRemoved = model.getProductList(tag.getId(), tag.getLocation(), tag.getQuantity());
                    } catch (IndexOutOfBoundsException exception) {
                        JOptionPane.showMessageDialog(view,"cannot remove more productsToBeRemoved than existing. Check stocks for item: " + tag.getId());
                        return;
                    }
                    gbc.gridy = gbc.gridy + gbc.gridheight;
                    gbc.gridx = 1;
                    gbc.gridheight = 1;
                    gbc.gridwidth = 1;
                    gbc.fill = GridBagConstraints.NONE;
                    panel.add(new JLabel("ID: " + tag.getId() + " Products to be removed: " + productsToBeRemoved.size()), gbc);
                    RowConverterViewerTableModel toBeRemovedTableModel = new RowConverterViewerTableModel(productsToBeRemoved);
                    tableModels.add(toBeRemovedTableModel);
                    productsToBeRemovedTable.setModel(toBeRemovedTableModel);
//                    gbc.gridx = gbc.gridx + gbc.gridWidth;
                    gbc.gridy = gbc.gridy + gbc.gridheight;
                    gbc.gridwidth = 5;
                    gbc.gridheight = 5;
                    gbc.weightx = 1;
                    gbc.weighty = 1;
                    gbc.fill = GridBagConstraints.BOTH;
                    panel.add(new JScrollPane(productsToBeRemovedTable), gbc);
                }
                JScrollPane scrollPane = new JScrollPane(panel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setMaximumSize(new Dimension(600, 700));
                scrollPane.setPreferredSize(new Dimension(600, 600));
                scrollPane.scrollRectToVisible(scrollPane.getVisibleRect());
                int confirm = JOptionPane.showConfirmDialog(view, scrollPane, "Do you really want to remove following products?", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    for (RowConverterViewerTableModel tableProductsToBeRemoved: tableModels) {
                        model.removeProducts((List<Product>) tableProductsToBeRemoved.getEntryModelList());
                    }
                }
            }
        });
        JDialog dialog = new JDialog();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setMaximumSize(new Dimension(600, 800));
        dialog.add(scrollPane);
        dialog.pack();
        dialog.setVisible(true);
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
        sorter.setSortsOnUpdates(true);
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


    //REQUIRES: the date must be a string type in YYYYMMDD form without any space in between
    //EFFECTS: convert the date in string form into LocalDate type
    public static LocalDate convertToLocalDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            int year = Integer.parseInt(date.substring(0, 4));
            int month = Integer.parseInt(date.substring(4, 6));
            int day = Integer.parseInt(date.substring(6, 8));
            return LocalDate.of(year, month, day);
        } catch (Exception e) {
            throw new IllegalArgumentException("The format of the date is illegal");
        }
    }


    public void setInventory(Inventory model) {
        this.model = model;
        view.setInventory(model);
    }



}
