package ui;

import java.beans.PropertyChangeListener;

//abstract class for specifying basic interface for controllers of MVC architecture implemented in this project
public abstract class AbstractController<M, V> implements PropertyChangeListener {
    protected M model;
    protected V view;

    public AbstractController(M model, V view) {
        this.model = model;
        this.view = view;
        setUpView();
    }

    public V getView() {
        return view;
    }

    public M getModel() {
        return model;
    }

    public abstract void setUpView();

}
