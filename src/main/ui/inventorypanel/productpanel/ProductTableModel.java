package ui.inventorypanel.productpanel;

import model.Product;

import javax.swing.table.AbstractTableModel;
import java.util.List;

//represents a table model that has products' info in its cells
public class ProductTableModel extends AbstractTableModel {
    String[] columnNames;
    List<Product> products;

    //product list given is shared by the product panel as well
    public ProductTableModel(List<Product> products, String[] columnNames) {
        this.columnNames = columnNames;
        this.products = products;
    }

    //EFFECTS: return the row size
    @Override
    public int getRowCount() {
        return products.size();
    }

    //EFFECTS: return the column size
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    //EFFECTS: return the name of the column
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    //EFFECTS: return the value at the specified table cell
    @Override
    public Object getValueAt(int row, int column) {
        Product product = products.get(row);
        switch (getColumnName(column)) {
            case "ID":
                return product.getID();
            case "SKU":
                return product.getSku();
            case "COST":
                return product.getCost();
            case "PRICE":
                return product.getPrice();
            case "BEST-BEFORE DATE":
                return product.getBestBeforeDate();
            case "DATE GENERATED":
                return product.getDateGenerated();
            case "LOCATION":
                return product.getLocation();
        }
        return null;
    }
}
