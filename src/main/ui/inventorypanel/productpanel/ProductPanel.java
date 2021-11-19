package ui.inventorypanel.productpanel;

import model.Inventory;
import model.InventoryTag;
import model.Product;
import ui.InventoryManagementSystemApplication;
import ui.inventorypanel.stockpanel.StockButtonTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//A panel that displays a list of individual products with buttons that can be used to add/remove products
public class ProductPanel extends JPanel implements ActionListener {
    //Order: based on best-before date from nearest to farthest
    private static final int BEST_BEFORE_DATE_NF = 0;
    //Order: based on best-before date from farthest to nearest
    private static final int BEST_BEFORE_DATE_FN = 1;
    //Order: alphabetical order, from A to Z
    private static final int ALPHABETICAL_AZ = 2;
    //Order: alphabetical order, from Z to A
    private static final int ALPHABETICAL_ZA = 3;
    private static String add = "ADD";
    private static String remove = "remove";

    private List<Product> products;
    private Inventory inventory;
    private ProductTable table;
    private StockButtonTable stockTable;
    private InventoryManagementSystemApplication application;
    private int order = BEST_BEFORE_DATE_NF;

    //represents a table where each entry contains information of each product
    private class ProductTable extends JTable implements MouseListener {
        String[] columnNames;
        AbstractTableModel tableModel;
        List<Product> products;




        //EFFECTS: create a new empty table
        private ProductTable(List<Product> products) {
            this.products = products;
            columnNames = new String[]{
                    "ID", "SKU", "COST", "PRICE", "BEST-BEFORE DATE", "DATE GENERATED", "LOCATION"
            };
            tableModel = new ProductTableModel(products, columnNames);
            setModel(tableModel);
            setUpTableDesign();
        }

        //MODIFIES: this
        //EFFECTS: set up the basic design and layout of this
        private void setUpTableDesign() {
            alignCellsCenter();
            alignHeaderCenter();
            getTableHeader().setBackground(Color.LIGHT_GRAY);
            //getTableHeader().setForeground(Color.WHITE);
            setGridColor(Color.BLACK);
            setShowGrid(true);
        }

        //MODIFIES: this
        //EFFECTS: align cells of this to the center
        private void alignCellsCenter() {
            DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)getDefaultRenderer(String.class);
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
        }

        //MODIFIES: this
        //EFFECTS: align the this header at the center
        private void alignHeaderCenter() {
            DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) getTableHeader().getDefaultRenderer();
            renderer.setHorizontalAlignment(SwingConstants.CENTER);
        }



