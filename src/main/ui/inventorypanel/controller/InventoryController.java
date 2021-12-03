package ui.inventorypanel.controller;

import model.Inventory;
import model.InventoryTag;
import ui.*;
import ui.inventorypanel.CategoryGenerator;
import ui.inventorypanel.ItemGenerator;
import ui.inventorypanel.productpanel.AddPanel;
import ui.inventorypanel.view.InventoryViewPanel;

import javax.swing.*;
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
            view.displayLocationStockView(id);
        }
    }

    public InventoryController(Inventory model, InventoryViewPanel view) {
        super(model, view);
        //setUpView?
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
        setUpProductGeneratorButton();
        setUpAddPanel();
    }

//
//    private void setUpSearchPanel(SearchPanel searchPanel) {
//
//    }


    private void setUpCategoryFilter(JComboBox categoryFilter) {
        categoryFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryFilter.getSelectedItem();
                if (selectedCategory.equals("TYPE_MANUALLY")) {
                    view.getCategoryField().setVisible(true);
                }
                setUpItemFilter(view.getItemFilter(), selectedCategory);
                TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
                RowFilter<TableModel, Integer> filter = createCategoryRowFilter(selectedCategory);
                sorter.setRowFilter(filter);
            }
        });
    }


    private void setUpItemFilter(JComboBox itemFilter) {
        ////
        itemFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) itemFilter.getSelectedItem();
                if (selectedItem.equals("TYPE_MANUALLY")) {
                    view.getItemField().setVisible(true);
                }
                TableRowSorter sorter = (TableRowSorter) view.getStockButtonTable().getRowSorter();
                RowFilter<TableModel, Integer> filter = createIDRowFilter(selectedItem);
                sorter.setRowFilter(filter);
            }
        });
    }

    //implemented
    //called only when category is newly selected by categoryFilter
    //MODIFIES: item filter
    //EFFECTS: set up the item filter, so it matches the newly selected category
    private void setUpItemFilter(JComboBox itemFilter, String selectedCategory) {
        List<String> ids = null;
        if (selectedCategory.equals("ALL")) {
            ids = model.getIDs();
        } else if (!selectedCategory.equals("TYPE_MANUALLY")) {
            //in case of TYPE_MANUALLY, it's the same as ids.addAll(null);
            ids = model.getIDs(selectedCategory);
        }
        if (ids.isEmpty()) {
            ids.add("No Item");
        } else {
            ids.add(0, "ALL");
            ids.add(1, "TYPE_MANUALLY");
        }
        itemFilter.setModel(new DefaultComboBoxModel(ids.toArray(new String[0])));
    }



    //TBI
    //MODIFIES: productTable
    //EFFECTS: initialize productTable
    private void setUpProductTable(JTable productTable) {
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
        categoryGenerator.getButton().addActionListener(e -> {
            String name = categoryGenerator.getCategoryField().getText();
            name = name.toUpperCase();
            if (name.equals("")) {
                JOptionPane.showMessageDialog(null, "Category name cannot be empty");
                return;
            }
            categoryGenerator.getCategoryField().removeAll();
            if (model.createCategory(name)) {
                //stockPanel.categoryAddedUpdate(name);
                JOptionPane.showMessageDialog(null, "New Category: "
                        + name + " has been successfully created");
            } else {
                JOptionPane.showMessageDialog(null, "The category with the name: "
                        + name + " is already existing");
            }
        });
    }

//done
    //MODIFIES: Inventory
    //EFFECTS:create a new item
    private void setUpItemGenerator(ItemGenerator itemGenerator) {
        itemGenerator.getButton().addActionListener(e -> {

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
            }
            itemGenerator.clearFields();
        });
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
                        String id = (String) row[0];
                        RowConverterTableModel tableModel = (RowConverterTableModel)(view.getProductTable().getModel());
                        tableModel.addRowsWithDataList(model.getProductList(id));
                    }
                } else if (e.getButton() == MouseEvent.BUTTON2) {
                    //
                }
            }
        });
        stockTable.setRowSorter(createRowSorter(stockTable, null, Comparator.<String>naturalOrder()));
    }

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    private void setUpAddPanel() {
        AddPanel addPanel = view.getAddPanel();
        JButton button = addPanel.getButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = addPanel.getId();
                if (!model.containsItem(id)) {
                    view.displayErrorMessage(addPanel, "There is no such ID");
                    return;
                }
                double cost = convertToDoubleCost(addPanel.getCostText());
                double price = convertToDoublePrice(addPanel.getPriceText(), id);
                LocalDate bestBeforeDate = null;
                try {
                    bestBeforeDate =
                            InventoryManagementSystemApplication.convertToLocalDate(addPanel.getBestBeforeDateText());
                } catch (IllegalArgumentException ila) {
                    view.displayErrorMessage(addPanel, ila.getMessage());
                }
                String location = trimLocationFormat(addPanel.getLocationText());
                int qty = addPanel.getQuantityText().isEmpty() ? 0 : Integer.parseInt(addPanel.getQuantityText());
                InventoryTag tag = new InventoryTag(id, cost, price,
                        LocalDate.now(), bestBeforeDate, location, qty);
                model.addProducts(tag);
            }
        });
    }

    private void setUpProductGeneratorButton() {
        JButton button = view.getProductGeneratorButton();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog();
                JButton button = new JButton();
                button.addActionListener(e1 -> {
                    dialog.setVisible(false);
//                    ((AbstractTableModel)stockTable.getModel()).fireTableDataChanged();
                });
                AddPanel addPanel = view.getAddPanel();

                dialog.setLayout(new FlowLayout());
                dialog.add(addPanel);
                dialog.pack();
                dialog.setVisible(true);
            }
        });
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
                if (category.equals("ALL") || category.equals("TYPE_MANUALLY")) {
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
                if (id.equals("ALL") || id.equals("TYPE_MANUALLY")) {
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
        frame.setSize(500, 600);
        frame.setVisible(true);
    }




}
