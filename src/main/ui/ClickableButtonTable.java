package ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class ClickableButtonTable extends JTable implements TableCellRenderer, MouseListener {

    public ClickableButtonTable() {
        addMouseListener(this);
    }



//    @Override
//    public boolean isCellEditable(int row, int column) {
//        return false;
//    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                            boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            JButton button = (JButton) value;
            return  button;
        } else if (value instanceof String || value instanceof Double || value instanceof Integer) {
            return this;
        }
        assert value == null;
        return this;
    }



    @Override
    public void mouseClicked(MouseEvent e) {

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
}
