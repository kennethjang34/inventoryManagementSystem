package ui.ledgerpanel.controller;

import model.*;
import ui.AbstractController;
import ui.FilterBox;
import ui.ledgerpanel.view.LedgerViewPanel;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.beans.PropertyChangeEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ui.inventorypanel.controller.InventoryController.convertToLocalDate;

public class LedgerController extends AbstractController<Ledger, LedgerViewPanel> {

    public void setLedger(Ledger ledger) {
        this.model = ledger;
        view.setLedger(ledger);
    }

    public void ledgerTableDateSelected(LocalDate date) {
        List<Account> selectedDateAccounts = model.getAccounts(date);
        view.addToAccountsTable(selectedDateAccounts);
    }



    public LedgerController(Ledger ledger) {
        super(ledger, new LedgerViewPanel(ledger));
    }

    @Override
    public void setUpView() {
        view.setController(this);
    }


    public void dateFilterItemSelected(String selectedDateString) {
        if (selectedDateString != null) {
            if (selectedDateString.equals(FilterBox.TYPE_MANUALLY)) {
                view.getDateField().setVisible(true);
                return;
            }
            updateItemFilter(selectedDateString);
            TableRowSorter sorter = (TableRowSorter) view.getLedgerTable().getRowSorter();
            RowFilter<TableModel, Integer> filter = view.createDateRowFilter(selectedDateString);
            sorter.setRowFilter(filter);
        }
    }



    public void idFilterItemSelected(String selectedItem) {
        if (selectedItem != null) {
            if (selectedItem.equals("TYPE_MANUALLY")) {
                view.getItemField().setVisible(true);
            }
            TableRowSorter sorter = (TableRowSorter) view.getLedgerTable().getRowSorter();
            RowFilter<TableModel, Integer> filter = view.createItemRowFilter(selectedItem);
            sorter.setRowFilter(filter);
        }
    }

    private void updateItemFilter(String selectedDateString) {
        JComboBox itemFilter = view.getItemFilter();
        List<String> itemIDList;
        if (selectedDateString.equals(FilterBox.EMPTY)) {
            itemIDList = new ArrayList<>();
            itemIDList.add(FilterBox.EMPTY);
            itemFilter.setModel(new DefaultComboBoxModel(itemIDList.toArray(new String[0])));
            return;
        } else if (selectedDateString.equals(FilterBox.ALL)) {
            itemIDList = model.getProcessedItemList();
        } else {
            //in case of TYPE_MANUALLY, it's the same as itemIDList.addAll(null);
            LocalDate date = convertToLocalDate(selectedDateString);
            itemIDList = model.getProcessedItemList(date);
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


}
