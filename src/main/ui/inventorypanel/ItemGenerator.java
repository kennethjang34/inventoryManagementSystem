package ui.inventorypanel;

import ui.inventorypanel.view.InventoryViewPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

//represents a panel that can generate items
public class ItemGenerator extends JPanel {
    private JTextField idField = new JTextField(10);
    private JTextField nameField = new JTextField(10);
    private JTextField categoryField = new JTextField(10);
    private JTextField priceField = new JTextField(10);
    private JTextField description = new JTextField(10);
    private JTextField note = new JTextField(10);
    private JButton button = new JButton("Create");
    private List<JTextField> textFields = new ArrayList<>();

    //EFFECTS: create a new panel that generates items
    public ItemGenerator(InventoryViewPanel viewPanel) {
        JPanel fieldPanel = new JPanel();
        initializeFieldPanel(fieldPanel);
        //button.addActionListener(stockPanel);
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 3;
        add(fieldPanel, gc);
        gc.gridx = 0;
        gc.gridy = 1;
        add(button, gc);
        textFields.add(idField);
        textFields.add(nameField);
        textFields.add(categoryField);
        textFields.add(priceField);
        textFields.add(description);
        textFields.add(note);
        for (JTextField field: textFields) {
            field.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComponent component = (JComponent) e.getSource();
                    component.transferFocus();
                }
            });
        }

        button.setAction(new AbstractAction("Create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewPanel.getController().itemGeneratorButtonClicked(idField.getText(), nameField.getText(),
                        categoryField.getText(), priceField.getText(), description.getText(), note.getText());
            }
        });


    }


    public JButton getButton() {
        return button;
    }

    //MODIFIES: this
    //EFFECTS: initialize the field panel
    private void initializeFieldPanel(JPanel fieldPanel) {
        fieldPanel.setLayout(new GridLayout(6, 2));
        fieldPanel.add(new JLabel("ID: "));
        fieldPanel.add(idField);
        fieldPanel.add(new JLabel("NAME: "));
        fieldPanel.add(nameField);
        fieldPanel.add(new JLabel("CATEGORY: "));
        fieldPanel.add(categoryField);
        fieldPanel.add(new JLabel("LIST PRICE: "));
        fieldPanel.add(priceField);
        fieldPanel.add(new JLabel("DESCRIPTION: "));
        fieldPanel.add(description);
        fieldPanel.add(new JLabel("NOTE: "));
        fieldPanel.add(note);
    }

    //MODIFIES: this
    //EFFECTS: clear all fields of field panel
    public void clearFields() {
        idField.setText("");
        categoryField.setText("");
        description.setText("");
        note. setText("");
        priceField.setText("");
        nameField.setText("");
    }

    public String getPriceFieldValue() {
        return priceField.getText();
    }

    public String getIDFieldValue() {
        return idField.getText();
    }

    public String getNameFieldValue() {
        return nameField.getText();
    }

    public String getCategoryFieldValue() {
        return categoryField.getText();
    }

    public String getDescriptionFieldValue() {
        return description.getText();
    }

    public String getNoteFieldValue() {
        return note.getText();
    }

}
