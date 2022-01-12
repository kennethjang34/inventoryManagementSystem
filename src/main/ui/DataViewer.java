package ui;

import ui.table.DataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.util.List;

//!!!!!!!!!tight coupling among methods

public interface DataViewer {
    //called when an entry of a data factory is removed.
    void entryRemoved(ViewableTableEntryConvertibleModel removed);

    //called when a list of  entries of a data factory(source) is removed.
    void entryRemoved(DataFactory source, List< ? extends ViewableTableEntryConvertibleModel> removed);

    //called when an entry of a data factory(source) is removed.
    void entryRemoved(DataFactory source, ViewableTableEntryConvertibleModel removed);

    //called when a list of  entries of a data factory is removed.
    void entryRemoved(List<? extends ViewableTableEntryConvertibleModel> removed);

    //called when a new entry is added to the data factory
    void entryAdded(ViewableTableEntryConvertibleModel added);


    //called when a new entry is added to the data factory (source)
    void entryAdded(DataFactory source, ViewableTableEntryConvertibleModel added);

    //called when a list of new entries is added to the data factory
    void entryAdded(List<? extends ViewableTableEntryConvertibleModel> added);


    //the updated methods are for when the data factory itself implements ViewableTableEntryConvertibleModel.
    void updated(ViewableTableEntryConvertibleModel updatedEntry);

    //source: updated data factory
    //property: changed property name
    void updated(ViewableTableEntryConvertibleModel source, String property, Object old, Object newProperty);

    void updated(ViewableTableEntryConvertibleModel source, Object old, Object newObject);

}
