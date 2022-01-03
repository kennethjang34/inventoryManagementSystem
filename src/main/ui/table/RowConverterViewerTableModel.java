package ui.table;

import model.Item;
import model.Product;
import ui.DataViewer;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.*;

public class RowConverterViewerTableModel extends AbstractTableModel implements DataViewer {
    protected LinkedHashMap<ViewableTableEntryConvertibleModel, Object[]> data;
    protected List<ViewableTableEntryConvertibleModel> tableEntries;
    protected String[] columnNames;
    protected int baseColumnIndex;
    protected boolean duplicateAllowed;
    protected boolean automation = true;

    //EFFECTS: create a new empty table model
    public RowConverterViewerTableModel() {
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>();
        baseColumnIndex = -1;
        duplicateAllowed = false;
    }



    public RowConverterViewerTableModel(List<? extends ViewableTableEntryConvertibleModel> entries) {
        data = new LinkedHashMap<>(entries.size());
        tableEntries = new LinkedList<>();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            entry.addDataChangeListener(this);
            data.put(entry, createRow(entry.convertToTableEntry()));
            tableEntries.add(entry);
        }
        this.columnNames = entries.get(0).getColumnNames();
        duplicateAllowed = false;
    }

    public RowConverterViewerTableModel(List<? extends ViewableTableEntryConvertibleModel> entries, String[] columnNames) {
        this.columnNames = columnNames;
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>(entries.size());
        for (ViewableTableEntryConvertibleModel entry: entries) {
            entry.addDataChangeListener(this);
            data.put(entry, createRow(entry.convertToTableEntry()));
            tableEntries.add(entry);
        }
        duplicateAllowed = false;
    }



    public RowConverterViewerTableModel(AbstractTableDataFactory model, String[] columnNames, String category) {
        data = new LinkedHashMap<>();
        tableEntries = new LinkedList<>();
        this.columnNames = columnNames;
        List<? extends ViewableTableEntryConvertibleModel> entries = (List<? extends ViewableTableEntryConvertibleModel>) model.getEntryModels();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            entry.addUpdateListener(this);
            tableEntries.add(entry);
            data.put(entry, createRow(entry.convertToTableEntry()));
        }
        model.addDataChangeListener(category, this);
    }

    //The table model won't get notified when there is an update in entries
    public RowConverterViewerTableModel(AbstractTableDataFactory model, String[] columnNames, String category, boolean watching) {
        data = new LinkedHashMap<>();
        tableEntries = new LinkedList<>();
        this.columnNames = columnNames;
        List<? extends ViewableTableEntryConvertibleModel> entries = model.getEntryModels();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            if (watching) {
                entry.addUpdateListener(this);;
            }
            tableEntries.add(entry);
            data.put(entry, createRow(entry.convertToTableEntry()));
        }
        model.addDataChangeListener(category, this);
    }




    public RowConverterViewerTableModel(List<? extends ViewableTableEntryConvertibleModel> entries, boolean watching) {
        data = new LinkedHashMap<>(entries.size());
        tableEntries = new LinkedList<>();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            if (watching) {
                entry.addUpdateListener(this);;
            }
            data.put(entry, createRow(entry.convertToTableEntry()));
            tableEntries.add(entry);
        }
        this.columnNames = entries.get(0).getColumnNames();
        duplicateAllowed = false;
    }

    public RowConverterViewerTableModel(List<? extends ViewableTableEntryConvertibleModel> entries, String[] columnNames, boolean watching) {
        this.columnNames = columnNames;
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>(entries.size());
        for (ViewableTableEntryConvertibleModel entry: entries) {
            if (watching) {
                entry.addUpdateListener(this);;
            }
            data.put(entry, createRow(entry.convertToTableEntry()));
            tableEntries.add(entry);
        }
        duplicateAllowed = false;
    }




    public void setAutomation(boolean automation) {
        this.automation = automation;
    }

    public void setDuplicateAllowed(boolean duplicate) {
        duplicateAllowed = duplicate;
    }

    public boolean getDuplicateAllowed() {
        return duplicateAllowed;
    }

    //
    @Override
    public Class getColumnClass(int index) {
        try {
            List<Object[]> list = new ArrayList<>(data.values());
            return list.get(0)[index].getClass();
        }
        catch (IndexOutOfBoundsException e) {
            return Object.class;
        } catch (NullPointerException e) {
            return Object.class;
        }
    }

    @Override
    public String getColumnName(int index) {
        try {
            return columnNames[index];
        } catch (IndexOutOfBoundsException e) {
            return super.getColumnName(index);
        }
    }




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

    public List<Object[]> getRows() {
        List<Object[]> rows = new ArrayList<>();
        for (ViewableTableEntryConvertibleModel entry: tableEntries) {
            rows.add(data.get(entry));
        }
        return rows;
    }

    public List<ViewableTableEntryConvertibleModel> getEntryModelList() {
        return tableEntries;
    }

    public List<ViewableTableEntryConvertibleModel> getEntryModelList(int[] selected) {
        List<ViewableTableEntryConvertibleModel> selectedRows = new LinkedList<>();
        for (int i: selected) {
            selectedRows.add(tableEntries.get(i));
        }
        return selectedRows;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = data.get(tableEntries.get(rowIndex))[columnIndex];
        return value;
    }

    //MODIFIES: this
    //EFFECTS: add new rows to this. If the given entry objects are PropertyChangeSupport,
    // add this to the objects as listener
    public void addRowsWithDataList(List<? extends ViewableTableEntryConvertibleModel> newRowData) {
        if (newRowData.isEmpty()) {
            return;
        }
        int oldLastRow = getRowCount() - 1;
        if (columnNames == null) {
            columnNames = newRowData.get(0).getColumnNames();
        }
        for (ViewableTableEntryConvertibleModel entry: newRowData) {
            if (data.put(entry, createRow(entry.convertToTableEntry())) == null) {
                tableEntries.add(entry);
                entry.addUpdateListener(this);
            }
        }
        fireTableRowsInserted(oldLastRow, getRowCount() - 1);
    }

    //MODIFIES: this
    //EFFECTS: add new rows to this. If the given entry objects are PropertyChangeSupport,
    // add this to the objects as listener
    public void addRowsWithDataList(List<? extends ViewableTableEntryConvertibleModel> newRowData, boolean watching) {
        if (newRowData.isEmpty()) {
            return;
        }
        int oldLastRow = getRowCount() - 1;
        for (ViewableTableEntryConvertibleModel entry: newRowData) {
            if (data.put(entry, createRow(entry.convertToTableEntry())) == null) {
                tableEntries.add(entry);
                if (watching) {
                    entry.addUpdateListener(this);
                }
            }
        }
        fireTableRowsInserted(oldLastRow, getRowCount() - 1);
    }



    //MODIFIES: this
    //EFFECTS: set the base column index to be used for comparing two rows
    public void setBaseColumnIndex(int index) {
        this.baseColumnIndex = index;
    }

    public int getBaseColumnIndex() {
        return baseColumnIndex;
    }

    //return the index of the column name. if there is no such name, return -1
    public int findColumn(String columnName) {
        if (columnNames == null) {
            return -1;
        }
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    //find the row the object belongs to
    //If there is no such row, return -1
    public int findRow(Object object) {
        List<Object[]> rows = getRows();
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Object[] row = rows.get(rowIndex);
            int i = 0;
            for (; i < row.length; i++) {
                if (row[i].equals(object)) {
                    return rowIndex;
                }
            }
        }
        return -1;
    }


    public Object[] getRow(int index) {
        return data.get(tableEntries.get(index));
    }

    public List<Object[]> getRowObjects(int[] indices) {
        List<Object[]> rows = new ArrayList<>();
        for (int i: indices) {
            rows.add(getRow(i));
        }
        return rows;
    }

    public List<Object[]> getRowObjects(List<? extends ViewableTableEntryConvertibleModel> entries) {
        List<Object[]> rows = new LinkedList<>();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            rows.add(data.get(entry));
        }
        return rows;
    }

    public Object[] getRowObject(ViewableTableEntryConvertibleModel entry) {
        return data.get(entry);
    }

    //if there is a row that contains the same item in the specified column, return the index of the row
    //Otherwise, return -1
    public int findRowIndex(Object obj, int baseColumnIndex) {
        for (int i = 0; i < tableEntries.size(); i++) {
            if (obj.equals(getRow(i)[baseColumnIndex])) {
                return i;
            }
        }
        return -1;
    }

    //if there is a row that contains the same item, return the index of the row
    //Otherwise, return -1
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    public int findRowIndex(Object[] row) {
        if (row == null || data.isEmpty() || row.length != data.get(0).length) {
            return -1;
        }

        if (baseColumnIndex < 0) {
            for (int i = 0; i < data.size(); i++) {
                Object[] existing = data.get(i);
                int j = 0;
                for (; j < existing.length; j++) {
                    if (!existing[j].equals(row[j])) {
                        break;
                    }
                }
                if (j == existing.length) {
                    return i;
                }
            }
        } else {
            Object obj = row[baseColumnIndex];
            for (int i = 0; i < data.size(); i++) {
                if (obj.equals(data.get(i)[baseColumnIndex])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int findRowIndex(ViewableTableEntryConvertibleModel entryModel) {
        ArrayList<ViewableTableEntryConvertibleModel> list = new ArrayList<>(data.keySet());
        return list.indexOf(entryModel);
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
        fireTableDataChanged();
    }

    public void removeRow(int correspondingTableIndex) {
        data.remove(tableEntries.remove(correspondingTableIndex));
        fireTableRowsDeleted(correspondingTableIndex, correspondingTableIndex);
    }

    public void removeRow(ViewableTableEntryConvertibleModel entryConvertibleModel) {
        int correspondingTableIndex = tableEntries.indexOf(entryConvertibleModel);
        if (tableEntries.remove(entryConvertibleModel)) {
            data.remove(entryConvertibleModel);
        }
        fireTableRowsDeleted(correspondingTableIndex, correspondingTableIndex);
    }




    protected Object[] createRow(Object[] entryData) {
        return entryData;
    }


    public List<ViewableTableEntryConvertibleModel> getEntryModels() {
        return new ArrayList<>(data.keySet());
    }



    @Override
    public void entryRemoved(ViewableTableEntryConvertibleModel removed) {
        int index = tableEntries.indexOf(removed);
        if (tableEntries.remove(removed)) {
            data.remove(removed);
            fireTableRowsDeleted(index, index);
        }
    }

    @Override
    public void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed) {
    }

    @Override
    public void entryAdded(ViewableTableEntryConvertibleModel added) {
        ViewableTableEntryConvertibleModel entry = added;
        Object[] row = createRow(entry.convertToTableEntry());
        if (data.put(entry, row) == null) {
            entry.addUpdateListener(this);;
            tableEntries.add(entry);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        } else {
            fireTableRowsUpdated(findRowIndex(entry), findRowIndex(entry));
        }
    }

    @Override
    public void entryAdded(List<? extends ViewableTableEntryConvertibleModel> list) {

    }


    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel updatedEntry) {
        if (columnNames == null) {
            columnNames = updatedEntry.getColumnNames();
        }

        if (data.put(updatedEntry, createRow(updatedEntry.convertToTableEntry())) == null) {
            tableEntries.add(updatedEntry);
            fireTableRowsInserted(tableEntries.size() - 1, tableEntries.size() - 1);
        } else {
            fireTableRowsUpdated(findRowIndex(updatedEntry), findRowIndex(updatedEntry));
        }
    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2) {
        Object[] row = data.get(source);
        if (row == null) {
            return;
        }
        int column = findColumn(property);
        if (column >= 0) {
            row[column] = o2;
            fireTableCellUpdated(tableEntries.indexOf(source), column);
        } else {
            for (int i = 0; i < row.length; i++) {
                if (row[i].equals(o1)) {
                    row[i] = o2;
                    fireTableCellUpdated(tableEntries.indexOf(source), i);
                    return;
                }
            }
        }
    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, Object old, Object newObject) {
        Object[] row = data.get(source);
        for (int i = 0; i < row.length; i++) {
            if (row[i].equals(old)) {
                row[i] = newObject;
                fireTableCellUpdated(tableEntries.indexOf(source), i);
                return;
            }
        }
    }


//
//    public void entryAdded(String category, TableEntryConvertibleModel added) {
//        TableEntryConvertibleModel entry = added;
//        data.put(entry, entry.convertToTableEntry());
//    }




//

}
