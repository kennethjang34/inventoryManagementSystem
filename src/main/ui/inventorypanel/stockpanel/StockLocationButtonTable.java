package ui.inventorypanel.stockpanel;

import model.QuantityTag;
import ui.inventorypanel.productpanel.ProductPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import static ui.inventorypanel.stockpanel.StockButtonTable.alignCellsCenter;
import static ui.inventorypanel.stockpanel.StockButtonTable.alignHeaderCenter;

//represents a table that contains info about stocks at different locations
public class StockLocationButtonTable extends JTable implements ActionListener, TableCellRenderer, MouseListener {
    private ProductPanel productPanel;
    private List<QuantityTag> tags;
    private String id;
    private Object[][] data;
    private String[] columnName = new String[]{
            "Location", "Quantity", "BUTTON"
    };

    //EFFECTS: create a new empty table for displaying locations of stocks
    public StockLocationButtonTable(List<QuantityTag> tags, ProductPanel productPanel) {
        this.productPanel = productPanel;
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
        addMouseListener(this);
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

        }
        return null;
    }


    //MODIFIES: this
    //EFFECTS: add products of a particular stock to the product panel
    @Override
    public void actionPerformed(ActionEvent e) {
        String location = e.getActionCommand();
        productPanel.addToList(id, location);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            int column = columnAtPoint(e.getPoint());
            if (getColumnClass(column).equals(JButton.class)) {
                JButton button = (JButton) getValueAt(getSelectedRow(),
                        column);
                button.doClick();
            } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
                QuantityTag tag = getTags().get(getSelectedRow());
                productPanel.addToList(tag.getId(), tag.getLocation());
            }
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            //
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
