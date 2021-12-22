package ui.inventorypanel.view;

import ui.table.AbstractTableDataFactory;
import ui.DataViewer;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class FilterBox extends JComboBox implements DataViewer {

    public static final String EMPTY = "Empty";
    public static final String ALL = "All";
    public static final String TYPE_MANUALLY = "Type_manually";
    private AbstractTableDataFactory model;
    private String propertyName;
//    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public FilterBox(AbstractTableDataFactory model, String category) {
        this.model = model;
        propertyName = category;
        List<String> items = new ArrayList<>();
        if (model.getContentsOf(category).isEmpty()) {
            items.add(EMPTY);
        } else {
            items.add(ALL);
            items.add(TYPE_MANUALLY);
            items.addAll(model.getContentsOf(category));
        }
        setModel(new DefaultComboBoxModel(items.toArray(new String[0])));
        model.addDataChangeListener(category,this);
    }





    @Override
    public void entryRemoved(ViewableTableEntryConvertibleModel entry) {
        removeItem(entry.toString());
    }

    @Override
    public void entryAdded(ViewableTableEntryConvertibleModel entry) {
        if (getItemCount() == 1 && getItemAt(0).equals(EMPTY)) {
            removeItemAt(0);
            addItem(ALL);
            addItem(TYPE_MANUALLY);
        }
        addItem(entry.toString());
    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, Object o1, Object o2) {
        removeItem(o1);
        if (getItemCount() == 1 && getItemAt(0).equals(EMPTY)) {
            removeItemAt(0);
            addItem(ALL);
            addItem(TYPE_MANUALLY);
        }
        addItem(o2);
    }


    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel updatedEntry) {
    }

    @Override
    public void entryUpdated(ViewableTableEntryConvertibleModel source, String property, Object o1, Object o2) {
    }

    public List<Object> getCorrespondingItems() {
        return null;
    }

    public void setPropertyWatched(String propertyName) {
        model.removeListener(this.propertyName, this);
        model.addDataChangeListener(propertyName, this);
        this.propertyName = propertyName;
    }
}
