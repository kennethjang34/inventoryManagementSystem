package ui.ledgerpanel;

import javafx.scene.control.TableCell;
import model.Ledger;
import model.QuantityTag;
import ui.inventorypanel.stockpanel.StockButtonTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;

public class AccountTable extends JTable implements TableCellRenderer, MouseListener, ActionListener {
    private Ledger ledger;
    private LedgerPanel ledgerPanel;
    private AccountTableModel tableModel;


    public AccountTable(Ledger ledger, LedgerPanel panel) {
        this.ledger = ledger;
        this.ledgerPanel = panel;
        tableModel = new AccountTableModel(ledger, this);
        setModel(tableModel);
        setDefaultRenderer(JButton.class, this);
        setDefaultRenderer(String.class, this);
        addMouseListener(this);
        //setTableHeader(new JTableHeader());
    }

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
        if (value instanceof JButton) {
            JButton button = (JButton)value;
            button.setText("ACCOUNTS");
            button.setBackground(Color.BLACK);
            return button;
        }
        return new JLabel((String)value);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String dateInfo = button.getActionCommand();
        LocalDate date = LocalDate.parse(dateInfo);
        ledgerPanel.addToSelected(date);
    }

    public void update() {
        tableModel.update();
    }

    public void display(String selectedDate) {
        LocalDate date = LocalDate.parse(selectedDate);
        tableModel.setPeriod(date, date);
    }

    public void displayAll() {
        tableModel.setPeriod(null, null);
        repaint();
    }
}
