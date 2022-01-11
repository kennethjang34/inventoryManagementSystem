package ui.table;

import model.TableEntryConvertible;

public abstract class ViewableTableEntryConvertibleModel extends AbstractViewableDataModel
        implements TableEntryConvertible {
    protected String[] columnNames;

    public ViewableTableEntryConvertibleModel(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public String[] getColumnNames() {
        return columnNames;
    }



}
