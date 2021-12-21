package ui.table;

import model.TableEntryConvertible;
import ui.RowDataChangeSupport;

public abstract class TableEntryConvertibleModel extends AbstractViewableDataModel
        implements TableEntryConvertible {
    protected String[] columnNames;

    public TableEntryConvertibleModel(String[] columnNames) {
        this.columnNames = columnNames;
        changeFirer = new RowDataChangeSupport(this);
    }

    public TableEntryConvertibleModel() {
        changeFirer = new RowDataChangeSupport(this);
    }

    public String[] getColumnNames() {
        return columnNames;
    }




}
