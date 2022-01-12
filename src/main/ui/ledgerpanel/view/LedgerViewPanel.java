package ui.ledgerpanel.view;

import model.*;
import ui.FilterBox;
import ui.ledgerpanel.controller.LedgerController;
import ui.table.ButtonTable;
import ui.table.RowConverterViewerTableModel;
import ui.table.ToolTipEnabledTable;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LedgerViewPanel extends JPanel {
    private LedgerController controller;
    private ButtonTable ledgerTable;
    private JTable accountsTable;
    private Ledger ledger;
    private AbstractAction ledgerTableButtonAction;
    private FilterBox dateFilter;
    private JComboBox<String> itemFilter;
    private JTextField dateField = new JTextField(10);
    private JTextField itemField = new JTextField(10);
    boolean tooltipExpansionForIDs = true;



    public LedgerViewPanel(Ledger ledger, LedgerController controller) {
        this.controller = controller;
        this.ledger = ledger;
        setUpLedgerTable();
        setUpAccountsTable();
        setUpDateFilter();
        itemFilter = new JComboBox();
        setUpItemFilter();
        deployComponents();
    }

    public LedgerViewPanel(Ledger ledger) {
        this.ledger = ledger;
        setUpLedgerTable();
        setUpAccountsTable();
        setUpDateFilter();
        itemFilter = new JComboBox();
        setUpItemFilter();
        deployComponents();
    }

    public void setUpDateFilter() {
        dateFilter = new FilterBox(this.ledger, Ledger.DataList.RECORDED_DATE.toString()) {
            @Override
            public void entryAdded(ViewableTableEntryConvertibleModel added) {
                if (added instanceof RecordedDate) {
                    added.addDataChangeListener(this);
                    if (getItemCount() == 1 && getItemAt(0).equals(EMPTY)) {
                        removeItemAt(0);
                        addItem(ALL);
                        addItem(TYPE_MANUALLY);
                    }
                    addItem(added.toString());
                }
                else if (added instanceof Account) {
                    Account account = (Account) added;
                    DefaultComboBoxModel<String> itemFilterModel = (DefaultComboBoxModel<String>) itemFilter.getModel();
                    if (itemFilterModel.getIndexOf(account.getID()) != -1) {
                        return;
                    }
                    if (dateFilter.getSelectedItem().equals(account.getDate().toString())
                            || dateFilter.getSelectedItem().equals(FilterBox.ALL)) {
                        if (itemFilter.getSelectedItem().equals(FilterBox.EMPTY)) {
                            itemFilter.removeAllItems();
                            itemFilter.addItem(ALL);
                            itemFilter.addItem(TYPE_MANUALLY);
                        }
                        itemFilter.addItem(account.getID());
                    }
                }
            }

            @Override
            public void updated(ViewableTableEntryConvertibleModel entry) {
                List<String> itemIDList;
                String selectedDateString = (String) getSelectedItem();
                RecordedDate updatedDate = (RecordedDate) entry;
                if (selectedDateString.equals(FilterBox.ALL)) {
                    itemIDList = LedgerViewPanel.this.ledger.getProcessedItemList();
                } else if (updatedDate.getDate().toString().equals(selectedDateString)){
                    itemIDList = updatedDate.getIDList();
                } else {
                    return;
                }

                if (itemIDList.isEmpty()) {
                    itemIDList.add(FilterBox.EMPTY);
                } else {
                    itemIDList.add(0, FilterBox.ALL);
                    itemIDList.add(1, FilterBox.TYPE_MANUALLY);
                }
                itemFilter.setModel(new DefaultComboBoxModel(itemIDList.toArray(new String[0])));
            }
        };
        dateFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    controller.dateFilterItemSelected((String) e.getItem());
                }
            }
        });
    }

    private void deployComponents() {
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600 , 850));
        JScrollPane ledgerTableScrollPane = new JScrollPane(ledgerTable);
        JScrollPane accountsTableScrollPane = new JScrollPane(accountsTable);
        dateField.setVisible(false);
        itemField.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        //Keep (0,0) empty for the product sorter that will be developed later
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 40;
        gbc.gridheight = 30;
        gbc.weightx = 1;
        gbc.weighty = 0.45;
        gbc.fill = GridBagConstraints.BOTH;
        add(accountsTableScrollPane, gbc);
        gbc.gridx = 0;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.weighty = 0.05;
        add(dateFilter, gbc);
        gbc.gridx = gbc.gridx + gbc.gridwidth;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        add(itemFilter, gbc);
        gbc.gridx = 0;
        gbc.gridy = gbc.gridy + gbc.gridheight;
        gbc.gridwidth = 40;
        gbc.gridheight = 30;
        gbc.weightx = 1;
        gbc.weighty = 0.45;
        gbc.anchor = GridBagConstraints.CENTER;
        add(ledgerTableScrollPane, gbc);
    }

    private void setUpItemFilter() {
        List<String> items = new ArrayList<>();
        if (ledger.getIDs().isEmpty()) {
            items.add("Empty");
        } else {
            items.add("All");
            items.add("TYPE_MANUALLY");
            items.addAll(ledger.getIDs());
        }
        itemFilter.setModel((new DefaultComboBoxModel(items.toArray(new String[0]))));
        itemFilter.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String selectedItem = (String) itemFilter.getSelectedItem();
                controller.idFilterItemSelected(selectedItem);
            }
        });
    }

    private void setUpLedgerTable() {
        ledgerTable = new ButtonTable(ledger, "Accounts", Ledger.DataList.RECORDED_DATE.toString());
        ledgerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 1 && e.getClickCount() == 2) {
                    int index = ledgerTable.convertRowIndexToModel(ledgerTable.getSelectedRow());
                    if (index != -1) {
                        ledgerTable.getButtons().get(index).doClick();
                    }
                }
            }
        });
        ledgerTable.setButtonAction(new AbstractAction("Accounts") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = ledgerTable.findModelRowIndex(e.getSource());
                int column = ledgerTable.findColumnModelIndex(Ledger.DataList.RECORDED_DATE.toString());
                LocalDate date = (LocalDate) ledgerTable.getValueAt(row, column);
                controller.ledgerTableDateSelected(date);
            }
        });
        TableRowSorter sorter = createRowSorter(ledgerTable, null, Comparator.naturalOrder());
        ledgerTable.setRowSorter(sorter);
        JPopupMenu menu = createLedgerTablePopUpMenu();
        ledgerTable.setComponentPopupMenu(menu);
    }

    //Not determined whether to implement it
    private JPopupMenu createLedgerTablePopUpMenu() {
        return null;
    }

    private void setUpAccountsTable() {
        accountsTable = new ToolTipEnabledTable(true);
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel();
        tableModel.setColumnNames(new String[]{Account.DataList.CODE.toString(), Account.DataList.ID.toString(), Account.DataList.DATE.toString(),
                Account.DataList.LOCATION.toString(), Account.DataList.AVERAGE_PRICE.toString(),
                Account.DataList.AVERAGE_COST.toString(), Account.DataList.QUANTITY.toString(), Account.DataList.DESCRIPTION.toString()
        });
        accountsTable.setModel(tableModel);
        accountsTable.setRowSorter(createRowSorter(accountsTable, null, Comparator.naturalOrder()));
    }

    public void setLedgerTableButtonAction(AbstractAction action) {
        ledgerTableButtonAction = action;
    }


    public void setAccountsTable(JTable accountsTable) {
        this.accountsTable = accountsTable;
    }

    public void setLedgerTable(JTable ledgerTable) {
        this.ledger = ledger;
    }

    public void addLedgerTableButtonActionListener(ActionListener listener) {
        //add the listener to every button
    }

    public ButtonTable getLedgerTable() {
        return ledgerTable;
    }

    public RowConverterViewerTableModel getLedgerTableModel() {
        return (RowConverterViewerTableModel) ledgerTable.getModel();
    }

    public RowConverterViewerTableModel getAccountsTableModel() {
        return (RowConverterViewerTableModel) accountsTable.getModel();
    }

    public JTable getAccountsTable() {
        return accountsTable;
    }

    public JComboBox<String> getItemFilter() {
        return itemFilter;
    }

    public FilterBox getDateFilter() {
        return dateFilter;
    }




    public void addToAccountsTable(List<Account> selectedDateAccounts) {
        getAccountsTableModel().addRowsWithDataList(selectedDateAccounts);
    }


    public JTextField getDateField() {
        return dateField;
    }

    public JTextField getItemField() {
        return itemField;
    }

    public void setLedger(Ledger ledger) {
        this.ledger = ledger;
        RowConverterViewerTableModel ledgerTableModel = (RowConverterViewerTableModel) ledgerTable.getModel();
        ledgerTableModel.setDataFactory(ledger);
        RowConverterViewerTableModel accountsTableModel = (RowConverterViewerTableModel) accountsTable.getModel();
        accountsTableModel.reset();
        dateFilter.setDataFactory(ledger);
        setUpItemFilter();
    }

    public void setController(LedgerController controller) {
        this.controller = controller;
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
        String date = (String) getDateFilter().getSelectedItem();
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
