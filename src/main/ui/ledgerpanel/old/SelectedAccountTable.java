package ui.ledgerpanel.old;

import model.Account;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.util.List;

//represents a table that is composed of particular accounts selected by the user
public class SelectedAccountTable extends JTable implements MouseListener, TableCellRenderer {
    private SelectedAccountTableModel tableModel;
    private LedgerPanel ledgerPanel;

    //EFFECTS: create a new table
    public SelectedAccountTable(LedgerPanel panel) {
        ledgerPanel = panel;
        tableModel = new SelectedAccountTableModel();
        setModel(tableModel);
        setDefaultRenderer(String.class, this);
        setSize(300, 400);
    }



    //toBeDetermined
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

    //EFFECTS: when value is string, return a JLabel containing the string
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return new JLabel((String) value);

    }


    //MODIFIES: this
    //EFFECTS: add a list of accounts that were written on the given date to this
    public void addToList(LocalDate date) {
        tableModel.addAccounts(ledgerPanel.getAccountsOn(date));
//        this.repaint();
    }



    //EFFECTS: return a list of codes that are on display
    public List<Account> getAccountsOndisplay() {
        return tableModel.getAccountList();
    }
}
