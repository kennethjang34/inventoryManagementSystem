package ui;

import ui.table.ViewableTableEntryConvertibleModel;

//!!!!!!!!!tight coupling among methods

public interface DataViewer {
    void entryRemoved(ViewableTableEntryConvertibleModel o);

    void entryAdded(ViewableTableEntryConvertibleModel o);


//    void entryUpdated(Object o1, Object o2);



    void entryUpdated(ViewableTableEntryConvertibleModel updatedEntry);

    void entryUpdated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2);

    void entryUpdated(ViewableTableEntryConvertibleModel source, Object old, Object newObject);
}
