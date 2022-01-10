package ui.inventorypanel.controller;

import model.*;
import ui.*;
import ui.inventorypanel.view.SearchPanel;
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



    public void stockTableLocationButtonClicked(ViewableTableEntryConvertibleModel selectedEntry) {
        if (selectedEntry instanceof Item) {
            Item item = (Item) selectedEntry;
            String id = item.getId();
            if (view.isLocationTableDialogDisplayed()) {
                view.addToLocationStockView(id);
            } else {
                view.displayLocationStockView(id);
            }
        }

    }

    public void inputSearch(String input) {
        Item item = model.getItem(input);
        if (item != null) {
            view.displayFoundItemWithProducts(item);
        } else {
            view.displayFoundProductWithSKU(model.getProduct(input));
        }

    }

    public void productsRemovalConfirmed(List<List<Product>> productsLists) {
        for (List<Product> products: productsLists) {
            model.removeProducts(products);
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
        view.setController(this);
        setUpCategoryField(view.getCategoryField());
        setUpItemField(view.getItemField());
        view.setLocationTableAction(new LocationTableButtonAction());
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

    public void productRemovalHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

//    public void setUpCategoryFilter(JComboBox categoryFilter) {
//        categoryFilter.addItemListener(new ItemListener() {
//            @Override
//            public void itemStateChanged(ItemEvent e) {
//                if (categoryFilter.getSelectedItem() != null) {
//                    String selectedCategory = (String) categoryFilter.getSelectedItem();
//                    if (selectedCategory.equals(FilterBox.TYPE_MANUALLY)) {
//                        view.getCategoryField().setVisible(true);
//                        return;
//                    }
//                    view.updateItemFilter(selectedCategory);
//                    TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
//                    RowFilter<TableModel, Integer> filter = createCategoryRowFilter(selectedCategory);
//                    sorter.setRowFilter(filter);
//                }
//            }
//        });
//    }

    public void categoryFilterSelected(String selectedCategory) {
        if (selectedCategory != null) {
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



    public void itemFilterSelected(String selectedItem) {
        if (selectedItem != null) {
            if (selectedItem.equals("TYPE_MANUALLY")) {
                view.getItemField().setVisible(true);
            }
            TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
            RowFilter<TableModel, Integer> filter = createIDRowFilter(selectedItem);
            sorter.setRowFilter(filter);
        }
    }




    public void productLocationChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void productPriceChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void productCostChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void productBBDChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void productIdChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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


    public void categoryGeneratorButtonClicked(String name) {
        name = name.toUpperCase();
        if (name.equals("")) {
            JOptionPane.showMessageDialog(null, "Category name cannot be empty");
            return;
        }
        if (model.createCategory(name)) {
            //stockPanel.categoryAddedUpdate(name);
            JOptionPane.showMessageDialog(null, "New Category: "
                    + name + " has been successfully created");
            view.getCategoryGenerator().clearFields();
        } else {
            JOptionPane.showMessageDialog(null, "The category with the name: "
                    + name + " is already existing");
        }
    }



    public void itemGeneratorButtonClicked(String idInput, String name, String categoryInput, String priceInput, String description, String note) {
        String id = idInput.toUpperCase();
        String category = categoryInput.toUpperCase();
        double listPrice;
        try {
            listPrice = Double.parseDouble(priceInput);
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
            model.createItem(id, name, category,
                    listPrice, description, note);
            //            stockPanel.itemAddedUpdate(id);
            JOptionPane.showMessageDialog(null,
                    "Item: " + id + " has been successfully created");
            view.getItemGenerator().clearFields();
        }
    }

    //MODIFIES: Inventory
    //EFFECTS:create a new item
//    private void setUpItemGenerator(ItemGenerator itemGenerator) {
//        Action buttonAction = new AbstractAction("Create") {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String id = itemGenerator.getIDFieldValue().toUpperCase();
//                String category = itemGenerator.getCategoryFieldValue().toUpperCase();
//                double listPrice;
//                try {
//                    listPrice = Double.parseDouble(itemGenerator.getPriceFieldValue());
//                } catch (NumberFormatException exception) {
//                    listPrice = 0;
//                }
//                if (id.equals("") || model.containsItem(id)) {
//                    JOptionPane.showMessageDialog(null,
//                            "ID is invalid or duplicate");
//                } else if (!model.containsCategory(category)) {
//                    JOptionPane.showMessageDialog(null,
//                            "The given category: " + category + " is invalid");
//                } else {
//                    model.createItem(id, itemGenerator.getNameFieldValue(), category,
//                            listPrice, itemGenerator.getDescriptionFieldValue(), itemGenerator.getNoteFieldValue());
//                    //            stockPanel.itemAddedUpdate(id);
//                    JOptionPane.showMessageDialog(null,
//                            "Item: " + id + " has been successfully created");
//                    itemGenerator.clearFields();
//                }
//            }
//        };
//        Action textFieldAction = new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                ((JComponent)(e.getSource())).transferFocus();
//            }
//        };
//        itemGenerator.setAction(textFieldAction, buttonAction);
//    }

    public void stockTableRowDoubleClicked(ViewableTableEntryConvertibleModel entry) {
        if (entry instanceof Item) {
            Item item = (Item) entry;
            RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) (view.getProductTable().getModel());
            item.addDataChangeListener(Item.DataList.PRODUCT.toString(), tableModel);
            tableModel.addRowsWithDataList(item.getProducts());
        }
    }


    public void itemCategoryChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void itemNameChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void itemDescriptionChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void itemNoteChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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

    public void itemListPriceChangeHelper(List<ViewableTableEntryConvertibleModel> entries) {
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


    public void itemStockRemovalHelper(List<Item> items) {
        List<Object[]> selectedRows = getStockTableModel().getRowObjects(items);
        List<Object[]> copy = new LinkedList<>();
        for (int i = 0; i < selectedRows.size(); i++) {
            Object[] newRow = (Arrays.copyOf(selectedRows.get(i), selectedRows.get(i).length));
            newRow[newRow.length - 1] = null;
            copy.add(newRow);
        }
        view.displayRemovalRegisterTable(copy);
    }

    public void removalRegisterButtonClicked(List<QuantityTag> toBeRemoved) {
        List<List<Product>> productsLists = new ArrayList<>();
        for (QuantityTag tag: toBeRemoved) {
            try {
                productsLists.add(model.getProductList(tag.getId(), tag.getLocation(), tag.getQuantity()));
            } catch (IndexOutOfBoundsException exception) {
                JOptionPane.showMessageDialog(view, "cannot remove more productsToBeRemoved than existing. Check stocks for item: " + tag.getId());
                return;
            }
        }
        view.displayRemovalConfirmDialog(productsLists);
    }



    private RowConverterViewerTableModel getStockTableModel() {
        return (RowConverterViewerTableModel) (view.getStockButtonTable().getModel());
    }

    public void productsAdditionRequest(String idInput, String costInput, String priceInput,
                              String bestBeforeDateInput, String locationInput, String qtyInput, String description) {
        JPanel addPanel = getView().getAddPanel();
        String id = idInput.toUpperCase();
        if (!model.containsItem(id)) {
            view.displayErrorMessage(addPanel, "There is no such ID");
            return;
        }
        double cost = convertToDoubleCost(costInput);
        double price = convertToDoublePrice(priceInput, id);
        LocalDate bestBeforeDate;
        try {
            bestBeforeDate =
                    InventoryManagementSystemApplication.convertToLocalDate(bestBeforeDateInput);
        } catch (IllegalArgumentException ila) {
            view.displayErrorMessage(addPanel, ila.getMessage());
            return;
        }
        String location = trimLocationFormat(locationInput);
        int qty = qtyInput.isEmpty() ? 0 : Integer.parseInt(qtyInput);
        InventoryTag tag = new InventoryTag(id, cost, price,
                LocalDate.now(), bestBeforeDate, location, qty, description);
        model.addProducts(tag);
        view.clearAddPanelFields();
    }

    public void addButtonClicked() {
        view.displayAddDialog(view);
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

    public void displaySearchDialog(Component parentComponent) {
        view.displaySearchDialog(parentComponent);
    }


}
