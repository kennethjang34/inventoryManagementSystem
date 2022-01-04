package ui.ledgerpanel.controller;

import model.*;
import ui.AbstractController;
import ui.FilterBox;
import ui.ledgerpanel.view.LedgerViewPanel;
import ui.table.ButtonTable;
import ui.table.RowConverterViewerTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ui.inventorypanel.controller.InventoryController.convertToLocalDate;

public class LedgerController extends AbstractController<Ledger, LedgerViewPanel> {

    private class LedgerButtonAction extends AbstractAction {

        private LedgerButtonAction() {
            super("Accounts");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ButtonTable ledgerTable = view.getLedgerTable();
            int row = ledgerTable.findModelRowIndex(e.getSource());
            int column = ledgerTable.findColumnModelIndex(Ledger.DataList.RECORDED_DATE.toString());
            LocalDate date = (LocalDate) ledgerTable.getValueAt(row, column);
            List<Account> selectedDateAccounts = model.getAccounts(date);
            view.addToAccountsTable(selectedDateAccounts);
        }
    }



    public LedgerController(Ledger ledger, LedgerViewPanel viewPanel) {
        super(ledger, viewPanel);
    }


    @Override
    public void setUpView() {
        setUpLedgerTable();
        setUpAccountsTable();
        setUpDateFilter();
        setUpItemFilter();
    }

    private void setUpAccountsTable() {
        JTable table = view.getAccountsTable();
        RowConverterViewerTableModel tableModel = (RowConverterViewerTableModel) table.getModel();
        tableModel.setColumnNames(new String[]{Account.DataList.CODE.toString(), Account.DataList.ID.toString(), Account.DataList.DATE.toString(),
                Account.DataList.LOCATION.toString(), Account.DataList.AVERAGE_PRICE.toString(),
                Account.DataList.AVERAGE_COST.toString(), Account.DataList.QUANTITY.toString(), Account.DataList.DESCRIPTION.toString()
        });
        table.setRowSorter(createRowSorter(table, null, Comparator.naturalOrder()));
    }

//    private JPopupMenu createAccountsTablePopUpMenu() {
//        return null;
//    }


    public void setUpLedgerTable() {
        ButtonTable table = view.getLedgerTable();
        table.setButtonAction(new LedgerButtonAction());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1 && e.getClickCount() == 2) {
                    int index = table.convertRowIndexToModel(table.getSelectedRow());
//                    Object[] row = table.getSelectedRowData();
                    if (index != -1) {
                        table.getButtons().get(index).doClick();
                    }
                }
            }
        });
        TableRowSorter sorter = createRowSorter(table, null, Comparator.naturalOrder());
        table.setRowSorter(sorter);
        JPopupMenu menu = createLedgerTablePopUpMenu();
        table.setComponentPopupMenu(menu);
    }

    private JPopupMenu createLedgerTablePopUpMenu() {
        return null;
    }


    private void setUpDateFilter() {
        FilterBox dateFilter = view.getDateFilter();
        dateFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dateFilter.getSelectedItem() != null) {
                    String selectedDateString = (String) dateFilter.getSelectedItem();
                    if (selectedDateString.equals(FilterBox.TYPE_MANUALLY)) {
                        view.getDateField().setVisible(true);
                        return;
                    }
                    updateItemFilter(selectedDateString);
                    TableRowSorter sorter = (TableRowSorter) view.getLedgerTable().getRowSorter();
                    RowFilter<TableModel, Integer> filter = createDateRowFilter(selectedDateString);
                    sorter.setRowFilter(filter);
                }
            }

        });
    }

    private void setUpItemFilter() {
        JComboBox itemFilter = view.getItemFilter();
        itemFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) itemFilter.getSelectedItem();
                if (selectedItem != null) {
                    if (selectedItem.equals("TYPE_MANUALLY")) {
                        view.getItemField().setVisible(true);
                    }
                    TableRowSorter sorter = (TableRowSorter) view.getLedgerTable().getRowSorter();
                    RowFilter<TableModel, Integer> filter = createItemRowFilter(selectedItem);
                    sorter.setRowFilter(filter);
                }
            }
        });
    }

    private void updateItemFilter(String selectedDateString) {
        JComboBox itemFilter = view.getItemFilter();
        List<String> itemIDList;
        if (selectedDateString.equals(FilterBox.ALL)) {
            itemIDList = model.getProcessedItemList();
        } else {
            //in case of TYPE_MANUALLY, it's the same as itemIDList.addAll(null);
            itemIDList = model.getProcessedItemList(convertToLocalDate(selectedDateString));
        }

        if (itemIDList.isEmpty()) {
            itemIDList.add(FilterBox.EMPTY);
        } else {
            itemIDList.add(0, FilterBox.ALL);
            itemIDList.add(1, FilterBox.TYPE_MANUALLY);
        }

        itemFilter.setModel(new DefaultComboBoxModel(itemIDList.toArray(new String[0])));
    }




    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    public TableRowSorter createRowSorter(JTable table,
                                          RowFilter<TableModel, Integer> filter, Comparator comparator) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        sorter.setRowFilter(filter);
        sorter.setComparator(0, comparator);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        for (int i = 0; i < table.getColumnCount(); i++) {
            sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
        }
        sorter.setSortKeys(sortKeys);
        return sorter;
    }


    public RowFilter<TableModel, Integer> createDateRowFilter(String date) {
        RowFilter<TableModel, Integer> rowFilter = new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                if (date.equals(FilterBox.ALL) || date.equals(FilterBox.TYPE_MANUALLY)) {
                    return true;
                }
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).equals(date)) {
                        return true;
                    }
                }
                return false;
            }
        };
        return rowFilter;
    }


    public RowFilter<TableModel, Integer> createItemRowFilter(String processedItem) {
        String date = (String) view.getDateFilter().getSelectedItem();
        RowFilter<TableModel, Integer> rowFilter = new RowFilter<TableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                if (processedItem.equals(FilterBox.ALL) || processedItem.equals(FilterBox.TYPE_MANUALLY)) {
                    if (date.equals(FilterBox.ALL)) {
                        return true;
                    }
                    for (int i = 0; i < entry.getValueCount(); i++) {
                        if (entry.getStringValue(i).equals(date)) {
                            return true;
                        }
                    }
                    return false;
                }
                for (int i = 0; i < entry.getValueCount(); i++) {
                    String cellContent = entry.getStringValue(i);
                    if (cellContent.contains(processedItem)) {
                        return true;
                    }
                }
                return false;
            }
        };
        return rowFilter;
    }


}
