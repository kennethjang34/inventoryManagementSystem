package ui.table;

import model.TableEntryConvertible;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class RowConverterTableModel {
//    protected LinkedHashMap<TableEntryConvertible, Object[]> data;
//    protected List<TableEntryConvertible> tableEntries;
//    protected String[] columnNames;
//    protected int baseColumnIndex;
//    protected boolean duplicateAllowed;
//    protected boolean automation = true;
//
//    //EFFECTS: create a new empty table model
//    public RowConverterTableModel() {
//        tableEntries = new LinkedList<>();
//        data = new LinkedHashMap<>();
//        baseColumnIndex = -1;
//        duplicateAllowed = false;
//    }
//
//
//
//    public RowConverterTableModel(List<? extends TableEntryConvertible> entries) {
//        data = new LinkedHashMap<>(entries.size());
//        tableEntries = new LinkedList<>();
//        for (TableEntryConvertible entry: entries) {
//            data.put(entry, createRow(entry.convertToTableEntry()));
//            tableEntries.add(entry);
//        }
//        this.columnNames = entries.get(0).getColumnNames();
//        duplicateAllowed = false;
//    }
//
//    public RowConverterTableModel(List<? extends TableEntryConvertible> entries, String[] columnNames) {
//        this.columnNames = columnNames;
//        tableEntries = new LinkedList<>();
//        data = new LinkedHashMap<>(entries.size());
//        for (TableEntryConvertible entry: entries) {
//            data.put(entry, createRow(entry.convertToTableEntry()));
//            tableEntries.add(entry);
//        }
//        duplicateAllowed = false;
//    }
//
//    public RowConverterTableModel(AbstractTableDataFactory model, String[] columnNames, String category) {
//        data = new LinkedHashMap<>();
//        tableEntries = new LinkedList<>();
//        this.columnNames = columnNames;
//        List<ViewableTableEntryConvertibleModel> entries = model.getEntryModels();
//        for (ViewableTableEntryConvertibleModel entry: entries) {
//            tableEntries.add(entry);
//            data.put(entry, createRow(entry.convertToTableEntry()));
//        }
//    }
//
//    public void setAutomation(boolean automation) {
//        this.automation = automation;
//    }
//
//    public void setDuplicateAllowed(boolean duplicate) {
//        duplicateAllowed = duplicate;
//    }
//
//    public boolean getDuplicateAllowed() {
//        return duplicateAllowed;
//    }
//
//    //
//    @Override
//    public Class getColumnClass(int index) {
//        if (data.isEmpty()) {
//            return Object.class;
//        }
//        List<Object[]> list = new ArrayList<>(data.values());
//        return list.get(0)[index].getClass();
//    }
//
//    @Override
//    public String getColumnName(int index) {
//        try {
//            return columnNames[index];
//        } catch (IndexOutOfBoundsException e) {
//            return super.getColumnName(index);
//        }
//    }
//
//
//
//
//    @Override
//    public int getRowCount() {
//        return data.size();
//    }
//
//    @Override
//    public int getColumnCount() {
//        if (columnNames == null) {
//            return 0;
//        }
//        return columnNames.length;
//    }
//
//    public List<Object[]> getRows() {
//        List<Object[]> rows = new ArrayList<>();
//        for (ViewableTableEntryConvertibleModel entry: tableEntries) {
//            rows.add(data.get(entry));
//        }
//        return rows;
//    }
//
//    public List<ViewableTableEntryConvertibleModel> getEntryModelList() {
//        return tableEntries;
//    }
//
//    public List<ViewableTableEntryConvertibleModel> getEntryModelList(int[] selected) {
//        List<ViewableTableEntryConvertibleModel> selectedRows = new LinkedList<>();
//        for (int i: selected) {
//            selectedRows.add(tableEntries.get(i));
//        }
//        return selectedRows;
//    }
//
//
//
//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        Object value =  data.get(tableEntries.get(rowIndex))[columnIndex];
//        return value;
//    }
//
//    //MODIFIES: this
//    //EFFECTS: add new rows to this. If the given entry objects are PropertyChangeSupport,
//    // add this to the objects as listener
//    public void addRowsWithDataList(List<? extends ViewableTableEntryConvertibleModel> newRowData) {
//        if (newRowData.isEmpty()) {
//            return;
//        }
//        int oldLastRow = getRowCount() - 1;
//        for (ViewableTableEntryConvertibleModel entry: newRowData) {
//            if (data.put(entry, createRow(entry.convertToTableEntry())) == null) {
//                tableEntries.add(entry);
//            }
//        }
//        fireTableRowsInserted(oldLastRow, getRowCount() - 1);
//    }
//
//    //MODIFIES: this
//    //EFFECTS: set the base column index to be used for comparing two rows
//    public void setBaseColumnIndex(int index) {
//        this.baseColumnIndex = index;
//    }
//
//    public int getBaseColumnIndex() {
//        return baseColumnIndex;
//    }
//
//    //return the index of the column name. if there is no such name, return -1
//    public int findColumn(String columnName) {
//        if (columnNames == null) {
//            return -1;
//        }
//        for (int i = 0; i < columnNames.length; i++) {
//            if (columnNames[i].equals(columnName)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    //find the row the object belongs to
//    //If there is no such row, return -1
//    public int findRow(Object object) {
//        List<Object[]> rows = getRows();
//        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
//            Object[] row = rows.get(rowIndex);
//            int i = 0;
//            for (; i < row.length; i++) {
//                if (row[i].equals(object)) {
//                    return rowIndex;
//                }
//            }
//        }
//        return -1;
//    }
//
//
//    public Object[] getRow(int index) {
//        return data.get(tableEntries.get(index));
//    }
//
//    public List<Object[]> getRowObjects(int[] indices) {
//        List<Object[]> rows = new ArrayList<>();
//        for (int i: indices) {
//            rows.add(getRow(i));
//        }
//        return rows;
//    }
//
//    public List<Object[]> getRowObjects(List<ViewableTableEntryConvertibleModel> entries) {
//        List<Object[]> rows = new LinkedList<>();
//        for (ViewableTableEntryConvertibleModel entry: entries) {
//            rows.add(data.get(entry));
//        }
//        return rows;
//    }
//
//    public Object[] getRowObject(ViewableTableEntryConvertibleModel entry) {
//        return data.get(entry);
//    }
//
//    //if there is a row that contains the same item in the specified column, return the index of the row
//    //Otherwise, return -1
//    public int findRowIndex(Object obj, int baseColumnIndex) {
//        for (int i = 0; i < tableEntries.size(); i++) {
//            if (obj.equals(getRow(i)[baseColumnIndex])) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    //if there is a row that contains the same item, return the index of the row
//    //Otherwise, return -1
//    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
//    public int findRowIndex(Object[] row) {
//        if (row == null || data.isEmpty() || row.length != data.get(0).length) {
//            return -1;
//        }
//
//        if (baseColumnIndex < 0) {
//            for (int i = 0; i < data.size(); i++) {
//                Object[] existing = data.get(i);
//                int j = 0;
//                for (; j < existing.length; j++) {
//                    if (!existing[j].equals(row[j])) {
//                        break;
//                    }
//                }
//                if (j == existing.length) {
//                    return i;
//                }
//            }
//        } else {
//            Object obj = row[baseColumnIndex];
//            for (int i = 0; i < data.size(); i++) {
//                if (obj.equals(data.get(i)[baseColumnIndex])) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
//
//    public int findRowIndex(ViewableTableEntryConvertibleModel entryModel) {
//        ArrayList<ViewableTableEntryConvertibleModel> list = new ArrayList<>(data.keySet());
//        return list.indexOf(entryModel);
//    }
//
//    public void setColumnNames(String[] columnNames) {
//        this.columnNames = columnNames;
//        fireTableDataChanged();
//    }
//
//    public void removeRow(int correspondingTableIndex) {
//        data.remove(tableEntries.remove(correspondingTableIndex));
//        fireTableRowsDeleted(correspondingTableIndex, correspondingTableIndex);
//    }
//
//    public void removeRow(ViewableTableEntryConvertibleModel entryConvertibleModel) {
//        int correspondingTableIndex = tableEntries.indexOf(entryConvertibleModel);
//        if (tableEntries.remove(entryConvertibleModel)) {
//            data.remove(entryConvertibleModel);
//        }
//        fireTableRowsDeleted(correspondingTableIndex, correspondingTableIndex);
//    }
//
//
//
//
//    protected Object[] createRow(Object[] entryData) {
//        return entryData;
//    }
//
//
//    public List<ViewableTableEntryConvertibleModel> getEntryModels() {
//        return new ArrayList<>(data.keySet());
//    }
//
//    public AbstractTableDataFactory getDataModel() {
//        return null;
//    }



}
