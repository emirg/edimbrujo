package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;
import org.json.simple.JSONObject;

public abstract class State {

    protected String name;
    private LinkedList<State> record;
    private LinkedList<String> events;
    protected boolean hasChanged;
    protected boolean destroy;

    public State(String name) {
        this.name = name;
        record = new LinkedList<>();
        events = new LinkedList<>();
        hasChanged = false;
        destroy = false;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void setDestroy(boolean destroy) {
        this.destroy = destroy;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        //TODO in concrete class
        return null;
    }

    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        //TODO in concrete class
        hasChanged = false;
        return this;
    }

    public void createState(State newState) {
        record.add((State) this.clone());
        this.setState(newState);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public State getState(int numState) {
        return record.get(numState);
    }

    public void setState(State newState) {
        //TODO in concrete class
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    @Override
    protected Object clone() {
        //TODO in concrete class
        return null;
    }

    public abstract JSONObject toJSON();

    public void addEvent(String event) {
        events.add(event);
    }

    public LinkedList<String> getEvents() {
        return events;
    }

    public void clearEvents() {
        events.clear();
    }

}
