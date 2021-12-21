package ui.table;

import ui.RowDataChangeSupport;
import ui.DataFactoryViewer;

import java.beans.PropertyChangeListener;
import java.util.List;

//represents an abstract model that can fire propertyChangeEvent
public abstract class AbstractDataModel {

    protected RowDataChangeSupport changeFirer;

    //EFFECTS: initialize the propertyChangeSupport
    public AbstractDataModel() {
        changeFirer = new RowDataChangeSupport(this);
    }

    public AbstractDataModel(Object sourceBean) {
        changeFirer = new RowDataChangeSupport(sourceBean);
    }

    //MODIFIES: this
    //EFFECTS: add a new property change listener to this that will be notified
    //when the specified property has changed
    public void addEntryDataModelListener(String propertyName, PropertyChangeListener listener) {
        changeFirer.addPropertyChangeListener(propertyName, listener);
    }

    public void addEntryDataModelListener(PropertyChangeListener listener) {
        changeFirer.addPropertyChangeListener(listener);
    }

    public void addDataModelListener(String category, DataFactoryViewer listener) {
        changeFirer.addTableDataModelListener(category, listener);
    }

    public void removeListener(String propertyName, PropertyChangeListener listener) {
        changeFirer.removePropertyChangeListener(propertyName, listener);
    }




    public abstract List<TableEntryConvertibleModel> getEntryModels();


//    public void entryRemoved(String category, TableEntryConvertibleModel o) {
//        changeFirer.fireRemovalEvent(category, o);
//    }
//
//    public void entryAdded(String category, TableEntryConvertibleModel o) {
//        changeFirer.fireAdditionEvent(category, o);
//    }



}
