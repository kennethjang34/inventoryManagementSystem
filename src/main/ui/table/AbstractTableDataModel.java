package ui.table;

import java.util.List;

public abstract class AbstractTableDataModel extends AbstractDataModel {


    public abstract List<String> getContentsOf(String property);

    public abstract Object[] getDataList();

    public String[] getTableColumnNames() {
        String[] columnNames = new String[getDataList().length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getDataList()[i].toString();
        }
        return columnNames;
    }


}
