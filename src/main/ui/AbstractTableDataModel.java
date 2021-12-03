package ui;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public abstract class AbstractTableDataModel extends AbstractModel {

//    public AbstractTableDataModel(Object source) {
//    }

    public abstract List<Object[]> getTableRows();

    public abstract Object[] getDataList();

    public String[] getTableColumnNames() {
        String[] columnNames = new String[getDataList().length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getDataList()[i].toString();
        }
        return columnNames;
//        if (getTableRows().isEmpty()) {
//            return null;
//        }
//        String[] columnNames = new String[getTableRows().get(0).length];
//        for (int i = 0; i < columnNames.length; i++) {
//            columnNames[i] = "Column" + 1;
//        }
//        return columnNames;
    }

}
