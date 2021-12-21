package ui.inventorypanel.view;

import ui.table.AbstractTableDataModel;
import ui.table.TableEntryConvertibleModel;
import ui.DataFactoryViewer;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class FilterBox extends JComboBox implements DataFactoryViewer {

    public static final String EMPTY = "Empty";
    public static final String ALL = "All";
    public static final String TYPE_MANUALLY = "Type_manually";
    private AbstractTableDataModel model;
    private String propertyName;
//    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public FilterBox(AbstractTableDataModel model, String category) {
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
        model.addDataModelListener(category,this);
    }



    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getOldValue() != null) {
            removeItem(evt.getOldValue().toString());
        }
        if (evt.getNewValue() != null) {
            if (getItemCount() == 1 && getItemAt(0).equals(EMPTY)) {
                removeItemAt(0);
                addItem(ALL);
                addItem(TYPE_MANUALLY);
            }
            addItem(evt.getNewValue().toString());
        }
    }

    @Override
    public void entryRemoved(Object entry) {
        removeItem(entry.toString());
    }

    @Override
    public void entryAdded(Object entry) {
        if (getItemCount() == 1 && getItemAt(0).equals(EMPTY)) {
            removeItemAt(0);
            addItem(ALL);
            addItem(TYPE_MANUALLY);
        }
        addItem(entry.toString());
    }

    public List<Object> getCorrespondingItems() {
        return null;
    }

    public void setPropertyWatched(String propertyName) {
        model.removeListener(this.propertyName, this);
        model.addEntryDataModelListener(propertyName, this);
        this.propertyName = propertyName;
    }
}
