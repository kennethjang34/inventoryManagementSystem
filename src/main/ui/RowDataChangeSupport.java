package ui;

import ui.table.TableEntryConvertibleModel;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RowDataChangeSupport extends PropertyChangeSupport {
    Map<String, List<DataViewer>> tableDataListeners;

    /**
     * Constructs a <code>PropertyChangeSupport</code> object.
     *
     * @param sourceBean The bean to be given as the source for any events.
     */
    public RowDataChangeSupport(Object sourceBean) {
        super(sourceBean);
        tableDataListeners = new LinkedHashMap<>();
    }

    public void addTableDataModelListener(String category, DataViewer listener) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(listener);
        tableDataListeners.put(category, list);
    }

    public void addTableDataModelListener(DataViewer listener) {
        for (Map.Entry<String, List<DataViewer>> entry: tableDataListeners.entrySet()) {
            List<DataViewer> list = entry.getValue();
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(listener);
            entry.setValue(list);
        }
    }


    public void removeTableModelListener(DataViewer listener) {
        for (Map.Entry<String, List<DataViewer>> entry : tableDataListeners.entrySet()) {
            List<DataViewer> list = entry.getValue();
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    public void removeTableModelListener(String category, DataViewer viewer) {
        List<DataViewer> viewers = tableDataListeners.get(category);
        if (viewers != null) {
            viewers.remove(viewer);
        }
    }

    public void fireRemovalEvent(String category, TableEntryConvertibleModel removed) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (DataViewer tableModel : list) {
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

    public void fireAdditionEvent(String category, TableEntryConvertibleModel added) {
        for (DataViewer tableModel: tableDataListeners.get(category)) {
            tableModel.entryAdded(added);
        }
    }
//
//    public void fireUpdateEvent(String category, Object old, Object newData) {
//        for (DataViewer tableModel: tableDataListeners.get(category)) {
//            tableModel.entryUpdated(old, newData);
//        }
//    }

    public void fireUpdateEvent(String category, TableEntryConvertibleModel updatedObject) {
        for (DataViewer tableModel: tableDataListeners.get(category)) {
            tableModel.entryUpdated(updatedObject);
        }
    }

    public void fireUpdateEvent(String category, TableEntryConvertibleModel source, Object old, Object newObject) {
        for (DataViewer tableModel: tableDataListeners.get(category)) {
            tableModel.entryUpdated(source, old, newObject);
        }
    }

    public void fireUpdateEvent(TableEntryConvertibleModel updatedObject) {
        for (Map.Entry<String, List<DataViewer>> entry: tableDataListeners.entrySet()) {
            List<DataViewer> list = entry.getValue();
            if (list != null) {
                for (DataViewer viewer: list) {
                    viewer.entryUpdated(updatedObject);
                }
            }
        }
    }


}
