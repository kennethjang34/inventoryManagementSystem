package ui.ledgerpanel.view;

import model.Ledger;
import ui.table.ButtonTable;

import javax.swing.*;

public class LedgerViewPanel extends JPanel {
    ButtonTable ledgerTable;
    JTable AccountsTable;

    public LedgerViewPanel(Ledger ledger) {
        ledgerTable = new ButtonTable(ledger.getEntryModels(), ledger.getColumnNames(), "Accounts");
    }


}
