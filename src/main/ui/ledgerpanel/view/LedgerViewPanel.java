package ui.ledgerpanel.view;

import model.*;
import ui.FilterBox;
import ui.table.ButtonTable;
import ui.table.RowConverterViewerTableModel;
import ui.table.ViewableTableEntryConvertibleModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Filter;
import java.util.stream.Stream;

import static ui.inventorypanel.controller.InventoryController.convertToLocalDate;

public class LedgerViewPanel extends JPanel {
    private ButtonTable ledgerTable;
    private JTable accountsTable;
    private Ledger ledger;
    private AbstractAction ledgerTableButtonAction;
    private FilterBox dateFilter;
    private JComboBox<String> itemFilter;
    private JTextField dateField = new JTextField(10);
    private JTextField itemField = new JTextField(10);
    boolean tooltipExpansionForIDs = true;

//    public enum DataList{
//        LEDGER, ACCOUNT, DATE_FILTER, ITEM_FILTER
//    }

    private KeyListener buttonEnterListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                JButton button = (JButton) e.getSource();
                button.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };


    public LedgerViewPanel(Ledger ledger) {
        this.ledger = ledger;
        setUpLedgerTable();
        setUpAccountsTable();
        dateFilter = new FilterBox(ledger, Ledger.DataList.RECORDED_DATE.toString()) {
            @Override
            public void entryUpdated(ViewableTableEntryConvertibleModel entry) {
                List<String> itemIDList;
                String selectedDateString = (String) getSelectedItem();
                RecordedDate updatedDate = (RecordedDate) entry;
                if (selectedDateString.equals(FilterBox.ALL)) {
                    itemIDList = ledger.getProcessedItemList();
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
        itemFilter = new JComboBox();
        setUpItemFilter();
        deployComponents();
//        ledger.addDataChangeListener(Ledger.DataList.RECORDED_DATE.toString(), itemFilter);
//        JScrollPane ledgerTableScrollPane = new JScrollPane(ledgerTable);
//        JScrollPane accountsTableScrollPane = new JScrollPane(accountsTable);
//        dateField.setVisible(false);
//        itemField.setVisible(false);
//        add(accountsTableScrollPane);
//        add(dateFilter);
//        add(dateField);
//        add(itemFilter);
//        add(itemField);
//        add(ledgerTableScrollPane);
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
    }

    private void setUpLedgerTable() {
        ledgerTable = new ButtonTable(ledger, "Accounts", Ledger.DataList.RECORDED_DATE.toString()) {
            @Override
            public String getToolTipText(MouseEvent e) {
                if (!tooltipExpansionForIDs) {
                    return null;
                }
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                int column = columnAtPoint(p);
                if (row == -1 || column == -1) {
                    return null;
                }
                if (!(getValueAt(row, column) instanceof Component)) {
                    return getValueAt(row, column).toString();
                }
                return null;
            }

            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        Point p = e.getPoint();
                        int column = columnAtPoint(p);
                        if (column == -1) {
                            return null;
                        }
                        return getColumnName(column);
                    }
                };
            }
        };



    }

    private void setUpAccountsTable() {
        accountsTable = new JTable() {

            @Override
            public String getToolTipText(MouseEvent e) {
                if (!tooltipExpansionForIDs) {
                    return null;
                }
                Point p = e.getPoint();
                int row = rowAtPoint(p);
                int column = columnAtPoint(p);
                if (row == -1 || column == -1) {
                    return null;
                }
                if (!(getValueAt(row, column) instanceof Component)) {
                    return getValueAt(row, column).toString();
                }
                return null;
            }


            @Override
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        Point p = e.getPoint();
                        int column = columnAtPoint(p);
                        if (column == -1) {
                            return null;
                        }
                        return getColumnName(column);
                    }
                };
            }
        };
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel();
        tableModel.setColumnNames(Stream.of(Account.DataList.values()).map(Account.DataList::toString).toArray(String[]::new));
        accountsTable.setModel(tableModel);
    }

    public void setLedgerTableButtonAction(AbstractAction action) {
        ledgerTableButtonAction = action;
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

    public void setIDCellToolTipExpansion(boolean set) {
        tooltipExpansionForIDs = set;

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
}
