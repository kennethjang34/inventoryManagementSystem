package ui.table;

import model.TableEntryConvertible;
import ui.RowDataChangeSupport;

public abstract class ViewableTableEntryConvertibleModel extends AbstractViewableDataModel
        implements TableEntryConvertible {
    protected String[] columnNames;

    public ViewableTableEntryConvertibleModel(String[] columnNames) {
        this.columnNames = columnNames;
        changeFirer = new RowDataChangeSupport(this);
    }

    public ViewableTableEntryConvertibleModel() {
        changeFirer = new RowDataChangeSupport(this);
    }

    public String[] getColumnNames() {
        return columnNames;
    }


}
