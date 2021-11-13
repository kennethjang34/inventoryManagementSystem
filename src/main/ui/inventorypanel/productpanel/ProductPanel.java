package ui.inventorypanel.productpanel;

import model.Inventory;
import model.Product;
import ui.inventorypanel.productpanel.AddPanel;
import ui.inventorypanel.stockpanel.StockButtonTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductPanel extends JPanel implements ActionListener {
    private List<Product> products;
    private Inventory inventory;
    private ProductTable table;
    StockButtonTable stockTable;
    private static String add = "ADD";
    private static String remove = "remove";
    //Order: based on best-before date from nearest to farthest
    private static final int BEST_BEFORE_DATE_NF = 0;
    //Order: based on best-before date from farthest to nearest
    private static final int BEST_BEFORE_DATE_FN = 1;
    //Order: alphabetical order, from A to Z
    private static final int ALPHABETICAL_AZ = 2;
    //Order: alphabetical order, from Z to A
    private static final int ALPHABETICAL_ZA = 3;

    private class ProductTable extends JTable implements MouseListener {

        String[] columnNames;
        AbstractTableModel tableModel;
        List<Product> products;

        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        private ProductTable(List<Product> products) {
            this.products = products;
            columnNames = new String[]{
                    "ID", "SKU", "COST", "PRICE", "BEST-BEFORE DATE", "DATE GENERATED"
            };
            tableModel = new AbstractTableModel() {

                @Override
                public int getRowCount() {
                    return products.size();
                }


                @Override
                public int getColumnCount() {
                    return columnNames.length;
                }

                @Override
                public String getColumnName(int column) {
                    return columnNames[column];
                }


                @Override
                public Object getValueAt(int row, int column) {
                    Product product = products.get(row);
                    switch (getColumnName(column)) {
                        case "ID":
                            return product.getId();
                        case "SKU":
                            return product.getSku();
                        case "COST":
                            return product.getCost();
                        case "PRICE":
                            return product.getPrice();
                        case "BEST-BEFORE DATE":
                            return product.getBestBeforeDate();
                        case "DATE GENERATED":
                            return product.getDateGenerated();
                    }

                    return null;
                }
            };
            setModel(tableModel);
        }

        //MODIFIES: this
        //EFFECTS: sort the table based on the best before date (closest to best before date to farthest)
        public void sortByBestBeforeDateInOrder() {
            Collections.sort(products, new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return (o1.getBestBeforeDate().isBefore(o2.getBestBeforeDate()) ? -1 : 1);
                }
            });
            tableModel.fireTableDataChanged();
        }


        //REQUIRES: Order must be one of those defined in the interface provided by this
        //MODIFIES: this
        //EFFECTS: sort the table based on the specified order
        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        public void sortProducts(int order) {
            switch (order) {
                case 0:
                    Collections.sort(products, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            return (o1.getBestBeforeDate().isBefore(o2.getBestBeforeDate()) ? -1 : 1);
                        }
                    });
                    break;
                case 1:
                    Collections.sort(products, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            return (o1.getBestBeforeDate().isBefore(o2.getBestBeforeDate()) ? 1 : -1);
                        }
                    });
                    break;
                case 2:
                    Collections.sort(products, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            return (o1.getId().compareToIgnoreCase(o2.getId()) < 0 ? -1 : 1);
                        }
                    });
                    break;
                case 3:
                    Collections.sort(products, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            return (o1.getId().compareToIgnoreCase(o2.getId()) < 0 ? 1 : -1);
                        }
                    });
                    break;
            }
            tableModel.fireTableDataChanged();
        }





        //EFFECTS: return column names
        public String[] getColumnNames() {
            return columnNames;
        }

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

    public void setStockTable(StockButtonTable stockTable) {
        this.stockTable = stockTable;
    }


    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public ProductPanel(Inventory inventory) {
        //this.stockTable = stockTable;
        setLayout(new BorderLayout());
        products = new ArrayList<>();
        this.inventory = inventory;
        table = new ProductTable(products);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 600));
        add(scrollPane, BorderLayout.CENTER);
        JButton addButton = new JButton(add);
        addButton.setActionCommand(add);
        addButton.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        JButton removeButton = new JButton(remove);
        removeButton.setActionCommand(remove);
        removeButton.addActionListener(this);
        buttonPanel.add(removeButton);
        JButton sortButton = new JButton("sortByBestBeforeDate");
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.sortByBestBeforeDateInOrder();
            }
        });
        buttonPanel.add(sortButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void addToList(String id, String location) {
        List<Product> toBeAdded = inventory.getProductList(id, location);
        for (Product product: toBeAdded) {
            if (!products.contains(product)) {
                products.add(product);
            }
        }
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();
        table.repaint();
    }

    public void addToList(String id) {
        List<Product> toBeAdded = inventory.getProductList(id);
        for (Product product: toBeAdded) {
            if (!products.contains(product)) {
                products.add(product);
            }
        }
        ((AbstractTableModel)table.getModel()).fireTableDataChanged();
        table.repaint();
    }



    //Action listener for add/remove buttons
    //EFFECTS: if the action command is "add", add new products
    //if the action command is "remove", remove the selected products;
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equalsIgnoreCase(add)) {
            JDialog dialog = new JDialog();
            JButton button = new JButton();
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    ((AbstractTableModel)stockTable.getModel()).fireTableDataChanged();
                }
            });
            AddPanel addPanel = new AddPanel(inventory, button);
            //JDialog dialog = optionPane.createDialog(null, "Add new products");
            dialog.setLayout(new FlowLayout());
            dialog.add(addPanel);
            dialog.pack();
            dialog.setVisible(true);
        } else if (e.getActionCommand().equalsIgnoreCase(remove)) {
            List<Product> toBeRemoved = new ArrayList<>();
            int[] selectedRows = table.getSelectedRows();
            for (Integer row: selectedRows) {
                toBeRemoved.add(products.get(row));
            }
            int confirm = JOptionPane.showConfirmDialog(new ProductTable(toBeRemoved),
                    "Do you intend to remove following products?");
            if (confirm == JOptionPane.YES_OPTION) {
                products.removeAll(toBeRemoved);
                for (Product product: toBeRemoved) {
                    inventory.removeProduct(product.getSku());
                }
                ((AbstractTableModel)table.getModel()).fireTableDataChanged();
                ((AbstractTableModel)stockTable.getModel()).fireTableDataChanged();
                JOptionPane.showMessageDialog(null, "Successfully removed");
            }
        }
        table.repaint();
        stockTable.repaint();
    }
//
//    public static void main(String[] args) {
//        Inventory inventory = new Inventory();
//        inventory.createCategory("Fruit");
//        inventory.createItem("APP", "apple", "Fruit", 4, "test", "test");
//        List<InventoryTag> tags = new ArrayList<>();
//        tags.add(new InventoryTag("APP", 20, 30, LocalDate.now(), "F13", 100));
//        inventory.addProducts(tags);
//        inventory.createItem("BNN", "banana", "Fruit", 12, "test", "test");
//        tags = new ArrayList<>();
//        tags.add(new InventoryTag("BNN", 1, 3, LocalDate.now(), "F13", 100));
//        inventory.addProducts(tags);
//        ProductPanel productPanel = new ProductPanel(inventory);
//        productPanel.addToList("APP", "F13");
//        assert productPanel.products.size() == 100;
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        frame.setSize(500, 600);
//        frame.add(productPanel);
//        productPanel.setPreferredSize(new Dimension(400, 500));
//        frame.pack();
//        frame.setVisible(true);
//
//    }

}
