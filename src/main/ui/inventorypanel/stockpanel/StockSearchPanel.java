package ui.inventorypanel.stockpanel;

import model.Inventory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockSearchPanel extends JPanel implements ActionListener {
    StockPanel stockPanel;
    Inventory inventory;
    //Base option: ALL Categories
    //Last option: Type Manually (if there is no such category, show nothing). When selected, set field visible
    JComboBox categoryBox;

    //Base option: ALL ids
    //Last option: Type Manually (if there is no such item with the id, show nothing). When selected, set field visible
    JComboBox itemBox;
    JTextField categoryField = new JTextField(10);
    JTextField itemField = new JTextField(10);
    private String selectedCategory;
    private String selectedItem;
//    private DefaultComboBoxModel<String> categoryModel;
//    private DefaultComboBoxModel<String> itemModel;
    private static final String ALL = "ALL";
    private static final String TYPE = "TYPE ID";
    private static final String categoryCommand = "CATEGORY";
    private static final String itemCommand = "ITEM";
    //EFFECTS: create a new stock search panel that can modify the given stock panel based on filters chosen by the user


    public StockSearchPanel(Inventory inventory, StockPanel stockPanel) {
        this.inventory = inventory;
        this.stockPanel = stockPanel;
        add(new JLabel("Category"));
        add(new JLabel("ID"));
        categoryBox = new JComboBox();
        itemBox = new JComboBox();
        initializeCategoryBox();
        initializeItemBox();
        initializeTextFields();
        categoryBox.setActionCommand("Category");
        itemBox.setActionCommand("Item");
        categoryBox.addActionListener(this);
        itemBox.addActionListener(this);
        add(categoryBox);
        add(categoryField);
        add(itemBox);
        add(itemField);
        categoryField.setVisible(false);
        itemField.setVisible(false);
        itemBox.setEditable(false);
        categoryBox.setEditable(false);
        stockPanel.displayAllItems();
    }


    //MODIFIES: this
    //EFFECTS: initialize category combo box with all categories inside the inventory.
    //If there is no category at all, display "No category"
    public void initializeCategoryBox() {
        DefaultComboBoxModel categoryModel = new DefaultComboBoxModel();
        if (inventory.getCategoryNames().length == 0) {
            categoryModel = new DefaultComboBoxModel<>();
            categoryModel.addElement("No category");
        } else {
            categoryModel.addElement(ALL);
            categoryModel.addElement(TYPE);
            for (String name: inventory.getCategoryNames()) {
                categoryModel.addElement(name);
            }
        }
        categoryBox.setModel(categoryModel);
    }

    //MODIFIES: this
    //EFFECTS: initialize item combo box with all categories inside the inventory.
    //If there is no category at all, display "No category"
    public void initializeItemBox() {
        DefaultComboBoxModel itemModel;
        if (inventory.getItemList().size() == 0 || selectedCategory == null
                || inventory.getItemList(selectedCategory).size() == 0) {
            itemModel = new DefaultComboBoxModel<>();
            itemModel.addElement("No item");
            return;
        } else {
            itemModel = new DefaultComboBoxModel();
            itemModel.addElement(ALL);
            itemModel.addElement(TYPE);
            for (String name: inventory.getIDs()) {
                itemModel.addElement(name);
            }
        }
        itemBox.setModel(itemModel);
    }

    //MODIFIES: this
    //EFFECTS: initialize both category and item text field.
    public void initializeTextFields() {
        categoryField.addActionListener(e -> {
            DefaultComboBoxModel model = (DefaultComboBoxModel) categoryBox.getModel();
            int index = model.getIndexOf(categoryField.getText());
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "There is no such category!");
            } else {
                categoryBox.setSelectedIndex(index);
                categoryField.setVisible(false);
            }
            categoryField.removeAll();

        });

        itemField.addActionListener(e -> {
            DefaultComboBoxModel model = (DefaultComboBoxModel) itemBox.getModel();
            int index = model.getIndexOf(itemField.getText());
            if (index == -1) {
                JOptionPane.showMessageDialog(null, "There is no such item!");
            } else {
                itemBox.setSelectedIndex(index);
                itemField.setVisible(false);
            }
            itemField.removeAll();
            repaint();
            //hide the text field again and find the specified element in the comboBox
        });
    }




    //MODIFIES: this
    //EFFECTS: for each stock search filter, update the search panel properly
    //If type manually option is chosen, show text fields
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:SuppressWarnings"})
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox)e.getSource();
        if (comboBox.getActionCommand().equalsIgnoreCase(categoryCommand)) {
            selectedCategory = (String) categoryBox.getSelectedItem();
            if (selectedCategory.equals(TYPE)) {
                promptCategoryTyping();
                return;
            } else if (selectedCategory.equals(ALL)) {
                stockPanel.displayAllItems();
            } else {
                updateItemComboBox(selectedCategory);
                stockPanel.displayItems(selectedCategory);
            }
            categoryField.setVisible(false);
        } else {
            selectedItem = (String)itemBox.getSelectedItem();
            if (selectedItem.equals(TYPE)) {
                promptItemTyping();
                return;
            } else if (selectedItem.equals(ALL)) {
                stockPanel.displayAllItems();
            } else {
                stockPanel.displayItem(selectedItem);
            }
            itemField.setVisible(false);
        }
    }

    //MODIFIES: this
    //EFFECTS: set category text field visible so that the user can type a category name
    public void promptCategoryTyping() {
        categoryField.setVisible(true);
        repaint();
        revalidate();
    }

    //MODIFIES: this
    //EFFECTS: set item text field visible so that the user can type a item id
    public void promptItemTyping() {
        itemField.setVisible(true);
        repaint();
        revalidate();
    }

    //MODIFIES: this
    //EFFECTS: update the item combo box so that it matches the current selected category
    private void updateItemComboBox(String selectedCategory) {
        DefaultComboBoxModel itemModel;
        itemModel = new DefaultComboBoxModel();
        itemModel.addElement(ALL);
        itemModel.addElement(TYPE);
        if (selectedCategory.equals(ALL)) {
            List<String> items = inventory.getIDs();
            for (String item: items) {
                itemModel.addElement(item);
            }
        } else {
            List<String> items = inventory.getIDs(selectedCategory);
            for (String item: items) {
                itemModel.addElement(item);
            }
        }
        itemBox.setModel(itemModel);
        itemBox.setSelectedIndex(0);
        repaint();
    }

    //MODIFIES: this
    //EFFECTS: add a new category to the category combo box
    public void addCategory(String name) {
        DefaultComboBoxModel boxModel = (DefaultComboBoxModel)categoryBox.getModel();
        //When box model size is 1, it implies the only content is "No category"
        if (boxModel.getSize() == 1) {
            categoryBox.setModel(new DefaultComboBoxModel());
            categoryBox.addItem(ALL);
            categoryBox.addItem(TYPE);
            categoryBox.addItem(name);
            stockPanel.displayAllItems();
            return;
        } else if (boxModel.getIndexOf(name) == -1) {
            categoryBox.addItem(name);
            return;
        }
    }

    //MODIFIES: this
    //EFFECTS: add a new item to the item combo box if the currently selected category contains this new item
    //else do nothing
    public void addItem(String id) {
        if (inventory.getCategoryOf(id).equalsIgnoreCase(selectedCategory)
                || selectedCategory.equals(ALL)) {
            DefaultComboBoxModel boxModel = (DefaultComboBoxModel)itemBox.getModel();
            if (boxModel.getIndexOf(id) == -1) {
                itemBox.addItem(id);
                stockPanel.displayAllItems();
            }
        }
    }
}
