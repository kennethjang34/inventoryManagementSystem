package ui.ledgerpanel.view;

import model.Account;
import model.Inventory;
import model.InventoryTag;
import model.Ledger;
import ui.FilterBox;
import ui.table.ButtonTable;
import ui.table.RowConverterViewerTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Filter;

public class LedgerViewPanel extends JPanel {
    private ButtonTable ledgerTable;
    private JTable accountsTable;
    private Ledger ledger;
    private AbstractAction ledgerTableButtonAction;
    private FilterBox dateFilter;
    private FilterBox itemFilter;
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
        dateFilter = new FilterBox(ledger, Ledger.DataList.RECORDED_DATE.toString());
        itemFilter = new FilterBox(ledger, Ledger.DataList.PROCESSED_ID.toString());
        JScrollPane ledgerTableScrollPane = new JScrollPane(ledgerTable);
        JScrollPane accountsTableScrollPane = new JScrollPane(accountsTable);
        dateField.setVisible(false);
        itemField.setVisible(false);
        add(accountsTableScrollPane);
        add(dateFilter);
        add(dateField);
        add(itemFilter);
        add(itemField);
        add(ledgerTableScrollPane);
    }

    private void setUpLedgerTable() {
        ledgerTable = new ButtonTable(ledger, "Accounts", Ledger.DataList.RECORDED_DATE.toString()) {
            @Override
            public String getToolTipText(MouseEvent e) {
                if (!tooltipExpansionForIDs) {
                    return null;
                }
                Point p = e.getPoint();
                int row = ledgerTable.rowAtPoint(p);
                int column = ledgerTable.columnAtPoint(p);
                if (row == -1 || column == -1) {
                    return null;
                }
                if (column == ledgerTable.findColumn(Ledger.DataList.PROCESSED_ID.toString())) {
                    return (String) ledgerTable.getValueAt(row, column);
                }
                return null;
            }
        };

    }


//    private void setUpDataFilter() {
//
//    }
//
//    private void setUpItemFilter() {
//
//    }


    private void setUpAccountsTable() {
        accountsTable = new JTable();
        RowConverterViewerTableModel tableModel = new RowConverterViewerTableModel();
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

    public FilterBox getItemFilter() {
        return itemFilter;
    }

    public FilterBox getDateFilter() {
        return dateFilter;
    }






    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Ledger ledger = new Ledger();
        InventoryTag tag = new InventoryTag("APP", 100, 200, LocalDate.now(), "F11", 100);
        ledger.addAccount(tag, null, LocalDate.now());
        tag = new InventoryTag("BNN", 34, 12, LocalDate.now(), "ADS", 2);
        ledger.addAccount(tag, null, LocalDate.now());
        tag = new InventoryTag("GRAPE", 0.3, 2, LocalDate.now(), "ZDF", -3);
        ledger.addAccount(tag, null, LocalDate.now());
        JPanel panel = new LedgerViewPanel(ledger);
        frame.add(panel);
        panel.setPreferredSize(new Dimension(500, 600));
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
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
