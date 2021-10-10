package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Admin {

    private class LoginAccount {

        private String id;
        private String pw;
        private String name;
        private LocalDate birthDay;
        private int personalCode;

        private LoginAccount(String id, String password, String name, LocalDate birthDay, int personalCode) {
            this.id = id;
            this.pw = password;
            this.name = name;
            this.birthDay = birthDay;
            this.personalCode = personalCode;
        }

        private String getPassword() {
            return pw;
        }

        private LocalDate getBirthDay() {
            return birthDay;
        }

        private int getPersonalCode() {
            return personalCode;
        }

        private String getId() {
            return id;
        }

        private String getName() {
            return name;
        }

        private boolean passwordMatch(String pw) {
            if (this.pw == pw) {
                return true;
            }
            return false;
        }
    }

    private final ArrayList<LoginAccount> accounts;

    public Admin() {
        accounts = new ArrayList<>();
    }

    public String passwordForgotten(String id,  String name, LocalDate birthDay) {
        LoginAccount account = getLoginAccount(id);
        if (account != null) {
            if ((account.getName() == name) && (account.getBirthDay().toString() == birthDay.toString())) {
                return account.getPassword();
            }
            return null;
        }
        return null;
    }

    private LoginAccount getLoginAccount(String id) {
        for (LoginAccount account: accounts) {
            if (account.getId() == id) {
                return account;
            }
        }
        return null;
    }


    public boolean checkLoginAccount(String id, String pw) {
        LoginAccount account = getLoginAccount(id);

        if (account != null) {
            if (account.passwordMatch(pw)) {
                return true;
            }
        }
        return false;
    }

}
