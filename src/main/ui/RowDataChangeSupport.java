package ui;

import ui.table.TableEntryConvertibleModel;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RowDataChangeSupport extends PropertyChangeSupport {
    Map<String, List<DataFactoryViewer>> tableDataListeners;

    /**
     * Constructs a <code>PropertyChangeSupport</code> object.
     *
     * @param sourceBean The bean to be given as the source for any events.
     */
    public RowDataChangeSupport(Object sourceBean) {
        super(sourceBean);
        tableDataListeners = new LinkedHashMap<>();
    }

    public void addTableDataModelListener(String category, DataFactoryViewer listener) {
        List<DataFactoryViewer> list = tableDataListeners.get(category);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(listener);
        tableDataListeners.put(category, list);
    }

    public void removeTableModelListener(DataFactoryViewer listener) {
        tableDataListeners.remove(listener);
    }

    public void fireRemovalEvent(String category, Object removed) {
        List<DataFactoryViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (DataFactoryViewer tableModel : list) {
                tableModel.entryRemoved(removed);
            }
        }
    }
//
//    public void fireRemovalEvent(String category, TableEntryConvertibleModel removed) {
//        for (TableView tableModel: tableDataListeners) {
//            tableModel.entryRemoved(removed);
//        }
//    }
//
//    public void fireAdditionEvent(TableEntryConvertibleModel added) {
//        for (TableView tableModel: tableDataListeners) {
//            tableModel.entryAdded(added);
//        }
//    }

    public void fireAdditionEvent(String category, Object added) {
        for (DataFactoryViewer tableModel: tableDataListeners.get(category)) {
            tableModel.entryAdded(added);
        }
    }
}
