package ui.table;

import ui.RowDataChangeSupport;
import ui.DataViewer;

import javax.xml.crypto.Data;
import java.beans.PropertyChangeListener;
import java.util.List;

//represents an abstract model that can fire propertyChangeEvent
public abstract class AbstractViewableDataModel {

    protected RowDataChangeSupport changeFirer;

    //EFFECTS: initialize the propertyChangeSupport
    public AbstractViewableDataModel() {
        changeFirer = new RowDataChangeSupport(this);
    }

    public AbstractViewableDataModel(Object sourceBean) {
        changeFirer = new RowDataChangeSupport(sourceBean);
    }

    //MODIFIES: this
    //EFFECTS: add a new property change listener to this that will be notified
    //when the specified property has changed
    public void addDataChangeListener(String propertyName, DataViewer listener) {
        changeFirer.addTableDataModelListener(propertyName, listener);
    }

    public void addDataChangeListener(DataViewer listener) {
        changeFirer.addTableDataModelListener(listener);
    }

    public void removeListener(String propertyName, DataViewer listener) {
        changeFirer.removeTableModelListener(propertyName, listener);
    }

    public void removeGeneralListener(DataViewer listener) {
        changeFirer.removeTableModelListener(RowDataChangeSupport.GENERAL, listener);
    }

    public void removeListener(DataViewer listener) {
        changeFirer.removeTableModelListener(listener);
    }

    public void addUpdateListener(DataViewer listener) {
        changeFirer.addUpdateDataModelListener(listener);
    }


//    public abstract List<TableEntryConvertibleModel> getEntryModels();


//    public void entryRemoved(String category, TableEntryConvertibleModel o) {
//        changeFirer.fireRemovalEvent(category, o);
//    }
//
//    public void entryAdded(String category, TableEntryConvertibleModel o) {
//        changeFirer.fireAdditionEvent(category, o);
//    }



}
