package ui;

import ui.table.DataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.util.List;

//!!!!!!!!!tight coupling among methods

public interface DataViewer {
    void entryRemoved(ViewableTableEntryConvertibleModel o);


    void entryRemoved(DataFactory source, List< ? extends ViewableTableEntryConvertibleModel> list);

    void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel removed);


    void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed);

    void entryAdded(ViewableTableEntryConvertibleModel o);

    void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added);

    void entryAdded(List<? extends ViewableTableEntryConvertibleModel> list);


//    void entryUpdated(Object o1, Object o2);

    void updated(ViewableTableEntryConvertibleModel updatedEntry);

    void updated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2);

    void updated(ViewableTableEntryConvertibleModel source, Object old, Object newObject);

}
