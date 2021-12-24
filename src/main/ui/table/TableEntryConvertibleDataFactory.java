package ui.table;

public abstract class TableEntryConvertibleDataFactory extends ViewableTableEntryConvertibleModel
        implements DataFactory {


    public TableEntryConvertibleDataFactory(String[] columnNames) {
        super(columnNames);
    }




    //for column names for the entries belonging to this, rather than the table entry of itself
    //for ex, for column names for product table, than an entry showing item info those products belonging
    public String[] getTableColumnNames() {
        String[] columnNames = new String[getDataList().length];
        for (int i = 0; i < columnNames.length; i++) {
            columnNames[i] = getDataList()[i].toString();
        }
        return columnNames;
    }




}
