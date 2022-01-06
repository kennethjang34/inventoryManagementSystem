package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.JsonConvertible;
import ui.InventoryManagementSystemApplication;
import ui.Viewable;
import ui.table.AbstractTableDataFactory;
import ui.table.ViewableTableEntryConvertibleModel;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//represents administration that have their own accounts
//will contain a list of login accounts.
//Written to provide a functionality for Inventory management system application.
public class Admin extends AbstractTableDataFactory implements JsonConvertible {


    public static final String LOGIN = "LOGIN" ;
    public static final String ACCOUNT = "ACCOUNT";
    //    public static final int ADMIN_ACCESS = -1;
//    public static final int INVENTORY_ACCESS = 1;
    private static Admin admin = new Admin();

    //list of login accounts. there is no limit on number of accounts but once an account is added,
    //it cannot be removed from the list. In any circumstance, this list shouldn't be passed to the user.
    private final ArrayList<LoginAccount> accounts;
    private LoginAccount currentAccount;

    public enum DataList{
        ID, PW, NAME, BIRTHDAY, PERSONAL_CODE, IS_ADMIN
    }


    @Override
    public Object[] getDataList() {
        return new String[]{
                DataList.ID.toString(), DataList.PW.toString(),
                DataList.NAME.toString(), DataList.BIRTHDAY.toString(), DataList.PERSONAL_CODE.toString(),
                DataList.IS_ADMIN.toString()
        };
    }

    @Override
    public List<Object> getContentsOf(String property) {
        switch (property) {
            case ACCOUNT:
                return new ArrayList<>(accounts);

        }

        return null;
    }


    @Override
    public List<? extends ViewableTableEntryConvertibleModel> getEntryModels() {
        return accounts;
    }

    //represents each individual login account.
    //Must be distinguished from accounts that contain information about inventory update.
    public static class LoginAccount extends ViewableTableEntryConvertibleModel implements JsonConvertible {

        //Once a login account is created, no data can be changed.
        private final String id;
        private final String pw;
        private final String name;
        private final LocalDate birthday;
        private final int personalCode;
        private boolean isAdmin;


//        public enum DataList{
//            ID, PW, NAME, BIRTHDAY, PERSONAL_CODE, IS_ADMIN
//        }

        public static String[] getDataListNames() {
            return new String[]{
                    DataList.ID.toString(), DataList.PW.toString(), DataList.NAME.toString(),
                    DataList.BIRTHDAY.toString(), DataList.PERSONAL_CODE.toString(), DataList.IS_ADMIN.toString(),
            };
        }

        //EFFECTS: create a new account that can access inventory and/or admin and return it.
        private LoginAccount(String id, String password, String name, LocalDate birthDay,
                             int personalCode, boolean isAdmin) {
            super(new String[]{
                DataList.ID.toString(), DataList.PW.toString(),
                    DataList.NAME.toString(), DataList.BIRTHDAY.toString(), DataList.PERSONAL_CODE.toString(),
                    DataList.IS_ADMIN.toString()
            });
            this.id = id;
            this.pw = password;
            this.name = name;
            this.birthday = birthDay;
            this.personalCode = personalCode;
            this.isAdmin = isAdmin;
        }


        //REQUIRES: data in json format must contain all the fields of this class with matching name.
        //EFFECTS: create a new account with data in json format
        private LoginAccount(JSONObject jsonLoginAccount) {
            super(new String[]{
                    DataList.ID.toString(), DataList.PW.toString(),
                    DataList.NAME.toString(), DataList.BIRTHDAY.toString(), DataList.PERSONAL_CODE.toString(),
                    DataList.IS_ADMIN.toString()
            });
            id = jsonLoginAccount.getString("id");
            pw = jsonLoginAccount.getString("pw");
            name = jsonLoginAccount.getString("name");
            personalCode = jsonLoginAccount.getInt("personalCode");
            JSONObject jsonDate = jsonLoginAccount.getJSONObject("birthday");
            isAdmin = jsonLoginAccount.getBoolean("isAdmin");
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

        //EFFECTS: return the access permission level
        private boolean isAdmin() {
            return isAdmin;
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
            json.put("isAdmin", isAdmin);
            return json;
        }

        @Override
        public Object[] convertToTableEntry() {
            return new Object[]{id, pw, name, birthday, personalCode, isAdmin
            };
        }
    }



    //EFFECTS: create a new administer.
    public Admin() {

        accounts = new ArrayList<>();
        currentAccount = null;

    }

