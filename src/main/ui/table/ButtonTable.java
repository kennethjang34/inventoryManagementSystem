package ui.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;


public class ButtonTable extends JTable {

//    Inventory inventory;
    //Maybe you don't need it
//    private String category = null;
//    private String item = null;

//    //REQUIRES: columnNames length must be one bigger than that of data's each row
//    public ButtonTable(List<Object[]> data, String[] columnNames, ActionListener actionListener, String buttonLabel) {
//
//    }



    private static class ButtonColumnRenderer extends DefaultTableCellRenderer {

        private static ButtonColumnRenderer instance;

        private static ButtonColumnRenderer getInstance() {
            if (instance == null) {
                instance = new ButtonColumnRenderer();
            }
            return instance;
        }

        @Override
        //EFFECTS: return a component to be drawn at the specified cell
        //in a way that a button can be properly drawn
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (Component) value;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }


    public ButtonTable(AbstractTableDataModel model, String buttonColumnName, String category) {
        setModel(new ButtonTableModel(model, buttonColumnName, category));
        setDefaultRenderer(JButton.class, ButtonColumnRenderer.getInstance());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int column = columnAtPoint(e.getPoint());
                    if (getColumnClass(column).equals(JButton.class)) {
                        JButton button = (JButton) getValueAt(getSelectedRow(), column);
                        button.doClick();
                    }
                }
            }
        });
    }


    public ButtonTable(List<? extends TableEntryConvertibleModel> dataList, String[] columnNames, String buttonColumnName) {
        setModel(new ButtonTableModel(dataList, columnNames, buttonColumnName));
        setDefaultRenderer(JButton.class, ButtonColumnRenderer.getInstance());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    int column = columnAtPoint(e.getPoint());
                    if (getColumnClass(column).equals(JButton.class)) {
                        JButton button = (JButton) getValueAt(getSelectedRow(), column);
                        button.doClick();
                    }
                }
            }
        });
    }

    //EFFECTS: return a list of buttons(each index in the list will be the same as their index in the model
    public List<JButton> getButtons() {
        ButtonTableModel model = (ButtonTableModel)getModel();
        return model.getButtons();
    }


    //EFFECTS: return selected row data
    public Object[] getSelectedRowData() {
        ButtonTableModel model = (ButtonTableModel)getModel();
        int index = getSelectedRow();
        if (index < 0) {
            return null;
        }
        return model.getRow(index);
    }

    public void setButtonAction(AbstractAction action) {
        ButtonTableModel tableModel = (ButtonTableModel)getModel();
        tableModel.setButtonAction(action);
    }

    public int findRow(Object object) {
        ButtonTableModel tableModel = (ButtonTableModel)getModel();
        return tableModel.findRow(object);
    }

    public int findRow(Object object, int columnBasisIndex) {
        ButtonTableModel tableModel = (ButtonTableModel)getModel();
        return tableModel.findRowIndex(object, columnBasisIndex);
    }

    public int findColumn(String columnName) {
        ButtonTableModel tableModel = (ButtonTableModel)getModel();
        return tableModel.findColumn(columnName);
    }

    public void setBaseColumnIndex(int index) {
        ButtonTableModel tableModel = (ButtonTableModel)getModel();
        tableModel.setBaseColumnIndex(index);

    }

    public void addDataWithNewButton(List<? extends TableEntryConvertibleModel> entries) {
        ButtonTableModel tableModel = (ButtonTableModel)getModel();
        tableModel.addRowsWithDataList(entries);
    }




}
