package ui.table;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//represents a table model with the last column dedicated to buttons
public class ButtonTableModel extends RowConverterViewerTableModel {

    Action buttonAction;


    //entries must implement ViewableTableEntryConvertibleModel abstract class
    public ButtonTableModel(List<? extends ViewableTableEntryConvertibleModel> entries, String[] columnNames,
                            String buttonColumnName) {
        super(entries, createColumnNames(columnNames, buttonColumnName));
    }

    //category: the specific category that the entries of the table  belong in the data factory they are from
    public ButtonTableModel(AbstractTableDataFactory factory, String buttonColumnName, String category) {
        super(factory, createColumnNames(factory.getColumnNames(), buttonColumnName), category);
    }


    //merge the entry column names from entry models with a new button column name
    private static String[] createColumnNames(String[] columnNames, String buttonColumnName) {
        String[] newColumnNames = new String[columnNames.length + 1];
        for (int i = 0; i < columnNames.length; i++) {
            newColumnNames[i] = columnNames[i];
        }
        newColumnNames[newColumnNames.length - 1] = buttonColumnName;
        return newColumnNames;
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
                    + newRow.length + " the existing row length: " + columnNames.length
            + "given row (withoutButton): " + Arrays.toString(withoutButton)
                    + "existing column names: " + Arrays.toString(columnNames));
        }
        for (int i = 0; i < withoutButton.length; i++) {
            newRow[i] = withoutButton[i];
        }
        JButton button = new JButton();
        button.setAction(buttonAction);
        newRow[newRow.length - 1] = button;
        return newRow;
    }





    //for each button of the last column, set the action to the given
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
