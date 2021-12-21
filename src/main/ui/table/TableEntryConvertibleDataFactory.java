package ui.table;

import model.TableEntryConvertible;

public abstract class TableEntryConvertibleDataFactory extends TableEntryConvertibleModel implements DataFactory {





    public TableEntryConvertibleDataFactory(String[] columnNames) {
        super(columnNames);
    }



    public String[] getTableColumnNames() {
        String[] columnNames = new String[getDataList().length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getDataList()[i].toString();
        }
        return columnNames;
    }

}
