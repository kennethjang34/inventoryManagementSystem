package ui.table;

import ui.DataViewer;

import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.util.List;

//represents a table model that can convert ViewableTableEntryConvertibleModel into a row object and pair the entry model and the actual row
public class RowConverterViewerTableModel extends AbstractTableModel implements DataViewer {
    protected LinkedHashMap<ViewableTableEntryConvertibleModel, Object[]> data;
    protected List<ViewableTableEntryConvertibleModel> tableEntries;
    protected String[] columnNames;
    protected int baseColumnIndex;
    protected boolean duplicateAllowed;
    //category indicates the section of the data factory that this table will display the information of
    protected String category;
    protected DataFactory factory;


    //EFFECTS: create a new empty table model
    public RowConverterViewerTableModel() {
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>();
        baseColumnIndex = -1;
        duplicateAllowed = false;
    }



    public RowConverterViewerTableModel(AbstractTableDataFactory factory, String category) {
        data = new LinkedHashMap<>();
        tableEntries = new LinkedList<>();
        columnNames = factory.getColumnNames();
        this.category = category;
        this.factory = factory;
        List<? extends ViewableTableEntryConvertibleModel> entries = factory.getEntryModels();
        if (entries != null) {
            for (ViewableTableEntryConvertibleModel entry : entries) {
                entry.addUpdateListener(this);
                tableEntries.add(entry);
                data.put(entry, createRow(entry.convertToTableEntry()));
            }
        }
        factory.addDataChangeListener(category, this);
        fireTableDataChanged();
    }


    public RowConverterViewerTableModel(List<? extends ViewableTableEntryConvertibleModel> entries) {
        data = new LinkedHashMap<>(entries.size());
        tableEntries = new LinkedList<>();
        if (entries.isEmpty()) {
            return;
        }
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



    public RowConverterViewerTableModel(AbstractTableDataFactory factory, String[] columnNames, String category) {
        data = new LinkedHashMap<>();
        tableEntries = new LinkedList<>();
        this.columnNames = columnNames;
        List<? extends ViewableTableEntryConvertibleModel> entries = factory.getEntryModels();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            entry.addUpdateListener(this);
            tableEntries.add(entry);
            data.put(entry, createRow(entry.convertToTableEntry()));
        }
        this.category = category;
        this.factory = factory;
        factory.addDataChangeListener(category, this);
    }

    //The table factory won't get notified when there is an update in entries
    public RowConverterViewerTableModel(AbstractTableDataFactory factory, String[] columnNames, String category, boolean watching) {
        data = new LinkedHashMap<>();
        tableEntries = new LinkedList<>();
        this.columnNames = columnNames;
        this.factory = factory;
        List<? extends ViewableTableEntryConvertibleModel> entries = factory.getEntryModels();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            if (watching) {
                entry.addUpdateListener(this);;
            }
            tableEntries.add(entry);
            data.put(entry, createRow(entry.convertToTableEntry()));
        }
        this.category = category;
        factory.addDataChangeListener(category, this);
    }




    public void setDataFactory(AbstractTableDataFactory factory) {
        this.factory = factory;
        data = new LinkedHashMap<>();
        tableEntries = new LinkedList<>();
        List<? extends ViewableTableEntryConvertibleModel> entries = factory.getEntryModels();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            entry.addUpdateListener(this);
            tableEntries.add(entry);
            data.put(entry, createRow(entry.convertToTableEntry()));
        }
        factory.addDataChangeListener(category, this);
        fireTableDataChanged();
    }


    //The column names will be retained.
    public void reset() {
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>();
        baseColumnIndex = -1;
        duplicateAllowed = false;
        fireTableDataChanged();
    }




    public void setDuplicateAllowed(boolean duplicate) {
        duplicateAllowed = duplicate;
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

    public List<? extends ViewableTableEntryConvertibleModel> getEntryModelList() {
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


    public List<Object[]> getRowObjects(List<? extends ViewableTableEntryConvertibleModel> entries) {
        List<Object[]> rows = new LinkedList<>();
        for (ViewableTableEntryConvertibleModel entry: entries) {
            rows.add(data.get(entry));
        }
        return rows;
    }

    //find the model row of the given entry model
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



    //will be overridden in child class when needed
    protected Object[] createRow(Object[] entryData) {
        return entryData;
    }


    public List<ViewableTableEntryConvertibleModel> getEntryModels() {
        return new ArrayList<>(data.keySet());
    }

    public ViewableTableEntryConvertibleModel getRowEntryModel(int row) {
        return tableEntries.get(row);
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
    public void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel removed) {
        int index = tableEntries.indexOf(removed);
        if (tableEntries.remove(removed)) {
            data.remove(removed);
            fireTableRowsDeleted(index, index);
        }
    }

    @Override
    public void entryRemoved(DataFactory source, List<? extends ViewableTableEntryConvertibleModel> list) {
        boolean changed = false;
        for (ViewableTableEntryConvertibleModel removed: list) {
            if (tableEntries.remove(removed)) {
                data.remove(removed);
                changed = true;
            }
        }
        if (changed)
            fireTableDataChanged();
    }

    @Override
    public void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> list) {
        boolean changed = false;
        for (ViewableTableEntryConvertibleModel removed: list) {
            if (tableEntries.remove(removed)) {
                data.remove(removed);
                changed = true;
            }
        }
        if (changed) {
            fireTableDataChanged();
        }
    }

    @Override
    public void entryAdded(ViewableTableEntryConvertibleModel added) {
        ViewableTableEntryConvertibleModel entry = added;
        Object[] row = createRow(entry.convertToTableEntry());
        if (data.put(entry, row) == null) {
            entry.addUpdateListener(this);
            tableEntries.add(entry);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        } else {
            fireTableRowsUpdated(findRowIndex(entry), findRowIndex(entry));
        }
    }

    @Override
    public void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added) {
        ViewableTableEntryConvertibleModel entry = added;
        Object[] row = createRow(entry.convertToTableEntry());
        if (data.put(entry, row) == null) {
            entry.addUpdateListener(this);
            tableEntries.add(entry);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        } else {
            fireTableRowsUpdated(findRowIndex(entry), findRowIndex(entry));
        }
    }

    @Override
    public void entryAdded(List<? extends ViewableTableEntryConvertibleModel> added) {

    }


    @Override
    public void updated(ViewableTableEntryConvertibleModel updatedEntry) {
        if (data.get(updatedEntry) == null) {
            return;
        }
        if (columnNames == null) {
            columnNames = updatedEntry.getColumnNames();
        }
        data.put(updatedEntry, createRow(updatedEntry.convertToTableEntry()));
        fireTableRowsUpdated(findRowIndex(updatedEntry), findRowIndex(updatedEntry));
    }

    @Override
    public void updated(ViewableTableEntryConvertibleModel source, String property, Object old, Object newProperty) {
        Object[] row = data.get(source);
        if (row == null) {
            return;
        }
        int column = findColumn(property);
        if (column >= 0) {
            row[column] = newProperty;
            fireTableCellUpdated(tableEntries.indexOf(source), column);
        } else {
            for (int i = 0; i < row.length; i++) {
                if (row[i].equals(old)) {
                    row[i] = newProperty;
                    fireTableCellUpdated(tableEntries.indexOf(source), i);
                    return;
                }
            }
        }
    }

    @Override
    public void updated(ViewableTableEntryConvertibleModel source, Object old, Object newObject) {
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

}