//        //MODIFIES: this
//        //EFFECTS: sort the table based on the best before date (closest to best before date to farthest)
//        public void sortByBestBeforeDateInOrder() {
//            Collections.sort(products, new Comparator<Product>() {
//                @Override
//                public int compare(Product o1, Product o2) {
//                    if (o1.getBestBeforeDate() == null) {
//                        return 1;
//                    } else if (o2.getBestBeforeDate() == null) {
//                        return -1;
//                    }
//                    return (o1.getBestBeforeDate().isBefore(o2.getBestBeforeDate()) ? -1 : 1);
//                }
//            });
//            tableModel.fireTableDataChanged();
//        }

        //REQUIRES: Order must be one of those defined in the interface provided by this
        //MODIFIES: this
        //EFFECTS: sort the table based on the specified order
        public void sortProducts() {
            switch (order) {
                case BEST_BEFORE_DATE_NF:
                    Collections.sort(products, ((o1, o2) -> {
                        if (o1 == null) {
                            return 1;
                        }
                        return (o1.getBestBeforeDate().isBefore(o2.getBestBeforeDate()) ? -1 : 1);
                    }));
                    break;
                case BEST_BEFORE_DATE_FN:
                    Collections.sort(products, (o1, o2) ->
                            (o1.getBestBeforeDate().isBefore(o2.getBestBeforeDate()) ? 1 : -1));
                    break;
                case ALPHABETICAL_AZ:
                    Collections.sort(products, (o1, o2) ->
                            (o1.getId().compareToIgnoreCase(o2.getId()) < 0 ? -1 : 1));
                    break;
                case ALPHABETICAL_ZA:
                    Collections.sort(products, (o1, o2) ->
                            (o1.getId().compareToIgnoreCase(o2.getId()) < 0 ? 1 : -1));
                    break;
            }
            tableModel.fireTableDataChanged();
        }


        //MODIFIES: this
        //EFFECTS: set the order to match the given option
        //if the given option doesn't exist, throw Illegal argume t exception
        public void setOrder(int option) {
            if (option > ALPHABETICAL_ZA || option < BEST_BEFORE_DATE_NF) {
                throw new IllegalArgumentException("No order option matches the given input");
            }
            order = option;
        }

        //EFFECTS: return column names
        public String[] getColumnNames() {
            return columnNames;
        }


        //ToBeDetermined
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && getSelectedRow() != -1) {
                //stub
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    //EFFECTS: set stock table of this to the given stock table
    public void setStockTable(StockButtonTable stockTable) {
        this.stockTable = stockTable;
    }

    //EFFECTS: create a new product panel
    public ProductPanel(Inventory inventory, InventoryManagementSystemApplication application) {
        //this.stockTable = stockTable;
        this.application = application;
        setLayout(new BorderLayout());
        products = new ArrayList<>();
        this.inventory = inventory;
        table = new ProductTable(products);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().addChangeListener(e -> {
            table.repaint();
            scrollPane.revalidate();
        });
        scrollPane.setPreferredSize(new Dimension(500, 300));
        add(scrollPane, BorderLayout.CENTER);
//        JButton addButton = new JButton(add);
//        addButton.setActionCommand(add);
//        addButton.addActionListener(this);
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.add(addButton);
//        JButton removeButton = new JButton(remove);
//        removeButton.setActionCommand(remove);
//        removeButton.addActionListener(this);
//        buttonPanel.add(removeButton);
//        JButton sortButton = new JButton("sortByBestBeforeDate");
//        sortButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                table.sortByBestBeforeDateInOrder();
//            }
//        });
//        buttonPanel.add(sortButton);
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    //EFFECTS: create a new panel that contains functional buttons: add, remove, sort
    public JPanel createButtonPanel() {
        JButton addButton = new JButton(add);
        addButton.setActionCommand(add);
        addButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        JButton removeButton = new JButton(remove);
        removeButton.setActionCommand(remove);
        removeButton.addActionListener(this);
        buttonPanel.add(removeButton);
        JComboBox sortingOptionBox = createSortingOrderComboBox();
        buttonPanel.add(sortingOptionBox);
        JButton sortButton = new JButton("SORT");
        sortButton.addActionListener(e -> table.sortProducts());
        buttonPanel.add(sortButton);
        return buttonPanel;
    }


    //EFFECTS: create a new combo box so the user can specify the order basis
    public JComboBox createSortingOrderComboBox() {
        JComboBox comboBox = new JComboBox();
        //each option's index corresponds to their integer values defined in this
        comboBox.addItem("Best-before date from nearest");
        comboBox.addItem("Best-before date from farthest");
        comboBox.addItem("Alphabetical from A");
        comboBox.addItem("Alphabetical from Z");
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                order = comboBox.getSelectedIndex();
            }
        });
        return comboBox;
    }



    //MODIFIES: this
    //EFFECTS: add products of a specific item that is located at a particular position to the table
    public void addToList(String id, String location) {
        List<Product> toBeAdded = inventory.getProductList(id, location);
        for (Product product: toBeAdded) {
            if (!products.contains(product)) {
                products.add(product);
            }
        }
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();
        //table.repaint();
    }

    //MODIFIES: this
    //EFFECTS: add products of a specific item to the table
    public void addToList(String id) {
        List<Product> toBeAdded = inventory.getProductList(id);
        for (Product product: toBeAdded) {
            if (!products.contains(product)) {
                products.add(product);
            }
        }
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();
        //table.repaint();
    }



    //Action listener for add/remove buttons
    //EFFECTS: if the action command is "add", add new products
    //if the action command is "remove", remove the selected products;
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equalsIgnoreCase(add)) {
            JDialog dialog = new JDialog();
            JButton button = new JButton();
            button.addActionListener(e1 -> {
                dialog.setVisible(false);
                ((AbstractTableModel)stockTable.getModel()).fireTableDataChanged();
            });
            AddPanel addPanel = new AddPanel(inventory, button, application);
            //JDialog dialog = optionPane.createDialog(null, "Add new products");
            dialog.setLayout(new FlowLayout());
            dialog.add(addPanel);
            dialog.pack();
            dialog.setVisible(true);
        } else if (e.getActionCommand().equalsIgnoreCase(remove)) {
            removeProducts();
        }
        //table.repaint();
    }

    //MODIFIES: this
    //EFFECTS: starts the process to remove products
    //ask the user to confirm, and when confirmed, remove selected products from the table
    // remove products inside the given list from the table
    private void removeProducts() {
        List<Product> toBeRemoved = new ArrayList<>();
        int[] selectedRows = table.getSelectedRows();
        for (Integer row: selectedRows) {
            toBeRemoved.add(products.get(row));
        }
        int confirm = JOptionPane.showConfirmDialog(new ProductTable(toBeRemoved),
                "Do you intend to remove following products?");
        if (confirm == JOptionPane.YES_OPTION) {
            products.removeAll(toBeRemoved);
            for (Product product : toBeRemoved) {
                inventory.removeProduct(product.getSku());
            }
            application.addAccount(createRemovedTags(toBeRemoved),
                    convertToString(getListOfIDs(toBeRemoved)), LocalDate.now());
            ((AbstractTableModel) table.getModel()).fireTableDataChanged();
            ((AbstractTableModel) stockTable.getModel()).fireTableDataChanged();
            JOptionPane.showMessageDialog(null, "Successfully removed");
        }
    }


    //EFFECTS: return a list of quantity tags that contain how many products of a certain item have been removed
    public List<InventoryTag> createRemovedTags(List<Product> products) {
        return InventoryTag.createTagsForRemoved(products);
    }

    //EFFECTS: return a list of item ids whose products have been removed/added
    public List<String> getListOfIDs(List<Product> products) {
        List<String> ids = new ArrayList<>();
        for (Product product: products) {
            if (!ids.contains(product.getId())) {
                ids.add(product.getId());
            }
        }
        return ids;
    }

    //EFFECTS: convert a list into a big chunk of string
    public String convertToString(List<? extends Object> objects) {
        String s = "";
        for (Object obj: objects) {
            s += obj.toString();
        }
        return s;
    }




}
