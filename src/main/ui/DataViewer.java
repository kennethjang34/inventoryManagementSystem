package ui;

import ui.table.TableEntryConvertibleModel;

import java.beans.PropertyChangeListener;

public interface DataViewer {
    void entryRemoved(TableEntryConvertibleModel o);

    void entryAdded(TableEntryConvertibleModel o);


//    void entryUpdated(Object o1, Object o2);


    void entryUpdated(TableEntryConvertibleModel updatedEntry);


    void entryUpdated(TableEntryConvertibleModel source, Object old, Object newObject);
}
