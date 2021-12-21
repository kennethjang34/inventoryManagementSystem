package ui.table;

import model.TableEntryConvertible;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class TableEntryConvertibleModel implements TableEntryConvertible {
    protected String[] columnNames;
    protected PropertyChangeSupport changeFirer;

    public TableEntryConvertibleModel(String[] columnNames) {
        this.columnNames = columnNames;
        changeFirer = new PropertyChangeSupport(this);
    }

    public TableEntryConvertibleModel() {
        changeFirer = new PropertyChangeSupport(this);
    }

    public String[] getDataList() {
        return columnNames;
    }

    //MODIFIES: this
    //EFFECTS: add a new property change listener to this that will be notified
    //when the specified property has changed
    public void addListener(String propertyName, PropertyChangeListener listener) {
        changeFirer.addPropertyChangeListener(propertyName, listener);
    }

    public void addListener(PropertyChangeListener listener) {
        changeFirer.addPropertyChangeListener(listener);
    }

    public void removeListener(String propertyName, PropertyChangeListener listener) {
        changeFirer.removePropertyChangeListener(propertyName, listener);
    }

}
