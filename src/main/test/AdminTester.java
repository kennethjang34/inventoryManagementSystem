package test;

import model.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTester {
    private Admin admin;
    private LocalDate today;
    private int personalNum;

    @BeforeEach
    void runBefore() {
        admin = new Admin();
        today = LocalDate.now();
        personalNum = 111111;
    }

    @Test
    void testConstructor() {
        admin = new Admin();
        assertEquals(false,admin.checkLoginAccount("abc", "ddd"));
    }

    @Test
    void testCreateLoginAccount() {
        String id = "thigh";
        String pw = "drumstick";
        String name = "chicken";
        LocalDate birthDay = LocalDate.of(2021, 07, 22);
        personalNum = 456789556;
        admin.createLoginAccount(id, pw, name, birthDay, personalNum);
        assertTrue(admin.checkLoginAccount(id, pw));
    }

    @Test
    void testCheckLoginAccount() {
        String id = "thigh";
        String pw = "drumstick";
        String name = "chicken";
        LocalDate birthDay = LocalDate.of(2021, 07, 22);
        int personalNum = 456789556;
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount(pw, id));
        id = "Italian";
        pw = "Pig";
        name = "belly";
        birthDay = LocalDate.of(2021, 10, 10);
        personalNum = 444444412;
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount("italian", "Pig"));
        assertFalse(admin.checkLoginAccount("Italian", "pig"));
        assertFalse(admin.checkLoginAccount("aadsfk", "Fas"));
    }

    @Test
    void testRetrievePassword() {
        String id = "aaa";
        String pw = "bbb";
        String name = "aba";
        admin.createLoginAccount(id, pw, name, today, personalNum);
        assertTrue(admin.checkLoginAccount(id, pw));
        String retrieved = admin.retrievePassword(id, "aba", today, personalNum);
        assertEquals(pw, retrieved);
        assertTrue(admin.checkLoginAccount(id, retrieved));
    }
}
