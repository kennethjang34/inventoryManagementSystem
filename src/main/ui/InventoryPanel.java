package ui;

import model.Inventory;
import model.InventoryTag;
import model.QuantityTag;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventoryPanel extends JPanel implements ActionListener {
    private final Inventory inventory;
    private final SearchPanel searchPanel;
    private JPanel addListPanel;
    private JPanel removeListPanel;
    private StockPanel stockPanel;
    private String add = "add";
    private String remove = "remove";
    private String update = "update";
    private String search = "search";
    private final ArrayList<InventoryTag> listToAdd;
    private final ArrayList<QuantityTag> listToRemove;
    private JTable tableForNew;
    private JTable tableForRemoval;
    private JTable stockTable;




    //A panel that will prompt the user to enter input for removing stocks
    private class RemovePanel extends JPanel implements ActionListener {


        JTextField codeField = new JTextField(10);
        //JTextField skuField = new JTextField();
        List<JTextField> quantityFields = new ArrayList<>();
        DefaultTableModel tableModel;
        String search = "search";

        private class QuantityField extends JTextField {

            @Override
            public String toString() {
                return getText();
            }

            private QuantityField(int width) {
                super(width);
            }

        }


        //EFFECTS: create a new panel that will pop up
        //if the user attempts to add an entry containing information of products to be removed
        private RemovePanel() {
            add(new JLabel("Item code"));
            add(codeField);
//            add(new JLabel("SKU"));
//            add(skuField);
            JButton searchButton = new JButton("Search");
            searchButton.setActionCommand(search);
            searchButton.addActionListener(this);
            add(searchButton);
            setSize(700, 800);
            setVisible(true);
        }

        //MODIFIES: this
        //EFFECTS: display a new panel that prompt the user to specify products to be removed
        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog locationDialog = new JDialog();
            locationDialog.setLayout(new FlowLayout());
            String itemCode = codeField.getText();
            List<QuantityTag> tags = inventory.getQuantitiesAtLocations(itemCode);
            if (tags == null) {
                locationDialog.add(new JLabel("There is no such product"));
                locationDialog.setVisible(true);
                return;
            }
            locationDialog.add(new JLabel("Item code: " + itemCode));
            for (int i = 0; i < tags.size(); i++) {
                quantityFields.add(new QuantityField(10));
            }
            tableModel = new DefaultTableModel() {
                @Override
                public int getRowCount() {
                    return tags.size();
                }

                @Override
                public int getColumnCount() {
                    return 3;
                }



                @Override
                public Object getValueAt(int rowIndex, int columnIndex) {
                    QuantityTag tag = tags.get(rowIndex);
                    if (columnIndex == 0) {
                        return tag.getLocation();
                    } else if (columnIndex == 1) {
                        return tag.getQuantity();
                    } else {
                        return quantityFields.get(rowIndex);
                    }
                }

                @Override
                public boolean isCellEditable(int row, int col) {
                    if (col == 2) {
                        return true;
                    }
                    return false;
                }

                @Override
                public void setValueAt(Object value, int row, int column) {
                    super.setValueAt(Integer.parseInt((String)value), row, column);
                    quantityFields.get(row).setText((String)value);
                    return;
                }
            };

            tableModel.setColumnIdentifiers(new Object[]{
                    "Location", "Quantity in stock", "Quantity to remove"
            });
            JTable table = new JTable(tableModel);
            table.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    super.focusLost(e);
                    JTable table = (JTable)e.getSource();
                    if (table.isEditing()) {
                        table.getCellEditor().stopCellEditing();
                    }
                }
            });
