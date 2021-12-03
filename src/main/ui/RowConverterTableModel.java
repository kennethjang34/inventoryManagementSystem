package ui;

import model.TableEntryConvertible;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RowConverterTableModel extends AbstractTableModel {
    protected List<Object[]> data;
    protected String[] columnNames;

    //EFFECTS: create a new empty table model
    public RowConverterTableModel() {
        data = new ArrayList<>();
    }

    public RowConverterTableModel(List<? extends TableEntryConvertible> entries) {
        data = new ArrayList<>(entries.size());
        for (TableEntryConvertible entry: entries) {
            data.add(entry.convertToTableEntry());
        }
        this.columnNames = entries.get(0).getDataList();
    }

    public RowConverterTableModel(List<Object[]> data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }


    @Override
    public Class getColumnClass(int index) {
        if (data.isEmpty()) {
            return Object.class;
        }
        return data.get(0)[index].getClass();
    }

//    public DataTableModel(List<Object[]> data) {
//        this.data = data;
//    }




    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        if (columnNames == null) {
            return 0;
        }
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex)[columnIndex];
    }

    //MODIFIES: this
    //EFFECTS: add new rows to this
    public void addRowsWithDataList(List<? extends TableEntryConvertible> newRowData) {
        if (newRowData.isEmpty()) {
            return;
        }
        if (columnNames == null) {
            columnNames = newRowData.get(0).getDataList();
        }
        int oldLastRow = getRowCount() - 1;
        for (TableEntryConvertible entry: newRowData) {
            data.add(entry.convertToTableEntry());
        }
        fireTableRowsInserted(oldLastRow, getRowCount() - 1);
    }
}
