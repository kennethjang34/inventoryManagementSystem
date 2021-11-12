package ui.stockpanel;

import model.Inventory;
import model.InventoryTag;
import model.Product;
import model.QuantityTag;
import ui.ClickableButtonTable;
import ui.productpanel.ProductPanel;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockButtonTable extends ClickableButtonTable implements ActionListener {
    Inventory inventory;
    ProductPanel productPanel;



    private class StockLocationButtonTable extends ClickableButtonTable implements ActionListener {
        private List<QuantityTag> tags;
        private String id;
        private Object[][] data;
        private Object[] column;

        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        public StockLocationButtonTable(List<QuantityTag> tags) {
            this.tags = tags;
            data = new Object[tags.size()][];
            column = new Object[]{
                    "Location", "Quantity", "Option"
            };

            for (int i = 0; i < tags.size(); i++) {
                QuantityTag tag = tags.get(i);
                data[i] = new Object[]{
                        tag.getLocation(), tag.getQuantity(), new JButton()
                };
            }
            DefaultTableModel tableModel = new DefaultTableModel(data, column) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class getColumnClass(int column) {
                    return getValueAt(0, column).getClass();
                }

                @Override
                public String getColumnName(int columnIndex) {
                    return (String)column[columnIndex];
                }
            };


            setDefaultRenderer(JButton.class, this);
            setModel(tableModel);
            for (int i = 0; i < getRowCount(); i++) {
                for (int j = 0; j < getColumnCount(); j++) {
                    if (getValueAt(i, j) instanceof JButton) {
                        JButton button = (JButton) getValueAt(i, j);
                        button.addActionListener(this);
                    }
                }
            }
            id = tags.get(0).getId();
        }





        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                JButton button = (JButton)value;
                button.setText("Add to Product List");
                String location = (String) getValueAt(row, 0);
                button.setActionCommand(location);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }


        //MODIFIES: this
        //EFFECTS: add products belonging to the selected location to the product list of the stock panel
        @Override
        public void mouseClicked(MouseEvent e) {
            int column = columnAtPoint(e.getPoint());
            if (getColumnClass(column).equals(JButton.class)) {
                JButton button = (JButton) getValueAt(getSelectedRow(), column);
                button.doClick();

            } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
                QuantityTag tag = tags.get(getSelectedRow());
                productPanel.addToList(tag.getId(), tag.getLocation());
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String location = e.getActionCommand();
            productPanel.addToList(id, location);
        }
    }

    //EFFECTS: create a table displaying inventory condition
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public StockButtonTable(Inventory inventory, ProductPanel productPanel) {
        this.inventory = inventory;
        this.productPanel = productPanel;
        setModel(new StockButtonTableModel(inventory));
        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                if (getValueAt(i, j) instanceof JButton) {
                    JButton button = (JButton) getValueAt(i, j);
                    button.addActionListener(this);
                }
            }
        }
        assert getModel().getValueAt(1, 8) instanceof JButton
                : getModel().getValueAt(1, 8).getClass().toString();
        setDefaultRenderer(JButton.class, this);
    }

    //EFFECTS: return a table cell renderer
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            JButton button = (JButton)value;
            button.setText("Location");
            String id = (String) getValueAt(row, 1);
            button.setActionCommand(id);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    //MODIFIES: this
    //EFFECTS: when double-clicked, create a new dialog that shows stocks based on locations
    @Override
    public void mouseClicked(MouseEvent e) {
        JDialog locationViewDialog;
        int column = columnAtPoint(e.getPoint());
        if (getColumnClass(column).equals(JButton.class)) {
            JButton button = (JButton) getValueAt(getSelectedRow(), column);
            button.doClick();
        } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
            locationViewDialog = new JDialog();
            locationViewDialog.setLayout(new FlowLayout());
            String id = (String) getValueAt(getSelectedRow(), 1);
            //assert id.equalsIgnoreCase("app") : id;
            List<QuantityTag> tags = inventory.getQuantitiesAtLocations(id);
            locationViewDialog.add(new StockLocationButtonTable(tags));
            locationViewDialog.setVisible(true);
        }
    }

    //MODIFIES: this
    //EFFECTS: when double-clicked, create a new dialog that shows stocks based on locations
    @Override
    public void actionPerformed(ActionEvent e) {
        String id = e.getActionCommand();
        JDialog locationViewDialog;
        locationViewDialog = new JDialog();
        locationViewDialog.setLayout(new FlowLayout());
        List<QuantityTag> tags = inventory.getQuantitiesAtLocations(id);
        locationViewDialog.add(new StockLocationButtonTable(tags));
        locationViewDialog.setSize(500, 600);
        locationViewDialog.setVisible(true);
    }

//
//    public static void main(String[] args) {
//        Inventory inventory = new Inventory();
//        inventory.createCategory("Fruit");
//        inventory.createItem("APP", "apple", "Fruit", 4, "test", "test");
//        List<InventoryTag> tags = new ArrayList<>();
//        tags.add(new InventoryTag("APP", 20, 30, LocalDate.now(), "f11", 1));
//        inventory.addProducts(tags);
////        if (inventory.getData() == null) {
////            throw new IllegalArgumentException(
//        inventory.createItem("BNN", "chicken", "Fruit", 12, "test", "test");
//        tags = new ArrayList<>();
//        tags.add(new InventoryTag("BNN", 1, 3, LocalDate.now(), "f13", 1));
//        inventory.addProducts(tags);
//        JFrame frame = new JFrame();
//        frame.setLayout(new BorderLayout());
//        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        ProductPanel panel = new ProductPanel(inventory);
//        StockPanel stockPanel = new StockPanel(inventory, panel);
//        frame.add(stockPanel, BorderLayout.SOUTH);
//        frame.add(panel, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
//    }
}
