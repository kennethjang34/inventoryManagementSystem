package ui.table;

import ui.RowDataChangeSupport;
import ui.DataViewer;

import java.beans.PropertyChangeListener;

//represents an abstract model that can fire propertyChangeEvent
public abstract class AbstractViewableDataModel {

    protected RowDataChangeSupport changeFirer;

    //EFFECTS: initialize the propertyChangeSupport
    public AbstractViewableDataModel() {
        changeFirer = new RowDataChangeSupport(this);
    }


    //MODIFIES: this
    //EFFECTS: add a new data change listener to this that will be notified
    //when the specified property has changed
    public void addDataChangeListener(String propertyName, DataViewer listener) {
        changeFirer.addTableDataModelListener(propertyName, listener);
    }

    //MODIFIES: this
    //EFFECTS: add a new data change listener for every property
    public void addDataChangeListener(DataViewer listener) {
        changeFirer.addTableDataModelListener(listener);
    }

    //MODIFIES: this
    //EFFECTS: remove this as data change listener from the specified property data change listener list
    public void removeListener(String propertyName, DataViewer listener) {
        changeFirer.removeTableModelListener(propertyName, listener);
    }


    //MODIFIES: this
    //EFFECTS: remove this as data change listener from all properties
    public void removeListener(DataViewer listener) {
        changeFirer.removeTableModelListener(listener);
    }

    //MODIFIES: this
    //EFFECTS: add this as a listener that will be notified only when updatedEvent was fired
    public void addUpdateListener(DataViewer listener) {
        changeFirer.addUpdateDataModelListener(listener);
    }


    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        changeFirer.addPropertyChangeListener(property, listener);
    }





}
