package ui.ledgerpanel;

import model.Account;
import model.Ledger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.*;

//represents a table model that displays accounts that occur on different dates
public class AccountTableModel extends AbstractTableModel {
    Ledger ledger;
    List<LocalDate> dates;
    LocalDate periodStart;
    LocalDate periodEnd;
    List<LocalDate> currentPeriod;
    //buttons' commands are also string forms of dates
    Map<LocalDate, JButton> buttonMap;
    String[] columnNames = new String[]{"Date", "IDs", "BUTTON"};
    private ActionListener buttonActionListener;



    //EFFECTS: create a new account table model with the given ledger
    public AccountTableModel(Ledger ledger, ActionListener buttonActionListener) {
        this.buttonActionListener = buttonActionListener;
        this.ledger = ledger;
        buttonMap = new HashMap<>();
        dates = new ArrayList<>();
        List<String> dateInfo = Arrays.asList(ledger.getDates());
        for (String s: dateInfo) {
            dates.add(LocalDate.parse(s));
        }

    }

    //REQUIRES: the period start date must be before or the same as end date
    public void setPeriod(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new RuntimeException("Period start date must be after or the same as end date");
        }
        periodStart = start;
        periodEnd = end;
        fireTableDataChanged();
    }




    //EFFECTS: update this table model up to date when a new date is added
    public void update() {
        List<String> datesInfo = Arrays.asList(ledger.getDates());
        dates = new ArrayList<>();
        for (String dateInfo: datesInfo) {
            dates.add(LocalDate.parse(dateInfo));
        }
        fireTableDataChanged();
    }

    //EFFECTS: return the number of columns
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    //EFFECTS: return the class of a particular column;
    @Override
    public Class getColumnClass(int col) {
        if (getColumnName(col).equalsIgnoreCase("BUTTON")) {
            return JButton.class;
        }
        return String.class;
    }

    //EFFECTS: return the number of rows
    @Override
    public int getRowCount() {
        if (periodStart == null && periodEnd == null) {
            return dates.size();
        } else {
            return datesInPeriod(periodStart, periodEnd).size();
        }
    }

    //EFFECTS: return a list of dates between the two dates (inclusive)
    private List<LocalDate> datesInPeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<LocalDate> period = new ArrayList<>();
        if (periodEnd == periodStart) {
            if (periodEnd == null) {
                return null;
            } else {
                period.add(periodEnd);
                return period;
            }
        } else {
            LocalDate startDate = periodStart;
            while (!startDate.isAfter(periodEnd)) {
                period.add(startDate);
                startDate.plusDays(1);
            }
            return period;
        }
    }

    //EFFECTS: return the column size
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (periodEnd != null || periodStart != null) {
            currentPeriod = datesInPeriod(periodStart, periodEnd);
        }
        LocalDate date = dates.get(rowIndex);
        if (getColumnName(columnIndex).equalsIgnoreCase("BUTTON")) {
            if (buttonMap.containsKey(date)) {
                return buttonMap.get(date);
            } else {
                JButton button = new JButton();
                button.setText("Accounts");
                button.setActionCommand(date.toString());
                button.addActionListener(buttonActionListener);
                return button;
            }
        } else {
            if (getColumnName(columnIndex).equalsIgnoreCase("Date")) {
                return date.toString();
            } else if (getColumnName(columnIndex).equalsIgnoreCase("IDs")) {
                List<String> ids = ledger.getIDs(date);
                String s = "";
                for (String id : ids) {
                    s += id + " ";
                }
                return s;
            }
            return null;
        }

    }
}
