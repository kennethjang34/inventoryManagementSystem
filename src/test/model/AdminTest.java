package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {
    private Admin admin;
    private LocalDate today;
    private int personalNum;
    String id = "TestId";
    String pw = "TestPw";
    String name = "Tester";
    LocalDate birthday = LocalDate.now();

    @BeforeEach
    void runBefore() {
        admin = new Admin();
        today = LocalDate.now();
        personalNum = 111111;
    }

    @Test
    void testConstructor() {
        admin = new Admin();
        assertTrue(admin.isEmpty());
        assertFalse(admin.checkLoginAccount("abc", "ddd"));
    }

    @Test
    void testCreateLoginAccount() {
        String id = "thigh";
        String pw = "drumstick";
        String name = "chicken";
        LocalDate birthDay = LocalDate.of(2021, 7, 22);
        personalNum = 456789556;
        admin.createLoginAccount(id, pw, name, birthDay, personalNum);
        assertTrue(admin.checkLoginAccount(id, pw));
    }

    @Test
    void testCreateLoginAccountWithExistingId() {
        String id = "thigh";
        String pw = "drumstick";
        String name = "chicken";
        LocalDate birthDay = LocalDate.of(2021, 7, 22);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        id = "thigh";
        pw = "wings";
        name = "duck";
        birthDay = LocalDate.of(2021, 8, 4);
//        assertEquals(admin.(id), admin.createLoginAccount(id, pw, name, birthDay, personalNum++));
        assertFalse(admin.checkLoginAccount(id, pw));
    }

    @Test
    void testCheckLoginAccount() {
        String id = "thigh";
        String pw = "drumstick";
        String name = "chicken";
        LocalDate birthDay = LocalDate.of(2021, 7, 22);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount(pw, id));
        id = "Italian";
        pw = "Pig";
        name = "belly";
        birthDay = LocalDate.of(2021, 10, 10);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount("italian", "Pig"));
        assertFalse(admin.checkLoginAccount("Italian", "pig"));
        assertFalse(admin.checkLoginAccount("ads", "Fas"));
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

    @Test
    void testRetrievePasswordWithWrongInfo() {
        String id = "aaa";
        String pw = "bbb";
        String name = "ccc";
        admin.createLoginAccount(id, pw, name, today, personalNum);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertNull(admin.retrievePassword(name, id, today, personalNum));
        assertNull(admin.retrievePassword(id, id, today, personalNum));
        assertNull(admin.retrievePassword(id, name, LocalDate.of(2011, 1, 5), personalNum));
        assertNull(admin.retrievePassword(id, name, today, personalNum+10));
    }

    @Test
    void testJsonConversion() {
        String id = "thigh";
        String pw = "drumstick";
        String name = "chicken";
        LocalDate birthDay = LocalDate.of(2021, 7, 22);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount(pw, id));
        id = "Italian";
        pw = "Pig";
        name = "belly";
        birthDay = LocalDate.of(2021, 10, 10);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount("italian", "Pig"));
        assertFalse(admin.checkLoginAccount("Italian", "pig"));
        assertFalse(admin.checkLoginAccount("ads", "Fas"));
        admin = new Admin(admin.toJson());
        id = "thigh";
        pw = "drumstick";
        name = "chicken";
        birthDay = LocalDate.of(2021, 7, 22);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount(pw, id));
        id = "Italian";
        pw = "Pig";
        name = "belly";
        birthDay = LocalDate.of(2021, 10, 10);
        admin.createLoginAccount(id, pw, name, birthDay, personalNum++);
        assertTrue(admin.checkLoginAccount(id, pw));
        assertFalse(admin.checkLoginAccount("italian", "Pig"));
        assertFalse(admin.checkLoginAccount("Italian", "pig"));
        assertFalse(admin.checkLoginAccount("ads", "Fas"));
    }

    @Test
    void testIsAdmin() {
        admin.createLoginAccount(id, pw, name, birthday, personalNum, true);
        assertTrue(admin.isAdminMember(id));
        admin.createLoginAccount("abcd", pw, name, birthday, personalNum, false);
        assertFalse(admin.isAdminMember("abcd"));
    }
}
