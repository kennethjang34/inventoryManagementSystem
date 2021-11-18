package ui.inventorypanel.stockpanel;

import model.Inventory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.util.HashMap;

//represents a table model that displays stock conditions
public class StockButtonTableModel extends AbstractTableModel {
    private Inventory inventory;
    private String[] columnNames;
    private ActionListener actionListener;
    private HashMap<String, JButton> buttonHashMap;
    private String category = null;
    private String item = null;

    //EFFECTS: create a new table model
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

    //EFFECTS: return false so that every cell is not editable
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    //EFFECTS: return the name of the column
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }


    //EFFECTS: return a value at a particular table cell
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String id;
        if (category == null && item == null) {
            id = inventory.getIDs().get(rowIndex);
        } else if (item == null) {
            id = inventory.getIDs(category).get(rowIndex);
        } else {
            id = item;
        }
        if (!getColumnName(columnIndex).equalsIgnoreCase("BUTTON")) {
            Object[] data = inventory.getData(id);
            return data[columnIndex];
        } else {
            return getButton(id);
        }
    }

    //MODIFIES: this
    //EFFECTS: if there is a button related to the given id, return the button
    //If there isn't, create one and return it
    public JButton getButton(String id) {
        if (buttonHashMap.containsKey(id)) {
            //assert buttonHashMap.get(id) != null : "hashmap";
            return buttonHashMap.get(id);
        } else {
            JButton button = new JButton();
            button.addActionListener(actionListener);
            buttonHashMap.put(id, button);
            return button;
        }
    }

    //EFFECTS: return the number of rows of the table
    @Override
    public int getRowCount() {
        if (category == null && item == null) {

            return inventory.getIDs().size();
        } else if (item != null) {
            return 1;
        } else {
            int size = inventory.getIDs(category).size();
            return size;
        }
    }

    //EFFECTS: return the number of colummns of the table
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    //EFFECTS: return column class of a particular column
    @Override
    public Class getColumnClass(int column) {

        assert getValueAt(0, column).getClass() != null : "class";
        return getValueAt(0, column).getClass();
    }

    //MODIFIES: this
    //EFFECTS: set the category of this
    public void setCategory(String category) {
        item = null;
        this.category = category;
        fireTableDataChanged();
    }

    //MODIFIES: this
    //EFFECTS: set the item of this
    public void setItem(String item) {
        this.item = item;
        fireTableDataChanged();
    }
}
