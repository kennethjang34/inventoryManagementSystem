package model;

import java.util.ArrayList;
import java.util.List;


//represents an abstract subject that is observed by observers
public abstract class Subject {
    //list of observers that get notified of change of this
    protected List<Observer> observers;

    //EFFECTS: create a new subject with an empty observer list
    public Subject() {
        observers = new ArrayList<>();
    }

    //MODIFIES: this
    //EFFECTS: register a new observer in this
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    //EFFECTS: notify all observers
    public abstract void notifyObservers();
}
