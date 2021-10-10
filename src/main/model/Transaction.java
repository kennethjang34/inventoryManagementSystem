
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Transaction {

    protected final int transactionNumber;
    protected final LocalDate date;
    protected final String description;

    Transaction(int transactionNumber, String description, LocalDate date) {
        this.date = date;
        this.transactionNumber = transactionNumber;
        this.description = description;
    }






    public int getTransactionNumber() {
        return transactionNumber;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public abstract ArrayList<Item> getItemList();

    public abstract double getDollarAmount();
}
