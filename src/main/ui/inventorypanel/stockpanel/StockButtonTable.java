package ui.inventorypanel.stockpanel;

import model.*;
import ui.inventorypanel.productpanel.ProductPanel;


import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

//represents table with stocks as cells and buttons for checking locations
public class StockButtonTable extends JTable implements ActionListener, TableCellRenderer, MouseListener, Observer {
    private Inventory inventory;
    private ProductPanel productPanel;
    private String[] columnName;
//    private String category;
//    private String id;

//    private StockLocationButtonTable locationButtonTable;


    //EFFECTS: create a table displaying inventory condition
    public StockButtonTable(Inventory inventory, StockSearchPanel searchTool, ProductPanel productPanel) {
        this.inventory = inventory;
        this.productPanel = productPanel;
        inventory.registerObserver(this);
        String[] dataList = inventory.getDataList();
        columnName = new String[dataList.length + 1];
        for (int i = 0; i < dataList.length; i++) {
            columnName[i] = dataList[i];
        }
        columnName[columnName.length - 1] = "BUTTON";
        setModel(new StockButtonTableModel(inventory, searchTool,this));
        setUpTableDesign();
        setDefaultRenderer(JButton.class, this);
        setRowSelectionAllowed(true);
        addMouseListener(this);
        setVisible(true);
    }


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
            //JDialog locationViewDialog;
            int column = columnAtPoint(e.getPoint());
            if (getColumnName(column).equalsIgnoreCase("BUTTON")) {
                JButton button = (JButton) getValueAt(getSelectedRow(), column);
                button.doClick();
            } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
                String id = (String) getValueAt(getSelectedRow(), 1);
                productPanel.addToList(id);
            }
        } else if (e.getButton() == MouseEvent.BUTTON2) {
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
        StockLocationButtonTable locationButtonTable = new StockLocationButtonTable(tags, productPanel);
        JScrollPane scrollPane = new JScrollPane(locationButtonTable);
        locationViewDialog.add(scrollPane);
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


    //MODIFIES: this
    //EFFECTS: update this with the latest info
    @Override
    public void update(int arg) {
        if (arg == ApplicationConstantValue.STOCK || arg == ApplicationConstantValue.ITEM
                || arg == ApplicationConstantValue.CATEGORY) {
            ((AbstractTableModel) getModel()).fireTableDataChanged();
        }
    }

}
