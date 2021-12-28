package ui;

import ui.table.ViewableTableEntryConvertibleModel;

import java.util.List;

//!!!!!!!!!tight coupling among methods

public interface DataViewer {
    void entryRemoved(ViewableTableEntryConvertibleModel o);

    void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed);

    void entryAdded(ViewableTableEntryConvertibleModel o);

    void entryAdded(List<? extends ViewableTableEntryConvertibleModel> list);


//    void entryUpdated(Object o1, Object o2);

    void entryUpdated(ViewableTableEntryConvertibleModel updatedEntry);

    void entryUpdated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2);

    void entryUpdated(ViewableTableEntryConvertibleModel source, Object old, Object newObject);
}
