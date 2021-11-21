package ui.inventorypanel.stockpanel;

import model.Inventory;
import model.Observer;
import model.Subject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StockSearchTool extends Subject implements ActionListener, Observer {
//    StockPanel stockPanel;
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
    private JPanel panel;
    public static final String ALL = "ALL";
    public static final String TYPE = "TYPE ID";
    private static final String categoryCommand = "CATEGORY";
    private static final String itemCommand = "ITEM";
    private static final String NO_ITEM = "No Item";
    private static final String NO_CATEGORY = "No Category";

    //EFFECTS: create a new stock search panel that can modify the given stock panel based on filters chosen by the user
    public StockSearchTool(Inventory inventory) {
        panel = new JPanel();
        this.inventory = inventory;
        inventory.registerObserver(this);
        panel.add(new JLabel("Category"));
        categoryBox = new JComboBox();
        itemBox = new JComboBox();
        updateCategoryBox();
        initializeItemBox();
        initializeTextFields();
        categoryBox.setActionCommand("Category");
        itemBox.setActionCommand("Item");
        categoryBox.addActionListener(this);
        itemBox.addActionListener(this);
        panel.add(categoryBox);
        panel.add(categoryField);
        panel.add(new JLabel("ID"));
        panel.add(itemBox);
        panel.add(itemField);
        categoryField.setVisible(false);
        itemField.setVisible(false);
        itemBox.setEditable(false);
        categoryBox.setEditable(false);
    }


    //MODIFIES: this
    //EFFECTS: initialize category combo box with all categories inside the inventory.
    //If there is no category at all, display "No category"
    public void updateCategoryBox() {
        DefaultComboBoxModel categoryModel = new DefaultComboBoxModel();
        if (inventory.getCategoryNames().length == 0) {
            categoryModel = new DefaultComboBoxModel<>();
            categoryModel.addElement(NO_CATEGORY);
        } else {
            categoryModel.addElement(ALL);
            categoryModel.addElement(TYPE);
            for (String name: inventory.getCategoryNames()) {
                categoryModel.addElement(name);
            }
            selectedCategory = ALL;
        }
        categoryBox.setModel(categoryModel);
    }

    //Will be deprecated
    //MODIFIES: this
    //EFFECTS: initialize item combo box with all categories inside the inventory.
    //If there is no category at all, display "No category"
    public void initializeItemBox() {
        DefaultComboBoxModel itemModel = new DefaultComboBoxModel<>();
        if (selectedCategory != null) {
            if (inventory.getItemList().size() != 0) {
                if (selectedCategory.equals(ALL)) {
                    itemModel.addElement(ALL);
                    itemModel.addElement(TYPE);
                    for (String name: inventory.getIDs()) {
                        itemModel.addElement(name);
                    }
                } else if (inventory.getIDs(selectedCategory).size() != 0) {
                    itemModel.addElement(ALL);
                    itemModel.addElement(TYPE);
                    for (String name: inventory.getIDs(selectedCategory)) {
                        itemModel.addElement(name);
                    }
                }
            }
        }
        if (itemModel.getSize() == 0) {
            itemModel.addElement(NO_ITEM);
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
            panel.repaint();
            //hide the text field again and find the specified element in the comboBox
        });
    }




    //MODIFIES: this
    //EFFECTS: for each stock search filter, update the search panel properly
    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox)e.getSource();
        if (comboBox.getActionCommand().equalsIgnoreCase(categoryCommand)) {
            handleCategorySelectedEvent();
        } else {
            handleIdSelectedEvent();
        }
    }

    //MODIFIES: this
    //EFFECTS: handle the request job by the user for category by displaying the corresponding component on this
    //If type manually option is chosen, show text fields
    private void handleCategorySelectedEvent() {
        selectedCategory = (String) categoryBox.getSelectedItem();
        if (selectedCategory.equals(TYPE)) {
            promptCategoryTyping();
            return;
        } else if (selectedCategory.equals(ALL)) {
            notifyObservers();
        } else {
            updateItemComboBox();
            notifyObservers();
        }
        categoryField.setVisible(false);
    }

    //MODIFIES: this
    //EFFECTS: handle the request job by the user by displaying the corresponding component on this
    //If type manually option is chosen, show text fields
    private void handleIdSelectedEvent() {
        selectedItem = (String)itemBox.getSelectedItem();
        if (selectedItem.equals(TYPE)) {
            promptItemTyping();
            return;
        } else if (selectedItem.equals(ALL)) {
            notifyObservers();
        } else {
            notifyObservers();
        }
        itemField.setVisible(false);
    }


    //EFFECTS: return the selected item of item combo box
    public String getSelectedID() {
        return (String)itemBox.getSelectedItem();
    }

    //EFFECTS: return the selected item of category combo box
    public String getSelectedCategory() {
        return (String)categoryBox.getSelectedItem();
    }

    //MODIFIES: this
    //EFFECTS: set category text field visible so that the user can type a category name
    public void promptCategoryTyping() {
        categoryField.setVisible(true);
        panel.repaint();
        panel.revalidate();
    }

    //MODIFIES: this
    //EFFECTS: set item text field visible so that the user can type a item id
    public void promptItemTyping() {
        itemField.setVisible(true);
        panel.repaint();
        panel.revalidate();
    }

    //MODIFIES: this
    //EFFECTS: update the item combo box so that it matches the current selected category
    private void updateItemComboBox() {
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
        panel.repaint();
    }

    //MODIFIES: this
    //EFFECTS: add a new category to the category combo box
    public void addCategory(String name) {
        DefaultComboBoxModel boxModel = (DefaultComboBoxModel)categoryBox.getModel();
        if (boxModel.getIndexOf(NO_CATEGORY) != -1) {
            categoryBox.setModel(new DefaultComboBoxModel());
            categoryBox.addItem(ALL);
            categoryBox.addItem(TYPE);
            categoryBox.addItem(name);
            handleCategorySelectedEvent();
            notifyObservers();
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
        if (inventory.getCategoryOf(id).equals(selectedCategory)
                || selectedCategory.equals(ALL)) {
            if (itemBox.getItemAt(0).equals(NO_ITEM)) {
                updateItemComboBox();
            }
            DefaultComboBoxModel boxModel = (DefaultComboBoxModel)itemBox.getModel();
            if (boxModel.getIndexOf(id) == -1) {
                itemBox.addItem(id);
                notifyObservers();
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: update category box, item box
    @Override
    public void update() {
        updateCategoryBox();
        updateItemComboBox();
    }

    @Override
    public void notifyObservers() {
        for (Observer observer: observers) {
            observer.update();
        }
    }

    //EFFECTS: return the panel displaying this
    public JPanel getPanel() {
        return panel;
    }
}
