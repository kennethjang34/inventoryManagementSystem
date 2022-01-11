package ui.inventorypanel.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

//A panel that prompts the user to enter inputs for creating new products
public class AddPanel extends JPanel {
    //JTextField codeField = new JTextField(10);
    private JTextField costField = new JTextField(10);
    private JTextField bbdField = new JTextField(10);
    private JTextField priceField = new JTextField(10);
    private JTextField idField = new JTextField(10);
    private JTextField locationField = new JTextField(10);
    private JTextField quantityField = new JTextField(10);
    private JTextField descriptionField = new JTextField(10);
    private JButton button;
    private List<JTextField> textFields = new ArrayList<>(7);


    private static JDialog dialog;

    public AddPanel(InventoryViewPanel viewPanel) {
        add(new JLabel("ID"));
        add(idField);
//        add(new JLabel("SKU: "));
//        add(codeField);
        add(new JLabel("Cost: "));
        add(costField);
        add(new JLabel("Price: "));
        add(priceField);
        add(new JLabel("Best-before date: "));
        add(bbdField);
        add(new JLabel("Location: "));
        add(locationField);
        add(new JLabel("Quantity: "));
        add(quantityField);
        add(new JLabel("Description: "));
        add(descriptionField);
        this.button = new JButton();
        button.setText("Register");
//        button.addActionListener(this);
        add(button);
        setSize(600, 700);
        textFields.add(costField);
        textFields.add(bbdField);
        textFields.add(priceField);
        textFields.add(idField);
        textFields.add(locationField);
        textFields.add(quantityField);
        textFields.add(descriptionField);

        //
        for (JTextField textField: textFields) {
            textField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComponent component = (JComponent) e.getSource();
                    component.transferFocus();
                }
            });
        }

        button.addKeyListener(InventoryViewPanel.getButtonEnterKeyListener());

        button.setAction(new AbstractAction("Register") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //needs to convert it to uppercase in controller
                String id = idField.getText();
                String costInput = costField.getText();
                String priceInput = priceField.getText();
                String bestBeforeDateText = bbdField.getText();
                String locationInput = locationField.getText();
                String qtyInput = quantityField.getText();
                String description = descriptionField.getText();
                viewPanel.getController().productsAdditionRequest(id, costInput, priceInput, bestBeforeDateText,
                        locationInput, qtyInput, description);
            }
        });
    }


    public void clearFields() {
        for (JTextField textField: textFields) {
            textField.setText("");
        }
    }

    public void displayAdditionDialog(Component parentComponent) {
        dialog = new JDialog();
        dialog.add(this);
        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }




}