    //REQUIRES: JSONObject must have all information needed to build admin with the matching name
    //EFFECTS: create a new admin with data in JSON format
    public Admin(JSONObject jsonAdmin) {
        accounts = new ArrayList<>();
        JSONArray jsonAccounts = jsonAdmin.getJSONArray("accounts");
        jsonAccounts.forEach(json -> accounts.add(new LoginAccount((JSONObject)json)));
        currentAccount = null;
    }



    public static Admin getAdmin() {
        return admin;
    }

    public List<LoginAccount> getAccounts() {
        return accounts;
    }


    //EFFECTS: if a login account can be found with the information given, return password of the account
    //return null otherwise.
    //When input data are compared to the existing accounts' data, it will be checked case-sensitively
    //For example, if name was set to "Pizza Chicken", neither "Pizza chicken" nor "pizza Chicken" will be matched
    //with the name successfully. "pizza chicken" will be the wrong input as well.
    //return null otherwise.
    public String retrievePassword(String id, String name, LocalDate birthDay, int personalNum) {
        LoginAccount account = getAccount(id);
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

//    //REQUIRES: the id must be already existing
//    //MODIFIES: this
//    //EFFECTS: set the current login account
//    public void setLoginAccount(String id) {
//        LoginAccount account = getLoginAccount(id);
//
//    }


    //EFFECTS: return true if there is no account in this
    //return false otherwise
    public boolean isEmpty() {
        return (accounts.size() == 0);
    }

//    public int getSize() {
//        return accounts.size();
//    }


    public LoginAccount getCurrentAccount() {
        return currentAccount;
    }

    //EFFECTS: return login account matching id if there exists such an account (case-sensitive).
    //return null otherwise.
    public LoginAccount getAccount(String id) {
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
        LoginAccount account = getAccount(id);
        if (account != null) {
            return account.passwordMatch(pw);
        }
        return false;
    }

    public LoginAccount getAccount(String id, String pw) {
        LoginAccount account = getAccount(id);
        if (account == null || !account.passwordMatch(pw)) {
            return null;
        }
        return  account;
    }

    //Only for when the admin is first constructed and so there is no admin member
    //REQUIRES: before calling this, the caller must ensure the user of the application is one of the admin members
    //MODIFIES: this
    //EFFECTS: if there is no login account at all, create a new account with the given information and return true
    //return fail if it fails
    public LoginAccount createLoginAccount(String id, String password, String name,
                                           LocalDate birthDay, int personalCode) {
        LoginAccount account = new LoginAccount(id, password, name, birthDay, personalCode, true);
        accounts.add(account);
        changeFirer.fireAdditionEvent(ACCOUNT, account);
        return account;
    }

    //REQUIRES: personal code must be a positive integer. No existing login account can have the same id
    //if the id has the same alphabetical arrangement as one of the existing ids, but in different cases like
    //Apple and apple, they will be regarded different
    //MODIFIES: this
    //EFFECTS: create a new account with the given information.
    public LoginAccount createLoginAccount(String id, String password, String name, LocalDate birthDay,
                                      int personalCode, boolean isAdmin) {
        if (getAccount(id) != null) {
            throw new IllegalArgumentException("Duplicate ID is not allowed");
        }
        LoginAccount account = new LoginAccount(id, password, name, birthDay, personalCode, isAdmin);
        accounts.add(account);
        changeFirer.fireAdditionEvent(ACCOUNT, account);
        //admin doesn't support TableEntryConvertible function
//        changeFirer.fireUpdateEvent(this);
        EventLog.getInstance().logEvent(new Event("new login account with ID: " + id + " is added"));
        return account;
    }



    //EFFECTS: return true if this login account is an admin member
    public boolean isAdminMember(String id) {
        if (getAccount(id) == null) {
            return false;
        }
        if (getAccount(id).isAdmin()) {
            return true;
        }
        return false;
    }

    public boolean isAdminMember(LoginAccount account) {
        String id = account.getId();
        if (getAccount(id) == null) {
            return false;
        }
        if (getAccount(id).isAdmin()) {
            return true;
        }
        return false;
    }

    public void setLoginAccount(LoginAccount account) {
        LoginAccount previous = currentAccount;
        currentAccount = account;
        if (previous == null) {
            if (account != null) {
                changeFirer.firePropertyChange(LOGIN, false, true);
            }
        } else if (account == null) {
                changeFirer.firePropertyChange(LOGIN, true, false);
        }
    }

    public boolean isLoggedIn() {
        return currentAccount != null;
    }

    public boolean isAdminLoggedIn() {
        return currentAccount != null && currentAccount.isAdmin();
    }



    //EFFECTS: convert this to JSONObject and return it.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("accounts", convertToJsonArray(accounts));
        return json;
    }


}
