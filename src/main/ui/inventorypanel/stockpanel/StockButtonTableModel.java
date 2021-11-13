package ui.inventorypanel.stockpanel;

import model.Inventory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class StockButtonTableModel extends AbstractTableModel {
    private Inventory inventory;
    private String[] columnNames;
    private ActionListener actionListener;
    private HashMap<String, JButton> buttonHashMap;

    public StockButtonTableModel(Inventory inventory, ActionListener actionListener) {
        buttonHashMap = new HashMap<>();
        this.inventory = inventory;
        this.actionListener = actionListener;
        String[] dataList = inventory.getDataList();
        columnNames = new String[dataList.length + 1];
        for (int i = 0; i < dataList.length; i++) {
            columnNames[i] = dataList[i];
        }
        columnNames[columnNames.length - 1] = "BUTTON";
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
        assert inventory.getListOfCodes().size() > 0;
        String id = inventory.getListOfCodes().get(rowIndex);
        if (!getColumnName(columnIndex).equalsIgnoreCase("BUTTON")) {
            Object[] data = inventory.getData(id);
            assert data[columnIndex] != null : getColumnName(columnIndex);
            return data[columnIndex];
        } else {
            if (buttonHashMap.containsKey(id)) {
                assert buttonHashMap.get(id) != null : "hashmap";
                return buttonHashMap.get(id);
            } else {
                JButton button = new JButton();
                button.addActionListener(actionListener);
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
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int column) {

        assert getValueAt(0, column).getClass() != null : "class";
        return getValueAt(0, column).getClass();
    }
}
