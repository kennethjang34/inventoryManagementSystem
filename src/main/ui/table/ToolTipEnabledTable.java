package ui.table;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;

public class ToolTipEnabledTable extends JTable {
    boolean tooltipEnabled = true;


    public ToolTipEnabledTable() {
        super();
    }

    public ToolTipEnabledTable(TableModel tableModel) {
        super(tableModel);
    }

    public ToolTipEnabledTable(boolean enabled) {
        super();
        this.tooltipEnabled = enabled;
    }


    @Override
    public String getToolTipText(MouseEvent e) {
        if (!tooltipEnabled) {
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



    public void setTooltipEnabled(boolean tooltipEnabled) {
        this.tooltipEnabled = tooltipEnabled;
    }


}
