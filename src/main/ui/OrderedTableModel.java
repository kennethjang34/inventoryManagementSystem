package ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderedTableModel extends AbstractTableModel {
    private List<Object[]> data;
    private String[] columnNames;
    private Comparator comparator;
    private int baseColumn;
    private boolean automaticOrdering = false;

    //EFFECTS: create a new empty table with the default comparator.
    //Default comparator compares String values by calling toString() in a way that
    //A < Z , ignoring cases
    //Automatic ordering is off by default
    public OrderedTableModel() {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        this.data = new ArrayList<>();
        comparator = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());
        baseColumn = 0;
        automaticOrdering = false;
        columnNames = null;
    }

    //EFFECTS: create a new table with the given data with the default comparator.
    //Default comparator compares String values by calling toString() in a way that
    //A < Z , ignoring cases
    //Automatic ordering is off by default
    public OrderedTableModel(List<Object[]> data, String[] columnNames) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        this.data = data;
        comparator = (o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString());
        baseColumn = 0;
        automaticOrdering = false;
        columnNames = columnNames;

    }

    //EFFECTS: create a new table with the given data with the given comparator as default comparator
    //Automatic ordering is off by default
    public OrderedTableModel(List<Object[]> data, String[] columnNames, Comparator comparator) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        this.data = data;
        this.comparator = comparator;
        baseColumn = 0;
        automaticOrdering = false;
    }

    //EFFECTS: create a new table with the given data with the given comparator as a default comparator
    //if automatic is true, automatic ordering will be turned on
    public OrderedTableModel(List<Object[]> data, String[] columnNames, Comparator comparator, boolean automatic) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        this.data = data;
        this.comparator = comparator;
        baseColumn = 0;
        automaticOrdering = automatic;
    }


    //MODIFIES: this
    //EFFECTS: set the data
    public void setData(List<Object[]> data) {
        this.data = data;
    }

    //MODIFIES: this
    //EFFECTS: set the base column
    public void setBaseColumn(int baseColumn) {
        if (baseColumn < 0 || baseColumn >= getColumnCount()) {
            throw new IllegalArgumentException("Base column cannot be negative or greater than the last column number");
        }
        this.baseColumn = baseColumn;
    }

    //MODIFIES: this
    //EFFECTS: set the default comparator
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    //MODIFIES: this
    //EFFECTS: if the given value is true, turn on automatic ordering of the table.
    //Otherwise, turn it off
    //Automatic ordering will automatically order the table whenever table's row is added or removed
    //When turned off, the new row will be appended at the bottom and removed row will be replaced by the next row
    public void setAutomaticOrdering(boolean option) {
        automaticOrdering = option;
    }


    //MODIFIES: this
    //EFFECTS: order the table using the given comparator based on the base column cell items
    //if the base column hasn't been set, uses the first column
    //if table row size is 0 or 1, do nothing
    public void order(Comparator comparator) {
        this.comparator = comparator;
        fireTableDataChanged();
    }


    //MODIFIES: this
    //EFFECTS: order the table using the given comparator based on cells belonging to the passed column
    //if table row size is 0 or 1, do nothing
    public void order(Comparator comparator, int baseColumn) {
        this.comparator = comparator;
        fireTableDataChanged();
    }

    //MODIFIES: this
    //EFFECTS: order the table using the default comparator based on the first column cell items
    //if the default comparator hasn't been set,
    //this automatically uses alphabetical (A to Z) order (not case-sensitive)
    //if table row size is 0 or 1, do nothing
    public void order() {
        fireTableDataChanged();
    }


    //EFFECTS: return the number of rows (data size)
    @Override
    public int getRowCount() {
        return data.size();
    }

    //EFFECTS: return the number of columns, which is determined by the row length given
    //If the table is empty, return 0;
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    //EFFECTS: return the value at the specified cell
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = data.get(rowIndex);
        return row[columnIndex];
    }


    //MODIFIES: this
    //EFFECTS: add a new column to this
    public void addColumn(String columnName, Object[] newColumnData) {
        for (int row = 0; row < data.size(); row++) {
            Object[] existingRow = data.get(row);
            Object[] newRow = new Object[existingRow.length + 1];
            for (int column = 0; column < existingRow.length; column++) {
                newRow[column] = existingRow[column];
            }
            newRow[existingRow.length] = newColumnData[row];
        }
        addColumnName(columnName);
        fireTableDataChanged();
    }

    //MODIFIES: this
    //EFFECTS: add a new column name
    private void addColumnName(String columnName) {
        String[] newColumnNames = new String[columnNames.length + 1];
        for (int i = 0; i < columnNames.length; i++) {
            newColumnNames[i] = columnNames[i];
        }
        newColumnNames[newColumnNames.length - 1] = columnName;
        columnNames = newColumnNames;
    }

    @Override
    //EFFECTS: return the column name
    public String getColumnName(int column) {
        return columnNames[column];
    }


}
