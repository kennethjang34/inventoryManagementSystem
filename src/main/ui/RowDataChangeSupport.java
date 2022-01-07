package ui;

import ui.table.DataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.beans.PropertyChangeSupport;
import java.util.*;

public class RowDataChangeSupport extends PropertyChangeSupport {
    Map<String, List<DataViewer>> tableDataListeners;
    public static final String UNSPECIFIED = "UNSPECIFIED";
    public static final String GENERAL = "GENERAL";
    public static final String UPDATE = "UPDATE";
    public static final String ADDITION = "UPDATE";
    public static final String REMOVAL = "REMOVAL";
    public static final String addNRem = "ADDNREM";

    /**
     * Constructs a <code>PropertyChangeSupport</code> object.
     *
     * @param sourceBean The bean to be given as the source for any events.
     */
    public RowDataChangeSupport(Object sourceBean) {
        super(sourceBean);
        tableDataListeners = new LinkedHashMap<>();
        tableDataListeners.put(UNSPECIFIED, new LinkedList<>());
        tableDataListeners.put(GENERAL, new LinkedList<>());
        tableDataListeners.put(UPDATE, new LinkedList<>());
    }

    //By default, every listener will be notified when an event occurs at the general level
    public void addTableDataModelListener(String category, DataViewer listener) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(listener);
        tableDataListeners.put(category, list);
        if (!category.equals(GENERAL)) {
            list = tableDataListeners.get(GENERAL);
            list.add(listener);
            tableDataListeners.put(GENERAL, list);
        }
    }

    //EFFECTS: if generalNotification is true, the listener will be registered as general listener
    // as well as the listener for the one specified.
    //Added for the specified category otherwise.
    public void addTableDataModelListener(String category, DataViewer listener, boolean generalNotification) {
        if (category == null) {
            throw new IllegalArgumentException();
        }
        List<DataViewer> list = tableDataListeners.get(category);
        if (list == null) {
            list = new LinkedList<>();
        }
        list.add(listener);
        tableDataListeners.put(category, list);
        if (!category.equals(GENERAL) && generalNotification == true) {
            list = tableDataListeners.get(GENERAL);
            list.add(listener);
            tableDataListeners.put(GENERAL, list);
        }
    }

    //Register as an unspecified listener. It will be notified for every event
    public void addTableDataModelListener(DataViewer listener) {
        List<DataViewer> list = tableDataListeners.get(UNSPECIFIED);
        list.add(listener);
        tableDataListeners.put(UNSPECIFIED, list);
    }

    public void addGeneralTableDataModelListener(DataViewer listener) {
        List<DataViewer> list = tableDataListeners.get(GENERAL);
        list.add(listener);
        tableDataListeners.put(GENERAL, list);
    }

    public void addUpdateDataModelListener(DataViewer listener) {
        List<DataViewer> list = tableDataListeners.get(UPDATE);
        list.add(listener);
        tableDataListeners.put(UPDATE, list);
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

    public void fireRemovalEvent(String category, ViewableTableEntryConvertibleModel removed) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryRemoved(removed);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryRemoved(removed);
        }
    }

    public void fireRemovalEvent(DataFactory source, String category, ViewableTableEntryConvertibleModel removed) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryRemoved(source, removed);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryRemoved(source, removed);
        }
    }



    public void fireRemovalEvent(String category, List<? extends ViewableTableEntryConvertibleModel> removed) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryRemoved(removed);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryRemoved(removed);
        }
    }

    public void fireRemovalEvent(DataFactory source, String category,
                                 List<? extends ViewableTableEntryConvertibleModel> removed) {

        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryRemoved(source, removed);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryRemoved(source, removed);
        }
    }

    public void fireAdditionEvent(String category, ViewableTableEntryConvertibleModel added) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryAdded(added);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryAdded(added);
        }
    }

    public void fireAdditionEvent(DataFactory source,
                                  String property, ViewableTableEntryConvertibleModel added) {

        List<DataViewer> list = tableDataListeners.get(property);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryAdded(source, added);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryAdded(source, added);
        }
    }


    public void fireAdditionEvent(ViewableTableEntryConvertibleModel added) {
        List<DataViewer> list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryAdded(added);
        }
    }




    public void fireAdditionEvent(String category, List<? extends ViewableTableEntryConvertibleModel> added) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.entryAdded(added);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.entryAdded(added);
        }
    }


    public void fireUpdateEvent(String category, ViewableTableEntryConvertibleModel source) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source);
            }
        }
        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source);
        }

        list = tableDataListeners.get(UPDATE);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source);
        }
    }

    public void fireUpdateEvent(String category, ViewableTableEntryConvertibleModel source, Object old, Object newObject) {
        List<DataViewer> list = tableDataListeners.get(category);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source, old, newObject);
            }
        }

        list = tableDataListeners.get(UNSPECIFIED);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source, old, newObject);
        }

        list = tableDataListeners.get(UPDATE);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source);
        }
    }

        //
    //General event notification
    public void fireUpdateEvent(ViewableTableEntryConvertibleModel source) {
        List<DataViewer> list = tableDataListeners.get(UNSPECIFIED);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source);
            }
        }

        list = tableDataListeners.get(GENERAL);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source);
            }
        }

        list = tableDataListeners.get(UPDATE);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source);
        }
    }


    public void fireUpdateEvent(ViewableTableEntryConvertibleModel source, Object old, Object newObj) {

        List<DataViewer> list = tableDataListeners.get(UNSPECIFIED);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source, old, newObj);
            }
        }

        list = tableDataListeners.get(GENERAL);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source, old, newObj);
            }
        }

        list = tableDataListeners.get(UPDATE);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source);
        }
    }

    public void fireUpdateEvent(ViewableTableEntryConvertibleModel source, String property, Object old, Object newObj) {
        List<DataViewer> list = tableDataListeners.get(UNSPECIFIED);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source, property, old, newObj);
            }
//            for (Iterator<DataViewer> it = list.iterator(); it.hasNext();) {
//                DataViewer viewer = it.next();
//                viewer.entryUpdated(source, property, old, newObj);
//            }
        }
        list = tableDataListeners.get(GENERAL);
        if (list != null) {
            for (int i = list.size() - 1; i >= 0; i--) {
                DataViewer viewer = list.get(i);
                viewer.updated(source, property, old, newObj);
            }

        }

        list = tableDataListeners.get(UPDATE);
        for (int i = list.size() - 1; i >= 0; i--) {
            DataViewer viewer = list.get(i);
            viewer.updated(source);
        }
    }
}
