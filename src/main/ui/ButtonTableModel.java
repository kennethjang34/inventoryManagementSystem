package ui;

import model.TableEntryConvertible;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

//represents a table model with a column dedicated to buttons
public class ButtonTableModel extends RowConverterTableModel {

//    ActionListener buttonActionListener;
    String buttonText;
    Action actionInitiator;
    Action buttonAction;
    private int baseColumnIndex = -1;

    public ButtonTableModel(List<Object[]> data, String[] columnNames, String buttonText, String buttonColumnName) {
        this.data = data;
        if (columnNames == null) {
            this.columnNames = null;
        } else {
            this.columnNames = new String[columnNames.length + 1];
            for (int i = 0; i < columnNames.length; i++) {
                this.columnNames[i] = columnNames[i];
            }
            this.columnNames[this.columnNames.length - 1] = buttonColumnName;
            addButtonColumn(data, buttonText);
        }
        actionInitiator = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert buttonAction != null;
                buttonAction.actionPerformed(e);
            }
        };
    }

    public ButtonTableModel(List<? extends TableEntryConvertible> entries, String buttonColumnName) {
        data = new ArrayList<>(entries.size());
        for (TableEntryConvertible entry: entries) {
            data.add(entry.convertToTableEntry());
        }
        String[] columnNames = entries.get(0).getDataList();
        this.columnNames = new String[columnNames.length + 1];
        for (int i = 0; i < columnNames.length; i++) {
            this.columnNames[i] = columnNames[i];
        }
        this.columnNames[this.columnNames.length - 1] = buttonColumnName;
        actionInitiator = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert buttonAction != null;
                buttonAction.actionPerformed(e);
            }
        };
//        addButtonColumn(data, null);
    }

    public ButtonTableModel(List<? extends TableEntryConvertible> entries, String[] columnNames,
                            String buttonColumnName) {
        data = new ArrayList<>(entries.size());
        for (TableEntryConvertible entry: entries) {
            data.add(entry.convertToTableEntry());
        }
        if (columnNames == null) {
            this.columnNames = null;
        } else {
            this.columnNames = new String[columnNames.length + 1];
            for (int i = 0; i < columnNames.length; i++) {
                this.columnNames[i] = columnNames[i];
            }
            this.columnNames[this.columnNames.length - 1] = buttonColumnName;
            addButtonColumn(data, null);
        }
        actionInitiator = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert buttonAction != null;
                buttonAction.actionPerformed(e);
            }
        };
//        addButtonColumn(data, null);
    }

    public ButtonTableModel(AbstractTableDataModel model, String buttonColumnName) {
        data.addAll(model.getTableRows());
        this.columnNames = new String[model.getTableColumnNames().length + 1];
        for (int i = 0; i < model.getTableColumnNames().length; i++) {
            this.columnNames[i] = model.getTableColumnNames()[i];
        }
        this.columnNames[this.columnNames.length - 1] = buttonColumnName;
        actionInitiator = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assert buttonAction != null;
                buttonAction.actionPerformed(e);
            }
        };
        addButtonColumn(data, null);
