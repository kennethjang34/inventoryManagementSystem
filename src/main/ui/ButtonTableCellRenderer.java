package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ButtonTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    //EFFECTS: return a component to be drawn at the specified cell
    //in a way that a button can be properly drawn
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            return (Component) value;
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
