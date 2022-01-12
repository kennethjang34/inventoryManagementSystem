package ui.table;

//Data factory represents an object that gives out its contents that are tableEntryConvertible
public abstract class AbstractTableDataFactory extends AbstractViewableDataModel implements DataFactory {


    //using data list, create and return column names
    public String[] getColumnNames() {
        String[] columnNames = new String[getDataList().length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getDataList()[i].toString();
        }
        return columnNames;
    }


}
