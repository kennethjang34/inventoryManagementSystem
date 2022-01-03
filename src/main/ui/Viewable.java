package ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Viewable {
    protected PropertyChangeSupport changeFirer;

    public Viewable() {
        changeFirer = new PropertyChangeSupport(this);
    }

    public Viewable(PropertyChangeSupport changeFirer) {
        this.changeFirer = changeFirer;
    }




}
