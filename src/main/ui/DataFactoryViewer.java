package ui;

import ui.table.TableEntryConvertibleModel;

import java.beans.PropertyChangeListener;

public interface DataFactoryViewer extends PropertyChangeListener {
    void entryRemoved(Object o);

    void entryAdded(Object o);


}