//            //https://stackoverflow.com/questions/18326822/edit-a-specific-cell-in-jtable-on-enter-key-and-show-the-cursor
//            InputMap im = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//            KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
//            KeyStroke f2 = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
//            im.put(enter, im.get(f2));
//            //
            JScrollPane pane = new JScrollPane(table);
            //pane.add(table);
            JButton button = new JButton("Register");
            button.addActionListener(e1 -> {
                List<QuantityTag> toRemove = new ArrayList<>();
                DefaultTableModel model = (DefaultTableModel)(tableForRemoval.getModel());
                for (int i = 0; i < tags.size(); i++) {
                    JTextField field = (JTextField)table.getValueAt(i, 2);
                    if (!field.getText().isEmpty()) {
                        if (Integer.parseInt(field.getText()) > 0) {
                            QuantityTag tag = tags.get(i);
                            if (listToRemove.contains(tag)) {
                                tag = listToRemove.get(listToRemove.indexOf(tag));
                                tag.setQuantity(tag.getQuantity() + Integer.parseInt(field.getText()));
                                model.setValueAt(tag.getQuantity(), i, 2);
                            } else {
                                toRemove.add(new QuantityTag(tag.getItemCode(),
                                        tag.getLocation(), Integer.parseInt(field.getText())));
                                model.addRow(new Object[]{
                                        tag.getItemCode(),
                                        tag.getLocation(), field.getText()
                                });
                            }
                        }
                    }
                }
                locationDialog.setVisible(false);
                listToRemove.addAll(toRemove);

            });
            locationDialog.add(pane);
            locationDialog.add(button);
            locationDialog.setSize(600, 700);
            locationDialog.setVisible(true);
        }
    }



    //A panel that will prompt the user to enter input for new stocks
    private class AddPanel extends JPanel implements ActionListener {
        JTextField codeField = new JTextField(10);
        JTextField costField = new JTextField(10);
        JTextField bbdField = new JTextField(10);
        JTextField locationField = new JTextField(10);
        JTextField quantityField = new JTextField(10);

        //EFFECTS: create a new panel that will pop up if the user attempts to add new stocks
        private AddPanel() {
            add(new JLabel("Item code: "));
            add(codeField);
            add(new JLabel("Cost: "));
            add(costField);
            add(new JLabel("Best-before date: "));
            add(bbdField);
            add(new JLabel("Location: "));
            add(locationField);
            add(new JLabel("Quantity: "));
            add(quantityField);
            JButton button = new JButton("Register");
            button.addActionListener(this);
            add(button);
            setSize(600, 700);
        }

        //MODIFIES: this
        //EFFECTS: add a new Inventory tag and add it to the list to add
        @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
        public void actionPerformed(ActionEvent e) {
            String code = codeField.getText();
            int cost = Integer.parseInt(costField.getText());
            LocalDate bestBeforeDate;
            String stringBBD = bbdField.getText();
            if (stringBBD.isEmpty()) {
                bestBeforeDate = null;
            } else {
                bestBeforeDate = InventoryManagementSystemApplication.convertToLocalDate(stringBBD);
            }
            String location = locationField.getText();
            int qty = Integer.parseInt(quantityField.getText());
            if (qty > 0) {
                DefaultTableModel model = (DefaultTableModel)(tableForNew.getModel());
                InventoryTag tag = new InventoryTag(code, cost, bestBeforeDate, location, qty);
                if (listToAdd.contains(tag)) {
                    InventoryTag existing = listToAdd.get(listToAdd.indexOf(tag));
                    existing.setQuantity(existing.getQuantity() + qty);
                    model.setValueAt(existing.getQuantity(),listToAdd.indexOf(existing), 4);
                } else {
                    listToAdd.add(new InventoryTag(code, cost, bestBeforeDate, location, qty));
                    model.addRow(new Object[]{ code, cost,
                            (bestBeforeDate == null ? "N/A" : stringBBD), location, qty});
                }
            }
        }
    }










    //EFFECTS: create a panel for accessing inventory
    public InventoryPanel(Inventory inventory) {
        setSize(700, 800);
        this.inventory = inventory;
        listToAdd = new ArrayList<>();
        listToRemove = new ArrayList<>();
        searchPanel = new SearchPanel(inventory);
        addListPanel = createAddListPanel();
        removeListPanel = createRemoveListPanel();
        stockPanel = new StockPanel(inventory);
        add(searchPanel);
        add(addListPanel);
        add(removeListPanel);
        JButton updateButton = new JButton("Update");
        updateButton.setActionCommand(update);
        updateButton.addActionListener(this);
        add(updateButton);
        add(stockPanel);
    }

    //MODIFIES: this
    //If add button is pressed, add new products. If remove button pressed, remove products
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(add)) {
            JDialog dl = new JDialog();
            dl.add(new AddPanel());
            dl.setSize(600, 700);
            dl.setVisible(true);
            addListPanel.revalidate();
        } else if (e.getActionCommand().equals(remove)) {
            JDialog dl = new JDialog();
            dl.add(new RemovePanel());
            dl.setSize(600, 700);
            dl.setVisible(true);
            removeListPanel.revalidate();
        } else if (e.getActionCommand().equals(update)) {
            inventory.addProducts(listToAdd);
            inventory.removeProducts(listToRemove);
            DefaultTableModel tableModel = (DefaultTableModel)tableForNew.getModel();
            tableModel.setRowCount(0);
            tableModel = (DefaultTableModel)tableForRemoval.getModel();
            tableModel.setRowCount(0);
            tableForNew.revalidate();
            tableForRemoval.revalidate();
            List<String> codes = new ArrayList<>();
            for (QuantityTag tag: listToRemove) {
                String code = tag.getItemCode();
                if (!codes.contains(code)) {
                    codes.add(code);
                }
            }

            for (InventoryTag tag: listToAdd) {
                String code = tag.getItemCode();
                if (!codes.contains(code)) {
                    codes.add(code);
                }
            }
            listToAdd.clear();
            listToRemove.clear();
            stockPanel.update(codes);
            stockPanel.revalidate();
        }
    }

    //EFFECTS: create a new panel for adding new products to the inventory
    public JPanel createAddListPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Products to add"));
        JButton addButton = new JButton("Add");
        addButton.setActionCommand(add);
        addButton.addActionListener(this);
        String[] columnNames = {"Item code", "Cost", "Best-before date", "Location", "Quantity"};
        Object[][] entries = new Object[listToAdd.size()][];
        for (int i = 0; i < listToAdd.size(); i++) {
            InventoryTag tag = listToAdd.get(i);
            entries[i] = new Object[]{
                    tag.getItemCode(), tag.getPrice(),
                    (tag.getBestBeforeDate() == null ? "N/A" : tag.getBestBeforeDate()),
                    tag.getLocation(), tag.getQuantity()
            };
        }
        JTable table = new JTable(entries, columnNames);
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(20);
        }
        tableForNew = tableToAdd();
        panel.add(tableForNew);
        panel.add(addButton);
        //panel.setVisible(true);
        return panel;
    }

    //EFFECTS: create a table for a list of products to add and return it
    private JTable tableToAdd() {
        String[] columnNames = {"Item code", "Cost", "Best-before date", "Location", "Quantity"};
        Object[][] entries = new Object[listToAdd.size()][];
        for (int i = 0; i < listToAdd.size(); i++) {
            InventoryTag tag = listToAdd.get(i);
            entries[i] = new Object[]{
                    tag.getItemCode(), tag.getPrice(),
                    (tag.getBestBeforeDate() == null ? "N/A" : tag.getBestBeforeDate()),
                    tag.getLocation(), tag.getQuantity()
            };
        }
        DefaultTableModel tableModel = new DefaultTableModel(entries, columnNames);
        JTable table = new JTable(tableModel);
        for (int i = 0; i < 5; i++) {
            table.getColumnModel().getColumn(i).setWidth(20);
        }
        return table;
    }

    //EFFECTS: create a table for a list of products to remove
    private JTable tableToRemove() {
        String[] columnNames = {"Item code","Location", "Quantity"};
        Object[][] entries = new Object[listToRemove.size()][];
        for (int i = 0; i < listToRemove.size(); i++) {
            QuantityTag tag = listToRemove.get(i);
            entries[i] = new Object[]{
                    tag.getItemCode(), tag.getLocation(), tag.getQuantity()
            };
        }
        DefaultTableModel tableModel = new DefaultTableModel(entries, columnNames);
        JTable table = new JTable(tableModel);
        for (int i = 0; i < 3; i++) {
            table.getColumnModel().getColumn(i).setWidth(20);
        }
        return table;
    }


    //EFFECTS: create a new panel for removing products from the inventory
    public JPanel createRemoveListPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Products to remove"));
        //The word to add is used to indicate pressing the button will lead to adding the product to the list to remove
        JButton removeButton = new JButton("add");
        removeButton.setActionCommand(remove);
        removeButton.addActionListener(this);
        tableForRemoval = tableToRemove();
        panel.add(tableForRemoval);
        panel.add(removeButton);
        panel.setSize(600, 700);
        return panel;
    }

    //EFFECTS: create a new panel for displaying a list of stocks
    public JPanel createStockPanel() {
        JPanel panel = new JPanel();

        return panel;





    }














}
