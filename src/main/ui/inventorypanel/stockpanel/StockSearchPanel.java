package ui.inventorypanel.stockpanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StockSearchPanel extends JPanel implements ActionListener {
    StockPanel stockPanel;

    //Base option: ALL Categories
    //Last option: Type Manually (if there is no such category, show nothing). When selected, set field visible
    JComboBox categoryBox = new JComboBox();

    //Base option: ALL ids
    //Last option: Type Manually (if there is no such item with the id, show nothing). When selected, set field visible
    JComboBox idBox = new JComboBox();
    JTextField categoryField = new JTextField();
    JTextField idField = new JTextField();


    //EFFECTS: create a new stock search panel that can modify the given stock panel based on filters chosen by the user
    public StockSearchPanel(StockPanel stockPanel) {
        this.stockPanel = stockPanel;
        add(new JLabel("Category"));
        add(new JLabel("ID"));
        add(categoryBox);
        add(categoryField);
        add(idBox);
        add(idField);
        categoryField.setVisible(false);
        idField.setVisible(false);
    }





    //MODIFIES: this
    //EFFECTS: for each stock search filter, update the search panel properly
    //If type manually option is chosen, show text fields
    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
