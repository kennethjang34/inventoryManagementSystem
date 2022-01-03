package ui.table;

import model.TableEntryConvertible;
import ui.RowDataChangeSupport;

public abstract class ViewableTableEntryConvertibleModel extends AbstractViewableDataModel
        implements TableEntryConvertible {
    protected String[] columnNames;

    public ViewableTableEntryConvertibleModel(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public ViewableTableEntryConvertibleModel() {
    }

    public String[] getColumnNames() {
        return columnNames;
    }



}
