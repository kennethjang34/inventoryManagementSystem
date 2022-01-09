package ui.table;

import com.sun.rowset.internal.Row;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Vector;

public class EntryRemovableTable extends ToolTipEnabledTable {
    private boolean editable = false;

    public EntryRemovableTable() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem removeFromTable = new JMenuItem("Remove from the table");
        removeFromTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedModelRows = new int[getSelectedRows().length];
                for (int i = 0; i< getSelectedRows().length; i++) {
                    selectedModelRows[i] = convertRowIndexToModel(getSelectedRows()[i]);
                }
                Arrays.sort(selectedModelRows);
                if (getModel() instanceof DefaultTableModel) {
                    DefaultTableModel tableModel = (DefaultTableModel) getModel();
                    for (int i = 0; i < selectedModelRows.length; i++) {
                        tableModel.removeRow(selectedModelRows[i] - i);
                    }
                } else if (getModel() instanceof RowConverterViewerTableModel){
                    RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) getModel();
                    for (int i = 0; i < selectedModelRows.length; i++) {
                        tableModel.removeRow(selectedModelRows[i] - i);
                    }
                }
            }
        });
        menu.add(removeFromTable);
        setComponentPopupMenu(menu);
    }


    public EntryRemovableTable(AbstractTableModel tableModel) {
        super(tableModel);
        JPopupMenu menu = new JPopupMenu();
        JMenuItem removeFromTable = new JMenuItem("Remove from the table");
        removeFromTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] selectedModelRows = new int[getSelectedRows().length];
                for (int i = 0; i< getSelectedRows().length; i++) {
                    selectedModelRows[i] = convertRowIndexToModel(getSelectedRows()[i]);
                }
                Arrays.sort(selectedModelRows);
                if (tableModel instanceof DefaultTableModel) {
                    DefaultTableModel tableModel = (DefaultTableModel) getModel();
                    for (int i = 0; i < selectedModelRows.length; i++) {
                        tableModel.removeRow(selectedModelRows[i] - i);
                    }
                } else if (tableModel instanceof RowConverterViewerTableModel){
                    RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) getModel();
                    for (int i = 0; i < selectedModelRows.length; i++) {
                        tableModel.removeRow(selectedModelRows[i] - i);
                    }
                }
            }
        });
        menu.add(removeFromTable);
        setComponentPopupMenu(menu);
    }


    public JTable getSelectedRowsTable() {
        int[] selected = getSelectedRows();
        if (selected.length != 0) {
            Object[][] tableData = new Object[selected.length][];
            Object[] columnNames = new Object[getColumnCount()];
            for (int i = 0; i < selected.length; i++) {
                for (int j = 0; j < getColumnCount(); j++) {
                    tableData[i][j] = getValueAt(selected[i], j);
                }
            }
            for (int i = 0; i < getColumnCount(); i++) {
                columnNames[i] = getColumnName(i);
            }
            DefaultTableModel tableModel = new DefaultTableModel(tableData, columnNames);
            JTable table = new JTable(tableModel);
            return table;
        }
        return null;
    }

    //default: false
    public void setTableEditable(boolean enabled) {
        editable = enabled;
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        return editable;
    }
}
