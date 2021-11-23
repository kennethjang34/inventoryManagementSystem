package ui;

import model.Observer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectPanel extends JPanel {
    private List<Observer> observers;
    private int changed;

    public SubjectPanel() {
        observers = new ArrayList<>();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (Observer observer: observers) {
            observer.update(changed);
        }
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }


}
