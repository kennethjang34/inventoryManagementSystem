package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {
    int code = 111111111;
    double cost = 111.11;
    String description = "accountCreatedForTest";
    LocalDate today = LocalDate.now();
    ArrayList<Object[]> itemList;


    @BeforeEach
    void runBeforeEach() {
        itemList = new ArrayList<>();
        Object[] entry = new Object[3];
        entry[0] = "SAD";
        entry[1] = cost;
        entry[2] = 250;
        itemList.add(entry);
        entry = new Object[3];
        entry[0] = "ADS";
        entry[1] = cost * 2;
        entry[2] = 666;
        itemList.add(entry);
        entry = new Object[3];
        entry[0] = "STR";
        entry[1] = cost * 3;
        entry[2] = 1;
        itemList.add(entry);
        entry = new Object[3];
        entry[0] = "BNN";
        entry[1] = cost * 5;
        entry[2] = 10;
        itemList.add(entry);
    }

    @Test
    void testConstructor() {
        Account account = new Account(code, description, today, itemList);
        assertEquals(code, account.getCode());
        assertEquals(250 + 666 + 1 + 10, account.getTotalQuantity());
        assertEquals(today.toString(), account.getDate().toString());
        assertEquals(1, account.getQuantity("STR"));
        assertEquals(10, account.getQuantity("BNN"));
        assertEquals(cost * 5, account.getPrice("BNN"));
        assertEquals(cost * 2, account.getPrice("ADS"));
        assertEquals(cost * 5 * 10, account.getTotalCost("BNN"));
        assertEquals(cost * 250, account.getTotalCost("SAD"));
        assertEquals(cost * 2 * 666, account.getTotalCost("ADS"));
        assertEquals("accountCreatedForTest", account.getDescription());
        assertEquals(250 * cost + 666 * cost * 2 + 1 * cost * 3 + 10 * cost * 5,
                account.getDollarAmount());
        ArrayList<Object[]> copyList = account.getEntries();
        Account newAccount = new Account(code+1, "this is an account with a copied entries", today, copyList);
        assertEquals(code + 1, newAccount.getCode());
        assertEquals(250 + 666 + 1 + 10, newAccount.getTotalQuantity());
        assertEquals(today.toString(), newAccount.getDate().toString());
        assertEquals(1, newAccount.getQuantity("STR"));
        assertEquals(10, newAccount.getQuantity("BNN"));
        assertEquals(cost * 5, newAccount.getPrice("BNN"));
        assertEquals(cost * 2, newAccount.getPrice("ADS"));
        assertEquals(cost * 5 * 10, newAccount.getTotalCost("BNN"));
        assertEquals(cost * 250, newAccount.getTotalCost("SAD"));
        assertEquals(cost * 2 * 666, newAccount.getTotalCost("ADS"));
        assertEquals("this is an account with a copied entries", newAccount.getDescription());
        assertEquals(250 * cost + 666 * cost * 2 + 1 * cost * 3 + 10 * cost * 5,
                newAccount.getDollarAmount());
    }




}
