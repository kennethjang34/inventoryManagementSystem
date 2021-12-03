package ui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrderedButtonTableModel extends OrderedTableModel {
    //Button identifier and button
//    private Map<String, JButton> buttonMap;


    public OrderedButtonTableModel(List<Object[]> data, String[] columnNames,
                                   String buttonColumnName, ActionListener actionListener) {
        super(data, columnNames);
        addButtonColumn(data, buttonColumnName, actionListener, "");
    }

    //EFFECTS: create a new table model with the given data and a JButton attached to it.
    //Add the action listener to the newly created JButton with the given string written on it
    public OrderedButtonTableModel(List<Object[]> data, String[] columnNames, String buttonColumnName,
                                   ActionListener actionListener, String buttonText) {
        super(data, columnNames);
        addButtonColumn(data, buttonColumnName, actionListener, buttonText);
    }

    //EFFECTS: add a new column of JButtons to the data of the table model
    //and add new buttons with the given action listener attached
    //set the text of the button to the given text
    //The text will be used for button column name as well
    private void addButtonColumn(List<Object[]> data, String buttonColumnName,
                                 ActionListener actionListener, String text) {
        int buttonColumnLength = data.size();
        JButton[] buttons = new JButton[buttonColumnLength];
        for (int i = 0; i < buttons.length; i++) {
            JButton button = new JButton();
            button.addActionListener(actionListener);
            button.setText(text);
        }
        addColumn(buttonColumnName, buttons);
    }
}
