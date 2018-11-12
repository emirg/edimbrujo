package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class State {

    private LinkedList<State> record;
    protected boolean hasChanged;

    public State() {
        record = new LinkedList<>();
        hasChanged = false;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public State next(LinkedList<State> states, HashMap<String, Action> actions) {
        //TODO in concrete class
        hasChanged = false;
        return this;
    }

    public void createState(State newState) {
        record.add((State) this.clone());
        this.setState(newState);
    }

    public State getState(int numState) {
        return record.get(numState);
    }

    public void setState(State newState) {
        //TODO in concrete class
    }

    @Override
    public String toString() {
        return "State";
    }

    @Override
    protected Object clone() {
        //TODO in concrete class
        return null;
    }

}
