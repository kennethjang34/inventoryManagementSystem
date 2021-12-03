package ui;

import java.beans.PropertyChangeListener;

public abstract class AbstractController<M, V> implements PropertyChangeListener {
    protected M model;
    protected V view;

    public AbstractController(M model, V view) {
        this.model = model;
        this.view = view;
    }

    public abstract void setUpView();

}
