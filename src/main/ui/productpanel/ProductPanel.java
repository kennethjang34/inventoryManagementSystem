package ui.productpanel;

import model.Inventory;
import model.InventoryTag;
import model.Product;
import model.QuantityTag;
import ui.ClickableButtonTable;
import ui.stockpanel.StockButtonTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductPanel extends JPanel implements ActionListener {
    private List<Product> products;
    private Inventory inventory;
    private ProductTable table;
    StockButtonTable stockTable;
    private static String add = "ADD";
    private static String remove = "remove";

    private class ProductTable extends ClickableButtonTable {

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
    }

    public void setStockTable(StockButtonTable stockTable) {
        this.stockTable = stockTable;
    }


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



    //Action listener for add/remove buttons
    //EFFECTS: if the action command is "add", add new products
    //if the action command is "remove", remove the selected products;
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equalsIgnoreCase(add)) {
            JDialog dialog = new JDialog();
            JButton button = new JButton();
            button.addActionListener(e1 -> dialog.setVisible(false));
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
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.createCategory("Fruit");
        inventory.createItem("APP", "apple", "Fruit", 4, "test", "test");
        List<InventoryTag> tags = new ArrayList<>();
        tags.add(new InventoryTag("APP", 20, 30, LocalDate.now(), "F13", 100));
        inventory.addProducts(tags);
        inventory.createItem("BNN", "banana", "Fruit", 12, "test", "test");
        tags = new ArrayList<>();
        tags.add(new InventoryTag("BNN", 1, 3, LocalDate.now(), "F13", 100));
        inventory.addProducts(tags);
        ProductPanel productPanel = new ProductPanel(inventory);
        productPanel.addToList("APP", "F13");
        assert productPanel.products.size() == 100;
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.add(productPanel);
        productPanel.setPreferredSize(new Dimension(400, 500));
        frame.pack();
        frame.setVisible(true);

    }

}
