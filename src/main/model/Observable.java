package model;

import java.util.ArrayList;
import java.util.List;


//represents an abstract subject that is observed by observers
public class Observable {
    //list of observers that get notified of change of this
    protected List<Observer> observers;
    protected int changed = -1;

    //EFFECTS: create a new subject with an empty observer list
    public Observable() {
        observers = new ArrayList<>();
    }

    //MODIFIES: this
    //EFFECTS: register a new observer in this
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    //MODIFIES: this
    //EFFECTS: set the changed to the given
    public void setChanged(int changed) {
        this.changed = changed;
    }

    //EFFECTS: notify all observers
    public void notifyObservers() {
        for (Observer observer: observers) {
            observer.update(changed);
        }
    }
}
