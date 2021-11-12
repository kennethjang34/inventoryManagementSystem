package ui.stockpanel;

import model.Inventory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.HashMap;

public class StockButtonTableModel extends AbstractTableModel {
    private Inventory inventory;
    private String[] columnNames;
    private HashMap<String, JButton> buttonHashMap;

    public StockButtonTableModel(Inventory inventory) {
        buttonHashMap = new HashMap<>();
        this.inventory = inventory;
        columnNames = (String[])Inventory.getDataList();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String id = inventory.getListOfCodes().get(rowIndex);
        if (!getColumnName(columnIndex).equalsIgnoreCase("OPTION")) {
            Object[] data = inventory.getData(id);
            return data[columnIndex];
        } else {
            if (buttonHashMap.containsKey(id)) {
                return buttonHashMap.get(id);
            } else {
                JButton button = new JButton();
                buttonHashMap.put(id, button);
                return button;
            }
        }
    }

    @Override
    public int getRowCount() {
        return inventory.getListOfCodes().size();
    }

    @Override
    public int getColumnCount() {
        return Inventory.getDataList().length;
    }

    @Override
    public Class getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }
}
