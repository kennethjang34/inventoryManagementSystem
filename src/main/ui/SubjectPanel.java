package ui;

import model.Observer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SubjectPanel extends JPanel {
    protected List<Observer> observers;

    public SubjectPanel() {
        observers = new ArrayList<>();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public abstract void notifyObservers();


}
