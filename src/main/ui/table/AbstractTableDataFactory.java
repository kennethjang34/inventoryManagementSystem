package ui.table;

public abstract class AbstractTableDataFactory extends AbstractViewableDataModel implements DataFactory {


    //using data list
    public String[] getColumnNames() {
        String[] columnNames = new String[getDataList().length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getDataList()[i].toString();
        }
        return columnNames;
    }


}
