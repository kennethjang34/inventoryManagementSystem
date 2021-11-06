package ui;

import model.Inventory;
import model.QuantityTag;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;

//A panel that will display stock situation of the inventory
public class StockPanel extends JPanel {
    private StockTableModel tableModel;
    private Inventory inventory;
    private String[] columnNames = {"Code", "Quantity", "Search"};
    private StockTableCellRenderer renderer;

    //represents a table model
    //that will contain information of stock situation with buttons to search for quantities at different locations
    private class StockTableModel extends AbstractTableModel implements ActionListener {
        List<String> codes = inventory.getListOfCodes();


        private StockTableModel() {
            renderer = new StockTableCellRenderer();
        }

        //EFFECTS: return row number
        @Override
        public int getRowCount() {
            return codes.size();
        }

        //EFFECTS: return column number
        @Override
        public int getColumnCount() {
            return 3;
        }

        //EFFECTS: return the value in a particular cell.
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            String code = codes.get(rowIndex);
            if (columnIndex == 0) {
                return code;
            } else if (columnIndex == 1) {
                return inventory.getQuantity(code);
            } else {
                JButton button = new JButton("Search");
                button.setActionCommand(code);
                button.addActionListener(this);
                return button;
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                return true;
            }
            return super.isCellEditable(rowIndex, columnIndex);
        }


        //MODIFIES: this
        //EFFECTS: modify this so that the panel displays the latest information of stocks
        public void update(List<String> codes) {
            for (int i = 0; i < codes.size(); i++) {
                String code = codes.get(i);
                int cellIndex = -1;
                for (int j = 0; j < tableModel.getRowCount(); j++) {
                    if (code.equalsIgnoreCase((String)tableModel.getValueAt(j, 0))) {
                        cellIndex = j;
                        break;
                    }
                }
                if (cellIndex != -1) {
                    setValueAt(inventory.getQuantity(code), cellIndex, 1);
                } else {
                    addRow(code);
                }
            }
            fireTableDataChanged();
        }

        //REQUIRES: code must be existing in the inventory
        //MODIFIES: this
        //EFFECTS: add a new row of stock information to the table and display it
        public void addRow(String code) {
            this.codes.add(code);
            fireTableDataChanged();
//            int quantity = inventory.getQuantity(code);
//            JButton button = new JButton("Search");
//            button.setActionCommand(code);
//            button.addActionListener(this);
        }

        //EFFECTS: return the name of the column
        public String getColumnName(int col) {
            return columnNames[col];
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog dialog = new JDialog();
            dialog.setLayout(new FlowLayout());
            String selected = e.getActionCommand();
            List<QuantityTag> tags = inventory.getQuantitiesAtLocations(selected);
            Object[][] data = new Object[tags.size()][];
            for (int i = 0; i < tags.size(); i++) {
                QuantityTag tag = tags.get(i);
                data[i] = new Object[]{
                        tag.getLocation(), tag.getQuantity()
                };
            }
            Object[] columnsForLocations = new Object[]{"Location", "Quantity"};
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.setDataVector(data, columnsForLocations);
            dialog.add(new JTable(tableModel));
            dialog.setSize(600, 700);
            dialog.setVisible(true);
        }
    }


    //A renderer than is responsible for rendering JButton in JTable
    private class StockTableCellRenderer extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {



        //REQUIRES: value must be of JButton type
        //EFFECTS: convert the value into JButton and return it
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (JButton)value;
            }
            return new JLabel(value.toString());
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {

            if (column == 2 || value instanceof JButton) {
                return (JButton)table.getValueAt(row, column);
                //return buttons.get(row);
            }
            return new TextField();
        }


        @Override
        public Object getCellEditorValue() {
            return new JButton();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }
    }

    //EFFECTS: create new stock panel with given inventory
    public StockPanel(Inventory inventory) {
        this.inventory = inventory;
        tableModel = new StockTableModel();
        JTable jtable = new JTable(tableModel);
        jtable.setDefaultRenderer(jtable.getColumnClass(2), renderer);
        jtable.setDefaultEditor(jtable.getColumnClass(2), renderer);
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
    public void update(List<String> codes) {
        tableModel.update(codes);
        revalidate();
    }



}
