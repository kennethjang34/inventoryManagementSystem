package ui;

import model.Inventory;
import model.InventoryTag;
import model.Item;
import model.QuantityTag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//A panel that will display stock situation of the inventory
public class StockPanel extends JPanel {
    private StockTableModel tableModel;
    private Inventory inventory;
    private String[] columnNames;
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
    private class StockTableModel extends DefaultTableModel implements ActionListener {
        Object[][] tableData;

        private StockTableModel() {
            tableData = inventory.getData();
            stockTree = new StockTree(tableData);
            //setDataVector(tableData, columnNames);
            setSize(new Dimension(500, 600));
            //setDefaultRenderer(getColumnClass(0), renderer);
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
//            if (columnIndex == 2) {
//                return true;
//            }
            return super.isCellEditable(rowIndex, columnIndex);
        }



//
//        //MODIFIES: this
//        //EFFECTS: modify this so that the panel displays the latest information of stocks
//        public void update(List<Item> items) {
//            for (Item item : items) {
//                int cellIndex = -1;
//                for (int j = 0; j < tableModel.getRowCount(); j++) {
//                    if (item.getId().equalsIgnoreCase((String) tableModel.getValueAt(j, 1))) {
//                        cellIndex = j;
//                        break;
//                    }
//                }
//                if (cellIndex != -1) {
//                    setValueAt(inventory.getQuantity(item.getId()), cellIndex, 1);
//                } else {
//                    addRow(item.convertToTableEntry());
//                }
//            }
//            fireTableDataChanged();
//        }

//        //REQUIRES: code must be existing in the inventory
//        //MODIFIES: this
//        //EFFECTS: add a new row of stock information to the table and display it
//        public void addRow(Item item) {
//
////            fireTableDataChanged();
////            int quantity = inventory.getQuantity(code);
////            JButton button = new JButton("Search");
////            button.setActionCommand(code);
////            button.addActionListener(this);
//        }

        //EFFECTS: return the name of the column
        public String getColumnName(int col) {
            return columnNames[col];
        }


        @Override
        public void actionPerformed(ActionEvent e) {
//            JDialog dialog = new JDialog();
//            dialog.setLayout(new FlowLayout());
//            String selected = e.getActionCommand();
//            List<QuantityTag> tags = inventory.getQuantitiesAtLocations(selected);
//            Object[][] data = new Object[tags.size()][];
//            for (int i = 0; i < tags.size(); i++) {
//                QuantityTag tag = tags.get(i);
//                data[i] = new Object[]{
//                        tag.getLocation(), tag.getQuantity()
//                };
//            }
//            Object[] columnsForLocations = new Object[]{"Location", "Quantity"};
//            DefaultTableModel tableModel = new DefaultTableModel();
//            tableModel.setDataVector(data, columnsForLocations);
//            JScrollPane scrollPane = new JScrollPane(new JTable(tableModel));
//            scrollPane.setVisible(true);
//            scrollPane.setSize(500, 600);
//            dialog.add(scrollPane);
//            dialog.setSize(600, 700);
//            dialog.setVisible(true);
        }
    }

//
//    //A renderer than is responsible for rendering JButton in JTable
//    private class StockTreeTableRenderer extends JTable {
//
//        TableCellRenderer renderer;
//        public StockTreeTableRenderer(TableModel model) {
//            this.renderer = renderer;
//
//        }



//        @Override
//        public Component getTableCellEditorComponent(
//                JTable table, Object value, boolean isSelected, int row, int column) {
//
//            if (column == 2 || value instanceof JButton) {
//                return (JButton)table.getValueAt(row, column);
//                //return buttons.get(row);
//            }
//            return new TextField();
//        }
//
//
//        @Override
//        public Object getCellEditorValue() {
//            return new JTextField();
//        }
//
//        @Override
//        public boolean isCellEditable(EventObject anEvent) {
//            return true;
//        }
//
//        @Override
//        public boolean shouldSelectCell(EventObject anEvent) {
//            return false;
//        }
//
//        @Override
//        public boolean stopCellEditing() {
//            return false;
//        }
//
//        @Override
//        public void cancelCellEditing() {
//
//        }
//
//        @Override
//        public void addCellEditorListener(CellEditorListener l) {
//
//        }
//
//        @Override
//        public void removeCellEditorListener(CellEditorListener l) {
//
//        }
//    }


    //EFFECTS: create new stock panel with given inventory
    public StockPanel(Inventory inventory) {
        this.inventory = inventory;
        columnNames = Inventory.getDataList();
        tableModel = new StockTableModel();
        JTable jtable = new JTable(tableModel);
        jtable.setDefaultRenderer(jtable.getColumnClass(2), stockTree);
//        jtable.setDefaultEditor(jtable.getColumnClass(2), new JTextFiel);
        //jtable.getColumnModel().getColumn(2).setCellRenderer(renderer);
        //jtable.getColumn("Search").setCellEditor(renderer);
        for (int i = 0; i < jtable.getColumnCount(); i++) {
            jtable.getColumnModel().getColumn(i).setPreferredWidth(300);
        }
        add(jtable);
        jtable.setSize(1000, 700);
        setSize(1000, 1500);
        setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: update the table and this to display the latest information
    public void update(List<Item> items) {
        //tableModel.update(codes);
        revalidate();
    }


    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        inventory.createCategory("Fruit");
        inventory.createItem("app", "apple", "Fruit", 4, "test", "test");
        List<InventoryTag> tags = new ArrayList<>();
        tags.add(new InventoryTag("abc", 20, 30, LocalDate.now(), "f11", 100));
        inventory.addProducts(tags);
//        if (inventory.getData() == null) {
//            throw new IllegalArgumentException(
        inventory.createItem("bnn", "banana", "Fruit", 12, "test", "test");
        tags = new ArrayList<>();
        tags.add(new InventoryTag("bnn", 1, 3, LocalDate.now(), "f13", 100));
        inventory.addProducts(tags);
        StockPanel panel = new StockPanel(inventory);
        panel.setSize(500, 600);
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }



}
