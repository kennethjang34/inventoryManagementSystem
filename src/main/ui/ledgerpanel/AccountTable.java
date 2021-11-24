package ui.ledgerpanel;

import model.Ledger;
import model.Observer;


import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;

//represents a table that displays several accounts sorted by the date it was written
public class AccountTable extends JTable implements TableCellRenderer, MouseListener, ActionListener, Observer {
    private LedgerPanel ledgerPanel;
    private AccountTableModel tableModel;

    //EFFECTS: create a new panel
    public AccountTable(Ledger ledger, LedgerPanel panel) {
        this.ledgerPanel = panel;
        tableModel = new AccountTableModel(ledger, this);
        ledger.registerObserver(this);
        setModel(tableModel);
        setDefaultRenderer(JButton.class, this);
        setDefaultRenderer(String.class, this);
        addMouseListener(this);
        //setTableHeader(new JTableHeader());
    }

    //MODIFIES: this
    //EFFECTS: when mouse is double-clicked or the button is clicked,
    // add the accounts that occurred on the selected date to the selected account table
    @Override
    public void mouseClicked(MouseEvent e) {
        int column = columnAtPoint(e.getPoint());
        if (getColumnClass(column).equals(JButton.class)) {
            JButton button = (JButton) getValueAt(getSelectedRow(), column);
            button.doClick();
        } else if (e.getClickCount() == 2 && getSelectedRow() != -1) {
            String dateInfo = (String) getValueAt(getSelectedRow(), 0);
            LocalDate date = LocalDate.parse(dateInfo);
            ledgerPanel.addToSelected(date);
        }
    }


    //toBeDetermined
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

    //EFFECTS: when the given value is a button, return it with text "ACCOUNTS" on it
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            JButton button = (JButton)value;
            button.setText("ACCOUNTS");
            button.setBackground(Color.BLACK);
            return button;
        }
        return new JLabel((String)value);
    }

    //MODIFIES: this
    //EFFECTS: add accounts that happened on the selected date to the selected account table
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String dateInfo = button.getActionCommand();
        LocalDate date = LocalDate.parse(dateInfo);
        ledgerPanel.addToSelected(date);
    }

    @Override
    //MODIFIES: this
    //EFFECTS: update the table according to the ledger
    public void update(int arg) {
        repaint();
//        tableModel.update();
    }

    //MODIFIES: this
    //EFFECTS: display only the requested accounts
    public void display(String selectedDate) {
        LocalDate date = LocalDate.parse(selectedDate);
        tableModel.setPeriod(date, date);
    }

    //MODIFIES: this
    //EFFECTS: display all accounts
    public void displayAll() {
        tableModel.setPeriod(null, null);
        repaint();
    }
}
