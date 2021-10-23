package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;

import java.time.LocalDate;
import java.util.ArrayList;

//represents administration that have their own accounts
//will contain a list of login accounts.
//Written to provide a functionality for Inventory management system application.
public class Admin implements JsonConvertible {

    //represents each individual login account.
    //Must be distinguished from accounts that contain information about inventory update.
    private static class LoginAccount implements JsonConvertible {

        //Once a login account is created, no data can be changed.
        private final String id;
        private final String pw;
        private final String name;
        private final LocalDate birthday;
        private final int personalCode;

        //EFFECTS: create a new account and return it.
        private LoginAccount(String id, String password, String name, LocalDate birthDay, int personalCode) {
            this.id = id;
            this.pw = password;
            this.name = name;
            this.birthday = birthDay;
            this.personalCode = personalCode;
        }

        //REQUIRES: data in json format must contain all the fields of this class with matching name.
        //EFFECTS: create a new account with data in json format
        private LoginAccount(JSONObject jsonLoginAccount) {
            id = jsonLoginAccount.getString("id");
            pw = jsonLoginAccount.getString("pw");
            name = jsonLoginAccount.getString("name");
            personalCode = jsonLoginAccount.getInt("personalCode");
            JSONObject jsonDate = jsonLoginAccount.getJSONObject("birthday");
            birthday = LocalDate.of(jsonDate.getInt("year"),
                    jsonDate.getInt("month"), jsonDate.getInt("day"));
        }

        //EFFECTS: return password
        private String getPassword() {
            return pw;
        }

        //EFFECTS: return birthday
        private LocalDate getBirthday() {
            return birthday;
        }

        //EFFECTS: return personal code
        private int getPersonalCode() {
            return personalCode;
        }


        //EFFECTS: return id
        private String getId() {
            return id;
        }

        //EFFECTS: return name
        private String getName() {
            return name;
        }

        //EFFECTS: return true if the password matches the info, false otherwise.
        private boolean passwordMatch(String pw) {
            return this.pw.equals(pw);
        }

        //EFFECTS: convert this to JSONObject and return it.
        @Override
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("id", id);
            json.put("pw", pw);
            json.put("name", name);
            JSONObject date = new JSONObject();
            date.put("year", birthday.getYear());
            date.put("month", birthday.getMonthValue());
            date.put("day", birthday.getDayOfMonth());
            json.put("birthday", date);
            json.put("personalCode", personalCode);
            return json;
        }
    }

    //list of login accounts. there is no limit on number of accounts but once an account is added,
    //it cannot be removed from the list. In any circumstance, this list shouldn't be passed to the user.
    private final ArrayList<LoginAccount> accounts;


    //EFFECTS: create a new administer.
    public Admin() {
        accounts = new ArrayList<>();
    }

    //REQUIRES: JSONObject must have all information needed to build admin with the matching name
    //EFFECTS: create a new admin with data in JSON format
    public Admin(JSONObject jsonAdmin) {
        accounts = new ArrayList<>();
        JSONArray jsonAccounts = jsonAdmin.getJSONArray("accounts");
        jsonAccounts.forEach(json -> accounts.add(new LoginAccount((JSONObject)json)));
    }

    //EFFECTS: if a login account can be found with the information given, return password of the account
    //return null otherwise.
    //When input data are compared to the existing accounts' data, it will be checked case-sensitively
    //For example, if name was set to "Pizza Chicken", neither "Pizza chicken" nor "pizza Chicken" will be matched
    //with the name successfully. "pizza chicken" will be the wrong input as well.
    //return null otherwise.
    public String retrievePassword(String id, String name, LocalDate birthDay, int personalNum) {
        LoginAccount account = getLoginAccount(id);
        String pw = null;
        if (account != null) {
            if ((account.getName().equalsIgnoreCase(name))
                    && (account.getBirthday().toString().equalsIgnoreCase(birthDay.toString()))
                    && account.getPersonalCode() == personalNum) {
                pw = account.getPassword();
            }
        }
        return pw;
    }


    //EFFECTS: return login account matching id if there exists such an account (case-sensitive).
    //return null otherwise.
    private LoginAccount getLoginAccount(String id) {
        for (LoginAccount account: accounts) {
            if (account.getId().equals(id)) {
                return account;
            }
        }
        return null;
    }

    //EFFECTS: return true if the id exists and pw matches password of id (case-sensitive)
    //return false otherwise.
    public boolean checkLoginAccount(String id, String pw) {
        LoginAccount account = getLoginAccount(id);
        if (account != null) {
            return account.passwordMatch(pw);
        }
        return false;
    }

    //REQUIRES: personal code must be a positive integer. No existing login account can have the same id
    //if the id has the same alphabetical arrangement as one of the existing ids, but in different cases like
    //Apple and apple, they will be regarded different
    //MODIFIES: this
    //EFFECTS: create a new account with the given information.
    public boolean createLoginAccount(String id, String password, String name, LocalDate birthDay, int personalCode) {
        if (getLoginAccount(id) != null) {
            return false;
        }
        accounts.add(new LoginAccount(id, password, name, birthDay, personalCode));
        return true;
    }

    //EFFECTS: convert this to JSONObject and return it.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("accounts", convertToJsonArray(accounts));
        return json;
    }
}
