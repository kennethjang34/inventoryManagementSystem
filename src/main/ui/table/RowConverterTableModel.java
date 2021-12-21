package ui.table;

import com.sun.tools.javac.util.Name;
import org.jetbrains.annotations.NotNull;
import ui.DataFactoryViewer;

import javax.swing.table.AbstractTableModel;
import java.beans.PropertyChangeEvent;
import java.util.*;

public class RowConverterTableModel extends AbstractTableModel implements DataFactoryViewer {
    protected LinkedHashMap<TableEntryConvertibleModel, Object[]> data;
    protected List<TableEntryConvertibleModel> tableEntries;
    protected String[] columnNames;
    protected int baseColumnIndex;
    protected boolean duplicateAllowed;
    protected boolean automation = true;

    //EFFECTS: create a new empty table model
    public RowConverterTableModel() {
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>();
        baseColumnIndex = -1;
        duplicateAllowed = false;
    }



    public RowConverterTableModel(List<? extends TableEntryConvertibleModel> entries) {
        data = new LinkedHashMap<>(entries.size());
        tableEntries = new LinkedList<>();
        for (TableEntryConvertibleModel entry: entries) {
            data.put(entry, createRow(entry.convertToTableEntry()));
            tableEntries.add(entry);
        }
        this.columnNames = entries.get(0).getDataList();
        duplicateAllowed = false;
    }

    public RowConverterTableModel(List<? extends TableEntryConvertibleModel> entries, String[] columnNames) {
        this.columnNames = columnNames;
        tableEntries = new LinkedList<>();
        data = new LinkedHashMap<>(entries.size());
        for (TableEntryConvertibleModel entry: entries) {
            data.put(entry, createRow(entry.convertToTableEntry()));
            tableEntries.add(entry);
        }
        duplicateAllowed = false;
    }

    public RowConverterTableModel(AbstractTableDataModel model, String[] columnNames, String category) {
        this.columnNames = columnNames;
        List<TableEntryConvertibleModel> entries = model.getEntryModels();
        for (TableEntryConvertibleModel entry: entries) {
            entry.addListener(this);
            tableEntries.add(entry);
            data.put(entry, createRow(entry.convertToTableEntry()));
        }
        model.addDataModelListener(category, this);
//        model.addEntryDataModelListener(category, this);
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

    @Override
    public Class getColumnClass(int index) {
        if (data.isEmpty()) {
            return Object.class;
        }
        List<Object[]> list = new ArrayList<>(data.values());
        return list.get(0)[index].getClass();
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
        for (TableEntryConvertibleModel entry: tableEntries) {
            rows.add(data.get(entry));
        }
        return rows;
    }

    public List<TableEntryConvertibleModel> getEntryModelList() {
        return tableEntries;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value =  data.get(tableEntries.get(rowIndex))[columnIndex];
        return value;
    }

    //MODIFIES: this
    //EFFECTS: add new rows to this. If the given entry objects are PropertyChangeSupport,
    // add this to the objects as listener
    public void addRowsWithDataList(List<? extends TableEntryConvertibleModel> newRowData) {
        if (newRowData.isEmpty()) {
            return;
        }
        int oldLastRow = getRowCount() - 1;
        for (TableEntryConvertibleModel entry: newRowData) {
            if (data.put(entry, createRow(entry.convertToTableEntry())) == null) {
                tableEntries.add(entry);
            }

//            int rowIndex = findRowIndex(newRow);
//            if (rowIndex == -1) {
//                data.putIfAbsent(entry, newRow);
////                if (entry instanceof TableEntryConvertibleModel) {
////                    ((TableEntryConvertibleModel) entry).changeFirer.addPropertyChangeListener(this);
////                }
//            } else if (!duplicateAllowed) {
//                data.set(rowIndex, newRow);
//            }
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

    public int findRowIndex(TableEntryConvertibleModel entryModel) {
        ArrayList<TableEntryConvertibleModel> list = new ArrayList<>(data.keySet());
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

    public void removeRow(TableEntryConvertibleModel entryConvertibleModel) {
        int correspondingTableIndex = tableEntries.indexOf(entryConvertibleModel);
        data.remove(tableEntries.remove(entryConvertibleModel));
        fireTableRowsDeleted(correspondingTableIndex, correspondingTableIndex);
    }




    protected Object[] createRow(Object[] entryData) {
        return entryData;
    }

    @Override
    public void propertyChange(@NotNull PropertyChangeEvent event) {
        if (event.getNewValue() != null) {
            TableEntryConvertibleModel entry = (TableEntryConvertibleModel)(event.getNewValue());
            if (columnNames == null) {
                columnNames = new String[entry.getDataList().length + 1];
                for (int i = 0; i < entry.getDataList().length; i++) {
                    columnNames[i] = entry.getDataList()[i];
                }
            }
            if (data.put(entry, createRow(entry.convertToTableEntry())) == null) {
                tableEntries.add(entry);
            }
            fireTableRowsUpdated(findRowIndex(entry), findRowIndex(entry));
        }
    }

    public List<TableEntryConvertibleModel> getEntryModels() {
        return new ArrayList<>(data.keySet());
    }

    public AbstractTableDataModel getDataModel() {
        return null;
    }

    @Override
    public void entryRemoved(Object removed) {
        int index = tableEntries.indexOf(removed);
        data.remove(tableEntries.remove(removed));
        fireTableRowsDeleted(index, index);
    }

    @Override
    public void entryAdded(Object added) {
        TableEntryConvertibleModel entry = (TableEntryConvertibleModel) added;
        Object[] row = createRow(entry.convertToTableEntry());
        if (data.put(entry, row) == null) {
            tableEntries.add(entry);
            fireTableRowsInserted(data.size() - 1, data.size() - 1);
        } else {
            fireTableRowsUpdated(findRowIndex(entry), findRowIndex(entry));
        }
    }
//
//    public void entryAdded(String category, TableEntryConvertibleModel added) {
//        TableEntryConvertibleModel entry = added;
//        data.put(entry, entry.convertToTableEntry());
//    }




//

}
