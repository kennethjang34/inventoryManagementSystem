package ui.ledgerpanel;

import model.Account;
import model.Ledger;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.List;


public class SelectedAccountTable extends JTable implements MouseListener, TableCellRenderer {
    private SelectedAccountTableModel tableModel;
    private Ledger ledger;
    private LedgerPanel ledgerPanel;

    public SelectedAccountTable(Ledger ledger, LedgerPanel panel) {
        this.ledger = ledger;
        ledgerPanel = panel;
        tableModel = new SelectedAccountTableModel(ledgerPanel);
        setModel(tableModel);
        setDefaultRenderer(String.class, this);
        setSize(300, 400);
    }




    @Override
    public void mouseClicked(MouseEvent e) {
        //stub
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return new JLabel((String) value);

    }

    public List<Account> getAccountsOn(LocalDate date) {
        List<Account> accountList = ledger.getAccounts(date);
        return accountList;
    }

    public void addToList(LocalDate date) {
        tableModel.addAccounts(ledgerPanel.getAccountsOn(date));
//        this.repaint();
    }



    //EFFECTS: return a list of codes that are on display
    public List<Account> getAccountsOndisplay() {
        return tableModel.getAccountList();
    }
}
