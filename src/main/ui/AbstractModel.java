package ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

//represents an abstract model that can fire propertyChangeEvent
public abstract class AbstractModel {
    protected PropertyChangeSupport changeFirer;

    //EFFECTS: initialize the propertyChangeSupport
    public AbstractModel() {
        changeFirer = new PropertyChangeSupport(this);
    }

    public AbstractModel(Object sourceBean) {
        changeFirer = new PropertyChangeSupport(sourceBean);
    }

    //MODIFIES: this
    //EFFECTS: add a new property change listener to this that will be notified
    //when the specified property has changed
    public void addListener(String propertyName, PropertyChangeListener listener) {
        changeFirer.addPropertyChangeListener(propertyName, listener);
    }

    public void removeListener(String propertyName, PropertyChangeListener listener) {
        changeFirer.removePropertyChangeListener(propertyName, listener);
    }

}
