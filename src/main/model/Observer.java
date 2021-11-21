package model;

public interface Observer {

    //MODIFIES: this
    //EFFECTS: update this, so it reflects the changes of its subject
    void update();
}
