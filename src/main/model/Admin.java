package model;

public class Admin {
    private final String id;
    private final String pw;
    private Inventory inventory;

    public Admin(String id, String password) {
        this.id = id;
        this.pw = password;
    }

}