//        addButtonColumn(data, null);
    }


    public void setBaseColumnIndex(int index) {
        baseColumnIndex = index;
    }

    //EFFECTS: add a new column of JButtons to the data of the table model
    //and add new buttons with the given action listener attached
    //set the text of the button to the given text
    private void addButtonColumn(List<Object[]> data, String buttonColumnName,
                                 ActionListener actionListener, String text) {
        int buttonColumnLength = data.size();
        for (int i = 0; i < data.size() + 1; i++) {
            JButton button = new JButton();
            button.addActionListener(actionListener);
            button.setText(text);
            Object[] newRow = new Object[columnNames.length];
            Object[] existing = data.get(i);
            for (int j = 0; j < existing.length; j++) {
                newRow[j] = existing[j];
            }
            newRow[columnNames.length - 1] = button;
            data.set(i, newRow);
        }
    }


    //EFFECTS: add a new column of JButtons to the data of the table model
    //and add new buttons with no action listener attached
    //set the text of the button to the given text
    private void addButtonColumn(List<Object[]> data, String text) {
        if (data.isEmpty()) {
            return;
        }
        int buttonColumnLength = data.size();
        for (int i = 0; i < data.size() + 1; i++) {
            JButton button = new JButton();
//            button.addActionListener(actionListener);
            button.setText(text);
            Object[] newRow = new Object[columnNames.length];
            Object[] existing = data.get(i);
            for (int j = 0; j < existing.length; j++) {
                newRow[j] = existing[j];
            }
            newRow[columnNames.length - 1] = button;
            data.set(i, newRow);
        }
    }


    //EFFECTS: return the button column index
    public int getButtonColumnIndex() {
        //button columns will be located at the end always
        return columnNames.length - 1;
    }

    public List<JButton> getButtons() {
        List<JButton> buttons = new ArrayList<>();
        for (Object[] row: data) {
            buttons.add((JButton)row[getButtonColumnIndex()]);
        }
        return buttons;
    }

    //event.getNewValue() returns TableConvertible object
    public void update(PropertyChangeEvent event) {
        TableEntryConvertible entry = (TableEntryConvertible)(event.getNewValue());
        if (columnNames == null) {
            columnNames = new String[entry.getDataList().length + 1];
            for (int i = 0; i < entry.getDataList().length; i++) {
                columnNames[i] = entry.getDataList()[i];
            }

        }
        Object[] entryData = entry.convertToTableEntry();
        Object[] newRow = createButtonRow(entryData);
        int rowIndex;
        if (baseColumnIndex == -1) {
            rowIndex = findRowIndex(newRow);
        } else {
            rowIndex = findRowIndex(newRow[baseColumnIndex], baseColumnIndex);
        }
        //There is already an existing row
        if (rowIndex != -1) {
            data.set(rowIndex, newRow);
            fireTableRowsUpdated(rowIndex, rowIndex);
        } else {
            //create a new row
            data.add(newRow);
            fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
        }
    }

    //MODIFIES: this
    //EFFECTS: add new rows to this
    public void addRows(List<Object[]> newRows) {
        int oldLastRow = getRowCount() - 1;
        for (Object[] row: newRows) {
            data.add(createButtonRow(row));
        }
        fireTableRowsInserted(oldLastRow, getRowCount() - 1);
    }

    //EFFECTS: create and return a new button row
    public Object[] createButtonRow(Object[] withoutButton) {
        Object[] newRow = new Object[withoutButton.length + 1];
        if (newRow.length != columnNames.length) {
            throw new IllegalArgumentException("The given row's columns don't match columns of the table");
        }
        for (int i = 0; i < withoutButton.length; i++) {
            newRow[i] = withoutButton[i];
        }
        JButton button = new JButton();
        button.setAction(buttonAction);
        newRow[newRow.length - 1] = button;
        return newRow;
    }




    @Override
    //MODIFIES: this
    //EFFECTS: add new rows to this
    public void addRowsWithDataList(List<? extends TableEntryConvertible> dataModels) {

        int oldLastRow = getRowCount() - 1;
        for (TableEntryConvertible entry: dataModels) {
            Object[] newRow = entry.convertToTableEntry();
            int rowIndex = findRowIndex(newRow);
            if (rowIndex == -1) {
                data.add(createButtonRow(newRow));
            } else {
                data.set(rowIndex, newRow);
            }
        }
        fireTableRowsInserted(oldLastRow, getRowCount() - 1);
    }




    //if there is a row that contains the same item, return the index of the row
    //Otherwise, return -1
    private int findRowIndex(Object[] row) {
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
        return -1;
    }

    //if there is a row that contains the same item in the specified column, return the index of the row
    //Otherwise, return -1
    public int findRowIndex(Object obj, int baseColumnIndex) {
        for (int i = 0; i < data.size(); i++) {
            if (obj.equals(data.get(i)[baseColumnIndex])) {
                return i;
            }
        }
        return -1;
    }


    public void setButtonAction(AbstractAction action) {
        buttonAction = action;
    }

    //find the row the object belongs to
    //If there is no such row, return -1
    public int findRow(Object object) {
        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            Object[] row = data.get(rowIndex);
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
        return data.get(index);
    }


    //return the index of the column name. if there is no such name, return -1
    public int findColumn(String columnName) {

        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
