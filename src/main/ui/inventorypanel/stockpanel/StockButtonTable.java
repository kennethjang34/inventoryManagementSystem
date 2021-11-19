package ui.inventorypanel.stockpanel;

import model.Inventory;
import model.QuantityTag;
import ui.inventorypanel.productpanel.ProductPanel;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

//represents table with stocks as cells and buttons for checking locations
public class StockButtonTable extends JTable implements ActionListener, TableCellRenderer, MouseListener {
    private Inventory inventory;
    private ProductPanel productPanel;
    private String[] columnName;
    private StockLocationButtonTable locationButtonTable;

    //represents a table that contains info about stocks at different locations
    private class StockLocationButtonTable extends JTable implements ActionListener, TableCellRenderer {
        private List<QuantityTag> tags;
        private String id;
        private Object[][] data;
        private String[] columnName = new String[]{
                "Location", "Quantity", "BUTTON"
        };


        //EFFECTS: create a new empty table for displaying locations of stocks
        public StockLocationButtonTable(List<QuantityTag> tags) {
            this.tags = tags;
            data = new Object[tags.size()][];
            for (int i = 0; i < tags.size(); i++) {
                QuantityTag tag = tags.get(i);
                data[i] = new Object[]{
                        tag.getLocation(), tag.getQuantity(), new JButton()
                };
            }
            DefaultTableModel tableModel = createLocationTableModel();
            setModel(tableModel);
            setDefaultRenderer(JButton.class, this);
            setUpTableDesign();
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

        //MODIFIES: this
        //EFFECTS: set up the basic design and layout for this
        private void setUpTableDesign() {
            alignCellsCenter(this);
            alignHeaderCenter(this);
            getTableHeader().setBackground(Color.BLACK);
            getTableHeader().setForeground(Color.WHITE);
            setGridColor(Color.BLACK);
            setShowGrid(true);
        }

        //EFFECTS: return tags
        private List<QuantityTag> getTags() {
            return tags;
        }

        //EFFECTS: create a table model and return it
        private DefaultTableModel createLocationTableModel() {
            return new DefaultTableModel(data, columnName) {
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
        }

        //EFFECTS: return the component to be drawn in each cell
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
                JLabel label = new JLabel(String.valueOf(value));
                DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) getDefaultRenderer(String.class);
                Component component = renderer.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                component.setForeground(Color.WHITE);
                return component;
//                if (component instanceof JTextField) {
//                    JTextField textField = (JTextField)component;
//                    textField.setFont(new Font("arial", ))
//                }
            }
            return null;
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

        //MODIFIES: this
        //EFFECTS: add products of a particular stock to the product panel
        @Override
        public void actionPerformed(ActionEvent e) {
            String location = e.getActionCommand();
            productPanel.addToList(id, location);
        }
    }

    //EFFECTS: create a table displaying inventory condition
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
        setUpTableDesign();
//        alignCellsCenter(this);
//        alignHeaderCenter(this);
//        getTableHeader().setBackground(Color.LIGHT_GRAY);
        //getTableHeader().setForeground(Color.WHITE);
//        setGridColor(Color.BLACK);
//        setShowGrid(true);
        setDefaultRenderer(JButton.class, this);
        //setDefaultRenderer(String.class, this);
        setRowSelectionAllowed(true);
//        setDefaultRenderer(String.class, this);
        addMouseListener(this);
        setVisible(true);
    }

//    //EFFECTS: return the column class
//    @Override
//    public Class getColumnClass(int column) {
//        return getValueAt(0, column).getClass();
//    }
////
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
            return new JLabel((String)value);
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
                    productPanel.addToList(id);
                }
            }
        } else {
            //stub for secondary click
        }
    }


    //toBeDetermined
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
        JScrollPane scrollPane = new JScrollPane(locationButtonTable);
        locationViewDialog.add(scrollPane);
//        locationViewDialog.setSize(500, 600);
        locationViewDialog.pack();
        locationViewDialog.setVisible(true);
    }

    //MODIFIES: this
    //EFFECTS: change the current category of the model of this
    public void setCategory(String category) {
        ((StockButtonTableModel)getModel()).setCategory(category);
    }

    //MODIFIES: this
    //EFFECTS: change the current item selected of the model of this
    public void setItem(String id) {
        ((StockButtonTableModel)getModel()).setItem(id);
    }

    //MODIFIES: this
    //EFFECTS: align the table cells at the center
    public static void alignCellsCenter(JTable table) {
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table.getDefaultRenderer(String.class);
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

    }

    //MODIFIES: this
    //EFFECTS: align the table header at the center
    public static void alignHeaderCenter(JTable table) {
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

    }

    //MODIFIES: this
    //EFFECTS: set up the basic design/layout of this
    private void setUpTableDesign() {
        alignCellsCenter(this);
        alignHeaderCenter(this);
        getTableHeader().setBackground(Color.BLACK);
        getTableHeader().setForeground(Color.WHITE);
        setGridColor(Color.BLACK);
        setShowGrid(true);
    }

}
