package ui.table;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//represents a table model with a column dedicated to buttons
public class ButtonTableModel extends RowConverterViewerTableModel {

//    ActionListener buttonActionListener;
//    String buttonText;
//    Action actionInitiator;
    Action buttonAction;


    //entry must be object[] or TableEntryConvertible instance
    public ButtonTableModel(List<? extends ViewableTableEntryConvertibleModel> entries, String[] columnNames,
                            String buttonColumnName) {
        super(entries, createColumnNames(columnNames, buttonColumnName));
//        if (columnNames == null) {
//            this.columnNames = null;
//        } else {
//            this.columnNames = new String[columnNames.length + 1];
//            for (int i = 0; i < columnNames.length; i++) {
//                this.columnNames[i] = columnNames[i];
//            }
//            this.columnNames[this.columnNames.length - 1] = buttonColumnName;
//        }
//        for (TableEntryConvertibleModel entry: entries) {
//            this.data.put(entry, entry.convertToTableEntry());
//        }

    }

    public ButtonTableModel(AbstractTableDataFactory model, String buttonColumnName, String category) {
        super(model, createColumnNames(model.getTableColumnNames(), buttonColumnName), category);
    }

//    public ButtonTableModel(List<? extends TableEntryConvertibleModel> entries, String buttonColumnName) {
////        data = new ArrayList<>(entries.size());
//        for (TableEntryConvertibleModel entry: entries) {
//            data.put(entry, entry.convertToTableEntry());
//        }
//        String[] columnNames = entries.get(0).getColumnNames();
//        this.columnNames = new String[columnNames.length + 1];
//        for (int i = 0; i < columnNames.length; i++) {
//            this.columnNames[i] = columnNames[i];
//        }
//        this.columnNames[this.columnNames.length - 1] = buttonColumnName;
//        addButtonColumn();
//    }





    private static String[] createColumnNames(String[] columnNames, String buttonColumnName) {
        String[] newColumnNames = new String[columnNames.length + 1];
        for (int i = 0; i < columnNames.length; i++) {
            newColumnNames[i] = columnNames[i];
        }
        newColumnNames[newColumnNames.length - 1] = buttonColumnName;
        return newColumnNames;
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
    private void addButtonColumn() {
        if (data.isEmpty()) {
            return;
        }
        int buttonColumnLength = data.size();
        for (Map.Entry<ViewableTableEntryConvertibleModel, Object[]> entry: data.entrySet()) {
            JButton button = new JButton();
//            button.addActionListener(actionListener);
            button.setAction(buttonAction);
            Object[] newRow = new Object[columnNames.length];
            Object[] existing = entry.getValue();
            for (int j = 0; j < existing.length; j++) {
                newRow[j] = existing[j];
            }
            newRow[columnNames.length - 1] = button;
            entry.setValue(newRow);
//            data.put(i, newRow);
        }
    }

    public int getButtonColumnIndex() {
        //button columns will be located at the end always
        return columnNames.length - 1;
    }

    public List<JButton> getButtons() {
        List<JButton> buttons = new ArrayList<>();
        for (Object entry: data.values().toArray()) {
            Object[] row = (Object[]) entry;
            buttons.add((JButton)row[getButtonColumnIndex()]);
        }
        return buttons;
    }

    @Override
    //EFFECTS: create and return a new button row
    public Object[] createRow(Object[] withoutButton) {
        Object[] newRow = new Object[withoutButton.length + 1];
        if (newRow.length != columnNames.length) {
            throw new IllegalArgumentException("The given row's columns don't match columns of the table.\n given: "
                    + newRow.length + " the existing row length: " + columnNames.length);
        }
        for (int i = 0; i < withoutButton.length; i++) {
            newRow[i] = withoutButton[i];
        }
        JButton button = new JButton();
        button.setAction(buttonAction);
        newRow[newRow.length - 1] = button;
        return newRow;
    }






    public void setButtonAction(Action action) {
        buttonAction = action;
        for (JButton button: getButtons()) {
            button.setAction(action);
        }
    }



    public Action getButtonAction() {
        return buttonAction;
    }




}
