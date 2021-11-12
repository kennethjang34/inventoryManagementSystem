package ui.stockpanel;

import model.*;
import ui.productpanel.ProductPanel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//A panel that will display stock situation of the inventory
public class StockPanel extends JPanel {
    private StockTableModel tableModel;
    private Inventory inventory;
    private Object[] columnNames;
    private StockTree stockTree;









    //represents a tree of data
    private class StockTree extends JTree implements TableCellRenderer {
        private DefaultMutableTreeNode root;
        private DefaultTreeModel treeModel;

        private StockTree(Object[][] data) {
            root = new DefaultMutableTreeNode();
            for (Object[] obj: data) {
//                if (obj == null) {
//                    throw new IllegalArgumentException();
//                }
                DefaultMutableTreeNode itemNode = new DefaultMutableTreeNode(obj);
                root.add(itemNode);
                Object[] entry = (Object[]) itemNode.getUserObject();
                String id = (String)entry[1];
                List<QuantityTag> tags = inventory.getQuantitiesAtLocations(id);
//                if (!inventory.containsCategory((String)entry[0])) {
//                    throw new IllegalArgumentException();
//                }
                for (QuantityTag tag: tags) {
                    itemNode.add(new DefaultMutableTreeNode(tag));
                }
            }
            treeModel = new DefaultTreeModel(root);
            setModel(treeModel);
//            addTreeSelectionListener(new TreeSelectionListener() {
//                @Override
//                public void valueChanged(TreeSelectionEvent e) {
//                    DefaultMutableTreeNode locationNode = (DefaultMutableTreeNode)
//                            e.getNewLeadSelectionPath().getLastPathComponent();
//
//                }
//            });
        }

        public TreeModel getTreeModel() {
            return treeModel;
        }


        public Object getValueAt(int row, int column) {
            TreePath path = getPathForRow(row);
            DefaultMutableTreeNode child = (DefaultMutableTreeNode)path.getLastPathComponent();
            if (child.isLeaf()) {
                QuantityTag tag = (QuantityTag)child.getUserObject();
                if (column == 0) {
                    return tag.getLocation();
                }
                return tag.getQuantity();
            }

            Object[] entry = (Object[])child.getUserObject();
            if (entry == null) {
                return null;
            }
            return entry[column];
        }

//        @Override
//        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
//                                                      boolean expanded, boolean leaf, int row, boolean hasFocus) {
//            return this;
//        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    //represents a table model
    //that will contain information of stock situation with buttons to search for quantities at different locations
    private class StockTableModel extends DefaultTableModel {
        Object[][] tableData;

        private StockTableModel() {
            tableData = inventory.getData();
            stockTree = new StockTree(tableData);
        }

        //EFFECTS: return row number
        @Override
        public int getRowCount() {
            if (tableData == null) {
                return 1;
            }
            return tableData.length;
        }

        //EFFECTS: return column number
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return stockTree.getValueAt(rowIndex, columnIndex);
        }


        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return super.isCellEditable(rowIndex, columnIndex);
        }


        //EFFECTS: return the name of the column
        public String getColumnName(int col) {
            return (String)columnNames[col];
        }
    }





    //EFFECTS: create new stock panel with given inventory
    public StockPanel(Inventory inventory) {
        JTable table = new StockButtonTable(inventory, null);
        add(table);
    }



    //EFFECTS: create new stock panel with given inventory and link it to the given product panel
    public StockPanel(Inventory inventory, ProductPanel productPanel) {
        StockButtonTable table = new StockButtonTable(inventory, productPanel);
        productPanel.setStockTable(table);
        add(table);
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.createCategory("Fruit");
        inventory.createItem("APP", "apple", "Fruit", 4, "test", "test");
        List<InventoryTag> tags = new ArrayList<>();
        tags.add(new InventoryTag("APP", 20, 30, LocalDate.now(), "F13", 1));
        inventory.addProducts(tags);
        inventory.createItem("BNN", "banana", "Fruit", 12, "test", "test");
        tags = new ArrayList<>();
        tags.add(new InventoryTag("BNN", 1, 3, LocalDate.now(), "F13", 1));
        inventory.addProducts(tags);
        ProductPanel productPanel = new ProductPanel(inventory);
        StockPanel stockPanel = new StockPanel(inventory, productPanel);

        stockPanel.setSize(500, 600);
        productPanel.setPreferredSize(new Dimension(300, 400));
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(productPanel, BorderLayout.NORTH);
        frame.add(stockPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



}
