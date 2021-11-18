package ui.inventorypanel.stockpanel;

import model.Inventory;
import model.QuantityTag;
import ui.inventorypanel.productpanel.ProductPanel;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class StockButtonTable extends JTable implements ActionListener, TableCellRenderer, MouseListener {
    private Inventory inventory;
    private ProductPanel productPanel;
    private String[] columnName;
    private StockLocationButtonTable locationButtonTable;



    private class StockLocationButtonTable extends JTable implements ActionListener, TableCellRenderer {
        private List<QuantityTag> tags;
        private String id;
        private Object[][] data;
        private String[] columnName;

        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        public StockLocationButtonTable(List<QuantityTag> tags) {
            this.tags = tags;
            data = new Object[tags.size()][];
            columnName = new String[]{
                    "Location", "Quantity", "BUTTON"
            };

            for (int i = 0; i < tags.size(); i++) {
                QuantityTag tag = tags.get(i);
                data[i] = new Object[]{
                        tag.getLocation(), tag.getQuantity(), new JButton()
                };
            }
            DefaultTableModel tableModel = new DefaultTableModel(data, columnName) {
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
                    return columnName[columnIndex];
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
            addMouseListener(StockButtonTable.this);
        }

        private List<QuantityTag> getTags() {
            return tags;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                JButton button = (JButton)value;
                button.setText("Add to Product List");
                String location = (String) getValueAt(row, 0);
                button.setActionCommand(location);
                return button;
            } else if (value instanceof String || value instanceof Double || value instanceof Integer) {
                return this;
            }

            throw new RuntimeException("Value inside JTable is invalid");
        }

//
//        //MODIFIES: this
//        //EFFECTS: add products belonging to the selected location to the product list of the stock panel
//        @Override
//        public void mouseClicked(MouseEvent e) {
//            int column = columnAtPoint(e.getPoint());
//            if (getColumnClass(column).equals(JButton.class)) {
//                JButton button = (JButton) getValueAt(getSelectedRow(), column);
//                button.doClick();
//
//            } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
//                QuantityTag tag = tags.get(getSelectedRow());
//                productPanel.addToList(tag.getId(), tag.getLocation());
//            }
//        }

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
        String[] dataList = inventory.getDataList();
        columnName = new String[dataList.length + 1];
        for (int i = 0; i < dataList.length; i++) {
            columnName[i] = dataList[i];
        }
        columnName[columnName.length - 1] = "BUTTON";
        setModel(new StockButtonTableModel(inventory, this));


        assert getModel().getValueAt(1, 8) instanceof JButton
                : getModel().getValueAt(1, 8).getClass().toString();
        setDefaultRenderer(JButton.class, this);
//        setDefaultRenderer(String.class, this);
        addMouseListener(this);
    }


    @Override
    public Class getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }
//
//    @Override
//    public String getColumnName(int col) {
//        return columnName[col];
//    }

    //EFFECTS: return a table cell renderer
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            JButton button = (JButton)value;
            ((JButton) value).setBackground(Color.BLACK);
            button.setText("LOCATION");
            String id = (String) getValueAt(row, 1);
            button.setActionCommand(id);
            return button;
        } else if (value instanceof String || value instanceof Double || value instanceof Integer) {
            return this;
        }
        return null;
    }

    //MODIFIES: this
    //EFFECTS: when double-clicked, create a new dialog that shows stocks based on locations
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getSource() instanceof StockLocationButtonTable) {
                int column = locationButtonTable.columnAtPoint(e.getPoint());
                if (locationButtonTable.getColumnClass(column).equals(JButton.class)) {
                    JButton button = (JButton) locationButtonTable.getValueAt(locationButtonTable.getSelectedRow(),
                            column);
                    button.doClick();
                } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
                    QuantityTag tag = locationButtonTable.getTags().get(locationButtonTable.getSelectedRow());
                    productPanel.addToList(tag.getId(), tag.getLocation());
                }
            } else {
                //JDialog locationViewDialog;
                int column = columnAtPoint(e.getPoint());
                if (getColumnName(column).equalsIgnoreCase("BUTTON")) {
                    JButton button = (JButton) getValueAt(getSelectedRow(), column);
                    button.doClick();
                } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
                    String id = (String) getValueAt(getSelectedRow(), 1);
//
//            locationViewDialog = new JDialog();
//            locationViewDialog.setLayout(new FlowLayout());
//            //assert id.equalsIgnoreCase("app") : id;
//            List<QuantityTag> tags = inventory.getQuantitiesAtLocations(id);
//            locationViewDialog.add(new StockLocationButtonTable(tags));
//            locationViewDialog.setVisible(true);
                    productPanel.addToList(id);
                }
            }
        } else {
            //stub for secondary click
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

    //MODIFIES: this
    //EFFECTS: when the button is clicked, create a new dialog that shows stocks based on locations
    @Override
    public void actionPerformed(ActionEvent e) {
        String id = e.getActionCommand();
        JDialog locationViewDialog;
        locationViewDialog = new JDialog();
        locationViewDialog.setLayout(new FlowLayout());
        List<QuantityTag> tags = inventory.getQuantitiesAtLocations(id);
        locationButtonTable = new StockLocationButtonTable(tags);
        locationViewDialog.add(locationButtonTable);
        locationViewDialog.setSize(500, 600);
        locationViewDialog.setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: change the current category of the model of this
    public void setCategory(String category) {
        ((StockButtonTableModel)getModel()).setCategory(category);
        //repaint();
    }

    //MODIFIES: this
    //EFFECTS: change the current item selected of the model of this
    public void setItem(String id) {
        ((StockButtonTableModel)getModel()).setItem(id);
    }


}
