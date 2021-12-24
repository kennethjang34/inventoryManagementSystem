package model;

import com.sun.xml.internal.bind.v2.model.core.ID;
import ui.table.TableEntryConvertibleDataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static model.Account.DataList.DATE;
import static model.Account.DataList.valueOf;

//Each recordedDate will have a list of accounts that occurred on the day this represents
public class RecordedDate extends TableEntryConvertibleDataFactory {
    LocalDate date;
    List<Account> accounts;


    public enum DataList {
        DATE, ID, QUANTITY
    }

    public RecordedDate(LocalDate date) {
        super(new String[]{
            DataList.DATE.toString(), DataList.ID.toString(), DataList.QUANTITY.toString()
        });
        this.date = date;
        accounts = new LinkedList<>();
    }

    public RecordedDate(LocalDate date, List<Account> accounts) {
        super(new String[]{
                DataList.DATE.toString(), DataList.ID.toString(), DataList.QUANTITY.toString()
        });
        this.date = date;
        this.accounts = accounts;
    }

    public List<Account> getAccounts() {
        return null;
    }

    public List<Account> getAccounts(String id) {
        return null;
    }

    public void addAccount(Account account) {
        //
    }

    public List<String> getIDList() {
        return null;
    }

    public int getQuantity() {
        return 0;
    }

    @Override
    public Object[] convertToTableEntry() {
        Object[] row = new Object[1 + getIDList().size()];
        for(int i = 0; i < row.length; i++) {
            row[i] = getIDList().get(i);
        }
        return row;
    }

    @Override
    public Object[] getDataList() {
        return DataList.values();
    }

    @Override
    public List<String> getContentsOf(String property) {
        List<String> contents = new LinkedList<>();
        switch (DataList.valueOf(property)) {
            case DATE:
                contents.add(date.toString());
                break;
            case ID:
                contents.addAll(getIDList());
                break;
            case QUANTITY:
                contents.add(String.valueOf(getQuantity()));
                break;
        }
        return contents;
    }

    @Override
    public List<ViewableTableEntryConvertibleModel> getEntryModels() {
        return new LinkedList<>(getAccounts());
    }
}
