package ui.inventorypanel.stockpanel;

import model.ApplicationConstantValue;
import model.Inventory;
import model.Observer;
import model.Subject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.util.HashMap;

//represents a table model that displays stock conditions
public class StockButtonTableModel extends AbstractTableModel implements Observer {
    private Inventory inventory;
    private String[] columnNames;
    private ActionListener actionListener;
    private HashMap<String, JButton> buttonHashMap;
    private StockSearchPanel searchTool;
    private String category = null;
    private String item = null;

    //EFFECTS: create a new table model
    public StockButtonTableModel(Inventory inventory, StockSearchPanel searchTool, ActionListener actionListener) {
        this.searchTool = searchTool;
        searchTool.registerObserver(this);
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

    //EFFECTS: return the number of columns of the table
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


    //Updated only by the application
    //MODIFIES: this
    //EFFECTS: update this. selected category/item
    @Override
    public void update(int arg) {
        if (arg == ApplicationConstantValue.CATEGORY || arg == ApplicationConstantValue.ITEM) {
            String selectedCategory = searchTool.getSelectedCategory();
            if (selectedCategory.equals(StockSearchPanel.ALL)) {
                category = null;
            } else if (!selectedCategory.equals(StockSearchPanel.TYPE)) {
                category = searchTool.getSelectedCategory();
            }
            fireTableDataChanged();
            String selectedID = searchTool.getSelectedID();
            if (selectedID.equals(StockSearchPanel.ALL)) {
                item = null;
            } else if (!selectedID.equals(StockSearchPanel.TYPE)) {
                item = searchTool.getSelectedID();
            }
            fireTableDataChanged();
        }
    }
}
