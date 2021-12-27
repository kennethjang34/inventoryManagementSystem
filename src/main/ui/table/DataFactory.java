package ui.table;


import java.util.List;

public interface DataFactory {
    Object[] getDataList();

    List<String> getContentsOf(String property);

    String[] getColumnNames();

    List<? extends ViewableTableEntryConvertibleModel> getEntryModels();


}
