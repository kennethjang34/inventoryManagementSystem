package ui.inventorypanel.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SearchPanel extends JPanel {

    private JTextField searchField;
    private JButton searchButton;
    private static SearchPanel searchPanel = new SearchPanel();

    private KeyListener buttonEnterListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                JButton button = (JButton) e.getSource();
                button.doClick();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    };

    private SearchPanel() {
        searchField = new JTextField(30);
        searchField.setText("Type item ID or product SKU to be searched for");
        searchButton = new JButton("Search");
        searchButton.addKeyListener(buttonEnterListener);
        searchField.addActionListener(e -> searchButton.doClick());
        add(searchField);
        add(searchButton);
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public static SearchPanel getSearchPanel() {
        return searchPanel;
    }

    public void display(Component parentComponent) {
        JDialog dialog = new JDialog();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.add(searchPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void displaySearchPanel(Component parentComponent) {
        JDialog dialog = new JDialog();
        dialog.add(searchPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setVisible(true);
    }
}
